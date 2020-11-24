package com.obtuse.util.clockwatcher;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.DateUtils;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

/**
 A simple asynchronous event scheduling facility.
 */

@SuppressWarnings("unused")
public class ObtuseClockWatcher {

    private static final WorkStepState.WorkProgressWatcher s_workProgressWatcher =
            new WorkStepState.WorkProgressWatcher() {

                private long _lastReportTime = 0L;

                @Override
                public void progress(
                        final @NotNull WorkStepState workStepState,
                        @NotNull final WorkStepState.ProgressType progressType
                ) {

                    if ( System.currentTimeMillis() - _lastReportTime > 1000L ) {

                        Logger.logMsg(
                                "pt=" + progressType + ", " +
                                workStepState.getCountToDate() +
                                " of " + (
                                        workStepState.isDone()
                                                ?
                                                workStepState.getActualTotalCount()
                                                :
                                                workStepState.getExpectedCount()
                                ) +
                                " done (" + ObtuseUtil.safeDivide(
                                        workStepState.getCountToDate() * 100,
                                        workStepState.getExpectedCount()
                                ) + "%)"

                        );

                        ObtuseUtil.doNothing();

                    }

                    _lastReportTime = System.currentTimeMillis();

                }

            };

    public static WorkStepState.WorkProgressWatcher getProgressWatcher() {

        return s_workProgressWatcher;

    }

    public static class SnoozeButton implements Runnable {

        public static final long SNOOZE_TIME_MS = 10000L;

        private boolean _alarmTime = false;

        @SuppressWarnings("FieldCanBeLocal") private long _startTime;

        public void run() {

            _startTime = System.currentTimeMillis();

            synchronized ( s_backgroundTaskQueue ) {

                vLog(
                        "sb:  snoozing for " + SNOOZE_TIME_MS + "ms until " +
                        new Date( System.currentTimeMillis() + SNOOZE_TIME_MS )
                );

                for ( int resumeCount = 0; resumeCount < 100; resumeCount += 1 ) {

                    long waitTimeLeft = ( _startTime + SNOOZE_TIME_MS ) - System.currentTimeMillis();
                    if ( waitTimeLeft > 0L ) {

                        vLog( "sb:  snooze time left is " + DateUtils.formatDuration( waitTimeLeft ) );

                        try {

                            try {

                                ObtuseUtil.doNothing();

                                s_backgroundTaskQueue.wait( waitTimeLeft );

                                ObtuseUtil.doNothing();

                            } finally {

                                ObtuseUtil.doNothing();

                            }

                            vLog( "sb:  wakeup at " + new Date() + " GOODBYE!" );

                            return;

                        } catch ( InterruptedException e ) {

                            Logger.logErr( "sb:  java.lang.InterruptedException caught", e );

                        }

                    }

                }

                vLog( "sb:  Good morning!" );

                ringAlarm();

            }

        }

        public void ringAlarm() {

            synchronized ( s_backgroundTaskQueue ) {

                _alarmTime = true;
                s_backgroundTaskQueue.notifyAll();

            }

        }

        public boolean isAlarmTime() {

            return _alarmTime;

        }

    }

    private static boolean s_verbose = false;
    private static final Queue<ObtuseBackgroundTask> s_backgroundTaskQueue = new LinkedList<>();
    private static final SortedMap<Long, List<ObtuseBackgroundTask>> s_timeOrderedTaskQueue = new TreeMap<>();

    public static final Thread BACKGROUND_TASK_RUNNER = new Thread( "OCW BG thread runner" ) {

        private boolean _backgroundTaskRunning = false;

        @Override
        public void run() {

            //noinspection InfiniteLoopStatement
            while ( true ) {

                vLog( "btr:  looking for work" );

                synchronized ( s_backgroundTaskQueue ) {

                    if ( s_backgroundTaskQueue.isEmpty() || _backgroundTaskRunning ) {

                        long napDuration = 1000L;
                        try {

                            while ( true ) {

                                try {

                                    ObtuseUtil.doNothing();

                                    s_backgroundTaskQueue.wait( napDuration );

                                    ObtuseUtil.doNothing();

                                } finally {

                                    ObtuseUtil.doNothing();

                                }

                                if ( s_backgroundTaskQueue.isEmpty() || _backgroundTaskRunning ) {

                                    // round we go again.

                                    ObtuseUtil.doNothing();

                                } else {

                                    // There's work to be done lads!

                                    break;

                                }

                            }

                        } catch ( InterruptedException e ) {

                            Logger.logErr( "java.lang.InterruptedException caught", e );

                        }

                    } else {

                        ObtuseBackgroundTask nextTask = s_backgroundTaskQueue.remove();

                        _backgroundTaskRunning = true;

                        SwingUtilities.invokeLater(

                                () -> {

                                    if ( SwingUtilities.isEventDispatchThread() ) {

                                        nextTask.doit();

                                        synchronized ( s_backgroundTaskQueue ) {

                                            _backgroundTaskRunning = false;
                                            s_backgroundTaskQueue.notifyAll();

                                        }

                                    } else {

                                        throw new HowDidWeGetHereError(
                                                "ObtuseClockWatcher:  trying to launch " +
                                                ObtuseUtil.enquoteToJavaString( nextTask.purpose ) +
                                                " when we are NOT on the event dispatch thread"
                                        );

                                    }

                                }
                        );

                    }

                }

            }

        }

    };

