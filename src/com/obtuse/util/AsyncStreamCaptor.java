package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 Asynchronously capture the contents of an {@link InputStream}.
 <p>Two instances of this class are one way to capture both the <tt>stdout</tt> and <tt>stderr</tt> of a
 {@link Process} instance.</p>
 */

public class AsyncStreamCaptor extends Thread {

    @NotNull private final String _what;
    @NotNull private final InputStream _inputStream;
    @Nullable private final Consumer<AsyncStreamCaptor> _asynchronousNotifier;
    @NotNull private final String _idString;
    @NotNull private final ByteArrayOutputStream _capturedStream;
    private final long _maxCaptureLength;
    private final boolean _discardAfterMaxCaptureLength;
    private int _discardedByteCount;
    private boolean _dataReadUntilEOF = false;
    private boolean _done = false;
    private int _bytesCapturedToDate = 0;

    /**
     Launch a thread to asynchronously capture the contents of an {@link InputStream}.
     @param what a descriptive string used in {@link Logger} calls and certain exceptions.
     @param inputStream the input stream that we're supposed to capture asynchronously.
     @param asynchronousNotifier the {@link Consumer}{@code <}{@link AsyncStreamCaptor}{@code >}
     that we're supposed to inform when we're done.
     Ignored if {@code null}.
     <p><b>Note that this notifier is called on the thread that this {@link AsyncStreamCaptor} is running on.
     This is NEVER the thread that created this {@link AsyncStreamCaptor} instance so BE CAREFUL!!!</b></p>
     @param idString an id string that the user of this instance might wish to use to identify this instance.
     It is up to the creator of this instance to make this id string as unique as makes sense in the context
     that this instance will be used.
     @param threadName the name that should be assigned to the {@link Thread} that this instance will create.
     This might be useful to give the thread a name which will be recognizable in the list of active threads
     that a debugger like the IntelliJ IDEA debugger might provide.
     @param maxCaptureLength the maximum number of bytes to capture. Note that the bytes are captured in a
     {@link ByteArrayOutputStream} which holds its data in a {@code byte} array. Since Java limits all arrays
     to 2<sup>31</sup>-1 elements, the most that can be captured is 2<sup>31</sup>-1 bytes.
     @param discardAfterMaxCaptureLength {@code true} if all data in the input stream after the first
     {@code maxCaptureLength} bytes should be discarded; {@code false} if the input stream should be left
     positioned at the first uncaptured byte should this instance capture {@code maxCaptureLength} bytes.
     @param  immediateStart {@code true} if the worker thread should be started by this constructor;
     {@code false} if the caller takes responsibility for starting the worker thread by invoking the
     newly created instance's {@code start()} method.
     */

    public AsyncStreamCaptor(
            @NotNull final String what,
            @NotNull final InputStream inputStream,
            @Nullable final Consumer<AsyncStreamCaptor> asynchronousNotifier,
            @NotNull String idString,
            @NotNull String threadName,
            long maxCaptureLength,
            final boolean discardAfterMaxCaptureLength,
            final boolean immediateStart
    ) {
        super( threadName );

        _what = what;
        _inputStream = inputStream;
        _asynchronousNotifier = asynchronousNotifier;
        _idString = idString;
        _maxCaptureLength = maxCaptureLength;
        _discardAfterMaxCaptureLength = discardAfterMaxCaptureLength;

        _capturedStream = new ByteArrayOutputStream( 1024 * 1024 );

        if ( immediateStart ) {

            start();

        }

    }

    public void run() {

//        Logger.logMsg( "AsyncStreamCaptor:  " + _idString + " launched" );

        readAggressively();

//        Logger.logMsg( "AsyncStreamCaptor:  " + _idString + " finished" );

        ObtuseUtil.doNothing();

    }

    /**
     Try to read the stream in 1MB chunks until the maximum capture length has been reached.
     */

