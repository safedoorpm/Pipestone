/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

/**
 * A message proxy that logs messages and counts how many errors are sent its way.
 */

@SuppressWarnings("UnusedDeclaration")
public class LoggingMessageProxy implements MessageProxy {

    private int _errorCount = 0;
    private int _infoCount = 0;
    private final MessageProxy _passThroughMessageProxy;

    public LoggingMessageProxy() {
        super();

        _passThroughMessageProxy = null;

    }

    public LoggingMessageProxy( MessageProxy messageProxy ) {
        super();

        _passThroughMessageProxy = messageProxy;

    }

    private void countError() {

        _errorCount += 1;

    }

    private void countInfo() {

        _infoCount += 1;

    }

    public void fatal( String msg ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg, (String)null );
            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg );

        }

    }

    public void fatal( String msg1, @Nullable String msg2 ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg1, (String)null );
            if ( msg2 != null ) {

                Logger.logErr( "FATAL:  " + msg2, (String)null );

            }

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg1, msg2 );

        }

    }

    public void fatal( String msg, Throwable e ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "FATAL:  " + msg, null, e );

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg, e );

        }

    }

    public void fatal( String msg1, @Nullable String msg2, String buttonContents ) {

        if ( _passThroughMessageProxy == null ) {

            fatal( msg1, msg2 );

            System.exit( 1 );

        } else {

            _passThroughMessageProxy.fatal( msg1, msg2, buttonContents );

        }

    }

    public void error( String msg ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg, (String)null );

        } else {

            _passThroughMessageProxy.error( msg );

        }

        countError();

    }

    public void error( String msg1, @Nullable String msg2 ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg1, (String)null );
            if ( msg2 != null ) {

                Logger.logErr( "ERROR:  " + msg2, (String)null );

            }

        } else {

            _passThroughMessageProxy.error( msg1, msg2 );

        }

        countError();

    }

    public void error( String msg, Throwable e ) {

        if ( _passThroughMessageProxy == null ) {

            Logger.logErr( "ERROR:  " + msg, null, e );

        } else {

            _passThroughMessageProxy.error( msg, e );

        }

        countError();

    }

    public void error( String msg1, @Nullable String msg2, String buttonContents ) {

        if ( _passThroughMessageProxy == null ) {

            error( msg1, msg2 );

        } else {

            _passThroughMessageProxy.error( msg1, msg2, buttonContents );

        }

        countError();

    }

    public void info( String msg ) {

        if ( _passThroughMessageProxy == null ) {

            //noinspection RedundantCast
            Logger.logMsg( "INFO:  " + msg, (String)null );

        } else {

            _passThroughMessageProxy.info( msg );

        }

        countInfo();

    }

    public void info( String msg1, String msg2 ) {

        if ( _passThroughMessageProxy == null ) {

            //noinspection RedundantCast
            Logger.logMsg( "INFO:  " + msg1, (String)null );
            if ( msg2 != null ) {

                //noinspection RedundantCast
                Logger.logMsg( "INFO:  " + msg2, (String)null );

            }

        } else {

            _passThroughMessageProxy.info( msg1, msg2 );

        }

        countInfo();

    }

    public void info(
            String msg1,
            @Nullable
            String msg2,
            String buttonContents
    ) {

        if ( _passThroughMessageProxy == null ) {

            info( msg1, msg2 );

        } else {

            _passThroughMessageProxy.info( msg1, msg2, buttonContents );

        }

        countInfo();

    }

    public int getErrorCount() {

        return _errorCount;

    }

    public boolean hasLoggedErrors() {

        return _errorCount > 0;

    }

    public int getInfoCount() {

        return _infoCount;

    }

    public boolean hasLoggedInfos() {

        return _infoCount > 0;

    }

}