    public static final Thread CLOCK_WATCHER = new Thread( "Obtuse clock watcher" ) {

        @Override
        public void run() {

            try {
                //noinspection InfiniteLoopStatement
                while ( true ) {

                    vLog( "cw:  looking for work" );

                    synchronized ( s_timeOrderedTaskQueue ) {

                        if ( s_timeOrderedTaskQueue.isEmpty() ) {

                            vLog(
                                    "cw:  queue is empty - wait (indefinitely) for the next event to appear"
                            );

                            try {

                                ObtuseUtil.doNothing();

                                s_timeOrderedTaskQueue.wait();

                                ObtuseUtil.doNothing();

                                vLog( "cw:  we've been poked while waiting indefinitely" );

                            } catch ( InterruptedException e ) {

                                Logger.logErr(
                                        "ObtuseClockWatcher:  java.lang.InterruptedException caught",
                                        e
                                );

                                ObtuseUtil.doNothing();

                            } finally {

                                ObtuseUtil.doNothing();

                            }

                        } else {

                            Long scheduledTimeMs = s_timeOrderedTaskQueue.firstKey();
                            vLog( "||| next task scheduled for " + new Date( scheduledTimeMs ) );
                            long msUntilNextEventTime = scheduledTimeMs - System.currentTimeMillis();
                            if ( msUntilNextEventTime <= 0 ) {

                                vLog( "||| run task scheduled at " + new Date( scheduledTimeMs ) );

                                List<ObtuseBackgroundTask> readyTasks = s_timeOrderedTaskQueue.get( scheduledTimeMs );
                                ObtuseBackgroundTask nextTask = readyTasks.remove( 0 );
                                if ( readyTasks.isEmpty() ) {

                                    s_timeOrderedTaskQueue.remove( scheduledTimeMs );

                                }

                                SwingUtilities.invokeLater( nextTask::doit );

                            } else {

                                try {

                                    vLog(
                                            "cw:  waiting for next task at " +
                                            new Date( System.currentTimeMillis() + msUntilNextEventTime )
                                    );

                                    try {

                                        ObtuseUtil.doNothing();

                                        s_timeOrderedTaskQueue.wait( msUntilNextEventTime );

                                        ObtuseUtil.doNothing();

                                    } finally {

                                        ObtuseUtil.doNothing();

                                    }

                                    vLog(
                                            "cw:  timeout or we've been poked " +
                                            "(while waiting for known next event time)"
                                    );

                                } catch ( InterruptedException e ) {

                                    vLog(
                                            "cw:  interrupted while waiting for " +
                                            "next event time on non-empty queue"
                                    );

                                }

                            }

                        }

                    }

                }

            } catch ( Throwable e ) {

                Logger.logErr( "ObtuseClockWatcher:  caught an unexpected exception/error", e );

                ObtuseUtil.doNothing();

            }

        }

    };

    /**
     We are a utility class so no public constructors.
     */

    private ObtuseClockWatcher() {
        super();
    }

    /**
     Schedule a new event to be launched at a specific time.
     <p>This is the public method via which new events are queued.
     Since queueing a new event involves the same work as re-queuing an event,
     this method just calls our private method {@link #queueEvent(boolean, long, ObtuseBackgroundTask)}.</p>
     <p>Having two methods provides a way to set a breakpoint when a new event is added.</p>
     @param whenMs the earliest time that the event can be launched at.
     @param event the event.
     */

    public static void doLater( final long whenMs, @NotNull final ObtuseBackgroundTask event ) {

        queueEvent( true, whenMs, event );

    }

    /**
     Queue or re-queue an event.
     <p>This method does the work involved in queuing a a new event or re-queuing an existing event.</p>
     <p>This method provides a place to set breakpoints when new events are added.</p>
     @param whenMs the earliest that the event should be run.
     @param event the event of interest.
     */

    public static void queueEvent(
            final boolean newEvent,
            final long whenMs,
            @NotNull final ObtuseBackgroundTask event
    ) {

        if ( newEvent ) {

            ObtuseUtil.doNothing();

        } else {

            //noinspection ResultOfMethodCallIgnored
            ObtuseUtil.always();

        }

        synchronized ( s_timeOrderedTaskQueue ) {

            s_timeOrderedTaskQueue.computeIfAbsent(
                    whenMs,
                    newList -> new ArrayList<>()
            ).add( event );

            s_timeOrderedTaskQueue.notifyAll();

        }

    }

    public static void vLog( String msg ) {

        if ( s_verbose ) {

            Logger.logMsg( msg );

        }

    }

    @SuppressWarnings("unused")
    public static void setVerbose( boolean verbose ) {

        s_verbose = verbose;

    }

    @SuppressWarnings("UnusedReturnValue")
    public static int addBackgroundTask( @NotNull final ObtuseBackgroundTask backgroundTask ) {

        synchronized ( s_backgroundTaskQueue ) {

            s_backgroundTaskQueue.add( backgroundTask );

            s_backgroundTaskQueue.notifyAll();

            return s_backgroundTaskQueue.size();

        }

    }

}
