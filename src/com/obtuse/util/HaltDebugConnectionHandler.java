/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

import javax.management.timer.Timer;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Halt when we're connected to.
 */

public class HaltDebugConnectionHandler extends AbstractDebugConnectionHandler {

    public HaltDebugConnectionHandler( Logger logger )
            throws UnknownHostException {
        super( logger, "haltDebugConnectionHandler" );

    }

    public static HaltDebugConnectionHandler launch( @Nullable File workingDirectory, @Nullable Logger logger ) {

        HaltDebugConnectionHandler handler;
        try {

            handler = new HaltDebugConnectionHandler( logger );

        } catch ( UnknownHostException e ) {

            AbstractDebugConnectionHandler.errmsg( logger, "UnknownHostException launching HaltDebugConnectionHandler", e );
            return null;

        }

        try {

            handler.launch( workingDirectory, 0 );

        } catch ( IOException e ) {

            AbstractDebugConnectionHandler.errmsg( logger, "IOException launching HaltDebugConnectionHandler", e );

        }

        return handler;

    }

    @Override
    public void connectionReceived( String handlerName, Socket clientSocket ) {

        String msg = "HALT triggered by connection from " + clientSocket;

        ObtuseUtil.closeQuietly( clientSocket );

        Trace.emitTrace( msg );
        if ( getLogger() != null ) {

            getLogger().msg( msg );

        }

        Logger.logErr( msg );
        System.exit( 123 );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "HaltDebugConnectionHandler", "Test", null );

        HaltDebugConnectionHandler.launch( null, null );

        ObtuseUtil.safeSleepMillis( Timer.ONE_SECOND * 120L );

    }

}
