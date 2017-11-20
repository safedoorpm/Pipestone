/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.names;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 Represent a relative path/name.
 <p/>Instances of this class are immutable.
 */

@SuppressWarnings("unused")
public class RelativePath implements Iterable<SegmentName>, Comparable<RelativePath> {

    private final List<SegmentName> _segments;
    private final String _compareValue;

    /**
     Create a relative or an absolute path instance.
     <p/>
     This constructor is generally used to create a relative path.
     Since the {@link AbsolutePath} class is derived from this class, this constructor is also used by the {@link AbsolutePath} class's constructor(s)
     to create absolute paths. See the {@link AbsolutePath} class's documentation for more information.
     <p/>
     The rest of this documentation for this constructor assumes that it is NOT being used by the {@link AbsolutePath} class.
     @param segments an array of the names which makeup the to-be-created path.
     @throws IllegalArgumentException if the requested path has no segments, the {@code segments} array has any {@code null} elements,
     or (except when this constructor is invoked by the {@link AbsolutePath} class)
     the {@code segments} array contains any occurrences of the {@link SegmentName#ROOT_SEGMENT} segment.
     */

    public RelativePath( @NotNull final SegmentName[] segments ) {
        super();

        List<SegmentName> tmpSegments = new ArrayList<>();
        StringBuilder sb = new StringBuilder( "{" );
        String separator = "";
        boolean isAbsolutePath = this instanceof AbsolutePath;
        if ( isAbsolutePath ) {

            tmpSegments.add( SegmentName.ROOT_SEGMENT );
            sb.append( separator ).append( SegmentName.ROOT_SEGMENT );
            separator = SegmentName.SEPARATOR;

        }

        boolean gotNonRoot = false;

        String ourName = isAbsolutePath ? "AbsolutePath" : "RelativePath";

        for ( SegmentName segmentName : segments ) {

            if (segmentName == null ) {

                throw new IllegalArgumentException( ourName + ":  null segment in segments parameter - " + Arrays.toString( segments ) );

            }

            if ( SegmentName.ROOT_SEGMENT.equals( segmentName ) ) {

                if ( gotNonRoot ) {

                    throw new IllegalArgumentException( ourName + ":  ROOT segment not allowed after first non-root segment" );

                }

                if ( isAbsolutePath ) {

                    continue;

                }

                throw new IllegalArgumentException( ourName + ":  ROOT segment not allowed in relative paths" );

            }

            gotNonRoot = true;

            tmpSegments.add( segmentName );
            sb.append( separator ).append( segmentName.getSegmentName() );
            separator = SegmentName.SEPARATOR;

        }

        _segments = Collections.unmodifiableList( tmpSegments );

        sb.append( "}" );
        _compareValue = sb.toString();

        if ( _segments.size() == 0 ) {

            throw new IllegalArgumentException( ourName + ":  paths must not be empty" );

        }

    }

    /**
     Create a relative or an absolute path instance.
     <p/>This constructor is exactly equivalent to
     <blockquote>
     {@code new RelativePath( segments.toArray( new SegmentName[segments.size()] ) )}
     </blockquote>
     See {@link #RelativePath(SegmentName[])} for more information.
     @param segments a collection of the names which makeup the to-be-created path.
     @throws IllegalArgumentException if the requested path has no segments, the {@code segments} collection has any {@code null} elements,
     or (except when this constructor is invoked by the {@link AbsolutePath} class)
     the {@code segments} collection contains any occurrences of the {@link SegmentName#ROOT_SEGMENT} segment.
     */
    public RelativePath( @NotNull final Collection<SegmentName> segments ) {
        this( segments.toArray( new SegmentName[segments.size()] ) );

    }

    /**
     Create a relative or an absolute path instance containing a single segment.
     <p/>This constructor is exactly equivalent to
     <blockquote>
     {@code new RelativePath( new SegmentName[] { segmentName } )}
     </blockquote>
     See {@link #RelativePath(SegmentName[])} for more information.
     @param segmentName the single segment name which is to be contained in the to-be-created path.
     @throws IllegalArgumentException Except when this constructor is invoked by the {@link AbsolutePath} class, if
     {@code segmentName} is the {@link SegmentName#ROOT_SEGMENT} segment.
     */

    public RelativePath( @NotNull final SegmentName segmentName ) {
        this( new SegmentName[] { segmentName } );

    }

    /**
     Create a relative or an absolute path instance containing a single segment.
     <p/>This constructor is exactly equivalent to
     <blockquote>
     {@code new RelativePath( new SegmentName[] { new SegmentName( segmentName ) } )}
     </blockquote>
     See {@link #RelativePath(SegmentName[])} for more information.
     @param segmentName the single segment name which is to be contained in the to-be-created path.
     @throws IllegalArgumentException if {@code segmentName} is equal to {@link SegmentName#ROOT_STRING_NAME} or (except when this constructor is invoked by the {@link AbsolutePath} class), if
     {@code segmentName} is the {@link SegmentName#ROOT_SEGMENT} segment.
     */

