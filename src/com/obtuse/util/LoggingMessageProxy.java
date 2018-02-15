/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A message proxy that logs messages and counts how many error and informational messages are sent its way.
 */

@SuppressWarnings("UnusedDeclaration")
public class LoggingMessageProxy implements MessageProxy {

    private int _errorCount = 0;
    private int _infoCount = 0;
    private final MessageProxy _passThroughMessageProxy;

    /**
     Create an instance which logs all messages via the Obtuse {@link Logger} facility.
     */

    public LoggingMessageProxy() {
        super();

        _passThroughMessageProxy = null;

    }

    /**
     Create an instance which logs all messages via a specified facility.
     @param messageProxy the specified facility (if <code>null</code> then messages are logged via the Obtuse {@link Logger} facility).
     */

    public LoggingMessageProxy( @Nullable final MessageProxy messageProxy ) {
        super();

        _passThroughMessageProxy = messageProxy;

    }

    /**
     Count an error message.
     */

    private void countError() {

        _errorCount += 1;

    }

    /**
     Count an informational message.
     */

    private void countInfo() {

        _infoCount += 1;

    }

    @Override
    public void fatal( final @NotNull String msg ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg, (String)null );
            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg );

        }

    }

    @Override
    public void fatal( final @NotNull String msg, @Nullable final String appendix ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg, (String)null );
            if ( appendix != null ) {

                Logger.logErr( "FATAL:  " + appendix, (String)null );

            }

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg, appendix );

        }

    }

    @Override
    public void fatal( final @NotNull String msg, @Nullable final Throwable e ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg, null, e );

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg, e );

        }

    }

    @Override
    public void fatal( final @NotNull String msg, @Nullable final String appendix, @Nullable final String contextName ) {

        if ( _passThroughMessageProxy == null ) {

            fatal( msg, appendix );

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg, appendix, contextName );

        }

    }

    @Override
    public void error( final @NotNull String msg ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg, (String)null );

        } else {

            _passThroughMessageProxy.error( msg );

        }

        countError();

    }

    @Override
    public void error( final @NotNull String msg, @Nullable final String appendix ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg, (String)null );
            if ( appendix != null ) {

                Logger.logErr( "ERROR:  " + appendix, (String)null );

            }

        } else {

            _passThroughMessageProxy.error( msg, appendix );

        }

        countError();

    }

    @Override
    public void error( final @NotNull String msg, @Nullable final Throwable e ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg, null, e );

        } else {

            _passThroughMessageProxy.error( msg, e );

        }

        countError();

    }

    @Override
    public void error( final @NotNull String msg, @Nullable final String appendix, @Nullable final String contextName ) {

        if ( _passThroughMessageProxy == null ) {

            error( msg, appendix );

        } else {

            _passThroughMessageProxy.error( msg, appendix, contextName );

        }

        countError();

    }

    @Override
    public void info( final @NotNull String msg ) {

        if ( _passThroughMessageProxy == null ) {

            //noinspection RedundantCast
            Logger.logMsg( "INFO:  " + msg, (String)null );

        } else {

            _passThroughMessageProxy.info( msg );

        }

        countInfo();

    }

    @Override
    public void info( final @NotNull String msg, @Nullable final String appendix ) {

        if ( _passThroughMessageProxy == null ) {

            //noinspection RedundantCast
            Logger.logMsg( "INFO:  " + msg, (String)null );
            if ( appendix != null ) {

                //noinspection RedundantCast
                Logger.logMsg( "INFO:  " + appendix, (String)null );

            }

        } else {

            _passThroughMessageProxy.info( msg, appendix );

        }

        countInfo();

    }

    @Override
    public void info( final @NotNull String msg, @Nullable final String appendix, @Nullable final String contextName ) {

        if ( _passThroughMessageProxy == null ) {

            info( msg, appendix );

        } else {

            _passThroughMessageProxy.info( msg, appendix, contextName );

        }

        countInfo();

    }

    /**
     Retrieve the count of error messages sent to any of this class's error() methods.
     @return the number of error messages sent to any of this class's error() methods.
     */

    public int getErrorCount() {

        return _errorCount;

    }

    /**
     Determine if any error messages have been sent to any of this class's error() methods.
     @return <code>true</code> if one or more of this class's error() methods have ever been called within this JVM; <code>false</code> otherwise.
     */

    public boolean hasLoggedErrors() {

        return _errorCount > 0;

    }

    /**
     Retrieve the count of informational messages sent to any of this class's info() methods.
     @return the number of informational messages sent to any of this class's info() methods.
     */

    public int getInfoCount() {

        return _infoCount;

    }

    /**
     Determine if any informational messages have been sent to any of this class's info() methods.
     @return <code>true</code> if one or more of this class's info() methods have ever been called within this JVM; <code>false</code> otherwise.
     */

    public boolean hasLoggedInfos() {

        return _infoCount > 0;

    }

}
