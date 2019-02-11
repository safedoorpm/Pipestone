/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.db.PostgresConnection;
import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingUnPackedEntityGroup;
import com.obtuse.util.gowing.p2a.StdGowingPacker;
import com.obtuse.util.gowing.p2a.StdGowingUnPacker;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

/**
 A sometimes motley collection of utility methods which I have found useful.
 */

@SuppressWarnings({ "UnusedDeclaration", "SameParameterValue" })
public class ObtuseUtil {

    private static final Pattern HEX_STRING_PATTERN = Pattern.compile( "([0-9a-f][0-9a-f])*" );
    private static boolean s_traceOnly = false;

    /**
     This is a class of utility methods - no point letting anyone instantiate instances.
     */

    private ObtuseUtil() {
        super();

    }

    /**
     Detect if we are running within the Java debugger.
     <p/>Be very very careful! Code that behaves differently in a debugger is, obviously, difficult to debug!!!
     <p/>This trick found at <a href="http://www.rgagnon.com/javadetails/java-detect-if-running-in-debug-mode.html">{@code
    http://www.rgagnon.com/javadetails/java-detect-if-running-in-debug-mode.html}</a>

     @return {@code true} if the current JVM is under the control of the Java debugger; {@code false} otherwise.
     */

    public static boolean inJavaDebugger() {

        boolean isDebug =
                java.lang.management.ManagementFactory.getRuntimeMXBean().
                        getInputArguments().toString().indexOf( "-agentlib:jdwp" ) > 0;

        return isDebug;

    }

    public static @Nullable String extractKeywordValueSemiColon( final String url, final String keyword ) {

        String wrappedURL = ";" + url;
        String wrappedKeyword = ";" + keyword + "=";
        int keywordOffset = wrappedURL.indexOf( wrappedKeyword );
        if ( keywordOffset < 0 ) {

            Logger.logErr( "unable to find keyword \"" + keyword + "\" in URL " + url );

            return null;

        }

        int valueOffset = keywordOffset + wrappedKeyword.length();
        String valueInURL = wrappedURL.substring( valueOffset );
        int endValueOffset = valueInURL.indexOf( ";" );
        if ( endValueOffset < 0 ) {

            Logger.logErr( "unable to find end of value for keyword \"" + keyword + "\" in URL " + url );

            return null;

        }

        return valueInURL.substring( 0, endValueOffset + 1 );

    }

    /**
     Generate a hexadecimal string representing a specified long value and then strip off the unnecessary leading zeros.
     If the specified long value was equal to zero then the return string will contain the single character <tt>0</tt>.

     @param value the long value to be converted to a minimal hex string.
     @return the converted long value.
     */

    public static @NotNull String getMinimalHexValue( final long value ) {

        String original = hexvalue( value );
        int off = 0;
        char[] originalChars = original.toCharArray();
        while ( off < 15 && originalChars[off] == '0' ) {
            off += 1;
        }

        return original.substring( off );

    }

    /**
     Decodes the percent encoding scheme. <br/> For example: "an+example%20string" -> "an example string"

     @param str the string to be decoded.
     @return the decoded string.
     */