    public RelativePath( @NotNull final String segmentName ) {
        this( new SegmentName[] { new SegmentName( segmentName ) } );

    }

//    public static SegmentName[] prefixIfNecessary( @NotNull final SegmentName[] segments ) {
//
//        if ( segments.length == 0 || !ROOT_SEGMENT.equals( segments[0] ) ) {
//
//            return concat( new SegmentName[] { ROOT_SEGMENT }, segments );
//
//        } else {
//
//            return concat( new SegmentName[0], segments );
//
//        }
//
//    }
//
//    public static SegmentName[] prefixIfNecessary( @NotNull final RelativePath path ) {
//
//        return prefixIfNecessary( path._segments );
//
//    }
//
//    public static SegmentName[] prefixIfNecessary( @NotNull final List<SegmentName> segments ) {
//
//        if ( segments.size() == 0 || !ROOT_SEGMENT.equals( segments.get( 0) ) ) {
//
//            return concat( new SegmentName[] { ROOT_SEGMENT }, segments );
//
//        } else {
//
//            return concat( new SegmentName[0], segments );
//
//        }
//
//    }


    /**
     Get a list of this instance's segment names.
     @return an unmodifiable list of this instance's segment names.
     */

    @NotNull
    public List<SegmentName> getNames() {

        return _segments;

    }

    /**
     Get a canonical representation of this instance.
     <p/>If two instances of this class have the same segments with the exact same names in the exact same sequence then they will both
     return the exact same value via this method; otherwise, the two instances will almost certainly return different values.
     <p/>There are ways to get two instances with different segments and names to both yield the same canonical form by doing very strange things
     with segment names (the approach that I have thought of involves using the value of {@link SegmentName#SEPARATOR} in rather creative ways).
     I don't believe that it is worth the trouble to make this class work in those presumably unlikely scenarios.
     Feel free to discuss this with me if you like (danny@matilda.com).
     */

    @NotNull
    public String getCanonicalForm() {

        return _compareValue;

    }

    /**
     Get an iterator which iterates over an unmodifiable list of this instance's segments.
     @return an iterator which iterates over this instance's segments.
     Any attempt to use the returned iterator to modify this instance's segments will fail with an appropriate exception
     (see {@link Collections#unmodifiableList(List)} for more information).
     */

    @NotNull
    public Iterator<SegmentName> iterator() {

        return _segments.iterator();

    }

    /**
     Return the number of segments in this instance.
     @return the number of segments in this instance.
     */
    public int size() {

        return _segments.size();

    }

    /**
     Determine if this path is empty.
     This method is provided for symmetry with other classes which implement a {@code size()} method.
     Since instances of this class always have at least one segment, this method always returns {@code false}.
     @return {@code false}
     */

    public boolean isEmpty() {

        return _segments.size() == 0;

    }

    /**
     Determine if this instance is an absolute path or a relative path.
     @return {@code true} if this instance is an instance of the {@link AbsolutePath} class; {@code} otherwise.
     */

    public boolean isAbsolute() {

        return this instanceof AbsolutePath;

    }

    /**
     Create a new path which consists of a specified path with the specified segment appended on the end.
     @param primary the specified path.
     @param segmentName the segment which is to be appended to the end of the being created path.
     @return an {@link AbsolutePath} containing the longer-by-one path if the specified path was an instance of the AbsolutePath class;
     otherwise, a {@link RelativePath} containing the longer-by-one path if the specified path.
     */

    @NotNull
    public static RelativePath concat( @NotNull final RelativePath primary, @NotNull final SegmentName segmentName ) {

        SegmentName[] prefix = new SegmentName[ primary._segments.size() + 1 ];
        int ix = 0;
        for ( SegmentName sn : primary._segments ) {

            prefix[ix] = sn;
            ix += 1;

        }
        prefix[ix] = segmentName;

        return primary instanceof AbsolutePath ? new AbsolutePath( prefix ) : new RelativePath( prefix );

    }

    /**
     Create a new path which consists of a specified head path concatenated with a specified tail path.
     @param head the specified head path.
     @param tail the specified tail path.
     @return an {@link AbsolutePath} containing the concatenated paths if the specified head path was an instance of the AbsolutePath class;
     otherwise, a {@link RelativePath} containing the concatenation of the two paths.
     */

