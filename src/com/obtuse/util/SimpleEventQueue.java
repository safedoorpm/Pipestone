/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.util.*;

/**
 * Very simple event queue manager.
 */

public class SimpleEventQueue<T extends SimpleEvent> {

    public static final FormattedImmutableDate LAST_POSSIBLE_EVENT_TIME = new FormattedImmutableDate( Long.MAX_VALUE );
    public static final FormattedImmutableDate FIRST_POSSIBLE_EVENT_TIME = new FormattedImmutableDate( 0L );

    private final SortedMap<FormattedImmutableDate,LinkedList<T>> _eventQueue = new TreeMap<FormattedImmutableDate,LinkedList<T>>();
    private FormattedImmutableDate _now;
    private FormattedImmutableDate _endTime = SimpleEventQueue.LAST_POSSIBLE_EVENT_TIME;

    private boolean _traceMode = false;

    /**
     * A wrapper for a set of simultaneous clock events and the timestamp associated with all of them.
     */

    public static class TimestampedClockEventContainer<T extends SimpleEvent> {

        private final FormattedImmutableDate _eventTime;
        private final List<T> _events;

        @SuppressWarnings({ "UnusedDeclaration" })
        public TimestampedClockEventContainer( Date eventTime, T event ) {
            super();

            _eventTime = new FormattedImmutableDate( eventTime );
            _events = new LinkedList<T>();
            add( event );

        }

        public TimestampedClockEventContainer( Date eventTime, Collection<T> event ) {
            super();

            _eventTime = new FormattedImmutableDate( eventTime );
            _events = new LinkedList<T>();
            addAll( event );

        }

        public void add( T event ) {

            _events.add( event );

        }

        public void addAll( Collection<T> events ) {

            _events.addAll( events );

        }

        public List<T> getClockEvents() {

            return Collections.unmodifiableList( _events );

        }

        public FormattedImmutableDate getEventTime() {

            return _eventTime;

        }

        public String toString() {

            return "TimestampedClockEventContainer( " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( _eventTime ) + ", " + SimpleEventQueue.eventsToString( _events ) + " )";

        }

    }

    /**
     * A wrapper for a single clock event and its associated timestamp.
     */

    public static class TimestampedClockEventInstance<T extends SimpleEvent> {

        private final FormattedImmutableDate _eventTime;
        private final T _event;

        public TimestampedClockEventInstance( Date eventTime, T event ) {
            super();

            //noinspection AssignmentToDateFieldFromParameter
            _eventTime = new FormattedImmutableDate( eventTime );
            _event = event;

        }

        public T getClockEvent() {

            return _event;

        }

        public FormattedImmutableDate getEventTime() {

            return _eventTime;

        }

        public String toString() {

            return "TimestampedClockEventInstance( " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( _eventTime ) + ", " + _event + " )";

        }

    }

    public SimpleEventQueue() {
        super();

        _now = SimpleEventQueue.FIRST_POSSIBLE_EVENT_TIME;

    }

    public static <T> String eventsToString( Collection<T> events ) {

        StringBuilder rval = new StringBuilder();
        rval.append( '[' );
        String comma = "";
        for ( T event : events ) {

            rval.append( comma ).append( event );
            comma = ",";

        }

        rval.append( ']' );

        return rval.toString();

    }

    public void showQueue( String why, boolean hideEmpties ) {

        StringBuilder msg = new StringBuilder( why ).append( ":  [ " );
        for ( FormattedImmutableDate when : _eventQueue.keySet() ) {

            if ( !_eventQueue.get( when ).isEmpty() || !hideEmpties ) {

                msg.append( "   " ).append( SimpleEventQueue.eventsToString( _eventQueue.get( when ) ) );

            }

//            int ix = 0;
//            for ( ClockEvent event : _eventQueue.get( when ) ) {
//
//                Logger.logMsg( "" + new Date( when ) + " / " + ix + ":  " + event );
//                ix += 1;
//
//            }

        }
        msg.append( " ]" );
        Logger.logMsg( msg.toString() );

    }

    public void setEndTime( Date when ) {

        //noinspection AssignmentToDateFieldFromParameter
        _endTime = new FormattedImmutableDate( when );

    }

    public void qBefore( Date when, T event ) {

        queueEvent( when, event, true );
        if ( _traceMode ) {

            Logger.logMsg( "event " + event + " queued before " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

        }

//        showQueue();

    }

//    private void queueBefore( long when, ClockEvent event ) {
//
//        if ( when > _endTime ) {
//
//            Logger.logMsg( "attempt to queue event \"" + event + "\" at " + new Date( when ) + " which is after the end time of " + new Date( _endTime ) + " - ignored" );
//            return;
//
//        }
//
//        List<ClockEvent> queue = findQueue( when );
//
//        queue.add( 0, event );
//
//    }

    public void qAfter( Date when, T event ) {

        queueEvent( when, event, false );
        if ( _traceMode ) {

            Logger.logMsg( "event " + event + " queued after " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

        }

//        showQueue();

    }

    private void queueEvent( Date xWhen, T event, boolean before ) {

        FormattedImmutableDate when = new FormattedImmutableDate( xWhen );
        if ( when.compareTo( _now ) < 0 || _now.equals( SimpleEventQueue.LAST_POSSIBLE_EVENT_TIME ) ) {

            Logger.logErr(
                    "ERROR:  attempt to queue event \"" + event + "\" at " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) +
                            " which is before the current clock time of " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( _now ) + " - ignored"
            );

            return;

        }

        if ( when.compareTo( _endTime ) > 0 ) {

            Logger.logErr(
                    "ERROR:  attempt to queue event \"" + event + "\" at " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) +
                            " which is after the end time of " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( _endTime ) + " - ignored"
            );

            return;

        }

        List<T> queue = findQueue( when );

        if ( before ) {

            queue.add( 0, event );

        } else {

            queue.add( event );

        }

    }

