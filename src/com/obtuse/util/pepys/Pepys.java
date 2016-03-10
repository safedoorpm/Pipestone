package com.obtuse.util.pepys;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.pepys.data.PepysAnchor;
import com.obtuse.util.pepys.data.PepysEventListener;
import com.obtuse.util.pepys.data.PepysSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 The desk across which all news travels.
 */

public class Pepys {

    public enum PepysEventType {
	CREATED,
	CHANGED,
	GONE;
    }

    /**
     Something that just happened.
     <p/>Note that if there is more than one listener for a particular event then the event is passed in turn to each listener.
     In order to make this at least somewhat repeatable, instances of this class are immutable.
     <p/>It is possible for the sender of an event to lie about the source, source id and/or auxillary source of an event and the type of an event for all event types other thant <code>GONE</code>.
     This class guarantees, barring any use of Java reflection to craft truly bogus events or the existence of multiple sources with the same source id, that the source id of <code>GONE</code> events is correct.
     */

    public abstract static class PepysEvent {

	private final PepysEventType _eventType;
	private final PepysSource _source;
	private final long _sourceId;
	private final PepysSource _auxSource;

	/**
	 Create an event with just an event type and a source.
	 @param eventType the type of the event (will not be <code>GONE</code>).
	 @param source the source of the event.
	 */

	public PepysEvent( @NotNull PepysEventType eventType, @NotNull PepysSource source ) {

	    this( eventType, source, null );

	}

	/**
	 Create an event with an event type, a source and an auxillary source.
	 @param eventType the event type (will not be GONE).
	 @param source the source of the event.
	 @param auxSource a secondary source of interest (the newly created source should <code>eventType</code> be <code>CREATED</code>).
	 */

	public PepysEvent( @NotNull PepysEventType eventType, @NotNull PepysSource source, @Nullable PepysSource auxSource ) {

	    if ( eventType == PepysEventType.GONE ) {

		throw new IllegalArgumentException( "cannot create 'gone' events via this constructor" );

	    }
//	    if ( eventType == PepysEventType.CREATED ) {
//
//		if ( auxSource == null ) {
//
//		    throw new IllegalArgumentException( "'create' events must have an auxSource attribute" );
//
//		}
//
//	    }

	    _eventType = eventType;
	    _source = source;
	    _sourceId = source.getPepysId();
	    _auxSource = auxSource;

	}

	/**
	 Send a <code>GONE</code> event.
	 @param sourceId the source id of the 'gone' source object.
	                 The event's type will be <code>GONE</code> and its <code>source</code> and <code>auxSource</code> fields will be null.
	 */

	private PepysEvent( long sourceId ) {
	    super();

	    _eventType = PepysEventType.GONE;
	    _source = null;
	    _sourceId = sourceId;
	    _auxSource = null;

	}

	/**
	 Get this event's type.
	 @return this event's type.
	 */

	public PepysEventType getEventType() {

	    return _eventType;

	}

	/**
	 Get this event's source.
	 <p/>Advisory notes (these are conventions which software developers using the Pepys facility are encouraged to follow
	 - your mileage may vary):
	 <ul><li>If this is a <code>CREATED</code> event then this should be the data entity which initially owns the new source
	 or the non-data source which 'spontaneously' created the new source.
	 How to distinguish these two cases is beyond the scope of this class (sorry).</li>
	 <li>If this is a <code>GONE</code> event then this field will be <code>null</code> (this is enforced by the {@link PepysEvent} class).</li>
	 <li>Otherwise, this field should reference the data entity which has changed or otherwise affected by this event.</li>
	 </ul>
	 @return this should be the event's source (this class guarantees that this return value will be <code>null</code> for
	 <code>GONE</code> events and will never be <code>null</code> for other event types).
	 */

	public PepysSource getSource() {

	    return _source;

	}

