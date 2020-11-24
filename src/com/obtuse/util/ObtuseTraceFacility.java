package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 Yet another trace facility.
 <p>This is almost certainly a pointless effort.
 Instead of reinventing the wheel,
 effort should probably be directed at enhancing the existing {@link Trace} facility.</p>
 */

public class ObtuseTraceFacility {

    private static final Long s_initLock = 0L;
    private static final ObtuseTraceFacility s_instance = new ObtuseTraceFacility();
    private static boolean s_initializing = false;

    private final File _traceFile;
    private final long _initializationTime;

    public static class Event {

        private final long _ts;
        private final String _description;
        private final long _tid;

        private String _eventToString = null;

        public Event( long ts, @NotNull final String description, final long tid ) {
            super();

            _ts = ts;
            _description = description;
            _tid = tid;

        }

        @SuppressWarnings("unused")
        public Event( final long ts, @NotNull final String description ) {
            this( ts, description, Thread.currentThread().getId() );
        }

        public long getTs() {

            return _ts;

        }

        @NotNull
        public String getDescription() {

            return _description;

        }

        public long getTid() {

            return _tid;

        }

        public String toString() {

            if ( _eventToString == null ) {

                synchronized (this) {

                    if ( _eventToString == null ) {

                        _eventToString =
                                "Event( " +
                                _ts + ", " +
                                ObtuseUtil.enquoteToJavaString( _description ) + ", " +
                                _tid +
                                " )";

                    }

                }

            }

            return _eventToString;

        }

    }

    /**
     Get the default {@link ObtuseTraceFacility} instance.
     @return the default {@link ObtuseTraceFacility} instance.
     */

    public static ObtuseTraceFacility getInstance() {

        return s_instance;

    }

    private ObtuseTraceFacility() {
        this( new File( System.getProperty( "user.home" ), "ObtuseTraceFacility" ) );

    }

    public ObtuseTraceFacility( @NotNull final File traceFilesDirectory ) {
        super();

        synchronized ( s_initLock ) {

            if ( s_initializing ) {

                throw new IllegalArgumentException(
                        "ObtuseTraceFacility:  recursive initialization of this facility (you are living in very interesting times)"
                );

            }

            boolean worked = false;
            Throwable exception = null;

            File traceFile = null;
            long initializationTime = -1;

            try {

                s_initializing = true;

                if ( !traceFilesDirectory.mkdirs() ) {

                    if ( !traceFilesDirectory.isDirectory() ) {

                        throw new IllegalArgumentException(
                                "ObtuseTraceFacility:  unable to create trace directory " +
                                ObtuseUtil.enquoteJavaObject( traceFilesDirectory )
                        );

                    }

                }

                initializationTime = System.currentTimeMillis();
                for ( int trial = 0; trial < 100; trial += 1 ) {

                    traceFile = new File( traceFilesDirectory, DateUtils.formatMarker2UTC( new Date( initializationTime ) ) );
                    try {

                        if ( traceFile.createNewFile() ) {

                            worked = true;

                            break;

                        }

                    } catch ( IOException e ) {

                        if ( exception == null ) {

                            exception = e;

                        }

                        Logger.logErr(
                                "ObtuseTraceFacility:  IOException creating trace file " +
                                ObtuseUtil.enquoteJavaObject( traceFile ),
                                e
                        );

                        ObtuseUtil.doNothing();

                    }

                    initializationTime += 1;

                }

                if ( worked ) {

                    s_initializing = false;

                } else {

                    throw new IllegalArgumentException(
                            "ObtuseTraceFacility:  unable to initialize OTF - not sure why",
                            exception
                    );

                }

            } catch ( RuntimeException e ) {

                if ( exception == null ) {

                    exception = e;

                }

            } finally {

                _initializationTime = initializationTime;
                _traceFile = traceFile;

                if ( s_initializing || exception != null ) {

                    Logger.logErr(
                            "ObtuseTraceFacility:  RuntimeException creating trace file " +
                            ObtuseUtil.enquoteJavaObject( traceFile ),
                            exception
                    );

                }

            }

            if ( s_initializing ) {

                //noinspection ConstantConditions
                if ( exception != null ) {

                    if ( exception instanceof RuntimeException ) {

                        throw new IllegalArgumentException(
                                "ObtuseTraceFacility:  unexpected runtime exception",
                                exception
                        );

                    } else {

                        throw new IllegalArgumentException(
                                "ObtuseTraceFacility:  unexpected checked exception",
                                exception
                        );

                    }

                }

            }

        }

    }

    public long getInitializationTime() {

        return _initializationTime;

    }

    public File getTraceFile() {

        return _traceFile;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "ObtuseTraceFacility", "testing" );

        Logger.logMsg( "we should be initialized by now - check things out" );

        ObtuseUtil.doNothing();

    }

}
