package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 An input stream wrapper that computes a {@link MessageDigest} 'on the fly'.
 <p>If the constructor is provided with a {@link MessageDigest} instance then
 every by that passes through an instance of this class is also passed to said
 {@link MessageDigest} instance via one of its {@code update} methods (see
 {@link MessageDigest} for more information.</p>
 <p>Note that the user of this class is responsible for all aspects of managing the
 {@link MessageDigest} other than calling an appropriate {@code update} in
 the digest instance as the bytes go by.</p>
 */

@SuppressWarnings("unused")
public class MessageDigestInputStream extends FilterInputStream {

    private final InputStream _upstream;
    private final MessageDigest _messageDigest;

    /**
     Creates a {@code MessageDigestInputStream} that wraps a specified input stream.
     If the optional {@link MessageDigest} is provided then it is saved for later use.
     @param upstream the mandatory {@link InputStream} instance that we are supposed to wrap.
     @param messageDigest the optional {@link MessageDigest} instance that we are supposed to send
     bytes to as they go by.
     If this parameter is {@code null} then this class is hopefully designed and implemented
     to provide a 100% transparent wrapping of the specified {@code upstream} input stream.
     */
    public MessageDigestInputStream( @NotNull final InputStream upstream, @Nullable final MessageDigest messageDigest ) {
        super( upstream );

        _upstream = upstream;
        _messageDigest = messageDigest;

    }

    @Override
    public int available() throws IOException {

        return _upstream.available();

    }

    @Override
    public void close() throws IOException {

        _upstream.close();

    }

    /**
     Calls {@link InputStream#mark(int)} on our wrapped input stream.
     <p>Using the mark and reset facility on a stream whose data is being check-summed seems
     like a pretty silly idea but who are we to judge.</p>
     @param readlimit passed to our call to our wrapped input stream's {@link InputStream#mark(int)} method.
     */

    @Override
    public void mark( int readlimit ) {

        _upstream.mark( readlimit );

    }

    /**
     Determine if this class supports the mark and reset facility.
     <p>Using the mark and reset facility on a stream whose data is being check-summed seems
     like a pretty silly idea but who are we to judge so this method just calls
     {@link InputStream#markSupported()} on our wrapped input stream and returns the result.</p>
     @return whatever our call to our wrapped input stream's {@code InputStream#markSupported()} returned.
     */

    @Override
    public boolean markSupported() {

        return false;

    }

    /**
     Read one byte from the wrapped {@link InputStream}.
     <p>Calls {@link InputStream#read} on the input stream provided to our constructor.
     If a {@link MessageDigest} instance was provided to our constructor and
     if the just mentioned call to {@link InputStream#read} returns a non-negative value then it is
     passed to the {@code MessageDigest}'s {@link MessageDigest#update(byte)} method.
     @return the {@code} int value returned by our call to our wrapped input stream's {@code read()} method.
     @throws IOException if something goes wrong during the call to our wrapped input stream's {@code read()} method.
     */

    @Override
    public int read() throws IOException {

        int rval = _upstream.read();
        if ( _messageDigest != null && rval >= 0 ) {

            _messageDigest.update( (byte) rval );

        }

        return rval;

    }

    /**
     Read bytes into an from the wrapped {@link InputStream}.
     <p>Calls {@link InputStream#read(byte[])}, with the {@code byte} array provided to this method,
     on the input stream provided to our constructor.
     If a {@link MessageDigest} instance was provided to our constructor and
     if the just mentioned call to {@link InputStream#read(byte[])} actually reads any data then the data is
     passed to the {@code MessageDigest}'s {@link MessageDigest#update(byte[],int,int)} method.
     @return the {@code} int value returned by our call to our wrapped input stream's {@code read()} method.
     @throws IOException if something goes wrong during the call to our wrapped input stream's {@code read(byte[])} method.
     */

    @Override
    public int read( @NotNull final byte[] b ) throws IOException {

        int rval = _upstream.read( b );

        if ( _messageDigest != null && rval > 0 ) {

            _messageDigest.update( b, 0, rval );

        }

        return rval;

    }

    /**
     Read bytes into a {@code byte} array from the wrapped {@link InputStream}.
     <p>Calls {@link InputStream#read(byte[],int,int)}, with the same parameters provided to this method,
     on the input stream provided to our constructor.
     If a {@link MessageDigest} instance was provided to our constructor and
     if the just mentioned call to {@link InputStream#read(byte[],int,int)} actually reads any data then the data is
     passed to the {@code MessageDigest}'s {@link MessageDigest#update(byte[],int,int)} method.
     @return the {@code} int value returned by our call to our wrapped input stream's {@code read()} method.
     @throws IOException if something goes wrong during the call to our wrapped input stream's {@code read(byte[],int,int)} method.
     */

    @Override
    public int read( @NotNull final byte[] b, int off, int len ) throws IOException {

        int rval = _upstream.read( b, off, len );

        if ( _messageDigest != null && rval > 0 ) {

            _messageDigest.update( b, off, rval );

        }

        return rval;

    }

    /**
     Calls {@link InputStream#reset()} on our wrapped input stream.
     <p>Using the mark and reset facility on a stream whose data is being check-summed seems
     like a pretty silly idea but who are we to judge.</p>
     @throws IOException if something goes wrong during the call to our wrapped input stream's {@code reset()} method.
     */

    @Override
    public void reset() throws IOException {

        _upstream.reset();

    }

    /**
     Calls {@link InputStream#skip(long)} on our wrapped input stream and returns the result.
     <p>Using this method on a stream whose data is being check-summed seems
     like a pretty silly idea but who are we to judge.</p>
     @param n the number of bytes that we are supposed to skip.
     See the JavaDocs for the particular {@link InputStream} derivative that we're wrapping for more info.
     @return whatever the call to our warpped input stream's {@code skip(long)} instance returned.
     @throws IOException if something goes wrong during the call to our wrapped input stream's {@code skip(long)} method.
     */

    @SuppressWarnings("RedundantThrows")
    @Override
    public long skip( long n ) throws IOException {

        // Just pass on the whole skip thing as it makes computing a checksum rather farcical.

        return 0;

    }

}