	/**
	 Get this event's source id.
	 @return this event's source's source id.
	 Note that if this is a <code>GONE</code> event then this method returns the source id of the now non-existent source.
	 */

	public long getSourceId() {

	    return _sourceId;

	}

	/**
	 Get this event's auxillary source.
	 @return the newly created source if this is a <code>CREATED</code> event. Currently <code>null</code> for all other event types.
	 */

	public PepysSource getAuxSource() {

	    return _auxSource;

	}

	public String toString() {

	    return "PepysEvent( " + _eventType + ", " + _source + ", " + _sourceId + ", " + _auxSource + " )";

	}

    }

    /**
     A source has ceased to exist.
     */

    public static class PepysGoneEvent extends PepysEvent {

	private PepysGoneEvent( long sourceId ) {
	    super( sourceId );

	}

    }

    /**
     A test event in some context.
     */

    public static class PepysTestEvent extends PepysEvent {

	private PepysTestEvent( @NotNull PepysEventType eventType, @NotNull PepysSource source ) {
	    super( eventType, source );

	}

    }

    /**
     For lazy developers.
     */

    public static class PepysGenericEvent extends PepysEvent {

	private PepysGenericEvent( @NotNull PepysEventType eventType, @NotNull PepysSource source ) {
	    super( eventType, source );

	}

    }

    public static class ProxiedPepysEvent extends PepysEvent {

	private final PepysEvent _proxiedEvent;

	public ProxiedPepysEvent( @NotNull PepysEventType eventType, @NotNull PepysSource source, PepysEvent proxiedEvent ) {
	    super( eventType, source );

	    _proxiedEvent = proxiedEvent;

	}

	public PepysEvent getProxiedEvent() {

	    return _proxiedEvent;

	}

	public PepysEvent getOriginalEvent() {

	    int depth;

	    PepysEvent ev = getProxiedEvent();
	    while ( ev instanceof ProxiedPepysEvent ) {

		ev = ( (ProxiedPepysEvent) ev ).getProxiedEvent();

	    }

	    return ev;

	}

	public String toString() {

	    return "ProxiedPepysEvent( eType=" + getEventType() + ", source=" + getSource() + ", pEvent=" + getProxiedEvent() + ", origEvent=" + getOriginalEvent() + " )";

	}

    }

    /**
     A weak reference to a single notification source object.
     */

    private static class PepysWeakSourceRef extends WeakReference<PepysSource> {

	/**
	 The source's id.
	 <p/>This is mostly used to refer to the source after it ceases to exist.
	 */

	private final long _sourceId;

	/**
	 A list of weak references to all the listeners who have expressed an interest in getting notifications from this source.
	 */

	private final List<WeakReference<PepysEventListener>> _listeners = new FormattingLinkedList<WeakReference<PepysEventListener>>();

	/**
	 Create a weak reference to a source.
	 @param source the referenced source.
	 */

	private PepysWeakSourceRef( @NotNull PepysSource source ) {
	    super( source );

	    _sourceId = source.getPepysId();

	}

	/**
	 Register a listener which is to be notified when an interesting event happens to this source.
	 <p/>It is the source's responsibility to decide what is sufficiently interesting to be worth reporting as an event.
	 Every registered listener gets all the events fired by the source.
	 <p/>The registered listeners are kept in a list of weak references. When one or more of these weak references is found to be stale, it is silently dropped.
	 <p/>There is currently no way to unregister a listener. This will almost certainly have to change soon.
	 @param listener the listener.
	 */

	private synchronized void registerEventListener( @NotNull PepysEventListener listener ) {

	    _listeners.add( new WeakReference<PepysEventListener>( listener ) );

	}

	/**
	 Deliver an event to all the listeners registered with this source.
	 <p/>
	 Note that irrespective of which thread calls this method, all events are delivered to listeners from within the
	 AWT event dispatching thread.
	 @param event the event to be delivered to all the listeners.
	              Note that since a single event instance is delivered to every registered source, it is probably a
	              bad idea to make events mutable.
	 */