    private void readAggressively() {

        ObtuseUtil.safeSleepMillis( 1000L );
        ObtuseUtil.doNothing();

        try {

            byte[] buffer = new byte[1024 * 1024];
            while ( true ) {

                // Figure out how many bytes we need to read before we reach the maximum capture length.

                long rqSize = _maxCaptureLength - _capturedStream.size();

                // Are we done yet?

                if ( rqSize <= 0 ) {

                    // We have captured our 'budget' of bytes.
                    // Are we supposed to discard whatever might be left in the stream?

                    if ( _discardAfterMaxCaptureLength ) {

                        // Yes - read and discard until we hit EOF.

                        while ( true ) {

                            Logger.logMsg( getIdString() + ":  reading " + buffer.length + " bytes" );

                            int readLength = _inputStream.read( buffer );

                            Logger.logMsg( getIdString() + ":  read returned " + readLength );

                            if ( readLength < 0 ) {

                                _dataReadUntilEOF = true;

                                break;

                            }

                            _discardedByteCount += readLength;

                        }

                    }

                    // We've discarded what was left if we were told to do so.
                    // Alternatively, whatever is left is _exactly_ the data that doesn't fit in our maximum capture length.
                    // Either way, we're done.

                    break;

                } else {

                    // We've still got data to read.
                    // Figure out how much of our remaining 'budget' will fit in our buffer.

                    rqSize = Math.min( rqSize, buffer.length );

                    // Read the next chunk subject to the constraints that we
                    // (1) never read more than the size of our buffer
                    // and
                    // (2) never read more than what's left in our budget

                    int readLength = _inputStream.read( buffer, 0, (int)rqSize );

                    // Bail if we reached the EOF.
                    if ( readLength < 0 ) {

                        _dataReadUntilEOF = true;

                        break;

                    } else if ( readLength > 0 ) {

                        // We got at least some data, add it to our captured stream.

                        _capturedStream.write( buffer, 0, readLength );
                        _bytesCapturedToDate = _capturedStream.size();

                    }

                    ObtuseUtil.doNothing();

                }

            }

        } catch ( IOException e ) {

            Logger.logErr( "java.io.IOException caught", e );

            ObtuseUtil.doNothing();

        }

        // One way or the other, we're done.

        _done = true; // If someone polls us, they'll see that we're done now.

//        Logger.logMsg( "we're done, doing notify:  " + toString() );

        // Wakeup anyone actually waiting for us to be done.

        synchronized ( this ) {

//            Logger.logMsg( getIdString() + ":  sending notifyAll" );

            notifyAll();

        }

        // Asynchronously invoke our notifier if one was specified.

        if ( _asynchronousNotifier != null ) {

            _asynchronousNotifier.accept( this );

        }

    }

    @NotNull
    @SuppressWarnings("unused")
    public String getWhat() {

        return _what;

    }

    @NotNull
    @SuppressWarnings("unused")
    public InputStream getInputStream() {

        return _inputStream;

    }

    @Nullable
    @SuppressWarnings("unused")
    public Consumer<AsyncStreamCaptor> getAsynchronousNotifier() {

        return _asynchronousNotifier;

    }

    @NotNull
    @SuppressWarnings("unused")
    public String getIdString() {

        return _idString;

    }

    @NotNull
    @SuppressWarnings("unused")
    public ByteArrayOutputStream getCapturedStream() {

        return _capturedStream;

    }

    /**
     Get the maximum number of bytes that the user wants us to keep.
     @return the maximum number of bytes that the user wants us to keep.
     */
    @SuppressWarnings("unused")
    public long getMaxCaptureLength() {

        return _maxCaptureLength;

    }

    /**
     Determine if we should flush the rest of the input stream once we've captured the maximum number of bytes specified by the user.
     @return {@code true} if we should flush the rest of the input stream once we've captured the maximum number of bytes specified by the user; {@code false} otherwise.
     */

    @SuppressWarnings("unused")
    public boolean discardAfterMaxCaptureLength() {

        return _discardAfterMaxCaptureLength;

    }

