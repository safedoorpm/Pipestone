/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

import javax.management.timer.Timer;
import java.io.File;
import java.io.IOException;
import java.net.*;

/**
 * Respond when something connects on a port.
 */

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractDebugConnectionHandler extends Thread {

    public static final String NEWLINE = System.getProperty( "line.separator" );
    private ServerSocket _listenSocket;
    private final String _handlerName;
    private final Logger _logger;
    private int _portNumber;
    private final InetAddress _ourHost;

    private boolean _terminate = false;
//    private String _magicString;

//    public static interface DebugConnectionHandlerFactory {
//
//        AbstractDebugConnectionHandler createHandler( Logger logger )
//                throws UnknownHostException;
//
//    }

    protected AbstractDebugConnectionHandler( @Nullable Logger logger, String handlerName )
            throws UnknownHostException {
        super( handlerName );

        _logger = logger;
        _handlerName = handlerName;

        _ourHost = InetAddress.getLocalHost();

    }

    @SuppressWarnings("UnusedDeclaration")
    protected AbstractDebugConnectionHandler( String handlerName )
            throws IOException {
        this( null, handlerName );

    }

    protected void launch( @Nullable File workingDirectory, int portNumber )
            throws IOException {

//        try {

            _listenSocket = new ServerSocket( portNumber );

            _listenSocket.setSoTimeout( 1000 );

//        } catch ( IOException e ) {
//
//            errmsg( "unable to create debug connection handler for " + getHandlerDescription() );
//            throw e;
//
//        }

        _portNumber = _listenSocket.getLocalPort();

//        Runtime runtime = Runtime.getRuntime();
//        _magicString = ObtuseUtil.hexvalue(
//                new MersenneTwister( runtime.freeMemory() & Integer.MAX_VALUE ).nextLong() ^
//                new MersenneTwister( System.currentTimeMillis() & Integer.MAX_VALUE ).nextLong()
//        );

        File cmdFile = workingDirectory == null ? new File( _handlerName + ".sh" ) : new File( workingDirectory, _handlerName + ".sh" );
        ObtuseUtil.writeBytesToFile(
                ( "telnet " + _ourHost.getCanonicalHostName() + " " + _portNumber + AbstractDebugConnectionHandler.NEWLINE ).getBytes(),
                cmdFile,
                false
        );

//        PrintStream fs = new PrintStream( _handlerName + ".sh" );
//        fs.println( "telnet " + _ourHost.getHostName() + " " + portNumber );
//        ObtuseUtil.closeQuietly( fs );

        start();

    }

    public void run() {

        try {

            while ( true ) {

                if ( _terminate ) {

                    errmsg( "debug connection handler " + getHandlerDescription() + " terminating" );

                    return;

                }

                Socket clientSocket = null;

                try {

                    clientSocket = _listenSocket.accept();

                    connectionReceived( _handlerName, clientSocket );

                } catch ( SocketTimeoutException e ) {

//                    errmsg( "socket timeout, spinning again" );

                } catch ( IOException e ) {

                    errmsg( "IOException accepting connection on debug connection handler " + getHandlerDescription() + " (waiting five seconds)" );
                    ObtuseUtil.safeSleepMillis( Timer.ONE_SECOND * 5L );

                } finally {

                    ObtuseUtil.closeQuietly( clientSocket );

                }

            }

        } finally {

            ObtuseUtil.closeQuietly( _listenSocket );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    protected void terminate() {

        _terminate = true;

    }

    public abstract void connectionReceived( String handlerName, Socket clientSocket );

    protected void errmsg( String msg ) {

        AbstractDebugConnectionHandler.errmsg( _logger, msg );

    }

    protected static void errmsg( Logger logger, String msg ) {

        if ( logger == null ) {

            Logger.logErr( msg );

        } else {

            logger.msg( msg );

        }

    }

    protected void errmsg( String msg, Throwable exception ) {

        AbstractDebugConnectionHandler.errmsg( _logger, msg, exception );

    }

    protected static void errmsg( Logger logger, String msg, Throwable exception ) {

        if ( exception == null ) {

            AbstractDebugConnectionHandler.errmsg( logger, msg );

        }

        if ( logger == null ) {

            Logger.logErr( msg, exception );

        } else {

            logger.msg( msg, exception );

        }

    }

    private String getHandlerDescription() {

        return _handlerName + ":" + _portNumber;

    }

    @SuppressWarnings("UnusedDeclaration")
    public String getHandlerName() {

        return _handlerName;

    }

    @SuppressWarnings("UnusedDeclaration")
    public int getPortNumber() {

        return _portNumber;

    }

    @SuppressWarnings("UnusedDeclaration")
    public InetAddress getOurHost() {

        return _ourHost;

    }

    public Logger getLogger() {

        return _logger;

    }

}
