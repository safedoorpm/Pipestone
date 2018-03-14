package com.obtuse.util;

/*
 * Copyright © 2015 Daniel Boulet
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 A pass-through stream that sends a copy of what passes through to a specified {@link OutputStream}.
 <p>This is intended to be used to debug a complex chain of streams.</p>
 */

@SuppressWarnings("unused")
public class TeeOutputStream extends OutputStream {

    @SuppressWarnings("FieldCanBeLocal")
    private final File _teeFile;

    private String _sessionName;

    private final OutputStream _outputStream;

    @SuppressWarnings({ "FieldCanBeLocal", "unused" })
    private final boolean _buffered;

    private final OutputStream _teeOutputStream;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd_HH.mm.ss.SSS" );

    /**
     Create a generic tee that makes no assumptions about where the data being captured is also going.
     @param sessionName the name which is to be used to construct the log file's name.
     @param outputStream the intended recipient output stream.
     @param buffered {@code true} if the data being sent to the log file should be buffered; {@code false} otherwise.
     @throws IOException when bad things happen to good streams.
     Exceptions thrown while working with the log file's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     */

    public TeeOutputStream(
            final @NotNull String sessionName,
            final @NotNull OutputStream outputStream,
            final boolean buffered
    )
            throws IOException {

        super();

        _outputStream = outputStream;

        _sessionName = sessionName;
        OutputStream teeOutputStream;
        try {

         _teeFile = constructLogFile( sessionName );
            _buffered = buffered;

            teeOutputStream = new FileOutputStream( _teeFile );
            if ( buffered ) {

                teeOutputStream = new BufferedOutputStream( teeOutputStream );

            }

        } catch ( IOException e ) {

            throw new IOException( "Tee-" + e.getMessage(), e );

        }

        _teeOutputStream = teeOutputStream;

    }

    /**
     Create a tee that is intended to be used to capture data being sent to a socket stream.
     <p>This constructor is a somewhat pointless wrapper equivalent to</p>
     <blockquote><code>new TeeOutputStream(<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;constructTag( remoteSocketAddress ) + "_" + constructTag( localSocketAddress ),<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;socketOutputStream,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;buffered<br>)</code></blockquote>
     @param remoteSocketAddress the socket stream's remote socket address.
     @param localSocketAddress the socket stream's local socket address.
     @param socketOutputStream the intended recipient output stream.
     @param buffered {@code true} if the data being sent to the log file should be buffered; {@code false} otherwise.
     @throws IOException when bad things happen to good streams.
     Exceptions thrown while working with the log file's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     */

    public TeeOutputStream(
            final @NotNull SocketAddress remoteSocketAddress,
            final @NotNull SocketAddress localSocketAddress,
            final @NotNull OutputStream socketOutputStream,
            final boolean buffered
    )
            throws IOException {
        this( constructTag( remoteSocketAddress ) + "_" + constructTag( localSocketAddress ), socketOutputStream, buffered );

    }

    /**
     Create a tag string for a socket address.
     @param sa the socket address.
     @return if {@code sa} is an {@link InetSocketAddress} then
     <blockquote><code>
     InetSocketAddress isa = (InetSocketAddress)sa;<br>
     return ( "" + isa.getAddress().toString() + ":" + isa.getPort() ).replace( '/', '_' );
     </code></blockquote>
     Note that replacing {@code '/'} with {@code '_'} is necessary to make the result usable in a filename which will
     probably be passed to something that tries to create the file.
     <p/>
     Otherwise, just returns {@code sa.toString()}.
     */

    @NotNull
    public static String constructTag( final @NotNull SocketAddress sa ) {

        if ( sa instanceof InetSocketAddress ) {

            InetSocketAddress isa = (InetSocketAddress)sa;
            return ( "" + isa.getAddress().toString() + ":" + isa.getPort() ).replace( '/', '_' );

        } else {

            return sa.toString();

        }

    }

    /**
     Construct a log file in an arbitrary directory.
     @param dir the directory that the log file should land in (if {@code null} then the file is created in the current directory).
     @param sessionName a tag line to be used to distinguish this log file from log files for other purposes.
     @return the just created log file.
     @throws IOException if something bad happens.
     */

    @NotNull
    public static File constructLogFile( @Nullable final File dir, final @NotNull String sessionName ) throws IOException {

        long now = System.currentTimeMillis();
        while ( true ) {

            String fileName = DATE_FORMAT.format( new Date( now ) ) + "_" + sessionName + ".session";
            File f = new File( dir, fileName );
            if ( f.createNewFile() ) {

                return f;

            }

            now += 1;

        }

    }