    /**
     Get the number of bytes which have been read and discarded/flushed.
     @return the number of bytes which have been read and discarded/flushed.
     */

    @SuppressWarnings("unused")
    public int getDiscardedByteCount() {

        return _discardedByteCount;

    }

    /**
     Determine if we read everything in the stream.
     @return {@code true} if we got to the end of the input stream (we may not have kept it all as we could
     have gotten to the end because we were flushing/discarding the data after the maximum capture length).
     */

    @SuppressWarnings("unused")
    public boolean wasDataReadUntilEOF() {

        return _dataReadUntilEOF;

    }

    /**
     Determine how many bytes we have actually captured to date.
     @return the number of bytes we have actually captured to date (what we have read minus what we have discarded).
     */

    public int getBytesCapturedToDate() {

        return _bytesCapturedToDate;

    }

    /**
     Determine if we captured everything in the stream.
     @return {@code true} if we read and captured the entire stream; {@code false} if we bailed before reading to the end of the stream
     or if we discarded any of the contents of the stream.
     */

    public boolean wasAllDataCaptured() {

        return _dataReadUntilEOF && _discardedByteCount == 0;

    }

    /**
     Determine how many bytes we read.
     @return the number of bytes which we have captured or discarded.
     */

    public int getBytesRead() {

        return _bytesCapturedToDate + _discardedByteCount;

    }

    /**
     Determine if this instance is done capturing data.
     @return {@code true} if this instance is done (has either reached the end of the input stream
     or consumed its entire {@code maxCaptureLength} byte budget); {@code false} otherwise.
     <p>If this method returns {@code true} then (1) the input stream will be empty if {@code discardAfterMaxCaptureLength} was
     {@code true} when this instance was created or (2) the input stream will be positioned at the next available byte
     {@code maxCaptureLength} bytes have been captured (note that it is entirely possible that there are no more bytes
     available if {@code maxCaptureLength} happens to be equal to the number of bytes originally available on the input stream).</p>
     */

    @SuppressWarnings("unused")
    public boolean isDone() {

        return _done;

    }

    @SuppressWarnings("unused")
    private void readUsingAvailableMethod() {

        try {

            while ( true ) {

                int availableBytes = _inputStream.available();
                Logger.logMsg( _what + ":  stream has at least " + availableBytes + " that can be read right now" );
                if ( availableBytes > 0 ) {

                    byte[] nextChunk = new byte[availableBytes];
                    int bytesReadCount = _inputStream.read( nextChunk );
                    Logger.logMsg( _what + ":  got " + bytesReadCount + " bytes from stream" );
                    if ( bytesReadCount > 0 ) {

                        _capturedStream.write( nextChunk, 0, bytesReadCount );

                    } else if ( bytesReadCount == 0 ) {

                        Logger.logMsg( _what + ":  got zero from attempt to read at least one byte from stream!" );

                        ObtuseUtil.doNothing();

                    } else {

                        Logger.logMsg( _what + ":  got " + bytesReadCount + " trying to read at least one byte from stream" );

                        ObtuseUtil.doNothing();

                    }

                } else {

                    Logger.logMsg( "got " + availableBytes + " from " + _what );

                    break;

                }

            }

        } catch ( IOException e ) {

            Logger.logErr( "java.io.IOException caught", e );

            ObtuseUtil.doNothing();

        }

    }

    public synchronized void waitUntilDone() {

        while ( !_done ) {

            try {

                wait();

            } catch ( InterruptedException e ) {

                Logger.logErr( "java.lang.InterruptedException caught", e );

                ObtuseUtil.doNothing();

            }

        }

//        Logger.logMsg( "we're done:  " + toString() );

    }

    public String toString() {

        return "AsyncStreamCaptor( " + ObtuseUtil.enquoteToJavaString( _idString ) + ", bytesCapturedToDate=" + _bytesCapturedToDate + ", done=" + _done + " )";

    }

}
