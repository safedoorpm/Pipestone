/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

import javax.management.timer.Timer;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Generate a trace file when we're connected to.
 */

public class TraceDebugConnectionHandler extends AbstractDebugConnectionHandler {

    public TraceDebugConnectionHandler( Logger logger )
            throws UnknownHostException {
        super( logger, "traceDebugConnectionHandler" );

    }

    public static TraceDebugConnectionHandler launch( @Nullable File workingDirectory, @Nullable Logger logger ) {

        TraceDebugConnectionHandler handler;
        try {

            handler = new TraceDebugConnectionHandler( logger );

        } catch ( UnknownHostException e ) {

            AbstractDebugConnectionHandler.errmsg( logger, "UnknownHostException launching TraceDebugConnectionHandler", e );
            return null;

        }

        try {

            handler.launch( workingDirectory, 0 );

        } catch ( IOException e ) {

            AbstractDebugConnectionHandler.errmsg( logger, "IOException launching TraceDebugConnectionHandler", e );

        }

        return handler;

    }

    @Override
    public void connectionReceived( String handlerName, Socket clientSocket ) {

        String msg = "triggered by connection from " + clientSocket;

        ObtuseUtil.closeQuietly( clientSocket );

        if ( getLogger() != null ) {

            getLogger().msg( "trace " + msg );

        }

        Logger.logErr( "trace " + msg );
        Trace.emitTrace( msg );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "TraceDebugConnectionHandler", "Test", null );

        TraceDebugConnectionHandler.launch( null, null );

        ObtuseUtil.safeSleepMillis( Timer.ONE_SECOND * 120L );

    }

}