    @NotNull
    public static RelativePath concat( @NotNull final RelativePath head, @NotNull final RelativePath tail ) {

        SegmentName[] prefix = new SegmentName[ head._segments.size() + tail.size() ];
        int ix = 0;
        for ( SegmentName sn : head._segments ) {

            prefix[ix] = sn;
            ix += 1;

        }

        if ( ix > 1 && SegmentName.ROOT_SEGMENT.equals( prefix[0] ) && tail.size() > 0 && SegmentName.ROOT_STRING_NAME.equals( tail._segments.get( 0).getSegmentName() ) ) {

            throw new IllegalArgumentException( "RelativePath.concat:  cannot concatenate an absolute name (" + tail + ") to the end of a non-trivial relative or absolute name (" + head + ")" );

        }

        for ( SegmentName sn: tail._segments ) {

            prefix[ix] = sn;
            ix += 1;

        }

        if ( prefix.length > 0 && SegmentName.ROOT_SEGMENT.equals( prefix[0] ) ) {

            return new AbsolutePath( prefix );

        } else {

            return new RelativePath( prefix );

        }

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first collection of segments and a specified second collection of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( @NotNull final Collection<SegmentName> head, @NotNull final Collection<SegmentName> tail ) {

        return concat( head.toArray( new SegmentName[head.size()]), tail.toArray( new SegmentName[tail.size()] ) );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first collection of segments and a specified second array of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( @NotNull final Collection<SegmentName> head, @NotNull final SegmentName[] tail ) {

        return concat( head.toArray( new SegmentName[head.size()]), tail );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first array of segments and a specified second collection of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( @NotNull final SegmentName[] head, @NotNull final Collection<SegmentName> tail ) {

        return concat( head, tail.toArray( new SegmentName[tail.size()] ) );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first array of segments and a specified second array of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( @NotNull final SegmentName[] head, @NotNull final SegmentName[] tail ) {

        SegmentName[] combined = new SegmentName[ head.length + tail.length ];
        int ix = 0;
        for ( SegmentName sn : head ) {

            combined[ix] = sn;
            ix += 1;

        }
        for ( SegmentName sn: tail ) {

            combined[ix] = sn;
            ix += 1;

        }

        return combined;

    }

    /**
     Compare this instance to a specified instance of this class.
     @param rhs the specified instance.
     @return the result of comparing the value returned by {@link #getCanonicalForm()} on this instance with the value returned by {@link #getCanonicalForm()}
     on the specified instance (I might make this ordering more sophisticated or I might change how {@link #getCanonicalForm()} works in the future;
     rely on the current implementation of this method and on the current implementation of {@link #getCanonicalForm()} at your own risk).
     */

    public int compareTo( @NotNull final RelativePath rhs ) {

        return _compareValue.compareTo( rhs._compareValue );

    }

    /**
     Determine if this instance is the same as a specified other object.
     @param rhs the other instance.
     @return {@code true} if-and-only-if the other object is an instance of this class and invoking {@code toString()} on that instance yields
     exactly the same string as invoking {@code toString()} on this instace; {@code false} otherwise.
     */

    public boolean equals( @Nullable final Object rhs ) {

        return rhs instanceof RelativePath && _compareValue.equals( ((RelativePath)rhs)._compareValue );

    }

    /**
     Compute a hashcode on this instance.
     @return exactly equivalent to {@code toString().hashCode()}.
     */

    public String toString() {

        return _compareValue;

    }

    private static void msgTime( @NotNull final String what, String msg, Throwable e ) {

        if ( what.startsWith( "!" ) ) {

            if ( e == null ) {

                Logger.logMsg( "ERROR - " + what.substring( 1 ) + " - " + msg );

            } else {

                Logger.logMsg( "OK    - " + what.substring( 1 ) + " - " + msg + " " + e );

            }

        } else {

            if ( e == null ) {

                Logger.logMsg( "OK    - " + what + " - " + msg );

            } else {

                Logger.logMsg( "ERROR - " + what + " - " + msg + " " + e );

            }

        }

    }

    private static void doit( @NotNull final String what, @NotNull final RelativePath rn ) {

        msgTime( what, "yielded \"" + rn + "\"", null );

    }

    private static void doit( @NotNull final String what, @NotNull final RelativePath rn1, @NotNull final RelativePath rn2 ) {

        try {

            RelativePath rv;
            if ( rn1 instanceof AbsolutePath ) {

                rv = AbsolutePath.concat( (AbsolutePath)rn1, rn2 );

            } else {

                rv = RelativePath.concat( rn1, rn2 );

            }

            msgTime( what, "concat of \"" + rn1 + "\" with \"" + rn2 + "\" yielded \"" + rv + "\"", null );

        } catch ( Throwable e ) {

            msgTime( what, "concat of \"" + rn1 + "\" and \"" + rn2 + "\"", e );

        }

    }

    private static void doit( @NotNull final String what, @NotNull final Supplier<RelativePath> fn1 ) {

        RelativePath rn1 = null;
        boolean getDone = false;

        try {

            rn1 = fn1.get();
            getDone = true;
//            doit( what, rn1 );

            msgTime( what, "yielded \"" + rn1 + "\"", null );

        } catch ( Throwable e ) {

            if ( getDone ) {

                msgTime( what, "yielded \"" + rn1 + "\"", e );

            } else {

                msgTime( what, "", e );

            }

        }

    }

    private static void doit( @NotNull final String what, @NotNull final Supplier<RelativePath> fn1, @NotNull final Supplier<RelativePath> fn2 ) {

        RelativePath rn1 = null;
        RelativePath rn2 = null;
        boolean getDone = false;

        try {

            rn1 = fn1.get();
            rn2 = fn2.get();
            getDone = true;

            RelativePath rv;
            if ( rn1 instanceof AbsolutePath ) {

                rv = AbsolutePath.concat( (AbsolutePath)rn1, rn2 );

            } else {

                rv = RelativePath.concat( rn1, rn2 );

            }

            msgTime( what, "concat of \"" + rn1 + "\" with \"" + rn2 + "\" yielded \"" + rv + "\"", null );

        } catch ( Throwable e ) {

            if ( getDone ) {

                msgTime( what, "concat of \"" + rn1 + "\" and \"" + rn2 + "\"", e );

            } else if ( rn1 == null ) {

                msgTime( what, "rn1 get failed", e );

            } else {

                msgTime( what, "rn2 get failed, rn1=\"" + rn1 + "\"", e );

            }

        }

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "Burke2", "testing", null );

        Logger.logMsg( "our pid is " + ObtuseUtil.getPid() );

        doit( "one relative name", () -> new RelativePath( "one_segment_string" ) );
        doit( "one segment name", () -> new RelativePath( new SegmentName( "one_segment_name" ) ) );
        doit( "!forbidden segment name", () -> new RelativePath( new SegmentName( "[looks_like_internal_name]" ) ) );
        doit( "one segment via array", () -> new RelativePath( new SegmentName[] { new SegmentName( "one_segment_array" ) } ) );
        doit( "two segments via array", () -> new RelativePath( new SegmentName[] { new SegmentName( "two_segment_array" ), new SegmentName( "second_element" ) } ) );

        doit( "one segment absolute root", new AbsolutePath( SegmentName.ROOT_SEGMENT ) );
        doit( "!relative starting with root",
                () -> new RelativePath( "" ),
                () -> new RelativePath( SegmentName.ROOT_SEGMENT )
        );
        doit( "path with just an empty segment", () -> new RelativePath( new SegmentName( "" ) ) );
        doit( "!relative path with no segments", () -> new RelativePath( new SegmentName[0] ) );
        doit( "absolute path with no segments", () -> new AbsolutePath( new SegmentName[0] ) );
        doit( "absolute path via default constructor", () -> new AbsolutePath() );
        doit( "absolute starting with root", new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT } ) );
        doit( "absolute starting with root and two empty segments", new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.EMPTY_NAME, SegmentName.EMPTY_NAME } ) );
        doit( "!relative starting with two roots", () -> new RelativePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.ROOT_SEGMENT } ) );
        doit( "absolute starting with two roots", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.ROOT_SEGMENT } ) );
        doit( "!relative with second segment root", () -> new RelativePath( new SegmentName[] { new SegmentName( "first" ), SegmentName.ROOT_SEGMENT } ) );
        doit( "!absolute with second segment root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "first" ), SegmentName.ROOT_SEGMENT } ) );
        doit( "!concat two non-trival absolutes", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "barney" ) } ), () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "fred" ) } ) );
        doit( "concat non-trival absolute after trivial absolute", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT } ), () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "fred" ) } ) );

        doit( "try to create a relative path with a root node via the back door", () -> SegmentName.ROOT_SEGMENT.asRelativePath() );
        doit( "one string absolute w/ no explicit root", () -> new AbsolutePath( "one_segment_string" ) );
        doit( "one segment name absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName( "one_segment_name" ) ) );
        doit( "one segment array absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "one_segment_array" ) } ) );
        doit( "two segment array absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "two_segment_array" ), new SegmentName( "second_element" ) } ) );

        doit( "absolute fed into absolute construct w/ no explicit root", () -> new AbsolutePath( new AbsolutePath( new SegmentName[] { new SegmentName( "recycled_one_segment_array" ) } ).getNames() ) );

    }

    public int stringLength() {

        return _compareValue.length();

    }

}