	private void fireEvent( @NotNull final PepysEvent event ) {

	    Logger.logMsg( "firing event for source " + _sourceId + " (" + _listeners.size() + " listeners)" );
	    final List<PepysEventListener> eligibleListeners;

	    synchronized ( this ) {

		eligibleListeners = getPepysEventListeners( "" + event, _listeners );

	    }

	    // If we found any still alive listeners then send them the event from within the AWT event dispatching thread.

	    if ( !eligibleListeners.isEmpty() ) {

		SwingUtilities.invokeLater(
			new Runnable() {

			    @Override
			    public void run() {

				for ( PepysEventListener listener : eligibleListeners ) {

				    try {
					switch ( event.getEventType() ) {

					    case CREATED:
						listener.PepysSourceCreated( event );
						break;

					    case CHANGED:
						listener.PepysSourceChanged( event );
						break;

					    case GONE:
						listener.PepysSourceGone( event );
						break;

					}

				    } catch ( Throwable e ) {

					Logger.logErr( e.getClass().getSimpleName() + ":  caught handling event type " + event.getEventType() + " - " + e, e );

				    }

				}

			    }

			}
		);

	    }

	}

	/**
	 Get a list of the currently listeners which still exist from a list of weak references to registered listeners.
	 Weak references to now gone listeners are silently dropped by this method.
	 @param what a very brief description of why this list is being requested.
	 @param listeners the list of weak references to listeners to be scanned for still existent listeners.
	 @return a list of weak references to the still existent listeners.
	 */

	@NotNull
	private List<PepysEventListener> getPepysEventListeners( String what, List<WeakReference<PepysEventListener>> listeners ) {

	    final List<PepysEventListener> eligibleListeners = new FormattingLinkedList<PepysEventListener>();
	    for ( Iterator<WeakReference<PepysEventListener>> iterator = listeners.iterator(); iterator.hasNext(); ) {

		WeakReference<PepysEventListener> listenerReference = iterator.next();

		PepysEventListener listener = listenerReference.get();
		if ( listener == null ) {

//		    Logger.logMsg( "" + what + ":  listener is gone" );
		    iterator.remove();

		} else {

//		    Logger.logMsg( "" + what + ":  listener exists" );
		    eligibleListeners.add( listener );

		}

	    }

	    return eligibleListeners;
	}

    }

    /**
     A phantom reference to/for a single source object.
     */

    public static class PepysPhantomSourceRef extends PhantomReference<PepysSource> {

	/**
	 A (static) set of all currently active (not stale) phantom references to source objects.
	 <p/>
	 This set exists to ensure that our phantom references do not get garbage collected before they get a chance to appear
	 in our reference queue of recently garbage collected source objects.
	 */

	private static final Set<PhantomReference<PepysSource>> _activePhantoms = new HashSet<PhantomReference<PepysSource>>();

	/**
	 A weak reference to the source object that this phantom reference is associated with.
	 We use this weak reference to send "source object gone" events when a source object gets garbage collected.
	 */

	private final PepysWeakSourceRef _weakSourceRef;

	/**
	 The source's id.
	 <p/>Every source must have a JVM-wide unique source id. Truly epic learning experiences (i.e. difficult to resolve mysteries)
	 will probably occur if this rule is violated.
	 */

	private final long _sourceId;

	/**
	 Create a phantom reference to a source object.
	 @param source the source object of interest.
	 @param phantomReferenceQueue the reference queue that the about-to-be-created phantom reference is to be placed on
	                              when the source object of interest gets garbage collected.
	 */

