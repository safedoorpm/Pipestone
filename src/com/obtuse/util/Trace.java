/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * A trace facility.
 */

@SuppressWarnings("UnusedDeclaration")
public class Trace {

    private static final DateFormat _logFileNameFormatter =
            new SimpleDateFormat( "'trace_'yyyy-MM-dd_HH.mm.ss.SSS'.txt'" );

    public static final DateFormat OUR_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );

    private static String s_supportContact;

    private static final Map<Integer, TraceHook> s_traceHooks = new TreeMap<>();

    private static final String TRACE_HOOKS_LOCK = "trace hooks lock";

    @SuppressWarnings("FieldCanBeLocal")
    private static int s_nextHookId = 0;

    private static boolean s_liveTrace = false;

    private static String s_programName = null;

    private static File s_traceFileDirectory = new File( BasicProgramConfigInfo.getWorkingDirectory(), "traces" );

    public static final Map<Long, Thread> s_exceptionsInProgress = new TreeMap<>();

    public static final int MAX_FORMATTED_TRACE_DEPTH = 100;

    private static boolean _printEvents = false;

    public static void setSupportContact( final String supportContact ) {

        Trace.s_supportContact = supportContact;

    }

    private static class TraceEvent {

        private final String _event;

        private final long _timestamp;

        private final long _tid;

        private Throwable _exception = null;

        private TraceEvent( final String event ) {
            super();

            _event = event;
            _timestamp = System.currentTimeMillis();
            _tid = Thread.currentThread().getId();

        }

        private TraceEvent( final String why, final Throwable e ) {
            this( why + ( e == null ? "" : " (associated exception:  " + e + ")" ) );

            _exception = e;

        }

        @SuppressWarnings( { "UnusedDeclaration" } )
        private String getEvent() {

            return _event;

        }

        @SuppressWarnings( { "UnusedDeclaration" } )
        private long getTimeStamp() {

            return _timestamp;

        }

        @SuppressWarnings("UnusedDeclaration")
        private Throwable getException() {

            return _exception;

        }

        private void emit( final List<String> results ) {

            String pfx = Trace.OUR_DATE_FORMAT.format( _timestamp ) + " {" + _tid + "}:  ";

            results.add( pfx + _event );

            if ( _exception != null ) {

                Trace.captureStackTrace( false, pfx, _exception, results );

            }

        }

        public String toString() {

            StringBuilder msg = new StringBuilder();
            msg.append( Trace.OUR_DATE_FORMAT.format( _timestamp ) )
               .append( " {" )
               .append( _tid )
               .append( "}:  " )
               .append( _event );

            if ( _exception != null ) {

                List<String> results = new LinkedList<>();
                Trace.captureStackTrace( false, "", _exception, results );
                for ( String s : results ) {

                    msg.append( "\n" ).append( s );

                }

            }

            return msg.toString();

        }

    }

    private static Queue<TraceEvent> s_traceEvents = new LinkedList<>();

    private static final List<TraceFileManager> s_traceFileManagers = new LinkedList<>();

    private static final int MAX_TRACE_EVENTS = 50000;

    private Trace() {

        super();

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void setLiveTrace( final boolean value ) {

        Trace.s_liveTrace = value;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void register( final TraceFileManager traceFileManager ) {

        synchronized ( Trace.s_traceFileManagers ) {

            Trace.s_traceFileManagers.add( traceFileManager );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void setProgramName( final String programName ) {

        Trace.s_programName = programName;

    }

    /**
     * Add a trace hook which is to be invoked whenever a trace report is requested (i.e. whenever emitResults is
     * called).
     *
     * @param hook the hook.
     *
     * @return the unique id number of the hook (if the same hook is added more than once, each 'addition' of the hook
     *         will get a different id number).
     */

    public static int addTraceHook( final TraceHook hook ) {

        synchronized ( Trace.TRACE_HOOKS_LOCK ) {

            Trace.s_nextHookId += 1;
            Trace.s_traceHooks.put( Trace.s_nextHookId - 1, hook );
            return Trace.s_nextHookId - 1;

        }

    }

    /**
     * Remove a trace hook. Attempts to remove hooks which don't exist are silently ignored (it seems foolish to add yet
     * new ways to crash an application in a facility which is intended to find bugs and within which deleting the wrong
     * hook id is hardly the end of the world).
     *
     * @param id the id number of the hook to be deleted.
     */

    @SuppressWarnings("UnusedDeclaration")
    public static void removeTraceHook( final int id ) {

        synchronized ( Trace.TRACE_HOOKS_LOCK ) {

            Trace.s_traceHooks.remove( id );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public TraceHook getHook( final int id ) {

        return Trace.s_traceHooks.get( id );

    }

    public static void event( final String event ) {

        Trace.event( event, null );

    }

    public static void event( final Throwable e ) {

        Trace.event( "unexpected exception", e );

    }

    public static void event( final String event, @Nullable final Throwable e ) {

        if ( _printEvents ) {

            System.out.println( "Trace event:  " + event + ( e == null ? "" : " (throwable is " + e + ")" ) );

        }

        Long threadId = Thread.currentThread().getId();

        synchronized ( Trace.s_exceptionsInProgress ) {

            // Avoid recursion

            if ( Trace.s_exceptionsInProgress.containsKey( threadId ) ) {

                return;

            }

        }

        try {

            if ( Trace.s_liveTrace ) {

                //noinspection UseOfSystemOutOrSystemErr
                System.out.println( "<><> " + event );
                if ( e != null ) {

                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();

                }

            }

            synchronized ( Trace.TRACE_HOOKS_LOCK ) {

                Trace.s_traceEvents.add( new TraceEvent( event, e ) );

                if ( Trace.s_traceEvents.size() > Trace.MAX_TRACE_EVENTS ) {

                    Trace.s_traceEvents.remove();

                }

            }

        } finally {

            synchronized ( Trace.s_exceptionsInProgress ) {

                Trace.s_exceptionsInProgress.remove( threadId );

            }

        }

    }

    public static List<String> getTrace( final String why, final String where ) {

        // Sitting on the trace hooks lock while collecting trace data risks a deadlock
        // (e.g. if some other thread tries to record a trace event) so we hold the
        // lock only long enough to collect copies of the raw trace data and hooks.

        List<TraceEvent> events;
        Map<Integer, TraceHook> hooks;
        synchronized ( Trace.TRACE_HOOKS_LOCK ) {

            events = new LinkedList<>();
            events.addAll( Trace.s_traceEvents );

            hooks = new TreeMap<>();
            for ( int hookId : Trace.s_traceHooks.keySet() ) {
                hooks.put( hookId, Trace.s_traceHooks.get( hookId ) );
            }

        }

        List<String> results = new LinkedList<>();
        try {

            String what = "Trace requested at " + new Date() +
                          ( Trace.s_programName == null ? "" : " for " + Trace.s_programName ) +
                          ( why == null ? "" : " (" + why + ")" );

            results.add( what );
            if ( s_supportContact != null ) {

                results.add( "IMPORTANT:  email this to " + s_supportContact + " (you should find a copy in \"" + where + "\")" );

            }

            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] curThreads = bean.getAllThreadIds();

            //noinspection MagicNumber
            ThreadInfo[] thInfos = bean.getThreadInfo( curThreads, 50 );
            for ( ThreadInfo info : thInfos ) {

                if ( info != null ) {

                    results.add(
                            "{" + info.getThreadId() + "}:" +
                            info.getThreadName() +
                            " (state = " + info.getThreadState() +
    // (discovered in about 2010 - could be out of date)
    // Java on Mac OS X sometimes crashes if the next line is uncommented!
    //                                 ", cpu = " + formatCpuTime(bean.getThreadCpuTime(info.getThreadId()) ) +
                            ")"
                    );

                    String lock = info.getLockName();
                    if ( lock != null ) {

                        String lockOwnerName = info.getLockOwnerName();
                        long lockOwnerId = info.getLockOwnerId();

                        results.add(
                                "    thread is waiting for \"" + lock + "\" which is currently held by {" +
                                lockOwnerId + "}:" + lockOwnerName
                        );

                    }

                    StackTraceElement[] stack = info.getStackTrace();
                    for ( StackTraceElement element : stack ) {

                        results.add(
                                "    at " + element.getClassName() + "." + element.getMethodName() + "(" +
                                element.getFileName() + ":" + element.getLineNumber() + ")"
                        );

                    }

                    results.add( "" );
                }

            }

            results.add( "" );
            results.add( "trace events:" );

            for ( TraceEvent event : events ) {

                event.emit( results );

            }

            // Process the hooks in the order that they were defined.

            if ( !hooks.isEmpty() ) {

                results.add( "" );
                results.add( "trace hooks:" );

                // System.out.println("trace hooks");

                for ( int hookId : hooks.keySet() ) {

                    results.add( "" );
                    TraceHook hook = hooks.get( hookId );
                    //noinspection UseOfSystemOutOrSystemErr
                    System.out.println( "doing hook " + hook );
                    List<String> hookResults = hook.run();

                    results.addAll( hookResults );

                }

            }

        } catch ( Throwable e ) {

            results.add( "*** unable to collect trace data due to exception:  " + e.getMessage() );

        }

        // System.out.println("trace data collected");

        return results;

    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static String emitTrace( final String why ) {

        String where = Trace._logFileNameFormatter.format( System.currentTimeMillis() );
        System.out.println( "where string built" );
        List<String> results = Trace.getTrace( why, where );
        System.out.println( "results collected" );

        String rval = Trace.emitResults( why, results, where, true );
        System.out.println( "file emitted" );

        return rval;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static String emitTrace( final Throwable e ) {

        return Trace.emitTrace( "unexpected exception", e );

    }

    public static String emitTrace( final String why, final Throwable e ) {

        if ( e == null ) {

            return Trace.emitTrace( why );

        } else {

            String where = Trace._logFileNameFormatter.format( System.currentTimeMillis() );
            String exceptionDescription = e.getMessage();
            if ( exceptionDescription == null ) {

                exceptionDescription = e.toString();

            }
            String longWhy = ( why == null ? "" : why + " / " ) + exceptionDescription;
            List<String> results = Trace.getTrace( longWhy, where );

            Trace.captureStackTrace( true, "", e, results );

            return Trace.emitResults( longWhy, results, where, true );

        }

    }

    private static void captureStackTrace( final boolean bothStyles, final String pfx, final Throwable e, final List<String> results ) {

        if ( bothStyles ) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter( sw );
            e.printStackTrace( pw );
            pw.flush();
            results.add( pfx );
            results.add( pfx + "Exception stack traceback:" );
            results.add( pfx + sw.toString() );  // FINALLY we can put the stack trace into our results list
            pw.close();
            ObtuseUtil.closeQuietly( sw );
            results.add( pfx );
            results.add( pfx + "same stack traceback using new formatter:" );

        } else {

            results.add( pfx + "Exception stack traceback (using new formatter):" );

        }

        String[] trace = Trace.formatDeeperStackTrace( e );
        for ( String line : trace ) {

            results.add( pfx + line );

        }

    }

    @SuppressWarnings("SameParameterValue")
    private static String emitResults( final String why, final List<String> results, final String where, final boolean compressOutput ) {

        final long timeStamp = System.currentTimeMillis();

        String traceFname = where;
        PrintWriter writer = null;

        try {

            //noinspection ResultOfMethodCallIgnored
            Trace.s_traceFileDirectory.mkdirs();

            File traceFile;
            if ( compressOutput ) {

                traceFname += ".gz";
                traceFile = new File( Trace.s_traceFileDirectory, traceFname );
                writer = new PrintWriter( new GZIPOutputStream( new FileOutputStream( traceFile ) ) );

            } else {

                traceFile = new File( Trace.s_traceFileDirectory, traceFname );
                writer = new PrintWriter( new FileOutputStream( new File( Trace.s_traceFileDirectory, traceFname ) ) );

            }

            traceFname = traceFile.getPath();

            for ( String s : results ) {
                writer.println( s );
            }

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open trace file \"" + traceFname + "\"", e );

        } catch ( IOException e ) {

            Logger.logErr( "I/O error writing trace file \"" + traceFname + "\"", e );

        } finally {

            ObtuseUtil.closeQuietly( writer );

        }

        // Report the trace via the friendly logger.

        Logger.logMsg( "A trace file has been captured (" + why + ")", null );

        Trace.tellTraceFileManagers( traceFname, timeStamp );

        return traceFname;

    }

    private static void tellTraceFileManagers( final String traceFname, final long timeStamp ) {

        //noinspection ClassWithoutToString,RefusedBequest
        new Thread( () -> {

	    synchronized ( Trace.s_traceFileManagers ) {

		boolean handled = false;
		for ( TraceFileManager tfm : Trace.s_traceFileManagers ) {

		    tfm.newTraceFile( traceFname, timeStamp );
		    handled = true;

		}

		if ( !handled && s_supportContact != null ) {

		    Logger.logMsg( "please email \"" + traceFname + "\" to " + s_supportContact, null );

		}
	    }

	} ).start();

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void startTracePortListener( final int port ) {

        try {

            final ServerSocket listenSocket = new ServerSocket();

            listenSocket.setReuseAddress( true );
            listenSocket.bind(
                    new InetSocketAddress(
                            "127.0.0.1",
                            port
                    )
            );
            Logger.logMsg( "listening for a trace request on port " + listenSocket.getLocalPort() );

            //noinspection RefusedBequest,ClassWithoutToString
            new Thread( "trace port listener" ) {

                public void run() {

                    while ( true ) {
                        try {

                            Socket sock = listenSocket.accept();
                            Logger.logErr( "trace requested" );
                            Trace.emitTrace( "externally requested" );
                            Logger.logErr( "trace done" );
                            ObtuseUtil.closeQuietly( sock );

                        } catch ( Throwable e ) {

                            Logger.logErr(
                                    "trace port listener caught an exception/error - thread terminating after one more trace",
                                    e
                            );
                            Trace.emitTrace( "trace port listener failed", e );

                            ObtuseUtil.closeQuietly( listenSocket );

                            return;

                        }

                    }

                }

            }.start();

        } catch ( IOException e ) {

            Trace.emitTrace( "trace port listener unable to allocate listen socket on port " + port, e );
            System.exit( 1 );

        }

    }

    public String toString() {

        return "Trace for " + Trace.s_programName;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void setDirectory( final File tmpSavrolaDir ) {

        if ( tmpSavrolaDir != null ) {

            Trace.s_traceFileDirectory = new File( tmpSavrolaDir, "traces" );

        }

    }

    public static void appendStackTrace( final Collection<String> trace, final Throwable e ) {

        StackTraceElement[] stack = e.getStackTrace();
        for ( int i = 0; i < stack.length && i < Trace.MAX_FORMATTED_TRACE_DEPTH; i += 1 ) {

            StackTraceElement element = stack[i];
            String source;
            if ( element.isNativeMethod() ) {

                source = "Native Method";

            } else if ( element.getFileName() == null ) {

                source = "Unknown Source";

            } else {

                source = element.getFileName() + ":" + element.getLineNumber();

            }

            trace.add( "\tat " + element.getClassName() + "." + element.getMethodName() + "(" + source + ")" );

        }

        if ( stack.length > Trace.MAX_FORMATTED_TRACE_DEPTH ) {

            trace.add( "\t... " + ( stack.length - Trace.MAX_FORMATTED_TRACE_DEPTH ) + " more" );

        }

    }

    public static boolean setPrintEvents( final boolean printEvents ) {

        boolean oldValue = _printEvents;

        _printEvents = printEvents;

        return oldValue;

    }

    public static boolean printEvents() {

        return _printEvents;

    }

    public static String[] formatDeeperStackTrace( final Throwable e ) {

        List<String> trace = new LinkedList<>();

        if ( e.getCause() == null ) {

            trace.add( "" + e.getClass().getName() + ": " + e.getLocalizedMessage() );
            Trace.appendStackTrace( trace, e );

        } else {

            trace.add( "" + e.getClass().getName() + ": " + e.getLocalizedMessage() );
            Trace.appendStackTrace( trace, e );
            trace.add( "Caused by: " + e.getCause().getClass().getName() + ": " + e.getCause().getLocalizedMessage() );
            Trace.appendStackTrace( trace, e.getCause() );

        }

        return trace.toArray( new String[ trace.size() ] );

    }

}