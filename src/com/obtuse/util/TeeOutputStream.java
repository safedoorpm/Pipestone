package com.obtuse.util;

/*
 * Copyright © 2015 Daniel Boulet
 */

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 %%% Something clever goes here.
 */

public class TeeOutputStream extends OutputStream {

    @SuppressWarnings("FieldCanBeLocal")
    private final File _teeFile;

    private final OutputStream _outputStream;

    @SuppressWarnings({ "FieldCanBeLocal", "unused" })
    private final boolean _buffered;

    private final OutputStream _teeOutputStream;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd_HH.mm.ss.SSS" );

    public TeeOutputStream(
	    @NotNull SocketAddress remoteSocketAddress,
	    @NotNull SocketAddress localSocketAddress,
	    @NotNull OutputStream socketOutputStream,
	    boolean buffered
    )
	    throws IOException {
	super();

	_outputStream = socketOutputStream;

	_teeFile = constructLogFile( remoteSocketAddress, localSocketAddress );
	_buffered = buffered;

	OutputStream teeOutputStream = new FileOutputStream( _teeFile );
	if ( buffered ) {

	    teeOutputStream = new BufferedOutputStream( teeOutputStream );

	}

	_teeOutputStream = teeOutputStream;

    }

    private static String constructTag( SocketAddress sa ) {

	if ( sa instanceof InetSocketAddress ) {

	    InetSocketAddress isa = (InetSocketAddress)sa;
	    return ( "" + isa.getAddress().toString() + ":" + isa.getPort() ).replace( '/', '_' );

	} else {

	    return sa.toString();

	}

    }

    public static File constructLogFile( SocketAddress remoteSocketAddress, SocketAddress localSocketAddress )
	    throws IOException {

	String sessionName = constructTag( remoteSocketAddress ) + "_" + constructTag( localSocketAddress );

	long now = System.currentTimeMillis();
	while ( true ) {

	    String fileName = DATE_FORMAT.format( new Date( now ) ) + "_" + sessionName + ".session";
	    File f = new File( fileName );
	    if ( f.createNewFile() ) {

		return f;

	    }

	    now += 1;

	}

    }

    public void write( int b ) throws IOException {

	_outputStream.write( b );
	_teeOutputStream.write( b );

    }

    public void write( byte[] b ) throws IOException {

	_outputStream.write( b );
	_teeOutputStream.write( b );

    }

    public void write( byte[] b, int off, int len ) throws IOException {

	_outputStream.write( b, off, len );
	_teeOutputStream.write( b, off, len );

    }

    public void close() throws IOException {

	_outputStream.close();
	_teeOutputStream.close();

    }

}