	private PepysPhantomSourceRef( @NotNull PepysSource source, @NotNull ReferenceQueue<PepysSource> phantomReferenceQueue ) {
	    super( source, phantomReferenceQueue );

	    _weakSourceRef = new PepysWeakSourceRef( source );
	    _sourceId = source.getPepysId();

	    // Make sure that we always have strong references to the phantom references to PepysSource objects to ensure that the
	    // the phantom references still exist to be enqueued onto our reference queue when the corresponding PepysSource
	    // object gets garbage collected.

	    synchronized ( PepysPhantomSourceRef._activePhantoms ) {

		PepysPhantomSourceRef._activePhantoms.add( this );

	    }

	}

	/**
	 Get this phantom reference's weak source reference.
	 @return this phantom reference's weak source reference.
	 */

	public PepysWeakSourceRef getWeakSourceRef() {

	    return _weakSourceRef;

	}

	/**
	 Get the source id of the source object 'referenced' by this phantom reference.
	 @return the source id of the source object 'referenced' by this phantom reference.
	 */

	public long getSourceId() {

	    return _sourceId;

	}

	/**
	 Send out an appropriate event when the source object 'referenced' by this phantom reference is reported to have been garbage collected.
	 */

	private void processSourceGone() {

	    Logger.logMsg( "source gone " + _sourceId );

	    PepysEvent goneEvent = new PepysGoneEvent( _sourceId );
	    _weakSourceRef.fireEvent( goneEvent );

	    // Don't need this phantom reference anymore. Drop it from our set of active phantom references.

	    synchronized ( PepysPhantomSourceRef._activePhantoms ) {

		PepysPhantomSourceRef._activePhantoms.remove( this );

	    }

	}

    }

    /**
     Our singleton instance.
     <p/>I am not sure that there is a good reason why the Pepys class is not just a utility class with only static classes and methods.
     */

    private static final Pepys s_pepys = new Pepys();

    /**
     The reference queue onto which our JVM's garbage collector will place phantom references for gone (as in garbage collected) source objects.
     */

    private final ReferenceQueue<PepysSource> _goneSources = new ReferenceQueue<PepysSource>();

    /**
     A weak hash mapping of source objects to their corresponding weak source references.
     <p/>Note that the {@link WeakHashMap} class automagically discards keys which correspond to garbage collected source objects.
     This is not a problem since we should not be receiving events from garbage collected source objects and we have a
     {@link PepysWeakSourceRef} to each source object (this weak reference contains what we need in order to send
     out the 'gone' event when we are informed of the garbage collection of a source object by the placement of the source object's phantom
     reference on our phantom reference reference queue).
     */

    private final WeakHashMap<PepysSource,PepysWeakSourceRef> _sourceMap = new WeakHashMap<PepysSource, PepysWeakSourceRef>();

    /**
     Indicate whether or not our utility thread should continue to watch for and process new phantom references on our phantom reference reference queue.
     <p/><code>true</code> implies continue dealing with 'inbound' phantom references; Setting this boolean to <code>false</code> will cause
     our utility thread to terminate.
     <p/>
     There is currently no way to change the value of this boolean although that could change.
     */

    private boolean _runUtilityThread;

    /**
     A reference to our utility thread.
     */

    private Thread _utilityThread;

    /**
     Our private constructor (used to create our singleton instance and to launch our utility thread).
     */

    private Pepys() {
	super();

	launchCleanerThread();

    }

    /**
     A utility method which launches our utility thread.
     <p/>
     The primary (currently only) responsibility of our utility thread is to react to the arrival of phantom references on our reference queue.
     <p/>
     Note that our utility thread is a <i>daemon</i> thread which means that its continued existence will not keep the JVM alive once all
     non-daemon threads have terminated.
     */

