/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 Manage a simple logging facility.
 <p/>
 Note that {@link BasicProgramConfigInfo#init} <b><u>MUST</u></b> be called before this class is used in any way
 which triggers
 the invocation of this class's static initializer(s).  Experience seems to indicate that it is sufficient to call
 {@link BasicProgramConfigInfo#init} before invoking any method defined by this class (your mileage may vary).

 @noinspection ClassWithoutToString, ForLoopReplaceableByForEach, RawUseOfParameterizedType,
 UseOfSystemOutOrSystemErr, UnusedDeclaration */

public class Logger {

    private List<LoggerListener> _listeners = new LinkedList<>();

    private StringBuffer _currentMessage = new StringBuffer();

    private File _outputFile = null;

    private String _outputFileName = null;

    private PrintStream _outputStream;

    private Date _messageStartTime = null;

    private PrintStream _mirror = null;     // If non-null, all messages sent to this logger are also sent here.

    private static Logger s_stdout = null;

    private static Logger s_stderr = null;

    private static Logger s_friendly = null;

    private static final DateFormat OUR_DATE_FORMAT;

    private static final String COMPONENT_NAME;

    public static final File LOGS_DIRECTORY;

    private static final DateFormat LOG_FILE_NAME_FORMATTER;
    private static String s_programName = null;

    private static boolean s_loggingEnabled = true;

    public static final String NESTING_INDENT = ".   ";

    private static final TreeSorter<String, Function<String, Boolean>> s_interestingStuff = new TreeSorter<>();

    //    private static int _nestingLevel = 0;
    private static Stack<String> _nestingLevelNames = new Stack<>();
    private static String _nestingString = "";

    @SuppressWarnings("UnnecessaryBoxing")
    private static final Long _nestingLevelLock = new Long( 0L );

    static {

        if ( BasicProgramConfigInfo.getComponentName() == null ) {

            COMPONENT_NAME = BasicProgramConfigInfo.getApplicationName();

        } else {

            COMPONENT_NAME = BasicProgramConfigInfo.getComponentName();

        }

        if ( BasicProgramConfigInfo.getDateFormat() == null ) {

            OUR_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS Z" );

        } else {

            OUR_DATE_FORMAT = BasicProgramConfigInfo.getDateFormat();

        }

        if ( BasicProgramConfigInfo.getLogFileNameFormat() == null ) {

            LOG_FILE_NAME_FORMATTER = new SimpleDateFormat( "yyyy-MM-dd_HH.mm.ss.SSS'.txt'" );

        } else {

            LOG_FILE_NAME_FORMATTER = new SimpleDateFormat( BasicProgramConfigInfo.getLogFileNameFormat() );

        }

        LOGS_DIRECTORY = new File( BasicProgramConfigInfo.getWorkingDirectory(), "logs" );

    }

    private static int s_globalVetoCount = 0;

    public Logger( final File outputFile, final boolean append )
            throws
            FileNotFoundException {

        super();

        _outputFile = outputFile;
        _outputStream = new PrintStream( new FileOutputStream( outputFile, append ), true );

    }

    public Logger( final String outputFileName, final PrintStream outputStream ) {

        super();

        _outputFileName = outputFileName;
        _outputStream = outputStream;

    }

    public static boolean setLoggingEnabled( final boolean enabled ) {

        boolean wasEnabled = s_loggingEnabled;

        s_loggingEnabled = enabled;

        return wasEnabled;

    }

    public static boolean isLoggingEnabled() {

        return Logger.s_loggingEnabled;

    }

    public static void maybeLogMsg( final @NotNull Supplier<String> composeLoggingMsg ) {

        if ( Logger.s_loggingEnabled ) {

            String loggingMessage = composeLoggingMsg.get();
            Logger.logMsg( loggingMessage );

        }

    }

    public static void maybeLogErr( final @NotNull Supplier<String> composeLoggingMsg ) {

        if ( Logger.s_loggingEnabled ) {

            String loggingMessage = composeLoggingMsg.get();
            Logger.logErr( loggingMessage );

        }

    }

    public static int pushNesting( final @NotNull String levelName ) {

        Logger.logMsg( "{ " + levelName );

        synchronized ( _nestingLevelLock ) {

            _nestingLevelNames.push( levelName );

        }

        return _nestingLevelNames.size();

    }

    public static String getTopNestingLevelName() {

        synchronized ( _nestingLevelLock ) {

            return _nestingLevelNames.peek();

        }

    }

    public static int popNestingLevel( final @NotNull String levelName ) {

        synchronized ( _nestingLevelLock ) {

            if ( levelName.equals( _nestingLevelNames.peek() ) ) {

                _nestingLevelNames.pop();

            } else {

                throw new HowDidWeGetHereError(
                        "Logger:  attempt to pop nesting level \"" + levelName + "\" when top level is \"" + _nestingLevelNames.peek() +
                        "\"" );

            }

            Logger.logMsg( "} " + levelName );

            return _nestingLevelNames.size();

        }

    }

    public static String getNestingString() {

        synchronized ( _nestingLevelLock ) {

            int nestingLevel = _nestingLevelNames.size();
            int requiredLength = nestingLevel * NESTING_INDENT.length();

            if ( _nestingString.length() < requiredLength ) {

                _nestingString = ObtuseUtil.replicate( NESTING_INDENT, nestingLevel );

            }

            return _nestingString.substring( 0, requiredLength );

        }

    }

    public static void logStackTrace( final Throwable e ) {

        StackTraceElement[] trace = e.getStackTrace();

        if ( trace == null ) {

            logMsg( "no stack trace" );

        } else {

//	    Logger.logMsg( "stack trace has " + trace.length + " elements" );
            logMsg( "" + e );
            int ix = 0;
            for ( StackTraceElement element : trace ) {

//		Logger.logMsg( "[" + ix + "] = " + element );
                logMsg( "    at " + element );

                ix += 1;

            }

        }

    }

    /**
     Close this logger and set our mirror to null (this has the side-effect of closing our mirror if it is open and
     neither {@link System#out} nor {@link System#err}).
     */

    public synchronized void close() {

        // Make sure we don't accidentally close stdout or stderr.
        // Note:  use of != instead of equals() is deliberate!

        //noinspection ObjectEquality
        if ( _outputStream != System.out && _outputStream != System.err && _outputStream != null ) {

            _outputStream.close();

        }

        //noinspection NullableProblems
        internalSetMirror( null, null );
    }

    /**
     Set this logger's mirror to the specified {@link java.io.PrintStream}. Any existing mirror is closed if it is
     open and
     neither {@link System#out} nor {@link System#err}).

     @param mirrorName the name of the mirror file/device/whatever.
     @param mirror     the PrintStream which is to be sent a copy of everything which is emitted by this Logger.
     */

    private void internalSetMirror( final String mirrorName, final PrintStream mirror ) {

        // Make sure we don't accidentally close stdout or stderr.
        // Note:  use of != instead of equals() is deliberate!

        //noinspection ObjectEquality
        if ( _mirror != null && _mirror != System.out && _mirror != System.err ) {

            println( "\n%%% mirror file closed" );
            _mirror.close();

        }

        _mirror = mirror;
        if ( _mirror != null ) {

            if ( mirrorName == null ) {

                println( "%%% mirror file (re)opened" );

            } else {

                println( "%%% mirror file \"" + mirrorName + "\" (re)opened" );

            }

        }

    }

    public void setMirror( final String mirrorFilename, final long key )
            throws
            FileNotFoundException {

        internalSetMirror( mirrorFilename, new PrintStream( new FileOutputStream( mirrorFilename, true ), true ) );

    }

    public static String formatTOD( final Date when ) {

        return Logger.OUR_DATE_FORMAT.format( when );

    }

    private void printSegment( final String s ) {

        if ( _messageStartTime == null ) {

            _messageStartTime = new Date();
        }

        _currentMessage.append( s );

    }

    private void printNewline() {

        if ( _messageStartTime == null ) {

            _messageStartTime = new Date();

        }

        String formattedMessageStartTime = Logger.OUR_DATE_FORMAT.format( _messageStartTime );
        if ( _outputStream != null ) {

            //noinspection UnnecessaryParentheses
            _outputStream.println(
                    MessageFormat.format(
                            "{0}:  {1}",
                            formattedMessageStartTime,
                            _currentMessage.toString()
                    )
            );

        }

        if ( _mirror != null ) {

            _mirror.println( formattedMessageStartTime + ":  " + _currentMessage.toString() );

        }

        flush();

        // Make a copy of our listeners list from within a synchronized block.
        // Use this list outside the block to call all the listeners.
        // While somewhat awkward, this ensures that the list of listeners does not change
        // while we're sending the message to them without having to call the listeners
        // from within a synchronized block (which just seems ugly and risks deadlocks).

        List<LoggerListener> tmpListeners;

        synchronized ( this ) {

            tmpListeners = new Vector<>( _listeners );

        }

//        Trace.event( "processing listeners" );

        for ( LoggerListener listener : tmpListeners ) {

            listener.logMessage( _messageStartTime, _currentMessage.toString() );

        }

//        Trace.event( "done processing listeners" );

        _currentMessage = new StringBuffer();
        _messageStartTime = null;

    }

    public synchronized void print( final String Xs ) {

        //noinspection UnnecessaryLocalVariable
        String s = Xs;
        int last = 0;

        while ( true ) {

            int ix = s.indexOf( (int)'\n', last );
            if ( ix < last ) {

                break;

            }

            String nextSection = s.substring( last, ix );
            printSegment( nextSection );
            printNewline();
            last = ix + 1;

        }

        if ( last < s.length() ) {

            String lastSection = s.substring( last );
            printSegment( lastSection );

        } else {

            printSegment( "" );   // needed to properly handle \n at the end of the string and harmless otherwise.

        }

    }

    public synchronized void println( final String logLine ) {

        int vetoCount = 0;

        synchronized ( s_interestingStuff ) {

            for ( String interesting : s_interestingStuff.keySet() ) {

                if ( logLine.contains( interesting ) ) {

                    for ( Function<String, Boolean> func : s_interestingStuff.getValues( interesting ) ) {

                        if ( func.apply( logLine ).booleanValue() ) {

                            vetoCount += 1;

                        }

                    }

                }

            }

            if ( vetoCount != 0 ) {

                synchronized ( s_interestingStuff ) {

                    s_globalVetoCount += 1;

                    return;

                }

            }

        }

        print( logLine );
        printNewline();

        // The Java 1.4.2 docs are not clear as to whether System.out or System.err are
        // opened with auto-flushing enabled so we force a flush here just to be sure.

        flush();

    }

    /**
     Determine how many log lines have actually been vetoed.

     @return the number of log lines which have actually been vetoed.
     */

    public static int getGlobalVetoCount() {

        return s_globalVetoCount;

    }

    /**
     Provide a mechanism for suppressing/vetoing log lines which contain a specified target string.
     <p/>
     Every future log line which contains the specified target string is passed to the specified {@link Function}{@code <String,Boolean>} instance's {@code Boolean apply( String targetString )} method.
     If that method returns {@code false} then the logging of the matching log line is suppressed (BE VERY VERY CAREFUL TO AVOID ACCIDENTALLY VETOING IMPORTANT LOG LINES).
     <p/>Attempts to add the same target string and {@code vetoer} instance more than once are silently ignored (i.e. only one occurrence of the {@code vetoer} instance will be added for any
     given target string).
     <p/>IMPORTANT: the log line is vetoed/suppressed if the specified {@code vetoer}'s {@code Boolean apply( String logLine )} method returns {@code false}; it is allowed through if the method returns {@link false}.

     @param targetString the specified target string.
     @param vetoer       the {@link Function}{@code <String,Boolean>} instance's {@code Boolean apply( String targetString )} method to call if a log line contains the specified target string.
     <p/>Expect bad things to happen if from within a call to the {@link Function}{@code <String,Boolean>} instance's {@code Boolean apply( String targetString )} method, you try to log something which is matched by your vetoer or attempt to add or remove a vetoer.
     */

    public synchronized void addLogLineVetoer( final @NotNull String targetString, final @NotNull Function<String, Boolean> vetoer ) {

        synchronized ( s_interestingStuff ) {

            // Remove any existing instances of the specified matcher for the specified key.

            cancelLineFilter( targetString, vetoer );

            // Put exactly one back.

            s_interestingStuff.add( targetString, vetoer );

        }

    }

    /**
     Remove a specified vetoer for a specified target string.
     <p/>Requests to remove vetoers which are not actually associated with the specified target string are silently ignored.

     @param targetString the specified target string.
     @param vetoer       the {@link Function}{@code <String,Boolean>} instance previously added for the specified target string.
     */

    public synchronized void cancelLineFilter( final @NotNull String targetString, final @NotNull Function<String, Boolean> vetoer ) {

        // Get rid of any existing instances of the specified matcher for the specified key.

        Collection<Function<String, Boolean>> removedVetoers = s_interestingStuff.removeValue( targetString, vetoer );

        // There should have been zero or one matcher removed.

        if ( removedVetoers.size() > 1 ) {

            throw new HowDidWeGetHereError( "Logger.cancelLineFilter:  more than one identical matcher for targetString=" +
                                            ObtuseUtil.enquoteToJavaString( targetString ) +
                                            " and matcher " +
                                            vetoer );

        }

    }

    /**
     Remove all usages of a specified vetoer irrespective of the target string.
     <p/>Requests to remove vetoers which are not actually associated with any specified target string are silently ignored.

     @param vetoer a {@link Function}{@code <String,Boolean>} to be used to identify vetoers which are to be deleted.
     */

    public synchronized void cancelLineFilters( final @NotNull Function<String, Boolean> vetoer ) {

        // Get rid of any existing instances of the specified matcher for the specified key.

        Collection<Function<String, Boolean>> vetoers = s_interestingStuff.removeValue( target -> target == vetoer, new Vector<>() );

        // It doesn't matter how many vetoers were deleted.

    }

    public synchronized void println() {

        println( "" );

    }

    /**
     Flush the underlying {@link java.io.PrintStream}. Calling this method is generally not necessary as it is called
     implicitly after each newline is written.
     */

    public synchronized void flush() {

        if ( _outputStream != null ) {

            _outputStream.flush();

        }

        if ( _mirror != null ) {

            _mirror.flush();

        }

    }

    /**
     Log an exception/error out via this Logger instance.
     <p/>
     The totally unadorned stack trace contained within the {@link Throwable} is written out via this Logger instance
     using {@link #print}.

     @param e the exception/error to be logged.
     */

    public void log( final Throwable e ) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        pw.flush();
        print( sw.toString() );  // FINALLY we can emit the stack trace via our print method
        // (this ensures that each line is time stamped - not necessary but cleaner)

        pw.close();

        try {

            sw.close();

        } catch ( IOException ee ) {

            // just ignore it.

        }

    }

    /**
     Get the {@link java.io.File} object associated with the underlying {@link java.io.PrintStream}.

     @return the appropriate {@link java.io.File} object or null if our {@link #Logger(String, java.io.PrintStream)}
     constructor was used to create this Logger.
     */

    public File getOutputFile() {

        return _outputFile;

    }

    /**
     Get the name of the output file.

     @return the name of this Logger's output file.
     */

    public String getOutputFileName() {

        if ( _outputFile == null ) {

            return _outputFileName;

        } else {

            return _outputFile.getPath();

        }

    }

    /**
     Get the Logger associated with stdout. The logger is allocated if it does not already exist.

     @return the Logger that is associated with {@link System#out}.
     */

    public static Logger getStdout() {

        if ( Logger.s_stdout == null ) {

            Logger.s_stdout = new Logger( "<stdout>", System.out );

            try {

                //noinspection ResultOfMethodCallIgnored
                Logger.LOGS_DIRECTORY.mkdirs();
                Logger.s_stdout.setMirror(
                        Logger.LOGS_DIRECTORY.getPath() + "/" +
                        ( Logger.s_programName == null ? Logger.COMPONENT_NAME : Logger.s_programName ) +
                        "_stdout_" + Logger.LOG_FILE_NAME_FORMATTER
                                .format( new Date() ), -1L
                );

            } catch ( FileNotFoundException e ) {

                // it was worth a shot.
                Trace.event( "caught an exception trying to set stdout's mirror", e );

            }

        }

        return Logger.s_stdout;

    }

    /**
     Get the Logger associated with stderr. The logger is allocated if it does not already exist.

     @return the Logger that is associated with {@link System#err}.
     */

    public static Logger getStderr() {

        if ( Logger.s_stderr == null ) {

            Logger.s_stderr = new Logger( "<stderr>", System.err );

            try {

                //noinspection ResultOfMethodCallIgnored
                Logger.LOGS_DIRECTORY.mkdirs();
                Logger.s_stderr.setMirror(
                        Logger.LOGS_DIRECTORY.getPath() + "/" +
                        ( Logger.s_programName == null ? Logger.COMPONENT_NAME : Logger.s_programName ) +
                        "_stderr_" + Logger.LOG_FILE_NAME_FORMATTER
                                .format( new Date() ), -1L
                );

            } catch ( FileNotFoundException e ) {

                // it was worth a shot.
                Logger.logErr( "caught an exception trying to set stderr's mirror", e );

            }

        }

        return Logger.s_stderr;

    }

//    public static void setProgramName( String programName ) {
//
//        _programName = programName;
//
//        _stderr = null;
//        getStderr();
//        _stdout = null;
//        getStdout();
//
//    }

    /**
     Get the Logger intended to be used for 'user friendly' messages.
     The logger is allocated if it does not already exist.
     <p/>
     Note that the 'user friendly' logger only sends messages to its listeners (i.e. if there are
     no listeners then there are no messages sent anywhere).

     @return the Logger that is intended to be used for 'user friendly' messages.
     */

    public static Logger getFriendly() {

        if ( Logger.s_friendly == null ) {

            Logger.s_friendly = new Logger( "<friendly>", null );

        }

        return Logger.s_friendly;

    }

    /**
     Send a log message to the 'user friendly' logger.

     @param msg the message to be printed.
     */

    public static void logFriendly( final String msg ) {

        Logger.getFriendly().println( Logger.getPrefix() + getNestingString() + msg );

    }

    /**
     Send a log message to stdout.

     @param msg the message to be printed.
     */

    public static void logMsg( final String msg ) {

        Trace.event( msg );
        Logger.getStdout().println( Logger.getPrefix() + getNestingString() + msg );

    }

    public static void logMsgs( final @NotNull String@NotNull[] lines ) {

        for ( String line : lines ) {

            Logger.logMsg( line );

        }

    }

    public static void logMsgs( final Collection<String> lines ) {

        for ( String line : lines ) {

            Logger.logMsg( line );

        }

    }

    /**
     Send something to a Logger.

     @param msg what to send.
     */

    public void msg( final String msg ) {

        Trace.event( msg );
        println( Logger.getPrefix() + getNestingString() + msg );

    }

    /**
     Log something with an optional throwable to a Logger.

     @param msg the message.
     @param e   the throwable (ignored if null).
     */

    public void msg( final String msg, final Throwable e ) {

        Trace.event( msg, e );
        println( Logger.getPrefix() + getNestingString() + msg );
        if ( e != null ) {

            log( e );

        }

    }

    private static String getPrefix() {

        return ObtuseUtil.center( "{" + Thread.currentThread().getId() + "}", 5 ) + ' ';

    }

    /**
     Send a log message to the 'user friendly' logger and a probably different message to stdout.

     @param friendly the 'user friendly' message.
     @param geek     the 'geek-readable' message (if null then the friendly message takes its place).
     */

    public static void logMsg( final String friendly, @Nullable final String geek ) {

        String prefixedMessage = Logger.getPrefix() + getNestingString() + ( geek == null ? friendly : geek );

        Logger.getFriendly().println( prefixedMessage );

        Trace.event( prefixedMessage );
        Logger.getStdout().println( prefixedMessage );

    }

    /**
     Send a log message to stderr.

     @param msg the message to be printed.
     */

    public static void logErr( final String msg ) {

        String prefixedMessage = Logger.getPrefix() + getNestingString() + msg;
        Trace.event( prefixedMessage );
        Logger.getStderr().println( prefixedMessage );

    }

    /**
     Send a log message to the 'user friendly' logger and a probably different message to stderr.

     @param friendly the 'user friendly' message.
     @param geek     the 'geek-readable' message (if null then the friendly message takes its place).
     */

    public static void logErr( final String friendly, final String geek ) {

        Logger.getFriendly().println( friendly );

        Logger.logErr( geek == null ? friendly : geek );

    }

    /**
     Send a log message and a stack trace to stderr.

     @param msg the message to be printed.
     @param e   the throwable containing the stack trace.
     */

    public static void logErr( final String msg, @Nullable final Throwable e ) {

        Logger.logErr( msg );

        if ( e != null ) {

            Trace.event( msg, e );
            Logger.getStderr().log( e );

        }

    }

    /**
     Send a log message to the 'user friendly' logger and a probably different message along with a
     stack trace to stderr.

     @param friendly the 'user friendly' message.
     @param geek     the 'geek-readable' message (if null then the friendly message takes its place).
     @param e        the throwable containing the stack trace.
     */

    public static void logErr( final String friendly, @Nullable final String geek, final Throwable e ) {

        Logger.getFriendly().println( friendly );

        Logger.logErr( geek == null ? friendly : geek, e );

    }

    /**
     Add a listener.

     @param listener the listener to be added to this instance's list of listeners.
     */

    public synchronized void addListener( final LoggerListener listener ) {

        _listeners.add( listener );

    }

    public String toString() {

        return "Logger()";

    }

}