    public static String decodePercent( final String str ) {

        try {

            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < str.length(); i++ ) {

                char c = str.charAt( i );
                switch ( c ) {

                    case '+':
                        sb.append( ' ' );
                        break;

                    case '%':
                        //noinspection MagicNumber
                        sb.append( (char)Integer.parseInt( str.substring( i + 1, i + 3 ), 16 ) );
                        i += 2;
                        break;

                    default:
                        sb.append( c );
                        break;

                }

            }

            return new String( sb.toString().getBytes() );

        } catch ( Exception e ) {

            throw new IllegalArgumentException( "ERROR: Bad percent-encoding.", e );

        }

    }

    /**
     Determine if {@code [aStart,aLength)} overlaps with {@code [bStart,bEnd)}.
     <p/>IMPORTANT: note that the two regions are closed on the left and open on the right.
     Consequently, {@code [10,20)} does NOT overlap with {@code [20,30)} because {@code 20} is NOT included in the first
     region even though it is included in the second.

     @param aStart start of the first region.
     @param aEnd   one pixel past the end of the first region.
     @param bStart start of the second region.
     @param bEnd   one pixel past the end of the second region.
     @return {@code true} if the regions overlap; {@code false} otherwise. Note that a region is considered to be empty if
     its start and end are equal (empty regions do not overlap with any region).
     @throws IllegalArgumentException if {@code aStart > aEnd} or {@code bStart > bEnd}.
     */

    public static boolean overlapsBounds( final int aStart, final int aEnd, final int bStart, final int bEnd ) {

        if ( aStart > aEnd ) {

            throw new IllegalArgumentException( "ObtuseUtil.overlaps:  aStart=" + aStart + " is greater than aEnd=" + aEnd );

        }

        if ( bStart > bEnd ) {

            throw new IllegalArgumentException( "ObtuseUtil.overlaps:  bStart=" + bStart + " is greater than bEnd=" + bEnd );

        }

        return aEnd > bStart && bEnd > aStart;

    }

    /**
     Determine if the {@code aLength} sized region starting at {@code aStart} overlaps with the {@code bLength} sized
     region starting at {@code bStart}.
     <p/>A few notes are in order:
     <ol><li>the {@code length} sized region starting at {@code start} is exactly equivalent to a {@code -length} sized
     region starting at {@code start-length}</li>
     <li>assuming that {@code length} is non-negative, the {@code length} sized region starting at {@code start} is
     equivalent to the region {@code [start,start+length)}
     (expressed in the traditional way of describing closed and open ended regions)</li>
     <li>zero length regions do not overlap with anything</li>
     <li>assuming that LEN is non-negative, a LEN byte region starting at START does not overlap with any non-negative
     length region starting at START+LEN;
     for example, the {@code 3} units long region starting at {@code 10} does not overlap with the {@code 5} units long
     region starting at {@code 13}</li>
     </ol>

     @param aStart  start of the first region.
     @param aLength the length of the first region.
     @param bStart  start of the second region.
     @param bLength the length of the second region.
     @return {@code true} if the regions overlap; {@code false} otherwise. Note that a region is considered to be empty if
     its start and end are equal (empty regions do not overlap with any region).
     @throws IllegalArgumentException if {@code aStart} > {@code aLength} or {@code bStart > {@code bLength}}.
     */

    public static boolean overlapsLength( final int aStart, final int aLength, final int bStart, final int bLength ) {

        if ( aLength <= 0 ) {

            return aLength != 0 && overlapsLength( aStart - aLength, -aLength, bStart, bLength );

        }

        if ( bLength <= 0 ) {

            return bLength != 0 && overlapsLength( aStart, aLength, bStart - bLength, -bLength );

        }

        int aEnd = aStart + aLength;
        int bEnd = bStart + bLength;

        return aEnd > bStart && bEnd > aStart;

    }

    /**
     Determine if messages passed to {@link #report(String)} are logged and traced or just traced.

     @param traceOnly {@code true} if messages passed to {@link #report(String)} are to be logged and traced; {@code
     false} if they are to be just traced.
     */

    public static void setTraceOnlyMode( final boolean traceOnly ) {

        s_traceOnly = traceOnly;

    }

    /**
     Determine if trace-only mode is enabled.

     @return {@code true} if trace-only mode is enabled; {@code false} otherwise.
     */

    public static boolean isTraceOnlyModeEnabled() {

        return s_traceOnly;

    }

    /**
     Depending on the setting of trace-only mode, either log and trace or just trace a message.
     <p/>Messages are logged and traced by passing them to {@link Logger#logMsg(String)} (which implicitly also passes
     them to {@link Trace#event(String)}).
     Messages are just traced by passing them directly to {@code Trace.event(String)}.
     <p/>See {link #setTraceOnlyMode(boolean)} for more information.

     @param msg the message in question.
     */

    public static void report( final String msg, final Throwable e ) {

        if ( s_traceOnly ) {

            Trace.event( msg );

        } else if ( e == null ) {

            Logger.logMsg( msg );

        } else {

            Logger.logErr( msg, e );

        }

    }

    /**
     Depending on the setting of trace-only mode, either log and trace or just trace a message.
     <p/>Messages are logged and traced by passing them to {@link Logger#logMsg(String)} (which implicitly also passes
     them to {@link Trace#event(String)}).
     Messages are just traced by passing them directly to {@code Trace.event(String)}.
     <p/>See {link #setTraceOnlyMode(boolean)} for more information.

     @param msg the message in question.
     */

    public static void report( final String msg ) {

        if ( s_traceOnly ) {

            Trace.event( msg );

        } else {

            Logger.logMsg( msg );

        }

    }

    /**
     Format a count/size value with proper pluralization.
     <p>This method eases the task of formatting a value with proper pluralization.
     A few examples should get the point across:</p>
     <blockquote>
     {@code formatCount( 0, "entry", "entries" )} yields {@code "2 entries"}
     <br>{@code formatCount( 1, "fish", "fishes" )} yields {@code "1 fish"}
     <br>{@code formatCount( 2, "entry", "entries" )} yields {@code "2 entries"}
     </blockquote>
     Specifically, a count value of 1 yields a result using the {@code singular} term
     whereas any other count value yields a result using the {@code plural} term.

     @param count    the count/size value.
     @param singular the phrase to postpend the count with if it is equal to 1.
     @param plural   the phrase to postpend the count with if it is not equal to 1.
     @return the formatted value.
     @deprecated see {@link #pluralize(long,String,String)}
     */

    @Contract(pure = true)
    @NotNull
    @Deprecated
    public static String formatCount(
            final int count,
            final @NotNull String singular,
            final @NotNull String plural
    ) {

        return "" + count + " " + ( count == 1 ? singular : plural );

    }

    /**
     Format a count/size value with proper pluralization.
     <p>Exactly equivalent to {@code formatCount( count, "element", "elements" )}</p>

     @param count the count/size.
     @return the formatted value.
     @deprecated see {@link #pluralize(long,String,String)}
     */

    @Contract(pure = true)
    @NotNull
    @Deprecated
    public static String formatCount( final int count ) {

        return pluralize( count, "element" );

    }

    public static void getGrumpy(
            final @NotNull String methodName,
            final @NotNull String taskColloquialName,
            final @NotNull String entityColloquialName,
            final @NotNull Class<?> expectedClass,
            @Nullable final Object entity
    ) {

        String whatItIs =
                entity == null ? "null" : "a " + entity.getClass().getCanonicalName();

        throw new IllegalArgumentException(
                methodName + ":  unable to " + taskColloquialName + " - " +
                entityColloquialName + " is supposed to be a " + expectedClass.getCanonicalName() + " but it is " + whatItIs
        );

    }

    public static void mumbleQuietly(
            final @NotNull String methodName,
            final @NotNull String taskColloquialName,
            final @NotNull String entityColloquialName,
            final @NotNull Class<?> expectedClass,
            @Nullable final Object entity
    ) {

        String whatItIs =
                entity == null ? "null" : "a " + entity.getClass().getCanonicalName();

        Logger.logErr(
                methodName + ":  unable to " + taskColloquialName + " - " +
                entityColloquialName + " is supposed to be a " + expectedClass.getCanonicalName() + " but it is " + whatItIs
        );

    }

    /**
     A derivative of the {@link Hashtable} whose instances start out mutable but can be made immutable upon request (there
     is no
     mechanism provided to make an immutable instance mutable again).
     <p/>This class is probably not perfectly immutable as it is a fair bit simpler than the unmodifiable ones implemented in
     {@link java.util.Collections}. It is, I (Danny) believe, better than not having an immutable hashtable class at all.
     2017-01-07

     @param <K> The type of the keys for the hash table.
     @param <V> The type of the values in the hash table.
     */

    @SuppressWarnings("unchecked")
    private static class UnmodifiableHashtable<K, V> extends Hashtable<K, V> {

        private boolean _readonly;
        private final Hashtable<? extends K, ? extends V> _ht;

        private UnmodifiableHashtable( final Hashtable<? extends K, ? extends V> ht ) {

            super( ht );
            _ht = ht;
            _readonly = false;
        }

        private void makeReadonly() {

            _readonly = true;

        }

        public void clear() {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                super.clear();

            }

        }

        public @NotNull Set<Map.Entry<K, V>> entrySet() {

            if ( _readonly ) {

                return (Set)Collections.unmodifiableSet( _ht.entrySet() );

            } else {

                return super.entrySet();

            }

        }

        public @NotNull Set<K> keySet() {

            if ( _readonly ) {

                return Collections.unmodifiableSet( _ht.keySet() );

            } else {

                return super.keySet();

            }

        }

        public V put( final K key, final V value ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                return super.put( key, value );

            }

        }

        public void putAll( final Map<? extends K, ? extends V> t ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                super.putAll( t );

            }

        }

        public void rehash() {

            //noinspection StatementWithEmptyBody
            if ( _readonly ) {

                // Been there, done that.

            } else {

                super.rehash();

            }

        }

        public V remove( final Object key ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                return super.remove( key );

            }

        }

        public @NotNull Collection<V> values() {

            if ( _readonly ) {

                return Collections.unmodifiableCollection( _ht.values() );

            } else {

                return super.values();

            }

        }

        public String toString() {

            return "UnmodifiableHashTable( " + super.toString() + " )";

        }

    }

    /**
     Turn a serializable object into a byte array. No muss, no fuss, no exceptions!

     @param thing                  the object which is to be serialized.
     @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     @return the serialized form of <tt>thing</tt> or null if serialization fails.
     */

    @SuppressWarnings({ "SameParameterValue" })
    public static @Nullable byte[] getSerializedVersion( final Serializable thing, final boolean printStackTraceOnError ) {

        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;

        try {

            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( bos );
            oos.writeObject( thing );
            oos.flush();

            return bos.toByteArray();

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        } finally {

            ObtuseUtil.closeQuietly( oos );
            ObtuseUtil.closeQuietly( bos );

        }

    }

    /**
     Write a serializable object to a file. No muss, no fuss, no exceptions!

     @param thing                  the object which is to be serialized and written to the file.
     @param outputFile             the file that the serialized object is to be written to.
     @param printStackTraceOnError true if a stack trace is to be printed if anything goes wrong.
     @return true if it worked; false otherwise.
     */

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public static boolean writeSerializableObjectToFile(
            final Serializable thing,
            final File outputFile,
            final boolean printStackTraceOnError
    ) {

        try {

            ObtuseUtil.writeSerializableObjectToFile( thing, outputFile );

            return true;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                e.printStackTrace();

            }

            return false;

        }

    }

    /**
     Write a serializable object to a file.
     <p/>
     If it worked then nothing is returned. If it failed then an exception is thrown.

     @param thing      the object which is to be serialized and written to the file.
     @param outputFile the file that the serialized object is to be written to.
     @throws java.io.IOException if something goes wrong. The most likely reasons are (probably)
     that it was not possible to create the output file or some part of <tt>thing</tt> is not serializable.
     */

    public static void writeSerializableObjectToFile( final Serializable thing, final File outputFile )
            throws IOException {

        ObjectOutputStream oos = null;

        try {

            oos = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream( outputFile ) ) );
            oos.writeObject( thing );
            oos.flush();

        } finally {

            ObtuseUtil.closeQuietly( oos );

        }

    }

    /**
     Turn the contents of a file back into a serializable thing. The first serialized object from the file is read
     and de-serialized.

     @param inputFile the file that the serialized object is to be read from.
     @return the de-serialized object or <tt>null</tt> if something goes wrong.
     @throws java.io.IOException    if an I/O error occurs opening or reading from the file or a deserialization
     error occurs (see {@link java.io.ObjectInputStream#readObject()} for details on what can
     go wrong).
     @throws ClassNotFoundException if the class of a serialized object in the file cannot be found.
     */

    public static @Nullable Serializable recoverSerializedVersion( final File inputFile )
            throws IOException, ClassNotFoundException {

        return ObtuseUtil.recoverSerializedVersion( new BufferedInputStream( new FileInputStream( inputFile ) ) );

    }

    /**
     Turn the contents of a file back into a serializable thing. The first serialized object from the file is read
     and de-serialized.

     @param inputFile              the file that the serialized object is to be read from.
     @param printStackTraceOnError true if a stack trace is to be printed if anything goes wrong.
     @return the de-serialized object. If something goes wrong then <tt>null</tt> is returned. Note that it is possible to
     serialize a null pointer which means that de-serializing can legitimately yield a null pointer.
     Use {@link #recoverSerializedVersion(File)} if this is a concern as it will throw an exception of the
     de-serialization fails.
     */

    public static Serializable recoverSerializedVersion( final File inputFile, final boolean printStackTraceOnError ) {

        try {

            return ObtuseUtil.recoverSerializedVersion( new BufferedInputStream( new FileInputStream( inputFile ) ) );

        } catch ( ClassNotFoundException | IOException e ) {

            if ( printStackTraceOnError ) {

                e.printStackTrace();

            }

            return null;

        }

    }

    /**
     Turn a byte array back into a serializable thing. Throws an exception if something goes wrong.

     @param sv the serialized form of the object.
     @return the de-serialized object or null if de-serialization fails.
     @throws java.io.IOException    if an I/O error occurs reading from the {@link java.io.InputStream} or a deserialization
     error occurs (see {@link java.io.ObjectInputStream#readObject()} for details on what can
     go wrong).
     @throws ClassNotFoundException if the class of a serialized object in the {@link java.io.InputStream} cannot be found.
     <p/>
     This method was marked as deprecated. I don't know why so I have disabled the deprecation marking.
     Danny 2017-01-07
     at-deprecated Use {@link #recoverSerializedVersion(java.io.File)} or
     {@link #recoverSerializedVersion(java.io.InputStream)} instead.
     */

    public static Serializable recoverSerializedVersion( final byte@NotNull[] sv )
            throws ClassNotFoundException, IOException {

        ByteArrayInputStream bis = null;

        try {

            bis = new ByteArrayInputStream( sv );

            return ObtuseUtil.recoverSerializedVersion( bis );

        } finally {

            ObtuseUtil.closeQuietly( bis );

        }

    }

    /**
     Turn an {@link java.io.InputStream} back into a serializable thing. The next serialized object from the stream is read
     and de-serialized. The stream is closed when this operation completes.

     @param is the input stream that the serialized object is to be read from.
     @return the de-serialized object or null if something goes wrong.
     @throws java.io.IOException    if an I/O error occurs reading from the {@link java.io.InputStream} or a deserialization
     error occurs (see {@link java.io.ObjectInputStream#readObject()} for details on what can
     go wrong).
     @throws ClassNotFoundException if the class of a serialized object in the {@link java.io.InputStream} cannot be found.
     */

    private static Serializable recoverSerializedVersion( final InputStream is )
            throws ClassNotFoundException, IOException {

        ObjectInputStream ois = null;
        try {

            ois = new ObjectInputStream( is );

            return (Serializable)ois.readObject();

        } finally {

            ObtuseUtil.closeQuietly( ois );

        }

    }

    /**
     Turn a byte array back into a serializable thing. Prints a stack trace and returns null if something goes wrong.

     @param sv                     the serialized form of the object.
     @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     @return the de-serialized object. If something goes wrong then <tt>null</tt> is returned. Note that it is possible to
     serialize a null pointer which means that de-serializing can legitimately yield a null pointer.
     Use {@link #recoverSerializedVersion(byte[])} if this is a concern as it will throw an exception of the
     de-serialization fails.
     */

    public static Serializable recoverSerializedVersion(
            final byte[] sv,
            final boolean printStackTraceOnError
    ) {

        ByteArrayInputStream bis = new ByteArrayInputStream( sv );
        try {

            return ObtuseUtil.recoverSerializedVersion( bis, printStackTraceOnError );

        } finally {

            ObtuseUtil.closeQuietly( bis );

        }

    }

    /**
     Turn an {@link java.io.InputStream} back into a serializable thing. The next serialized object from the stream is read
     and de-serialized. The stream is closed when this call completes.

     @param is                     the input stream that the serialized object is to be read from.
     @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     @return the de-serialized object. If something goes wrong then <tt>null</tt> is returned. Note that it is possible to
     serialize a null pointer which means that de-serializing can legitimately yield a null pointer.
     Use {@link #recoverSerializedVersion(InputStream)} if this is a concern as it will throw an exception of the
     de-serialization fails.
     */

    public static Serializable recoverSerializedVersion( final InputStream is, final boolean printStackTraceOnError ) {

        ObjectInputStream ois = null;

        try {

            ois = new ObjectInputStream( is );
            Serializable thing = (Serializable)ois.readObject();

            return thing;

        } catch ( Exception e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        } finally {

            ObtuseUtil.closeQuietly( ois );
            ObtuseUtil.closeQuietly( is );

        }

    }

    /**
     Read the contents of a file into a byte array without needing to worry about exceptions.

     @param fname                  the name of the file to be read.
     @param maxLength              the maximum number of bytes to read (if the file is longer than this then the
     excess data is silently not returned).
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     A zero-length byte array is returned if the file exists and is readable but contains no data.
     */

    public static byte[] readEntireFile( final String fname, final int maxLength, final boolean printStackTraceOnError ) {

        if ( fname == null ) {

            return null;

        }

        return ObtuseUtil.readEntireFile( new File( fname ), maxLength, printStackTraceOnError );

    }

    /**
     Read the contents of a file into a byte array without needing to worry about exceptions.

     @param file                   the file to be read.
     @param maxLength              the maximum number of bytes to read (if the file is longer than this then the
     excess data is silently not returned).
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     A zero-length byte array is returned if the file exists and is readable but contains no data.
     */

    public static byte[] readEntireFile( final File file, final int maxLength, final boolean printStackTraceOnError ) {

        if ( file == null ) {

            return null;

        }

        FileInputStream fs = null;
        try {

            fs = new FileInputStream( file );

            byte[] contents = ObtuseUtil.readEntireStream( fs, maxLength, printStackTraceOnError );

            return contents;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

/*
            e.printStackTrace();
            System.err.println( "ObtuseUtil:readEntireFile - unable to read contents of \""
                                + fname + "\" - bye!" );
            System.exit( 1 );
            return null;    // keep the compiler happy.
*/

        } finally {

            ObtuseUtil.closeQuietly( fs );

        }

    }

    /**
     Read the contents of an {@link java.io.InputStream} into a byte array and return the byte array without needing to worry
     about exceptions.

     @param is                     the {@link java.io.InputStream} to read the data from.
     @param maxLength              the maximum number of bytes to read (if the stream's contents are longer than this
     then the excess data is left in the stream).
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     A zero-length byte array is returned if the stream contains no data.
     */

    public static byte[] readEntireStream(
            final InputStream is, final int maxLength, final boolean printStackTraceOnError
    ) {

        if ( is == null ) {

            return null;

        }

        try {

            byte[] tmp = new byte[maxLength];
            int actualLen = is.read( tmp );
            if ( actualLen <= 0 ) {

                return new byte[0];

            }

            byte[] contents = new byte[actualLen];
            System.arraycopy( tmp, 0, contents, 0, actualLen );

            return contents;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;    // keep the compiler happy.

        }

    }

    /**
     Write the contents of a byte array into a file without needing to worry about exceptions.

     @param bytes                  the byte array to be written.
     @param fname                  the name of the file to be written.
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return true if the operation succeeded and false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToFile( final byte@NotNull[] bytes, final String fname, final boolean printStackTraceOnError ) {

        return ObtuseUtil.writeBytesToFile( bytes, new File( fname ), printStackTraceOnError );

    }

    /**
     Write the contents of a byte array into a file without needing to worry about exceptions.

     @param bytes                  the byte array to be written.
     @param file                   the file to be written.
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return true if the operation succeeded and false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToFile( final byte@NotNull[] bytes, final File file, final boolean printStackTraceOnError ) {

        try ( FileOutputStream fs = new FileOutputStream( file ) ) {

            boolean rval = ObtuseUtil.writeBytesToStream( bytes, fs, printStackTraceOnError );

            return rval;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return false;

        }

    }

    /**
     Append the contents of a byte array into a file without needing to worry about exceptions.

     @param bytes                  the byte array to be written.
     @param file                   the file to be written.
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return true if the operation succeeded and false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean appendBytesToFile( final byte@NotNull[] bytes, final @NotNull File file, final boolean printStackTraceOnError ) {

        try ( FileOutputStream fs = new FileOutputStream( file, true ) ) {

            boolean rval = ObtuseUtil.writeBytesToStream( bytes, fs, printStackTraceOnError );

            return rval;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return false;

        }

    }

    /**
     Write the contents of a byte array to an {@link java.io.OutputStream} without needing to worry about exceptions.

     @param bytes                  the byte array to be written to the stream.
     @param os                     the {@link java.io.OutputStream} to write the byte array to.
     @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     @return true if it worked, false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToStream(
            final byte[] bytes,
            final OutputStream os,
            final boolean printStackTraceOnError
    ) {

        try {

            os.write( bytes );

            return true;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return false;

        }

    }

    /**
     Copy the contents of a file to a new file.
     <p>The new file must not exist when this method is called.</p>
     @param inputFile the (existing) file to be copied from.
     @param outputFile the (non-existent) file to be copied to.
     @param tracebackOnError print traceback to {@code stderr} on error if {@code true}; print nothing on error if {@code false}.
     @return {@code true} if it worked; {@code false} if it failed.
     */

    public static boolean copyFile( final @NotNull File inputFile, final @NotNull File outputFile, final boolean tracebackOnError ) {

        InputStream is = null;
        OutputStream os = null;
        boolean rval;
        boolean removeOutputFile = false;
        try {

            if ( outputFile.exists() ) {

                throw new IOException( "" + outputFile + " (File already exists)" );

            }

            removeOutputFile = true;

            is = new FileInputStream( inputFile );
            os = new FileOutputStream( outputFile );

            long totalCopied = 0;
            byte[] buffer = new byte[64 * 1024];
            int inLength;
            while ( ( inLength = is.read( buffer ) ) > 0 ) {

                os.write( buffer, 0, inLength );
                totalCopied += inLength;

            }

            Logger.logMsg( "wrote a total of " + totalCopied + " bytes" );

            rval = true;

        } catch ( IOException e ) {

            if ( tracebackOnError ) {

                Logger.logErr( "ObtuseUtil.copyFile( \"" + inputFile + "\", \"" + outputFile + "\" ):  " + e.getMessage(), e );

            }

            if ( removeOutputFile ) {

                //noinspection ResultOfMethodCallIgnored
                outputFile.delete();

            }

            rval = false;

        } finally {

            closeQuietly( is );
            closeQuietly( os );

        }

        return rval;

    }

    /**
     Get the size of the serialized version of an object.

     @param thing the object which is to be serialized so that the length of its serialized form can be returned (the
     serialized form of the object is discarded - it is often much more sensible to just serialize the
     object using {@link #getSerializedVersion} and then just get the length of the returned byte
     array).
     @return the length of the serialized form of the object.
     */

    public static int getSerializedSize( final Serializable thing ) {

        byte[] sv = ObtuseUtil.getSerializedVersion( thing, false );
        if ( sv == null ) {

            return 0;

        } else {

            return sv.length;

        }

    }

    /**
     Validate the number and type of arguments passed to an XML-RPC routine.

     @param methodName the method which is to be called.
     @param actual     the actual parameters which are to be passed to the method.
     @param expected   a description of the parameter types which are expected.
     @return null if arguments are valid and an appropriate error message otherwise.
     */

    @SuppressWarnings({ "RawUseOfParameterizedType", "CollectionDeclaredAsConcreteClass" })
    public static String validateArgs(
            final String methodName,
            @SuppressWarnings("rawtypes") final Vector actual,
            @SuppressWarnings("rawtypes") final Class[] expected
    ) {

        if ( actual.size() != expected.length ) {

            return methodName + " requires " + expected.length + " arguments but caller provided " +
                   actual.size();

        }

        for ( int i = 0; i < expected.length; i += 1 ) {

            Object arg = actual.elementAt( i );
            if ( arg == null ) {

                return "XMLRPC is not supposed to support null values but argument "
                       + ( i + 1 ) + " (one-origin) to " + methodName +
                       " is null (impressive! WRONG but still impressive!)";

            }

            if ( !arg.getClass().equals( expected[i] ) ) {

                return "argument " + ( i + 1 ) + " (one-origin) parameter to " + methodName +
                       " is of the wrong type (expected "
                       + expected[i] + ", received " + arg.getClass() + ")";

            }

        }

        return null;

    }

    /**
     Make a copy of an array.
     <p>Not really much value to this method vs {@link Arrays#copyOf(Object[], int)} but I can remember where
     to find this method whereas I always seem to fumble around a bit when trying to find where Java put
     the array utility methods.</p>
     @param array the array to be copied. If you pass {@code null} then you get {@code null}.
     @param <T> the type of element in the array.
     @return a copy of the array or null if {@code array} is {@code null}.
     */

    public static <T> T[] arrayCopy( @Nullable final T[] array ) {

        return array == null ? null : Arrays.copyOf( array, array.length );

    }

    public static byte[] arrayCopy( @Nullable final byte[] array ) {

        if ( array == null ) {

            return null;

        }
        byte[] copy = new byte[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static short[] arrayCopy( @Nullable final short[] array ) {

        if ( array == null ) {

            return null;

        }
        short[] copy = new short[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static int[] arrayCopy( @Nullable final int[] array ) {

        if ( array == null ) {

            return null;

        }
        int[] copy = new int[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static long[] arrayCopy( @Nullable final long[] array ) {

        if ( array == null ) {

            return null;

        }
        long[] copy = new long[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static float[] arrayCopy( @Nullable final float[] array ) {

        if ( array == null ) {

            return null;

        }
        float[] copy = new float[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static double[] arrayCopy( @Nullable final double[] array ) {

        if ( array == null ) {

            return null;

        }
        double[] copy = new double[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    public static boolean[] arrayCopy( @Nullable final boolean[] array ) {

        if ( array == null ) {

            return null;

        }
        boolean[] copy = new boolean[array.length];
        System.arraycopy(array, 0, copy, 0, array.length );
        return copy;

    }

    private static final DecimalFormat s_readable = new DecimalFormat( "###,###,###,###,###,###,##0" );

    public static String readable( final long value ) {

        return ObtuseUtil.s_readable.format( value );

    }

    private static String readable( final String sValue ) {

        int offset = sValue.indexOf( '.' );
        if ( offset < 0 ) {

            offset = sValue.length();

        }

        offset -= 3;
        String rval = sValue;
        int lastPlace = rval.startsWith( "-" ) ? 1 : 0;

        while ( offset > lastPlace ) {

            rval = rval.substring( 0, offset ) + ',' + rval.substring( offset );
            offset -= 3;

        }

        return rval;

    }

    public static String lpadReadable( final long value, final int w ) {

        return ObtuseUtil.lpad( ObtuseUtil.readable( value ), w );

    }

    public static String lpadReadable( final float value, final int w, final int digits ) {

        return ObtuseUtil.lpad( ObtuseUtil.readable( ObtuseUtil.lpad( value, 0, digits ) ), w );

    }

    public static String lpadReadable( final double value, final int w, final int digits ) {

        return ObtuseUtil.lpad( ObtuseUtil.readable( ObtuseUtil.lpad( value, 0, digits ) ), w );

    }

    public static String rpadReadable( final long value, final int w ) {

        return ObtuseUtil.rpad( ObtuseUtil.readable( value ), w );

    }

    public static String rpadReadable( final float value, final int w, final int digits ) {

        return ObtuseUtil.rpad( ObtuseUtil.readable( ObtuseUtil.lpad( value, 0, digits ) ), w );

    }

    public static String rpadReadable( final double value, final int w, final int digits ) {

        return ObtuseUtil.rpad( ObtuseUtil.readable( ObtuseUtil.lpad( value, 0, digits ) ), w );

    }

    public static String lpadReadable0( final float value, final int w, final int digits ) {

        return ObtuseUtil.lpad( ObtuseUtil.readable( ObtuseUtil.lpad0( value, 0, digits ) ), w );

    }

    public static String lpadReadable0( final double value, final int w, final int digits ) {

        return ObtuseUtil.lpad( ObtuseUtil.readable( ObtuseUtil.lpad0( value, 0, digits ) ), w );

    }

    public static String rpadReadable0( final float value, final int w, final int digits ) {

        return ObtuseUtil.rpad( ObtuseUtil.readable( ObtuseUtil.lpad0( value, 0, digits ) ), w );

    }

    public static String rpadReadable0( final double value, final int w, final int digits ) {

        return ObtuseUtil.rpad( ObtuseUtil.readable( ObtuseUtil.lpad0( value, 0, digits ) ), w );

    }

    public static String lpad( final float value, final int w, final int digits ) {

        return ObtuseUtil.lpad( (double)value, w, digits );

    }

    private static DecimalFormat[] s_cachedFormats = new DecimalFormat[1];

    public static String lpad( final double di, final int w, final int v ) {

        if ( Double.isNaN( di ) ) {

            return ObtuseUtil.lpad( "NaN", w );

        }

        if ( Double.isInfinite( di ) ) {

            if ( di < 0 ) {

                return ObtuseUtil.lpad( "-Inf", w );

            } else {

                return ObtuseUtil.lpad( "+Inf", w );

            }

        }

        if ( v >= ObtuseUtil.s_cachedFormats.length ) {

            DecimalFormat[] tmp = new DecimalFormat[v + 1];
            System.arraycopy( ObtuseUtil.s_cachedFormats, 0, tmp, 0, ObtuseUtil.s_cachedFormats.length );
            ObtuseUtil.s_cachedFormats = tmp;

        }

        if ( ObtuseUtil.s_cachedFormats[v] == null ) {

            StringBuilder formatBuilder = new StringBuilder( "0." );
            for ( int i = 0; i < v; i += 1 ) {

                formatBuilder.append( '#' );

            }

            String format = formatBuilder.toString();
            ObtuseUtil.s_cachedFormats[v] = new DecimalFormat( format );

        }

        return ObtuseUtil.lpad( ObtuseUtil.s_cachedFormats[v].format( di ), w );

    }

    private static DecimalFormat[] s_cachedZeroFormats = new DecimalFormat[1];

    public static String lpad0( final double di, final int w, final int v ) {

        if ( Double.isNaN( di ) ) {

            return ObtuseUtil.lpad( "NaN", w );

        }

        if ( v >= ObtuseUtil.s_cachedZeroFormats.length ) {

            DecimalFormat[] tmp = new DecimalFormat[v + 1];
            System.arraycopy( ObtuseUtil.s_cachedZeroFormats, 0, tmp, 0, ObtuseUtil.s_cachedZeroFormats.length );
            ObtuseUtil.s_cachedZeroFormats = tmp;

        }

        if ( ObtuseUtil.s_cachedZeroFormats[v] == null ) {

            StringBuilder formatBuilder = new StringBuilder( "0." );
            for ( int i = 0; i < v; i += 1 ) {

                formatBuilder.append( "0" );

            }

            String format = formatBuilder.toString();
            ObtuseUtil.s_cachedZeroFormats[v] = new DecimalFormat( format );

        }

        return ObtuseUtil.lpad( ObtuseUtil.s_cachedZeroFormats[v].format( di ), w );

    }

    /**
     Pad a string on the left to a specified width using a specified padding character.

     @param s the string to be padded.
     @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     itself is returned as-is).
     @param p the character to be used to pad the string on the left if it is shorter than <tt>w</tt>.
     @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String lpad( final String s, final int w, final char p ) {

        String str = s == null ? "null" : s;
        return ObtuseUtil.generatePaddingString( w, p, str ) + str;

    }

    public static String generatePaddingString( final int w, final char p, final String str ) {

        int padding = w - str.length();
        String padString;
        if ( padding > 0 ) {

            char[] padArray = new char[padding];
            for ( int i = 0; i < padding; i += 1 ) {

                padArray[i] = p;

            }

            padString = new String( padArray );

        } else {

            padString = "";

        }

        return padString;

    }

    /**
     Pad a string on the left and on the right to a specified width using blanks as the padding character.
     <p/>What happens conceptually is that while the string is narrower than the requested width,
     a single space character is added on an alternating basis on the left of the string or on the right of the string.
     For example, if the input string is two characters long and the requested width is five characters then the resulting
     string will start with two space characters followed by the two characters of the input string followed by one space
     character.
     <p/>
     Note that a call to this method using <tt>center( s, w )</tt> is equivalent to a call to
     {@link #center(String, int, char)}
     using <tt>center( s, w, '&nbsp;' )</tt>.

     @param s the string to be centered by appropriate padding on the left and the right.
     @param w the width (i.e. length) of the centered string (if the string is already longer than this then the string
     itself is returned as-is).
     @return the centered string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String center( final String s, final int w ) {

        return center( s, w, ' ' );

    }

    /**
     Pad a string on the left and on the right to a specified width using a specified padding character.
     <p/>What happens conceptually is that while the string is narrower than the requested width,
     a padding character is added on an alternating basis on the left of the string or on the right of the string.
     For example, if the input string is two characters long and the requested width is five characters then the resulting
     string will start with two padding characters followed by the two characters of the input string followed by one
     padding character.
     <p/>A call to this method using <tt>center( null, w )</tt> is treated as thought it was a call to this method using
     <tt>center( "null", w )</tt>.

     @param s  the string to be centered by appropriate padding on the left and the right (if <tt>s</tt> is <tt>null</tt>
     then the result
     is as though <tt>s</tt> were the six character string <tt>"null"</tt>).
     @param w  the width (i.e. length) of the centered string (if the string is already longer than this then the string
     itself is returned as-is).
     @param ch the padding character.
     @return the centered string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String center( final String s, final int w, final char ch ) {

        if ( s == null ) {

            return center( "null", w, ch );

        }

        int actualLength = s.length();
        if ( actualLength >= w ) {

            return s;

        }

        int leftPadding = ( w - actualLength + 1 ) >> 1;
        int rightPadding = ( w - actualLength ) >> 1;

        return generatePaddingString( leftPadding, ch, "" ) + s + generatePaddingString( rightPadding, ch, "" );

    }

    /**
     Pad a string on the left to a specified width using blanks as the padding character. Note that a call to this
     method using <tt>lpad( s, w )</tt> is equivalent to a call to {@link #lpad(String, int, char)} using <tt>lpad( s,
     w, '&nbsp;' )</tt>.

     @param s the string to be padded.
     @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     itself is returned as-is).
     @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String lpad( final String s, final int w ) {

        return ObtuseUtil.lpad( s, w, ' ' );

    }

    /**
     Pad the string representation of a long on the left to a specified width using a specified padding character.

     @param l the long whose string representation is to be padded.
     @param w the width (i.e. length) of the padded string representation (if the string representation is already
     longer than this then the string representation itself is returned as-is).
     @param p the character to be used to pad the string representation on the left if it is shorter than <tt>w</tt>.
     @return the padded string representation or the actual string representation if it is already at least as wide as
     <tt>w</tt>.
     */

    public static String lpad( final long l, final int w, final char p ) {

        return ObtuseUtil.lpad( "" + l, w, p );

    }

    /**
     Pad the string representation of a long on the left to a specified width using blanks as the padding character.
     Note that a call to this method using <tt>lpad( l, w )</tt> is equivalent to a call to {@link
    #lpad(long, int, char)} using <tt>lpad( w, w, '&nbsp;' )</tt>.

     @param l the long whose string representation is to be padded.
     @param w the width (i.e. length) of the padded string representation (if the string representation is already
     longer than this then the string representation itself is returned as-is).
     @return the padded string representation or the actual string representation if it is already at least as wide as
     <tt>w</tt>.
     */

    public static String lpad( final long l, final int w ) {

        return ObtuseUtil.lpad( "" + l, w );

    }

    /**
     Pad a string on the right to a specified width using a specified padding character.

     @param s the string to be padded.
     @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     itself is returned as-is).
     @param p the character to be used to pad the string on the right if it is shorter than <tt>w</tt>.
     @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String rpad( final String s, final int w, final char p ) {

        String str = s == null ? "null" : s;
        return str + ObtuseUtil.generatePaddingString( w, p, str );

    }

    /**
     Pad a string on the right to a specified width using blanks as the padding character. Note that a call to this
     method using <tt>lpad( s, w )</tt> is equivalent to a call to {@link #lpad(String, int, char)} using <tt>lpad( s,
     w, '&nbsp;' )</tt>.

     @param s the string to be padded.
     @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     itself is returned as-is).
     @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String rpad( final String s, final int w ) {

        return ObtuseUtil.rpad( s, w, ' ' );

    }

    /**
     Pad the string representation of a long on the right to a specified width using a specified padding character.

     @param l the long whose string representation is to be padded.
     @param w the width (i.e. length) of the padded string representation (if the string representation is already
     longer than this then the string representation itself is returned as-is).
     @param p the character to be used to pad the string representation on the right if it is shorter than
     <tt>w</tt>.
     @return the padded string representation or the actual string representation if it is already at least as wide as
     <tt>w</tt>.
     */

    public static String rpad( final long l, final int w, final char p ) {

        return ObtuseUtil.rpad( "" + l, w, p );

    }

    /**
     Pad the string representation of a long on the right to a specified width using blanks as the padding character.
     Note that a call to this method using <tt>lpad( l, w )</tt> is equivalent to a call to {@link
    #lpad(long, int, char)} using <tt>lpad( w, w, '&nbsp;' )</tt>.

     @param l the long whose string representation is to be padded.
     @param w the width (i.e. length) of the padded string representation (if the string representation is already
     longer than this then the string representation itself is returned as-is).
     @return the padded string representation or the actual string representation if it is already at least as wide as
     <tt>w</tt>.
     */

    public static String rpad( final long l, final int w ) {

        return ObtuseUtil.rpad( "" + l, w );

    }

    /**
     Replicate a string a specified number of times.
     For example, <tt>replicate( "hello", 3 )</tt> yields <tt>"hellohellohello"</tt>.

     @param str   the string to replicate.
     @param count the number of copies to be made.
     @return the replicated string.
     */

    public static String replicate( final String str, final int count ) {

        StringBuilder rval = new StringBuilder();
        for ( int i = 0; i < count; i += 1 ) {

            rval.append( str );

        }

        return rval.toString();

    }

    /**
     Return a String containing the hex representation of a long value. For example, <tt>hexvalue( 27L )</tt> yields
     <tt>"000000000000001b"</tt>. Note that the string is always 16 characters long.

     @param v the long value whose hex representation is to be returned.
     @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "MagicNumber" })
    public static String hexvalue( final long v ) {

        //noinspection UnnecessaryParentheses

        return ""
               + ObtuseUtil.hexvalue( (int)( ( v >> 32 ) & 0x00000000ffffffffL ) )
               + ObtuseUtil.hexvalue( (int)( v & 0x00000000ffffffffL ) );

    }

    /**
     Return a String containing the hex representation of an int value. For example, <tt>hexvalue( 27 )</tt> yields
     <tt>"0000001b"</tt>. Note that the string is always 8 characters long.

     @param v the int value whose hex representation is to be returned.
     @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "UnnecessaryParentheses", "MagicNumber" })
    public static String hexvalue( final int v ) {

        return ""
               + ObtuseUtil.hexvalue( (byte)( ( v >> 24 ) & 0xff ) )
               + ObtuseUtil.hexvalue( (byte)( ( v >> 16 ) & 0xff ) )
               + ObtuseUtil.hexvalue( (byte)( ( v >> 8 ) & 0xff ) )
               + ObtuseUtil.hexvalue( (byte)( v & 0xff ) );

    }

    /**
     Return a String containing the hex representation of a single byte value. For example, <tt>hexvalue( (byte)27
     )</tt> yields <tt>"1b"</tt>. Note that the returned string is always two characters long.

     @param v the byte value whose hex representation is to be returned.
     @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "UnnecessaryParentheses", "MagicNumber" })
    public static String hexvalue( final byte v ) {

        int high = ( v >> 4 ) & 0xf;
        int low = (int)v & 0xf;

        return ""
               + "0123456789abcdef".charAt( high )
               + "0123456789abcdef".charAt( low );

    }

    /**
     Return a String containing the hex representation of a single char value. For example, <tt>hexvalue( ' ' )</tt>
     yields <tt>"1b"</tt>.

     @param v the byte value whose hex representation is to be returned.
     @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "UnnecessaryParentheses", "MagicNumber" })
    public static String hexvalue( final char v ) {

        return ObtuseUtil.hexvalue( Character.toString( v ).getBytes() );

    }

    /**
     Convert a byte array to its hexadecimal representation. For example, <tt>hexvalue( new byte[] { 1, 10, 100 }
     )</tt> yields <tt>"010a64"</tt>. Note that the returned string always contains twice as many characters as the
     input array contains bytes.
     <p/>
     While this method uses a {@link StringBuffer} to avoid creating lots and lots of dead strings, it will still
     consume quite a bit of memory if the byte array is sufficiently large.

     @param bv the byte array to be converted.
     @return the hex representation of <tt>v</tt>.
     */

    public static String hexvalue( final byte@Nullable[] bv ) {

        if ( bv == null ) {

            return "null";

        }

        StringBuilder rval = new StringBuilder();
        for ( byte b : bv ) {

            rval.append( ObtuseUtil.hexvalue( b ) );

        }

        return rval.toString();

    }

    /**
     Convert a byte array to its hexadecimal representation. For example, <tt>hexvalue( new byte[] { 1, 10, 100 }
     )</tt> yields <tt>"010a64"</tt>. Note that the returned string always contains twice as many characters as the
     input array contains bytes.
     <p/>
     While this method uses a {@link StringBuffer} to avoid creating lots and lots of dead strings, it will still
     consume quite a bit of memory if the byte array is sufficiently large.

     @param bv  the byte array to be converted.
     @param off offset within the array to start converting at.
     @param len number of bytes to convert.
     @return the hex representation of <tt>v</tt>.
     */

    public static String hexvalue( final byte@Nullable[] bv, final int off, final int len ) {

        if ( bv == null ) {

            return "null";

        }

        StringBuilder rval = new StringBuilder();
        for ( int ix = off; ix < len; ix += 1 ) {

            byte b = bv[ix];
            rval.
                        append( "0123456789abcdef".charAt( ( b >> 4 ) & 0xf ) ).
                        append( "0123456789abcdef".charAt( b & 0xf ) );

        }

        return rval.toString();

    }

    /**
     Decode a string of hex digits as a byte array.
     */

    public static @NotNull byte[] decodeHexAsByteArray( final @NotNull String hexString ) {

        String hex = hexString.toLowerCase();

        Matcher m;
        synchronized ( HEX_STRING_PATTERN ) {

            m = HEX_STRING_PATTERN.matcher( hex );

        }

        if ( !m.matches() ) {

            throw new NumberFormatException( "not a hex string" );

        }

        byte[] rval = new byte[hex.length() >> 1];
        char[] hexChars = hex.toCharArray();
        int inputIx = 0;
        int outputIx = 0;
        while ( inputIx < hex.length() ) {

            char ch1 = hexChars[inputIx];
            if ( !( ch1 >= '0' && ch1 <= '9' || ch1 >= 'a' && ch1 <= 'f' ) ) {

                throw new NumberFormatException( "msb at offset " + inputIx + " (" + ch1 + ") is not a valid hex digit" );

            }

            inputIx += 1;

            char ch2 = hexChars[inputIx];
            if ( !( ch2 >= '0' && ch2 <= '9' || ch2 >= 'a' && ch2 <= 'f' ) ) {

                throw new NumberFormatException( "lsb at offset " + inputIx + " (" + ch2 + ") is not a valid hex digit" );

            }

            inputIx += 1;

            @SuppressWarnings("ConstantConditions") int highNibble =
                    ( ch1 >= '0' && ch1 <= '9' ) ? ch1 - '0' : 10 + ch1 - 'a';
            @SuppressWarnings("ConstantConditions") int lowNibble =
                    ( ch2 >= '0' && ch2 <= '9' ) ? ch2 - '0' : 10 + ch2 - 'a';
            rval[outputIx] = (byte)( ( highNibble << 4 ) | lowNibble );

            outputIx += 1;

        }

        return rval;

    }

    /**
     Capitalizes the first letter of the specified string.

     @param str the string whose first letter is to be capitalized.
     @return the capitalized string.
     @throws IndexOutOfBoundsException if the string is empty.
     */

    public static String capitalize( final @NotNull String str ) {

        return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );

    }

    /**
     Sleep for specified number of milliseconds without having to bother catching the InterruptedException potentially
     thrown by the regular sleep method.

     @param milliseconds the minimum number of milliseconds that the calling thread should be suspended for (the
     thread could conceivably be suspended for an arbitrary number of additional milliseconds
     depending upon far too many factors to enumerate here).
     */

    public static void safeSleepMillis( final long milliseconds ) {

        try {

            Thread.sleep( milliseconds );

        } catch ( InterruptedException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();

        }

    }

    /**
     Dump a byte array in hex - prints the contents of a byte array onto {@link System#out} in geek-readable form.

     @param data the byte array to be formatted and printed onto {@link System#out}.
     */

    @SuppressWarnings({ "MagicNumber" })
    public static void dump( final byte@NotNull[] data ) {

        for ( int offset = 0; offset < data.length; offset += 16 ) {

            StringBuilder rval = new StringBuilder( ObtuseUtil.hexvalue( offset ) ).append( " " );
            for ( int j = 0; j < 16; j += 1 ) {

                if ( j % 4 == 0 ) {

                    rval.append( ' ' );

                }

                if ( offset + j < data.length ) {

                    rval.append( ObtuseUtil.hexvalue( data[offset + j] ) );

                } else {

                    rval.append( "  " );

                }

            }

            rval.append( " *" );

            for ( int j = 0; j < 16 && offset + j < data.length; j += 1 ) {

                byte b = data[offset + j];
                //noinspection ImplicitNumericConversion
                if ( b < ' ' || b > '~' ) {

                    rval.append( '.' );

                } else {

                    rval.append( (char)b );

                }

            }

            Logger.logMsg( rval.append( "*" ).toString() );

        }

    }

    /**
     Escape ampersands and less-than characters in a string using HTML-style &amp;amp; and &amp;lt; constructs.

     @param str the string to be escaped.
     @return the escaped string.
     */

    public static String htmlEscape( final String str ) {

        String rval;
        String s = str;
        StringBuilder rvalBuilder = new StringBuilder();
        while ( true ) {

            int ix1 = s.indexOf( (int)'&' );
            int ix2 = s.indexOf( (int)'<' );
            if ( ix1 < 0 && ix2 < 0 ) {

                break;

            }

            int ix;
            if ( ix1 < 0 ) {

                ix = ix2;

            } else if ( ix2 < 0 ) {

                ix = ix1;

            } else {

                ix = ix1 < ix2 ? ix1 : ix2;

            }

            rvalBuilder.append( s, 0, ix );
            s = s.substring( ix );
            if ( s.startsWith( "&" ) ) {

                rvalBuilder.append( "&amp;" );

            } else {

                rvalBuilder.append( "&lt;" );

            }

            s = s.substring( 1 );

        }
        rval = rvalBuilder.toString();

        rval += s;

        return rval;

    }

    /**
     Close something while ignoring any {@link java.io.IOException}s.

     @param thing the thing to be closed (which can be null in which case nothing is done).
     */

    public static void closeQuietly( @Nullable final Closeable thing ) {

        try {

            if ( thing != null ) {

                thing.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( @Nullable final ServerSocket sock ) {

        try {

            if ( sock != null ) {

                sock.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( @Nullable final Socket sock ) {

        try {

            if ( sock != null ) {

                sock.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( @Nullable final ZipFile zipFile ) {

        try {

            if ( zipFile != null ) {

                zipFile.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( @Nullable final ResultSet rs ) {

        try {

            if ( rs != null ) {

                rs.close();

            }

        } catch ( SQLException e ) {

            Logger.logErr( "close of result set failed", e );

        }

    }

    public static void closeQuietly( @Nullable final PreparedStatement rs ) {

        try {

            if ( rs != null ) {

                rs.close();

            }

        } catch ( SQLException e ) {

            Logger.logErr( "close of prepared statement failed", e );

        }

    }

    public static void closeQuietly( @Nullable final PostgresConnection postgresConnection ) {

        try {

            if ( postgresConnection != null ) {

                postgresConnection.close();

            }

        } catch ( SQLException e ) {

            Logger.logErr( "close of PostgresConnection failed", e );

        }
    }

    /**
     A method which deliberately does nothing.
     Useful as a statement upon which to set a breakpoint.
     */

    @SuppressWarnings("EmptyMethod")
    public static void doNothing() {

    }

    /**
     Use double quotes to escape commas and quotes in the way that MS Excel seems to do it.
     <p/>
     Some examples are probably in order.
     <ul>
     <li>if the string is
     <blockquote><tt>hello world</tt></blockquote>
     then the output is the string itself (unchanged).
     <li>if the string is
     <blockquote><tt>hello,world</tt></blockquote>
     then the output is
     <blockquote><tt>"hello,world"</tt></blockquote>
     <li>if the string is
     <blockquote><tt>"hello" world</tt></blockquote>
     then the output is
     <blockquote><tt>"""hello"" world"</tt></blockquote>
     <li>if the string is
     <blockquote><tt>hello ",world"</tt></blockquote>
     then the output is
     <blockquote><tt>"hello "",world"""</tt></blockquote>
     </ul>
     Note that all the double quotes in the above examples actually appear in the strings.
     In other words, the above examples do <b>NOT</b> use double quotes to enclose strings.

     @param string the string to be enquoted.
     @return the original string if it does not contain quotes or commas; the enquoted string otherwise.
     @throws NullPointerException if <tt>string</tt> is null.
     */

    public static String enquoteToCSV( final String string ) {

        if ( !string.contains( "," ) && !string.contains( "\"" ) ) {

            return string;

        }

        StringBuilder rval = new StringBuilder( "\"" );
        int quoteOffset = -1;
        while ( true ) {

            int newQuoteOffset = string.indexOf( '"', quoteOffset + 1 );

            if ( newQuoteOffset < 0 ) {

                break;

            }

            rval.append( string, quoteOffset + 1, newQuoteOffset + 1 ).append( '"' );

            quoteOffset = newQuoteOffset;

        }

        return rval.append( string.substring( quoteOffset + 1 ) ).append( '"' ).toString();

    }

    /**
     Turn an arbitrary object into an enquoted for Java string.

     @param obj the object to be enquoted.
     @return the value generated by {@link #enquoteToJavaString}{@code ( String.valueOf( obj ) )}.
     */

    public static String enquoteJavaObject( @Nullable final Object obj ) {

        return obj == null ? "null" : enquoteToJavaString( String.valueOf( obj ) );

    }

    /**
     Turn a string into a properly enquoted Java string (without surrounding quotes) in a {@link StringBuilder}.
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"' etc into
     "\\n", "\\t", "\\\"" etc. For example, the 17 character Java string "hello\tthere\nworld" gets turned into
     the 19 character Java string "hello\\tthere\\nworld".

     @param string the string.
     @return the resulting enquoted string in a {@link StringBuilder} (if {@code string} is {@code null} or {@code "null"}
     then the return value is a {@link StringBuilder} equivalent to the string {@code "null"}).
     That two different input values yield the same result is pretty ugly. This is why this method is private.
     */

    private static StringBuilder enquoteJavaStringToNakedStringBuilder( @Nullable final String string ) {

        if ( string == null ) {

            return new StringBuilder( "null" );

        }

        StringBuilder rval = new StringBuilder();
        for ( char ch : string.toCharArray() ) {

            rval.append( enquoteToNakedJavaCharacter( ch ) );

        }

        return rval;

    }

    /**
     Turn a string into a properly enquoted Java string (with surrounding double quotes) in a {@link String}.
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"' etc into
     "\\n", "\\t", "\\\"" etc. For example, the 17 character Java string {@code "hello\tthere\nworld"} gets turned into
     the 21 character Java string {@code "\"hello\\tthere\\nworld\""}.

     @param string the string.
     @return the resulting enquoted string (if {@code string} is {@code null} then the return value is the string {@code
     "null"}).
     */

    public static String enquoteToJavaString( @Nullable final String string ) {

        return string == null ? "null" : enquoteToJavaStringBuilder( string ).toString();

    }

    /**
     Turn a string into a properly enquoted Java string (without surrounding quotes) in a {@link String}.
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"' etc into
     "\\n", "\\t", "\\\"" etc. For example, the 17 character Java string {@code "hello\tthere\nworld"} gets turned into
     the 19 character Java string {@code "hello\\tthere\\nworld"}.

     @param string the string.
     @return the resulting enquoted string (if {@code string} is {@code null} or {@code "null"} then the return value is
     the string {@code "null"}).
     That two different input values yield the same result is pretty ugly. This is why this method is private.
     */

    public static String enquoteToNakedJavaString( @Nullable final String string ) {

        if ( string == null ) {

            return "null";

        }

        return enquoteJavaStringToNakedStringBuilder( string ).toString();

    }

    /**
     Turn a string into a properly enquoted Java string (with surrounding double quotes) in a {@link String}.
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"', etc in the input
     string into
     "\\n", "\\t", "\\\"", etc in the output string. For example, the 17 character Java string {@code
    "hello\tthere\nworld"} gets turned into
     the 21 character Java string {@code "\"hello\\tthere\\nworld\""}.

     @param string the string.
     @return the resulting enquoted string (if {@code string} is {@code null} then the return value is a
     {@link StringBuilder} equivalent to the string {@code "null"};
     if {@code string} is the six character string {@code "null"} then the result is a {@link StringBuilder} equivalent to
     the string {@code "\"null\""}).
     */

    private static StringBuilder enquoteToJavaStringBuilder( @Nullable final String string ) {

        if ( string == null ) {

            return new StringBuilder( "null" );

        }

        return enquoteJavaStringToNakedStringBuilder( string ).insert( 0, '"' ).append( '"' );

    }

    public static class StringMapping {

        public final String from;

        public final String to;

        private StringMapping( String from, String to ) {

            super();

            this.from = from;
            this.to = to;

        }

        public String toString() {

            return enquoteToJavaString( from ) + "->" + enquoteToJavaString( to );

        }

    }

    public static class StringCharMapping {

        public final String from;

        public final char to;

        private StringCharMapping( String from, char to ) {

            super();

            this.from = from;
            this.to = to;

        }

        public String toString() {

            return enquoteToJavaString( from ) + "->" + enquoteToJavaCharacter( to );

        }

    }

    private static final StringCharMapping[] s_reverseJavaMappings = {
            new StringCharMapping( "\\b", '\b' ),
            new StringCharMapping( "\\n", '\n' ),
            new StringCharMapping( "\\r", '\r' ),
            new StringCharMapping( "\\t", '\t' ),
            new StringCharMapping( "\\\\", '\\' ),
            new StringCharMapping( "\\\'", '\'' ),
            new StringCharMapping( "\\\"", '"' )
    };

    /**
     Turn a char into a {@link String} containing a properly quoted version of the char (without surrounding quotes).
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"' etc into
     "\\n", "\\t", "\\\"", etc.

     @param ch the {@code char}.
     @return the resulting enquoted {@link String}.
     */

    public static String enquoteToNakedJavaCharacter( final char ch ) {

        switch ( ch ) {

            case '\b':
                return "\\b";

            case '\n':
                return "\\n";

            case '\r':
                return "\\r";

            case '\t':
                return "\\t";

            case '\\':
                return "\\\\";

            case '\'':
                return "\\\'";

            case '"':
                return "\\\"";

            default:
                return String.valueOf( ch );

        }

    }

    @Nullable
    public static String parseJavaString( final @NotNull String javaString ) {

        if ( "null".equals( javaString ) ) {

            return null;

        }

        if ( javaString.startsWith( "\"" ) && javaString.endsWith( "\"" ) ) {

            return parseNakedJavaString( javaString.substring( 1, javaString.length() - 1 ) );

        } else {

            throw new IllegalArgumentException(
                    "ObtuseUtil.parseJavaString:  string (" + javaString + ") is not surrounded by double quotes"
            );

        }

    }

    @NotNull
    public static String parseNonNullJavaString( final @NotNull String inputString ) {

        if ( "null".equals( inputString ) ) {

            throw new IllegalArgumentException(
                    "ObtuseUtil.parseNonNullJavaString:  input string describes a null string (inputString.equals(\"null\") == true)" );

        }

        String rval = parseJavaString( inputString );

        if ( rval == null ) {

            throw new HowDidWeGetHereError(
                    "ObtuseUtil.parseNonNullJavaString:  " +
                    "got a null string after we (should have) verified that we wouldn't - " +
                    "(enquoted) input string was " + enquoteToJavaString( inputString )
            );

        }

        return rval;

    }

    @NotNull
    public static String parseNakedJavaString( final @NotNull String nakedInputString ) {

        StringBuilder sb = new StringBuilder();
        int totalLength = nakedInputString.length();
        int off = 0;
        while ( off < nakedInputString.length() ) {

            char mapped = nakedInputString.charAt( off );
            String original = String.valueOf( mapped );

            for ( StringCharMapping scm : s_reverseJavaMappings ) {

                if ( nakedInputString.startsWith( scm.from, off ) ) {

                    if ( scm.from.equals( "\\\"" ) ) {

                        Logger.logErr( "ObtuseUtil.parseNakedJavaString:  DANGER Will Robinson!!!" );

                        doNothing();

                    }

                    original = scm.from;
                    mapped = scm.to;

                    break;

                }

            }

            off += original.length();

            sb.append( mapped );

        }

        return sb.toString();

    }

    /**
     Turn a char into a {@link String} containing a properly quoted version of the char (with surrounding single quotes).
     <p/>The main thing that this method does is that it turns special characters like '\n', '\t', '\"' etc into
     strings like "'\\n'", "'\\t'", "'\\\"'" etc.

     @param ch the {@code char}.
     @return the resulting enquoted {@link String}.
     */

    public static String enquoteToJavaCharacter( final char ch ) {

        return "'" + enquoteToNakedJavaCharacter( ch ) + "'";

    }

    public static MessageDigest getMD5MessageDigest() {

        MessageDigest md5Algorithm;

        try {

            md5Algorithm = MessageDigest.getInstance( "MD5" );

        } catch ( NoSuchAlgorithmException e ) {

            // There's no real risk that this can happen is there?

            throw new IllegalArgumentException( "This version of Java does not support MD5 checksums" );

        }

        return md5Algorithm;

    }

    public static @NotNull String computeMD5( final @NotNull byte[] data ) {

        byte[] digest = computeMD5bytes( data );

        return ObtuseUtil.hexvalue( digest );

    }

    public static byte[] computeMD5bytes( @NotNull final byte @NotNull [] data ) {

        MessageDigest md5Algorithm;
        md5Algorithm = getMD5MessageDigest();

        BufferedInputStream fis = null;

        md5Algorithm.reset();

        return md5Algorithm.digest( data );

    }

    public static @NotNull String computeMD5( final InputStream is )
            throws IOException {

        byte[] digest = computeMD5bytes( is );

        return ObtuseUtil.hexvalue( digest );

    }

    public static byte[] computeMD5bytes( final InputStream is ) throws IOException {

        MessageDigest md5Algorithm;
        md5Algorithm = getMD5MessageDigest();

        BufferedInputStream fis = null;

        try {

            md5Algorithm.reset();
            fis = new BufferedInputStream( is );

            //noinspection MagicNumber
            byte[] buffer = new byte[64*1024];
            while ( true ) {

                int rLen = fis.read( buffer );
                if ( rLen < 0 ) {

                    break;

                }

                md5Algorithm.update( buffer, 0, rLen );

            }

        } finally {

            ObtuseUtil.closeQuietly( fis );

        }

        return md5Algorithm.digest();

    }

    public static @NotNull String computeMD5( final File file )
            throws IOException {

        FileInputStream fis = new FileInputStream( file );
        try {

            return ObtuseUtil.computeMD5( fis );

        } finally {

            ObtuseUtil.closeQuietly( fis );

        }

    }

    public static int safeDivide( final int numerator, final int denominator ) {

        return denominator == 0 ? 0 : numerator / denominator;

    }

    public static int safeDivide( final int numerator, final int denominator, final int safeReturnValue ) {

        return denominator == 0 ? safeReturnValue : numerator / denominator;

    }

    public static long safeDivide( final long numerator, final long denominator ) {

        return denominator == 0 ? 0 : numerator / denominator;

    }

    public static long safeDivide( final long numerator, final long denominator, final long safeReturnValue ) {

        return denominator == 0 ? safeReturnValue : numerator / denominator;

    }

    public static double safeDivide( final double numerator, final double denominator ) {

        return denominator == 0.0 ? 0.0 : numerator / denominator;

    }

    public static double safeDivide( final double numerator, final double denominator, final double safeReturnValue ) {

        return denominator == 0.0 ? safeReturnValue : numerator / denominator;

    }

    public static float safeDivide( final float numerator, final float denominator ) {

        return denominator == 0.0f ? 0.0f : numerator / denominator;

    }

    public static float safeDivide( final float numerator, final float denominator, final float safeReturnValue ) {

        return denominator == 0.0f ? safeReturnValue : numerator / denominator;

    }

    /**
     Add the contents of an array to a collection and return the collection.
     <p/>Returning the collection facilitates certain constructs including:
     <blockquote>
     <tt>doit( ObtuseUtil.addAll( new LinkedList&lt;String>(), new String[] { "hello", "there", "world" } );</tt>
     </blockquote>

     @param collection  the collection to which things are to be added.
     @param newElements the new elements to add to the collection.
     @return the collection after the elements have been added.
     */

    @SafeVarargs
    public static <T> Collection<T> addAll(
            final Collection<T> collection,
            final T... newElements
    ) {

        Collections.addAll( collection, newElements );
        return collection;

    }

    /**
     Returns an unmodifiable view of the specified hash table.
     This method allows modules to provide users with "read-only" access to internal hash tables (including
     {@link java.util.Dictionary}s).
     Query operations on the returned hash table "read through" to the specified hash table,
     and attempts to modify the returned hash table, whether direct or via its collection views,
     result in an UnsupportedOperationException.
     <p/>This method is probably less than perfect in the sense that there are probably at least somewhat sneaky ways
     to modify the 'unmodifiable' hashtable that it returns. That said, it is far better than nothing.

     @param ht the hash table for which an unmodifiable view is to be returned.
     @return an unmodifiable view of the specified hash table.
     */

    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
    public static <K, V> Hashtable<K, V> unmodifiableHashtable( final Hashtable<? extends K, ? extends V> ht ) {

        UnmodifiableHashtable<K, V> unmodifiableHashtable = new UnmodifiableHashtable<>( ht );

        unmodifiableHashtable.makeReadonly();

        return unmodifiableHashtable;

    }

    private static void doString( @Nullable final String input ) {

        Logger.logMsg( "input string is " + ( input == null ? "null" : ( "<<<" + input + ">>>" ) ) );

        String nakedOutput = enquoteToNakedJavaString( input );
        String output = enquoteToJavaString( input );
        String parsed = parseJavaString( output );
        StringBuilder nsb = enquoteJavaStringToNakedStringBuilder( input );
        StringBuilder sb = enquoteToJavaStringBuilder( input );

        if (
                input == null
                        ?
                        parsed != null
                        :
                        !input.equals( parsed )
                ) {

            Logger.logErr( "got unexpected output=" + output + ", parsed=" + parsed + " from input=<<<" + input + ">>>" );

            doNothing();

        }

        Logger.logMsg( "" +
                       ( input == null ? "null " : ( "" + input.length() + " char " ) ) +
                       "input string becomes " +
                       nakedOutput.length() +
                       " char naked output string " +
                       nakedOutput );
        Logger.logMsg( "" +
                       ( input == null ? "null " : ( "" + input.length() + " char " ) ) +
                       "input string becomes " +
                       output.length() +
                       " char output string " +
                       output );
        Logger.logMsg( "" +
                       ( input == null ? "null " : ( "" + input.length() + " char " ) ) +
                       "input string becomes " +
                       ( parsed == null ? "null " : ( "" + parsed.length() + " char " ) ) +
                       "parsed string" +
                       ( parsed == null ? "" : parsed ) );
        Logger.logMsg( "" +
                       ( input == null ? "null " : ( "" + input.length() + " char " ) ) +
                       "input string becomes " +
                       nsb.length() +
                       " char naked output StringBuilder " +
                       nsb );
        Logger.logMsg( "" +
                       ( input == null ? "null " : ( "" + input.length() + " char " ) ) +
                       "input string becomes " +
                       sb.length() +
                       " char output StringBuilder " +
                       sb );

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "ObtuseUtil", "testing", null );

        doString( null );
        doString( "null" );
        doString( "hello\tthere\nworld" );
        doString( "dq=\", sq=\', bs=\b, nl=\n, rt=\r, t=\t, bs=\\" );

        Logger.logMsg( "input char \n becomes naked char string {" + enquoteToNakedJavaCharacter( '\n' ) + "}" );
        Logger.logMsg( "input char \n becomes char string {" + enquoteToJavaCharacter( '\n' ) + "}" );

        String s = "hello";

        for ( int w = s.length() - 1; w <= s.length() + 4; w += 1 ) {

            Logger.logMsg( "center( \"" + s + "\", " + w + " ) yields \"" + center( s, w ) + '"' );

        }

        for ( String value : new String[]{ "hello world", "", null } ) {

            if ( ObtuseUtil.writeSerializableObjectToFile( value, new File( "test.ser" ), true ) ) {

                try {

                    String recoveredVersion = (String)ObtuseUtil.recoverSerializedVersion( new File( "test.ser" ) );
                    if ( recoveredVersion == null ) {

                        if ( value == null ) {

                            Logger.logMsg( "null made the round trip alive" );

                        } else {

                            Logger.logMsg( "got null back from serialized version of \"" +
                                           enquoteToJavaString( value ) +
                                           "\"" );

                        }

                    } else if ( "Hello world".equals( recoveredVersion ) ) {

                        Logger.logMsg( "serialization and de-serialization to/from files seems to work" );

                    }

                    doNothing();

                } catch ( IOException e ) {

                    Logger.logErr( "IOException trying to recover serialized version of \"" +
                                   enquoteToJavaString( value ) +
                                   "\"", e );

                } catch ( ClassNotFoundException e ) {

                    Logger.logErr( "ClassNotFoundException trying to recover serialized version of \"" +
                                   enquoteToJavaString( value ) +
                                   "\"", e );

                }

            } else {

                Logger.logMsg( "unable to serialize test.ser" );

            }
        }

    }

    private static Integer s_pid = null;

    /**
     Get our process id (not guaranteed to work on all platforms).
     <p/>This is pretty much pure black magic.
     Found at
     <blockquote><tt>http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id</tt></blockquote>
     (first referenced 2013/09/24; last referenced 2017/11/15)
     <p>There is some grounds for optimism.
     Apparently, <blockquote>{@code long pid = ProcessHandle.current().getPid();}</blockquote> yields the desired result
     on Java 9.

     @return the process id of the process running our JVM or {@code -1} if the particular flavour of black magic used by
     this method fails on your platform.
     */

    public static long getPid() {

        return ProcessHandle.current().pid();

        // How to do this pre-Java 9:

        //        if ( ObtuseUtil.s_pid == null ) {
        //
        //            ObtuseUtil.s_pid = -1;
        //            try {
        //
        //                java.lang.management.RuntimeMXBean runtime =
        //                        java.lang.management.ManagementFactory.getRuntimeMXBean();
        //                @SuppressWarnings("JavaReflectionMemberAccess")
        //                java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField( "jvm" );
        //                jvm.setAccessible( true );
        //                sun.management.VMManagement mgmt =
        //                        (sun.management.VMManagement)jvm.get( runtime );
        //                @SuppressWarnings("JavaReflectionMemberAccess")
        //                java.lang.reflect.Method pidMethod = mgmt.getClass().getDeclaredMethod( "getProcessId" );
        //                pidMethod.setAccessible( true );
        //
        //                ObtuseUtil.s_pid = (Integer)pidMethod.invoke( mgmt );
        //
        //            } catch ( InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e ) {
        //                // we did our best
        //            }
        //
        //        }
        //
        //        return ObtuseUtil.s_pid.intValue();

    }

    public static @NotNull String fDim( final @NotNull String name, final Dimension d ) {

        return name + "=" + ObtuseUtil.fDim( d );

    }

    public static @NotNull String fDim( final Dimension d ) {

        return d == null ? "null" : ObtuseUtil.fDim( d.width, d.height );

    }

    public static @NotNull String fDim( final int width, final int height ) {

        return "(" + width + "," + height + ")";

    }

    public static String fDim( final Component c ) {

        return fDim( c.getMinimumSize() ) + "/" + fDim( c.getPreferredSize() ) + "/" + fDim( c.getMaximumSize() );

    }

    public static @NotNull String fBounds( final String name, final Rectangle r ) {

        return name + "=" + ( r == null ? "null" : ObtuseUtil.fBounds( r ) );

    }

    public static @NotNull String fBounds( final Rectangle r ) {

        return r == null ? "null" : ObtuseUtil.fBounds( r.x, r.y, r.width, r.height );

    }

    public static @NotNull String fBounds( final int x, final int y, final int width, final int height ) {

        return "@( " + x + ", " + y + ", " + width + "x" + height + " )";

    }

    public static @NotNull String fInsets( final Insets in ) {

        return "i( l=" + in.left + ", r=" + in.right + ", t=" + in.top + ", b=" + in.bottom + " )";

    }

    /**
     Always returns true (avoids having to fool Java compiler when you want an always {@code true} value).

     @return {@code true}
     */

    public static boolean always() {

        return true;

    }

    /**
     Always returns false (avoids having to fool Java compiler when you want an always {@code false} value).

     @return {@code false}
     */

    public static boolean never() {

        return false;

    }

    public static boolean packQuietly(
            final @NotNull EntityName groupName,
            final @NotNull GowingPackable[] items,
            final @NotNull File outputFile,
            boolean verbose
    ) {

        boolean worked = true;
        try ( GowingPacker packer = new StdGowingPacker( groupName, outputFile, verbose ) ) {

            for ( GowingPackable item : items ) {

                packer.queuePackableEntity( groupName, item );

            }

            packer.finish();

            doNothing();

        } catch ( FileNotFoundException e ) {

            worked = false;
            Logger.logErr( "unable to create output file " + outputFile, e );

            ObtuseUtil.doNothing();

        } catch ( IOException e ) {

            worked = false;
            Logger.logErr( "I/O error writing to " + outputFile, e );

            ObtuseUtil.doNothing();

        } catch ( Throwable e ) {

            worked = false;
            Logger.logErr( "Unexpected throwable writing to " + outputFile, e );

            ObtuseUtil.doNothing();

        }

        return worked;

    }

    public static boolean packQuietly(
            final @NotNull EntityName groupName,
            final @NotNull GowingPackable item,
            final @NotNull File outputFile,
            boolean verbose
    ) {

        return packQuietly( groupName, new GowingPackable[]{ item }, outputFile, verbose );

    }

    public static GowingUnPackedEntityGroup unpack(
            final @NotNull File inputFile,
            final @NotNull GowingEntityFactory[] gowingEntityFactories
    ) throws GowingUnpackingException {

        GowingUnPackedEntityGroup unPackResult;
        try (
                GowingUnPacker unPacker = new StdGowingUnPacker(
                        new GowingTypeIndex( "test unpacker" ),
                        inputFile
                )
        ) {

            unPacker.getUnPackerContext().registerFactories( gowingEntityFactories );

            unPackResult = unPacker.unPack();

            ObtuseUtil.doNothing();

        } catch ( IOException e ) {

            throw new GowingUnpackingException( "java.io.IOException caught", ParsingLocation.VERY_EARLY, e );

        }

        return unPackResult;

    }

    /**
     Pluralize a value and a noun.
     <p>Examples:</p>
     <blockquote>
     {@code pluralize( 0, "joy", "bird", "birds" )} yields {@code "0 joy"}
     <br>{@code pluralize( 1, "joy", "bird", "birds" )} yields {@code "1 bird"}
     <br>{@code pluralize( 2, "joy", "bird", "birds" )} yields {@code "2 birds"}
     </pre>
     </blockquote>
     @param value the value.
     @param none the noun if {@code value} is 0.
     @param one the noun if {@code value} is 1.
     @param many the noun if {@code value} is neither 0 or 1.
     @return the pluralized value and noun.
     */

    public static String pluralize(
            final long value,
            final @NotNull String none,
            final @NotNull String one,
            final @NotNull String many
    ) {

        return value + " " + ( value == 1 ? one : value == 0 ? none : many );

    }

    /**
     Pluralize a value and a noun.
     <p>Examples:</p>
     <blockquote>
     {@code pluralize( 0, "bird", "birds" )} yields {@code "0 birds"}
     <br>{@code pluralize( 1, "bird", "birds" )} yields {@code "1 bird"}
     <br>{@code pluralize( 2, "bird", "birds" )} yields {@code "2 birds"}
     </pre>
     </blockquote>
     @param value the value.
     @param singular the noun if {@code value} is 1.
     @param plural the noun if {@code value} is not 1.
     @return the pluralized value and noun.
     */

    public static String pluralize(
            final long value,
            final @NotNull String singular,
            final @NotNull String plural
    ) {

        return value + " " + ( value == 1 ? singular : plural );

    }

    /**
     Pluralize a value and a noun where the plural form has an {@code "s"} suffix.
     <p>Examples:</p>
     <blockquote>
     {@code pluralize( 0, "bird", "birds" )} yields {@code "0 birds"}
     <br>{@code pluralize( 1, "bird", "birds" )} yields {@code "1 bird"}
     <br>{@code pluralize( 2, "bird", "birds" )} yields {@code "2 birds"}
     </pre>
     </blockquote>
     @param value the value.
     @param noun the noun.
     @return the pluralized value and noun (the noun will be suffixed by {@code "s"}
     if {@code value} is not {@code 1}).
     */

    public static String pluralize( final long value, final @NotNull String noun ) {

        return pluralize( value, noun, noun + 's' );

    }

    /**
     Pluralize a value and a noun where the plural form has an {@code "es"} suffix.
     <p>Examples:</p>
     <blockquote>
     {@code pluralize( 0, "fish", "fishes" )} yields {@code "0 fishes"}
     <br>{@code pluralize( 1, "fish", "fishes" )} yields {@code "1 fish"}
     <br>{@code pluralize( 2, "fish", "fishes" )} yields {@code "2 fishes"}
     </pre>
     </blockquote>
     <p>Note subtly different method name.</p>
     @param value the value.
     @param noun the noun.
     @return the pluralized value and noun (the noun will be suffixed by {@code "es"}
     if {@code value} is not {@code 1}).
     */

    public static String pluralizes( final long value, final @NotNull String noun ) {

        return pluralize( value, noun, noun + "es" );

    }

    /**
     Pluralize a generic value.
     <p>Exactly equivalent to {@code pluralize( value, "element" )}.</p>
     <p>Examples:</p>
     <blockquote>
     {@code pluralize( 0 )} yields {@code "0 elements"}
     <br>{@code pluralize( 1 )} yields {@code "1 element"}
     <br>{@code pluralize( 2 )} yields {@code "2 elements"}
     </pre>
     </blockquote>
     @param value the value.
     @return the pluralized value with either " element" or "elements" appended as appropriate.
     <p>See {@link #pluralize(long, String)}</p>
     */

    public static String pluralize( final long value ) {

        return pluralize( value, "element" );

    }

}