    private void launchCleanerThread() {

	_runUtilityThread = true;

	_utilityThread = new Thread( "PepysCleaner" ) {

	    public void run() {

		while ( _runUtilityThread ) {

		    try {

			while ( true ) {

//			    Logger.logMsg( "checking phantom queue" );
//			    for ( PepysSource ps : _sourceMap.keySet() ) {
//
//				Logger.logMsg( "saw " + ps );
//
//			    }

			    Reference<? extends PepysSource> phantomRef = _goneSources.remove( 1000L );

			    if ( phantomRef == null ) {

				break;

			    } else {

				PepysPhantomSourceRef pepysPhantomRef = (PepysPhantomSourceRef)phantomRef;
				Logger.logMsg( "cleaning source id " + pepysPhantomRef.getSourceId() );
				pepysPhantomRef.processSourceGone();

			    }

			}

		    } catch ( InterruptedException e ) {

			// just ignore these.

		    }

		}

	    }

	};

	_utilityThread.setDaemon( true );
	_utilityThread.start();

    }

    /**
     A static method used by listeners to express an interest in receiving events from a particular source object.
     * @param source the source object of interest.
     @param listener the listener which is to receive events sent by the specified source object.
     */

    public static void registerInterest(
	    @NotNull PepysSource source,
	    @NotNull PepysAnchor anchor,
	    @NotNull PepysEventListener listener
    ) {

	Pepys.s_pepys.xRegisterInterest( source, anchor.anchor( listener ) );

    }

    /**
     The actual instance method which deals with expressions of interest in receiving events from a particular source object.
     <p/>
     An invocation of this method will implicitly create the infrastructure required to deal with (send events for and react to the
     garbage collection of) a previously unknown source object.
     <p/>
     There is currently no way to explicitly 'register' or 'de-register' a source object with Pepys.
     This is unlikely to ever change at least in part because explicit registration seems pretty useless and
     for this class to completely 'de-register' a previously 'registered' source object since there is no way to suppress the
     eventual placement of a previously 'registered' source object's phantom reference onto our reference queue. We could, in theory,
     keep track of which previously 'registered' source objects have been 'de-registered' but that seems like a waste of effort.
     Alternatively, we could mark the phantom references to 'de-registered' source objects so that we ignore them when they arrive on
     our reference queue but this would be less work than keeping track of previously 'de-registered' source objects, it also seems
     pretty pointless.
     @param source the source object of interest.
     @param listener the listener which is to receive events sent by the specified source object.
     */

    private synchronized void xRegisterInterest( @NotNull PepysSource source, @NotNull PepysEventListener listener ) {

	WeakReference weakSource = new WeakReference<PepysSource>( source );
	WeakReference weakListener = new WeakReference<PepysEventListener>( listener );
	PepysWeakSourceRef listeners = _sourceMap.get( source );
	if ( listeners == null ) {

	    PepysPhantomSourceRef phantomRef = new PepysPhantomSourceRef( source, _goneSources );

	    listeners = phantomRef.getWeakSourceRef();

	    _sourceMap.put( source, listeners );

	}

	Logger.logMsg( "remembering listener for " + source.getClass().getSimpleName() + " source id " + source.getPepysId() );
	listeners.registerEventListener( listener );

    }

    /**
     A static method intended to be used by source objects to request that Pepys deliver an event to 'registered' listeners.
     <p/>
     Note that irrespective of which thread makes the request, all Pepys events are delivered to listeners from within the
     AWT event dispatching thread (this explicitly includes 'gone' events).
     <p/>
     Pepys events are queued for delivery in strictly the order in which they are passed to this method. For presumably obvious reasons,
     events are only queued for delivery to listeners which exist at the time that the event is being queued. Once an event has been queued,
     a strong reference to the listener will exist until after the event is actually delivered. This prevents the listener from being
     garbage collected until after the queued event has been delivered.
     <p/>
     Any event which is not a 'gone' event will contain a strong reference to the source object and will therefore be delivered while
     the source object still exists (the strong reference to the source object in the event guarantees this result).
     <p/>
     Note that there is a potential race condition of sorts. This race can only be
     encountered if two or more events are sent from different threads at essentially the same moment in time. If such a race is
     possible in a given application then the order in which the two events are queued for delivery would be unpredictable even if calls
     to this method were somehow strictly ordered by the implementation of this method.
     <p/>
     At the moment when an event is queued for delivery, there could be other events which were previously queued and which are still
     awaiting delivery. All of these previously queued events will be delivered (by passing them to the appropriate listener's event method
     and waiting for said method call to return) before this event is delivered. Also, any event not yet queued when this event is queued
     will not be delivered until after this event has been delivered.
     <p/>
     Wise software developers will avoid using code from within one class to fire events on behalf of a source object which is
     implemented by some other class (let the source object's implementation do the firing as this at least avoids race
     conditions like the firing of events for very recently garbage collected source objects).
     @param event the event to be delivered to registered listeners of the source object specified by said event (this method just returns
                  without doing anything useful if the specified event reference is null).
                  With the sole exception of 'gone' events, these events contain a strong reference to the source object in question.
                  The existence of this strong reference ensures that, with the exception of 'gone' events, events which are delivered to
                  a listener will reference a source object which still exists at the time of delivery. See earlier advice regarding the
                  wisdom of letting the source object's class's implementation be responsible for calling this method.
     */