    /**
     Construct a log file in the current directory.
     @param sessionName a tag line to be used to distinguish this log file from log files for other purposes.
     @return the just created log file.
     @throws IOException if something bad happens.
     */

    @NotNull
    public static File constructLogFile( final @NotNull String sessionName ) throws IOException {

        return constructLogFile( null, sessionName );

    }

    /**
     Get the tee output stream.
     @return the tee output stream.
     */

    @NotNull
    public OutputStream getTeeOutputStream() {

        return _teeOutputStream;

    }

    /**
     Get the downstream output stream.
     @return the downstream output stream.
     */

    @NotNull
    public OutputStream getOutputStream() {

        return _outputStream;

    }

    /**
     Get the name of the tee's log file.
     @return the tee's log file.
     */
    @NotNull
    public File getTeeFile() {

        return _teeFile;

    }

    /**
     Get this session's name (the session name used to create the tee's log file.
     See {@link #TeeOutputStream(String, OutputStream, boolean)} for more info).
     @return this session's name.
     */

    @NotNull
    public String getSessionName() {

        return _sessionName;

    }

    /**
     Write a byte to both the tee output stream and the downstream output stream.
     @param b the byte to be written.
     @throws IOException if something goes wrong.
     Exceptions thrown while working with the tee's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     @see        java.io.OutputStream#write(int)
     */
    @Override
    public void write( final int b ) throws IOException {

        _outputStream.write( b );
        try {

            _teeOutputStream.write( b );

        } catch ( IOException e ) {

            throw new IOException( "Tee-" + e.getMessage(), e );

        }

    }

    /**
     Write an array of bytes to both the tee output stream and the downstream output stream.
     @param b the array of bytes to be written.
     @throws IOException if something goes wrong.
     Exceptions thrown while working with the tee's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     @see        java.io.OutputStream#write(byte[])
     */
    @Override
    public void write( final @NotNull byte@NotNull[] b ) throws IOException {

        _outputStream.write( b );
        try {

            _teeOutputStream.write( b );

        } catch ( IOException e ) {

            throw new IOException( "Tee-" + e.getMessage(), e );

        }

    }

    /**
     Write <code>len</code> bytes from the specified byte array
     starting at offset <code>off</code> to both the tee output stream and the downstream output stream.
     @param b the specified byte array to be written.
     @param off the offset within the array to start writing at.
     @param len the number of bytes to write.
     @throws IOException if something goes wrong.
     Exceptions thrown while working with the tee's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     @see        java.io.OutputStream#write(byte[],int,int)
     */
    @Override
    public void write( final @NotNull byte@NotNull[] b, final int off, final int len ) throws IOException {

        _outputStream.write( b, off, len );

        try {

            _teeOutputStream.write( b, off, len );


        } catch ( IOException e ) {

            throw new IOException( "Tee-" + e.getMessage(), e );

        }
    }

    /**
     Close the output stream.
     @throws IOException if something goes wrong.
     Exceptions thrown while working with the tee's output stream are wrapped in
     a new {@link IOException} instance as follows:
     <blockquote><code>new IOException( "Tee-" + e.getMessage(), e )</code></blockquote>
     @see        java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {

        _outputStream.close();
        try {

            _teeOutputStream.close();


        } catch ( IOException e ) {

            throw new IOException( "Tee-" + e.getMessage(), e );

        }

    }

    @Override
    public String toString() {

        if ( _sessionName == null ) {

            return "TeeOutputStream( <<session name not set yet>> )";

        } else {

            return "TeeOutputStream( " + ObtuseUtil.enquoteToJavaString( getSessionName() ) + " )";

        }

    }

    public static void main( String[] args ) {

        try {

            TeeOutputStream os = new TeeOutputStream(
                    "/failed tee file open",
                    new FileOutputStream( "TeeOutputStream test file.junk" ),
                    false
            );

        } catch ( IOException e ) {

            e.printStackTrace();

        }

        try {

            TeeOutputStream os = new TeeOutputStream(
                    "junk file",
                    new FileOutputStream( "TeeOutputStream test file.junk" ),
                    false
            );
            os.write( "Hello world".getBytes() );
            os.getTeeOutputStream().close();
            os.write( "Boom!".getBytes() );

        } catch ( IOException e ) {

            e.printStackTrace();

        }

    }

}