    private List<T> findQueue( FormattedImmutableDate when ) {

        if ( when.compareTo( _now ) < 0 ) {

            throw new IllegalArgumentException(
                    "attempt to add event at " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) +
                            " when time is already " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( _now )
            );

        }

        LinkedList<T> queue = _eventQueue.get( when );
        if ( queue == null ) {

            queue = new LinkedList<T>();
            _eventQueue.put( when, queue );

        }

        return queue;

    }

    private TimestampedClockEventContainer<T> getOrPeekNextEventBatch( boolean peek, boolean advanceNow ) {

        while ( true ) {

            if ( _eventQueue.isEmpty() ) {

                return null;

            }

            FormattedImmutableDate peekNow = _eventQueue.firstKey();

            if ( _eventQueue.get( peekNow ).isEmpty() ) {

                _eventQueue.remove( peekNow );

            } else {

                if ( peek ) {

                    return new TimestampedClockEventContainer<T>( peekNow, _eventQueue.get( peekNow ) );

                } else {

                    _now = new FormattedImmutableDate( peekNow.getTime() + ( advanceNow ? 1 : 0 ) );
                    return new TimestampedClockEventContainer<T>( peekNow, _eventQueue.remove( peekNow ) );

                }

            }

        }

    }

    private TimestampedClockEventInstance<T> getOrPeekNextEvent( boolean peek ) {

        TimestampedClockEventContainer<T> nextBatch = getOrPeekNextEventBatch( true, false );
        if ( nextBatch == null ) {

            return null;

        } else if ( peek ) {

            return new TimestampedClockEventInstance<T>( nextBatch.getEventTime(), nextBatch.getClockEvents().get( 0 ) );

        } else {

            return new TimestampedClockEventInstance<T>( nextBatch.getEventTime(), _eventQueue.get( nextBatch.getEventTime() ).remove( 0 ) );

        }

    }

    /**
     * Peek at the next available event.
     * If there are any events left in this instance then the first of these events is returned.
     * Calls to this method do not change the state of this instance.
     * @return the next available event or null if there are no more events in this instance.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public TimestampedClockEventInstance<T> peekNextEvent() {

        return getOrPeekNextEvent( true );

    }

    /**
     * Get the next available event.
     * If there are any events left in this instance then the first of these events is removed and returned and the clock is set to the time of said event.
     * Otherwise, null is returned and the clock is left unchanged.
     * @return the next available event or null if there are no events left in this instance.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public TimestampedClockEventInstance<T> getNextEvent() {

        return getOrPeekNextEvent( false );

    }

    /**
     * Peek at the next available batch of events.
     * <p/>
     * Calls to this method do not change the state of this instance.
     * @return the next available batch of events or null if there are no more events in this instance.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public TimestampedClockEventContainer<T> peekNextEventBatch() {

        return getOrPeekNextEventBatch( true, false );

    }

    /**
     * Get the next available batch of events and advance the clock either past or to the time of said events.
     * <p/>
     * If this instance has no events left then null is returned and the state of this instance is not changed.
     * Otherwise, all events scheduled at the earliest time for which events are scheduled are removed and returned, and the clock is set to
     *     <ul>
     *         <li>the time of these events if <tt>advancePast</tt> is false
     *         (i.e. it will STILL be possible to queue events at the time of the events just returned)</li>
     *         <li>one quantum past the time of these events if <tt>advancePast</tt> is true
     *         (i.e. it will NOT be possible to queue events at the time of the events just returned)</li>
     *     </ul>
     * @param advancePast true if the clock is to be advanced past the time of the returned batch of events; false if the clock is
     * to be advanced to the time of the returned batch of events; ignored if no events are returned.
     * @return the next available batch of events or null if there are no more events.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public TimestampedClockEventContainer<T> getNextEventBatch( boolean advancePast ) {

        return getOrPeekNextEventBatch( false, advancePast );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public FormattedImmutableDate getClock() {

        return _now;

    }

    /**
     * Empty this event queue and make it impossible to add more events.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public void endUniverse() {

        _eventQueue.clear();
        _now = SimpleEventQueue.LAST_POSSIBLE_EVENT_TIME;

    }

    /**
     * Clear this event queue and prepare it for reuse.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public void clear() {

        _eventQueue.clear();
        _now = FIRST_POSSIBLE_EVENT_TIME;

    }

//        while ( true ) {
//
//            if ( _eventQueue.isEmpty() ) {
//
//                return null;
//
//            }
//
//            long peekNow = _eventQueue.firstKey().longValue();
//            LinkedList<ClockEvent> queue = _eventQueue.get( peekNow );
//
//            if ( queue.isEmpty() ) {
//
//                _eventQueue.remove( peekNow );
//
//            } else {
//
//                if ( peek ) {
//
//                    return new TimestampedClockEventContainer( peekNow, queue.getFirst() );
//
//                } else {
//
//                    _now = peekNow;
//                    return new TimestampedClockEventContainer( _now, queue.remove() );
//
//                }
//
//            }
//
//        }
//
//    }

//    public TimestampedClockEventContainer getNextEvent() {
//
//        return peekOrGetNextEvent( false );
//
//    }
//
//    public TimestampedClockEventContainer peekNextEvent() {
//
//        return peekOrGetNextEvent( true );
//
//    }

    @SuppressWarnings({ "StaticMethodNamingConvention" })
    public void go() {

        runClock();

    }

    private void runClock() {

        while ( true ) {

            if ( _traceMode ) {

                showQueue( "getting", true );

            }

            TimestampedClockEventInstance<T> event = getNextEvent();
            if ( event == null ) {

                break;

            }

            if ( _traceMode ) {

                showQueue( "doing", true );
                Logger.logMsg( "doing eventContainer " + event.getClockEvent() + " for " + event.getEventTime() );

            }

            try {

                event.getClockEvent().run( event.getEventTime() );

            } catch ( Throwable e ) {

                Logger.logErr( "ERROR:  event \"" + event.getClockEvent() + "\" failed @ " + event.getEventTime(), e );

            }

        }

//        while ( !_eventQueue.isEmpty() ) {
//
//            _now = _eventQueue.firstKey().intValue();
//            List<ClockEvent> queue = _eventQueue.get( _now );
//
//            Date when = new Date( _now );
//            Logger.logMsg( "doing events for " + when );
//
//            while ( !queue.isEmpty() ) {
//
//                ClockEvent nextEvent = queue.remove( 0 );
//                Logger.logMsg( "doing event " + nextEvent + " for " + when );
//
//                try {
//
//                    nextEvent.run( _now );
//
//                } catch ( Throwable e ) {
//
////                    Logger.flushMsg();
//                    Logger.logErr( "event \"" + nextEvent + "\" failed @ " + when, e );
////                    Logger.flushErr();
//
//                }
//
//            }
//
//            _eventQueue.remove( _now );
//            _now += 1;
//
//        }

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "SimpleEventQueue", "Test", null );

        final SimpleEventQueue<SimpleEvent> eventQueue = new SimpleEventQueue<SimpleEvent>();

        eventQueue.setEndTime( new FormattedImmutableDate( 7200 ) );

        eventQueue.qBefore(
                new FormattedImmutableDate( 3600 ),
                new SimpleEvent( "event 1" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );
                        eventQueue.qBefore(
                                new FormattedImmutableDate( 3600 ),
                                new SimpleEvent( "event 2" ) {

                                    @Override
                                    public void run( FormattedImmutableDate when ) {

                                        Logger.logMsg( ">> " + this + " inner hello @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                                    }

                                }
                        );

                    }

                }
        );

        eventQueue.qBefore(
                new FormattedImmutableDate( 3600 ),
                new SimpleEvent( "event 3" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " before @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                    }

                }
        );

        eventQueue.qAfter(
                new FormattedImmutableDate( 3600 ),
                new SimpleEvent( "event 4" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " after @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                    }

                }
        );

        eventQueue.qBefore(
                new FormattedImmutableDate( 0 ),
                new SimpleEvent( "event 5" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                    }

                }
        );

        eventQueue.qBefore(
                new FormattedImmutableDate( 7200 ),
                new SimpleEvent( "event 6" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                    }

                }
        );

        eventQueue.qAfter(
                new FormattedImmutableDate( 7200 ),
                new SimpleEvent( "event 7" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logMsg( ">> " + this + " @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                        // This will explode since we are trying to add an event prior to the current time.

                        eventQueue.qAfter(
                                new FormattedImmutableDate( 0 ),
                                new SimpleEvent( "event 7" ) {

                                    @Override
                                    public void run( FormattedImmutableDate when ) {

                                        Logger.logMsg( ">> " + this + " @ " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( when ) );

                                    }

                                }
                        );

                    }

                }
        );

        eventQueue.qAfter(
                new FormattedImmutableDate( 7201 ),
                new SimpleEvent( "first discarded event" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logErr( "*** this event should have been discarded" );

                    }

                }
        );

        eventQueue.qBefore(
                new FormattedImmutableDate( 7201 ),
                new SimpleEvent( "second discarded event" ) {

                    @Override
                    public void run( FormattedImmutableDate when ) {

                        Logger.logErr( "*** this event should have been discarded" );

                    }

                }
        );

        eventQueue.go();

        Logger.logMsg( "all done" );

    }

}