    public static void fireEvent( @Nullable PepysEvent event ) {

	if ( event != null ) {

	    Pepys.s_pepys.xFireEvent( event );

	}

    }

    /**
     A static method intended to be used by source objects to request that Pepys send an ordered sequence of events to 'registered' listeners.
     Once a call to this method queues its first event, all other events passed for said call will be queued for delivery before any
     events requested by other callers are queued.
     <p/>See {@link #fireEvent} for more information (the discussion of race conditions in the Javadocs for {@link #fireEvent} do not apply
     to events delivered via this method).
     <p/>
     @param events the array of events to be delivered (any null elements in the specified array are ignored).
                   This method just returns without doing anything useful if the specified array reference is null.
     */

    public static void fireEvents( @Nullable PepysEvent[] events ) {

	if ( events != null ) {

	    synchronized ( Pepys.s_pepys ) {

		for ( PepysEvent event : events ) {

		    if ( event != null ) {

			Pepys.s_pepys.xFireEvent( event );

		    }

		}

	    }

	}

    }

    /**
     A static method intended to be used by source objects to request that Pepys deliver a collection of events to 'registered' listeners
     (the events will be queued in the order that they are returned by an iterator created by the specified collection's <code>iterator()</code> method).
     Once a call to this method queues its first event, all other events passed for said call will be queued for delivery before any
     events requested by other callers are queued.
     <p/>See {@link #fireEvent} for more information (the discussion of race conditions in the Javadocs for {@link #fireEvent} do not apply
     to events delivered via this method).
     <p/>
     @param events the collection of events to be delivered (any null elements in the specified collection are ignored).
                   This method just returns without doing anything useful if the specified collection reference is null.
     */

    public static void fireEvents( @Nullable Collection<PepysEvent> events ) {

	if ( events != null ) {

	    synchronized ( Pepys.s_pepys ) {

		for ( PepysEvent event : events ) {

		    if ( event != null ) {

			Pepys.s_pepys.xFireEvent( event );

		    }

		}

	    }

	}

    }

    /**
     The instance method responsible for handling requests to fire events.
     <p/>Calls to this method are serialized (one call completes to the point that its event is queued for delivery
     before the next call commences).
     <p/>See {@link #fireEvent} for more information.
     @param event the event to be delivered to registered listeners of the source object specified by said event.
     */

    private synchronized void xFireEvent( @NotNull PepysEvent event ) {

	PepysWeakSourceRef weakSourceRef = _sourceMap.get( event.getSource() );

	if ( weakSourceRef == null ) {

	    Logger.logMsg( "got an event for which we don't have a registered source - " + event + " (event ignored)" );

	} else {

	    weakSourceRef.fireEvent( event );

	}

    }

    // Some fairly rudimentary test code.

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Pepys", "testing", null );

	Pepys.doit();

	Logger.logMsg( "back from doit" );

	Runtime.getRuntime().gc();

	ObtuseUtil.safeSleepMillis( 3000L );
	Logger.logMsg( "really done" );
    }

    private static void doit() {

	PepysSource s1 = new PepysSource() {

	    @Override
	    public long getPepysId() {

		return 1L;

	    }

	    public String toString() { return "source s1"; }

	};

	PepysSource s2 = new PepysSource() {

	    @Override
	    public long getPepysId() {

		return 2L;

	    }

	    public String toString() { return "source s2"; }

	};

	PepysSource s3 = new PepysSource() {

	    @Override
	    public long getPepysId() {

		return 3L;

	    }

	    public String toString() { return "source s3"; }

	};

	PepysAnchor<PepysEventListener> anchor = new PepysAnchor();

	PepysEventListener s1listener1 = new PepysEventListener() {

	    @Override
	    public void PepysSourceCreated( @NotNull PepysEvent event ) {

		Logger.logMsg( "l1: s1 got source changed event " + event );

	    }

	    @Override
	    public void PepysSourceChanged( @NotNull PepysEvent event ) {

		Logger.logMsg( "l1: s1 got source changed event " + event );

	    }

	    @Override
	    public void PepysSourceGone( @NotNull PepysEvent event ) {

		Logger.logMsg( "l1: s2 got source gone event " + event );

	    }

	};

	Pepys.registerInterest(
		s1,
		anchor, s1listener1
	);

	PepysEventListener s1listener2 = new PepysEventListener() {

	    @Override
	    public void PepysSourceCreated( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s1 got source created event " + event );

	    }

	    @Override
	    public void PepysSourceChanged( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s1 got source changed event " + event );

	    }

	    @Override
	    public void PepysSourceGone( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s1 got source gone event " + event );

	    }

	};
	PepysEventListener s2listener3 = new PepysEventListener() {

	    @Override
	    public void PepysSourceCreated( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s2 got source created event " + event );

	    }

	    @Override
	    public void PepysSourceChanged( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s2 got source changed event " + event );

	    }

	    @Override
	    public void PepysSourceGone( @NotNull PepysEvent event ) {

		Logger.logMsg( "l2: s2 got source gone event " + event );

	    }

	};
	Pepys.registerInterest(
		s1,
		anchor, s1listener2
	);
	Pepys.registerInterest(
		s2,
		anchor, s2listener3
	);

//	PepysEventListener s1listener2 = new PepysEventListener() {
//
//	    @Override
//	    public void PepysSourceChanged( @NotNull PepysEvent event ) {
//
//		Logger.logMsg( "listener(source changed method):  " + event );
//
//	    }
//
//	    @Override
//	    public void PepysSourceGone( @NotNull PepysEvent event ) {
//
//		Logger.logMsg( "listener(source gone method):  " + event );
//
//	    }
//
//	};
//
//	Pepys.registerInterest(
//		s1,
//		s1listener2
//	);

	Pepys.fireEvent(
		new PepysTestEvent( PepysEventType.CHANGED, s1 )
	);

	ObtuseUtil.safeSleepMillis( 3000L );
	s1listener2 = anchor.castOff( s1listener2 );
	Runtime.getRuntime().gc();
	ObtuseUtil.safeSleepMillis( 3000L );
	Pepys.fireEvent(
		new PepysTestEvent( PepysEventType.CHANGED, s1 )
	);
	Pepys.fireEvent(
		new PepysTestEvent( PepysEventType.CHANGED, s2 )
	);
	s2 = null;
	s1listener1 = anchor.castOff( s1listener1 );
	ObtuseUtil.safeSleepMillis( 3000L );
	s1 = null;
	Runtime.getRuntime().gc();
	ObtuseUtil.safeSleepMillis( 3000L );
	Logger.logMsg( "done" );
	ObtuseUtil.safeSleepMillis( 3000L );

    }

}
