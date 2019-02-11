/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.names;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.GowingPackableCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 Represent a relative path/name.
 <p/>Instances of this class are immutable.
 */

@SuppressWarnings("unused")
public class RelativePath
        extends GowingAbstractPackableEntity
        implements GowingBackReferenceable, Iterable<SegmentName>, Comparable<RelativePath> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( RelativePath.class );

    private static final int VERSION = 1;

    private static final EntityName SEGMENTS = new EntityName( "_s" );
    private static final EntityName COMPARE_VALUE = new EntityName( "_cv" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;

        }

        @Override
        @NotNull
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new RelativePath( unPacker, bundle );

        }

    };

    private List<SegmentName> _segments;
    private final String _compareValue;
    private GowingEntityReference _segmentsEntityReference;

    private String _canonicalFormString;

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

    public RelativePath( final @NotNull SegmentName@NotNull[] segments ) {
        super( new GowingNameMarkerThing() );

        _compareValue = finishConstructor( this instanceof AbsolutePath, segments );

    }

    /**
     Create a relative or an absolute path instance.
     <p/>
     This constructor is generally used to create a relative path.
     Since the {@link AbsolutePath} class is derived from this class, this constructor is also used by the {@link AbsolutePath} class's constructor(s)
     to create absolute paths. See the {@link AbsolutePath} class's documentation for more information.
     <p/>
     The rest of this documentation for this constructor assumes that it is NOT being used by the {@link AbsolutePath} class.
     @param segmentNames an array of the String names which makeup the to-be-created path.
     @throws IllegalArgumentException if the requested path has no segments, the {@code segmentNames} array has any {@code null} elements,
     or (except when this constructor is invoked by the {@link AbsolutePath} class)
     the {@code segments} array contains any occurrences of the {@link SegmentName#ROOT_SEGMENT} segment.
     */

    public RelativePath( final @NotNull String... segmentNames ) {
        super( new GowingNameMarkerThing() );

        SegmentName[] segments = new SegmentName[segmentNames.length];
        for ( int ix = 0; ix < segmentNames.length; ix += 1 ) {

            segments[ix] = new SegmentName( segmentNames[ix] );

        }

        _compareValue = finishConstructor( this instanceof AbsolutePath, segments );

    }

    @NotNull
    private String finishConstructor( final boolean shouldBeAbsoluteInstance, @Nullable final SegmentName @NotNull [] segments ) {

        List<SegmentName> tmpSegments = new ArrayList<>();
        StringBuilder sb = new StringBuilder( "{" );
        String separator = "";
        boolean isAbsolutePath = this instanceof AbsolutePath;
        if ( shouldBeAbsoluteInstance != isAbsolutePath ) {

            throw new IllegalArgumentException(
                    "RelativePath.finishConstructor:  expected to be creating " +
                    ( shouldBeAbsoluteInstance ? "AbsolutePath" : "RelativePath" ) +
                    " instance but we're in a " +
                    ( isAbsolutePath ? "AbsolutePath" : "RelativePath" ) +
                    " instance"
            );

        }

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

        return sb.toString();

    }

    protected RelativePath( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {
        super( unPacker, bundle.getSuperBundle() );

        _segmentsEntityReference = bundle.getMandatoryEntityReference( SEGMENTS );
        _compareValue = bundle.MandatoryStringValue( COMPARE_VALUE );

        ObtuseUtil.doNothing();

    }

    /**
     Create a relative or an absolute path instance.
     <p/>This constructor is exactly equivalent to
     <blockquote>
     {@code new RelativePath( segments.toArray( new SegmentName[0] ) )}
     </blockquote>
     See {@link #RelativePath(SegmentName[])} for more information.
     @param segments a collection of the names which makeup the to-be-created path.
     @throws IllegalArgumentException if the requested path has no segments, the {@code segments} collection has any {@code null} elements,
     or (except when this constructor is invoked by the {@link AbsolutePath} class)
     the {@code segments} collection contains any occurrences of the {@link SegmentName#ROOT_SEGMENT} segment.
     */
    public RelativePath( final @NotNull Collection<SegmentName> segments ) {
        this( segments.toArray( new SegmentName[0] ) );

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

    public RelativePath( final @NotNull SegmentName segmentName ) {
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

    public RelativePath( final @NotNull String segmentName ) {
        this( new SegmentName[] { new SegmentName( segmentName ) } );

    }

    /**
     Get a list of this instance's segment names.
     @return an unmodifiable list of this instance's segment names.
     */

    @NotNull
    public List<SegmentName> getNames() {

        return _segments == null ? Collections.emptyList() : _segments;

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

        if ( _canonicalFormString == null ) {

            StringBuilder sb = new StringBuilder();

            String arrow = "";

            for ( SegmentName segmentName : _segments ) {

                sb.append( arrow ).append( segmentName.getSegmentName() );

                arrow = "->";

            }

            _canonicalFormString = sb.toString();

        }

        return _canonicalFormString;

    }

    /**
     Get an iterator which iterates over an unmodifiable list of this instance's segments.
     @return an iterator which iterates over this instance's segments.
     Any attempt to use the returned iterator to modify this instance's segments will fail with an appropriate exception
     (see {@link Collections#unmodifiableList(List)} for more information).
     */

    @NotNull
    public Iterator<SegmentName> iterator() {

        return getNames().iterator();

    }

    /**
     Return the number of segments in this instance.
     @return the number of segments in this instance.
     */

    public int size() {

        return _segments == null ? 0 : _segments.size();

    }

    /**
     Determine if this path is empty.
     This method is provided for symmetry with other classes which implement a {@code size()} method.
     Since instances of this class always have at least one segment, this method always returns {@code false}.
     @return {@code false}
     */

    public boolean isEmpty() {

        return _segments == null || _segments.isEmpty();

    }

    /**
     Determine if this instance is an absolute path or a relative path.
     @return {@code true} if this instance is an instance of the {@link AbsolutePath} class; {@code} otherwise.
     */

    public boolean isAbsolute() {

        return this instanceof AbsolutePath;

    }

    public @NotNull String@NotNull[] getStringPathArray() {

        String[] rval = new String[size()];

        int ix = 0;
        for ( SegmentName sn : getNames() ) {

            rval[ix] = sn.getSegmentName();

            ix += 1;

        }

        return rval;

    }

    /**
     Create a new path which consists of a specified path with the specified segment appended on the end.
     @param head the specified path.
     @param segmentName the segment which is to be appended to the end of the being created path.
     @return  a {@link RelativePath} consisting of a single {@link SegmentName} named {@code segmentName} if {@code head} is {@code null};
     an {@link AbsolutePath} containing the longer-by-one path if {@code head} is an {@link AbsolutePath};
     otherwise, a {@link RelativePath} containing the longer-by-one path.
     */

    @NotNull
    public static RelativePath concat( final @Nullable RelativePath head, final @NotNull String segmentName ) {

        if ( head == null ) {

            return new RelativePath( segmentName );

        }

        SegmentName[] prefix = new SegmentName[ head.size() + 1 ];
        int ix = 0;
        for ( SegmentName sn : head.getNames() ) {

            prefix[ix] = sn;
            ix += 1;

        }

        prefix[ix] = new SegmentName( segmentName );

        return head instanceof AbsolutePath ? new AbsolutePath( prefix ) : new RelativePath( prefix );

    }

    /**
     Create a new path which consists of a specified path with the specified segment appended on the end.
     @param head the specified path.
     @param segmentName the segment which is to be appended to the end of the being created path.
     @return  a {@link RelativePath} consisting of a single {@link SegmentName} named {@code segmentName} if {@code head} is {@code null};
     an {@link AbsolutePath} containing the longer-by-one path if {@code head} is an {@link AbsolutePath};
     otherwise, a {@link RelativePath} containing the longer-by-one path.
     */

    @NotNull
    public static AbsolutePath concat( final @NotNull AbsolutePath head, final @NotNull String segmentName ) {

        SegmentName[] prefix = new SegmentName[ head.size() + 1 ];
        int ix = 0;
        for ( SegmentName sn : head.getNames() ) {

            prefix[ix] = sn;
            ix += 1;

        }

        prefix[ix] = new SegmentName( segmentName );

        return new AbsolutePath( prefix );

    }

    /**
     Create a new path which consists of a specified path with the specified segment appended on the end.
     @param head the specified path.
     @param segmentName the segment which is to be appended to the end of the being created path.
     @return a {@link RelativePath} consisting of the single {@link SegmentName} {@code segmentName} if {@code head} is {@code null};
     an {@link AbsolutePath} containing the longer-by-one path if {@code head} is an {@link AbsolutePath};
     otherwise, a {@link RelativePath} containing the longer-by-one path.
     */

    @NotNull
    public static RelativePath concat( final @Nullable RelativePath head, final @NotNull SegmentName segmentName ) {

        if ( head == null ) {

            return new RelativePath( segmentName );

        }

        SegmentName[] prefix = new SegmentName[ head.size() + 1 ];
        int ix = 0;
        for ( SegmentName sn : head.getNames() ) {

            prefix[ix] = sn;
            ix += 1;

        }
        prefix[ix] = segmentName;

        return head instanceof AbsolutePath ? new AbsolutePath( prefix ) : new RelativePath( prefix );

    }

    /**
     Create a new path which consists of a specified head path concatenated with a specified tail path.
     @param head the specified head path.
     @param tail the specified tail path.
     @return {@code tail} if {@code head} is {@code null};
     an {@link AbsolutePath} containing the concatenated paths if {@code head} is an {@link AbsolutePath};
     otherwise, a {@link RelativePath} containing the concatenation of the two paths.
     */

    @NotNull
    public static RelativePath concat( final @Nullable RelativePath head, final @NotNull RelativePath tail ) {

        if ( head == null ) {

            return tail;

        }

        SegmentName[] prefix = new SegmentName[ head.size() + tail.size() ];
        int ix = 0;
        for ( SegmentName sn : head.getNames() ) {

            prefix[ix] = sn;
            ix += 1;

        }

        if ( ix > 1 && SegmentName.ROOT_SEGMENT.equals( prefix[0] ) && tail.size() > 0 && SegmentName.ROOT_STRING_NAME.equals( tail.getNames().get( 0 ).getSegmentName() ) ) {

            throw new IllegalArgumentException( "RelativePath.concat:  cannot concatenate an absolute name (" + tail + ") to the end of a non-trivial relative or absolute name (" + head + ")" );

        }

        for ( SegmentName sn: tail.getNames() ) {

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

    public static SegmentName[] concat( final @NotNull Collection<SegmentName> head, final @NotNull Collection<SegmentName> tail ) {

        return concat( head.toArray( new SegmentName[0] ), tail.toArray( new SegmentName[0] ) );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first collection of segments and a specified second array of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( final @NotNull Collection<SegmentName> head, final @NotNull SegmentName@NotNull[] tail ) {

        return concat( head.toArray( new SegmentName[0] ), tail );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first array of segments and a specified second collection of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( final @NotNull SegmentName@NotNull[] head, final @NotNull Collection<SegmentName> tail ) {

        return concat( head, tail.toArray( new SegmentName[0] ) );

    }

    /**
     Create an array of segments which consists of the concatenation of a specified first array of segments and a specified second array of segments.
     @param head the specified first collection of segments.
     @param tail the specified second collection of segments.
     @return an array of the concatenation of segments.
     */

    public static SegmentName[] concat( final @NotNull SegmentName@NotNull[] head, final @NotNull SegmentName@NotNull[] tail ) {

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
     <p>Firstly, absolute paths are less than relative paths regardless of how long the paths are or the values of their respective segments.</p>
     <p>If both paths are absolute paths or both paths are relative paths then
     this method does a segment by segment pairwise comparison of this instance's segments to the other instance's segments.
     If a pairwise difference is found (i.e. segment <em>n</em> in one path is different than segment <em>n</em> in the other path)
     then the result of this method is
     <blockquote>
     {@code segment[}<em>n</em>{@code ].compareTo( rhs.segment[}<em>n</em>{@code ] )}
     </blockquote>
     Otherwise, there are a few cases of interest:
     <ul>
     <li>if this path and the other path are the same length and the segments in this path and the segments in the other path are pairwise equal
     then this path is equal to the other path - we return 0.</li>
     <li>if this path is shorter than the other path and the two paths are equal up to the end of this path then this path is
     less than the other path - we return a negative value.</li>
     <li>if this path is longer than the other path and the two paths are equal up to the end of the other path then this path is
     greater than the other path - we return a positive value.</li>
     </ul>
     </p>
     @param rhs the specified instance.
     @return the result of this path to the other path (see above for a detailed explanation of how the comparison is done).
     */

    @SuppressWarnings("CompareToUsesNonFinalVariable")
    public int compareTo( final @NotNull RelativePath rhs ) {

        // Deal with the case where exactly one of the two paths are absolute paths.

        if ( this instanceof AbsolutePath && !( rhs instanceof AbsolutePath ) ) {

            return 1;

        } else if ( !(this instanceof AbsolutePath) && rhs instanceof AbsolutePath ) {

            return -1;

        }

        // Compare the two paths according to the rules described in the Javadocs above.

        int ix = 0;
        int leftSize = _segments.size();
        int rightSize = rhs._segments.size();

        while ( ix < leftSize && ix < rightSize ) {

            SegmentName left = _segments.get( ix );
            SegmentName right = rhs._segments.get( ix );

            int rval = left.compareTo( right );
            if ( rval != 0 ) {

                return rval;

            }

            ix += 1;

        }

        return leftSize - rightSize;

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
     @return a hashcode intended to be different if two paths have the same segments but in a different order.
     */

    @Override
    public int hashCode() {

        int hc = 0;
        int offset = 0;
        //noinspection NonFinalFieldReferencedInHashCode
        for ( SegmentName sn : _segments ) {

            hc ^= Integer.rotateLeft( sn.hashCode(), offset );
            offset += ( hc & 3 ) + 1;

        }

        return hc;

    }

    public String toString() {

        return _compareValue;

    }

    private static void msgTime( final @NotNull String what, final String msg, final Throwable e ) {

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

                Logger.logErr( "ERROR - " + what + " - " + msg + " " + e, e );

            }

        }

    }

    private static void doit( final @NotNull String what, final @NotNull RelativePath rn ) {

        msgTime( what, "yielded \"" + rn + "\"", null );

    }

    private static void doit( final @NotNull String what, final @NotNull RelativePath rn1, final @NotNull RelativePath rn2 ) {

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

    private static void doit( final @NotNull String what, final @NotNull Supplier<RelativePath> fn1 ) {

        RelativePath rn1 = null;
        boolean getDone = false;

        try {

            rn1 = fn1.get();
            getDone = true;

            msgTime( what, "yielded \"" + rn1 + "\"", null );

        } catch ( Throwable e ) {

            if ( getDone ) {

                msgTime( what, "yielded \"" + rn1 + "\"", e );

            } else {

                msgTime( what, "", e );

            }

        }

    }

    private static void doit( final @NotNull String what, final @NotNull Supplier<RelativePath> fn1, final @NotNull Supplier<RelativePath> fn2 ) {

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

        BasicProgramConfigInfo.init( "Kenosee", "Obtuse", "testing", null );

        Logger.logMsg( "our pid is " + ObtuseUtil.getPid() );

        doit( "one relative name", () -> new RelativePath( "one_segment_string" ) );
        doit( "one segment name", () -> new RelativePath( new SegmentName( "one_segment_name" ) ) );
        doit( "!forbidden segment name", () -> new RelativePath( new SegmentName( "[looks_like_internal_name]" ) ) );
        doit( "one segment via array", () -> new RelativePath( new SegmentName[] { new SegmentName( "one_segment_array" ) } ) );
        doit( "two segments via array", () -> new RelativePath( new SegmentName[] { new SegmentName( "two_segment_array" ), new SegmentName( "second_element" ) } ) );

        doit( "one segment absolute root", () -> new AbsolutePath( SegmentName.ROOT_SEGMENT ) );
        doit( "!relative starting with root",
                () -> new RelativePath( "" ),
                () -> new RelativePath( SegmentName.ROOT_SEGMENT )
        );
        doit( "turn root path into relative path", () -> new AbsolutePath( "hello", "there", "world" ).makeRelative() );
        doit( "path with just an empty segment", () -> new RelativePath( new SegmentName( "" ) ) );
        doit( "relative path with no segments", () -> new RelativePath( new SegmentName[0] ) );
        doit( "absolute path with one segments", () -> new AbsolutePath( new SegmentName( "hello" ) ) );
        doit( "absolute path via default constructor", () -> new AbsolutePath() );
        doit( "absolute starting with root", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT } ) );
        doit( "absolute starting with root and two empty segments", new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.EMPTY_NAME, SegmentName.EMPTY_NAME } ) );
        doit( "!relative starting with two roots", () -> new RelativePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.ROOT_SEGMENT } ) );
        doit( "absolute starting with two roots", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, SegmentName.ROOT_SEGMENT } ) );
        doit( "!relative with second segment root", () -> new RelativePath( new SegmentName[] { new SegmentName( "first" ), SegmentName.ROOT_SEGMENT } ) );
        doit( "!absolute with second segment root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "first" ), SegmentName.ROOT_SEGMENT } ) );
        doit( "!concat two non-trival absolutes", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "barney" ) } ), () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "fred" ) } ) );
        doit( "concat non-trival absolute after trivial absolute", () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT } ), () -> new AbsolutePath( new SegmentName[] { SegmentName.ROOT_SEGMENT, new SegmentName( "fred" ) } ) );

        doit( "concat empty path with non-empty path", () -> concat( new RelativePath( new SegmentName[0] ), new RelativePath( "stuff" ) ) );
        doit( "concat empty path with longer non-empty path", () -> concat( new RelativePath( new SegmentName[0] ), new RelativePath( "more", "stuff" ) ) );
        doit( "concat two relative paths with no segments", () -> concat( new RelativePath( new SegmentName[0] ), new RelativePath( new SegmentName[0] ) ) );

        doit( "!try to create a relative path with a root node via the back door", () -> SegmentName.ROOT_SEGMENT.asRelativePath() );
        doit( "one string absolute w/ no explicit root", () -> new AbsolutePath( "one_segment_string" ) );
        doit( "one segment name absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName( "one_segment_name" ) ) );
        doit( "one segment array absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "one_segment_array" ) } ) );
        doit( "two segment array absolute w/ no explicit root", () -> new AbsolutePath( new SegmentName[] { new SegmentName( "two_segment_array" ), new SegmentName( "second_element" ) } ) );

        doit( "absolute fed into absolute construct w/ no explicit root", () -> new AbsolutePath( new AbsolutePath( new SegmentName[] { new SegmentName( "recycled_one_segment_array" ) } ).getNames() ) );

        doit( "compare short to long paths", new RelativePath( "equal" ), new RelativePath( "equal", "extra" ), -1 );
        doit( "compare long to short paths", new RelativePath( "equal", "extra" ), new RelativePath( "equal" ), 1 );
        doit( "compare equal short paths", new RelativePath( "equal" ), new RelativePath( "equal" ), 0 );
        doit( "compare equal long paths", new RelativePath( "equal", "extra" ), new RelativePath( "equal", "extra" ), 0 );
        doit( "compare first segment differs paths", new RelativePath( "less" ), new RelativePath( "than" ), -1 );
        doit( "compare first segment differs paths", new RelativePath( "than" ), new RelativePath( "less" ), 1 );
        doit( "compare second segment differs paths", new RelativePath( "same", "less" ), new RelativePath( "same", "than" ), -1 );
        doit( "compare second segment differs paths", new RelativePath( "same", "than" ), new RelativePath( "same", "less" ), 1 );
        doit( "compare abs otherwise equal paths (gt)", new AbsolutePath( "same", "than" ), new RelativePath( "same", "than" ), 1 );
        doit( "compare abs otherwise equal paths (lt)", new RelativePath( "same", "than" ), new AbsolutePath( "same", "than" ), -1 );

    }

    private static void doit( final @NotNull String what, final @NotNull RelativePath left, final @NotNull RelativePath right, final int expectedValue ) {

        boolean worked;
        int rval = 0;
        try {
            rval = left.compareTo( right );
            if ( expectedValue == 0 && rval == 0 ) {
                worked = true;
            } else if ( expectedValue < 0 && rval < 0 ) {
                worked = true;
            } else //noinspection RedundantIfStatement
                if ( expectedValue > 0 && rval > 0 ) {
                worked = true;
            } else {
                worked = false;
            }
        } catch ( Throwable e ) {
            e.printStackTrace();
            worked = false;
        }

        msgTime( what, "comparison of \"" + left + "\" with \"" + right + "\" yielded \"" + rval + "\"", worked ? null : new IllegalArgumentException() );

    }

    public int stringLength() {

        return _compareValue.length();

    }

    /**
     Determine if any segment contains a specified String.
     <p>This can be useful for ensuring that calls to {@link #getCanonicalForm()}
     or the {@code getDelimitedPathString} family of methods yield unique values for all
     paths of interest.</p>
     @param string the String of interest.
     @return {@code true} if any of the this path's segments contain the specified String; {@code false} otherwise.
     */

    public boolean hasSegmentContainingString( final @NotNull String string ) {

        for ( SegmentName sn : getNames() ) {

            if ( sn.getSegmentName().contains( string ) ) {

                return false;

            }

        }

        return true;

    }

    /**
     Determine if any segment contains any of specified array of strings.
     <p>This can be useful for ensuring that calls to {@link #getCanonicalForm()}
     or the {@code getDelimitedPathString} family of methods yield unique values for all
     paths of interest.</p>
     @param strings the array of strings of interest.
     @return {@code true} if any of the this path's segments contain any of the specified string; {@code false} otherwise.
     */

    public boolean hasSegmentContainingString( final @NotNull String@NotNull[] strings ) {

        for ( SegmentName sn : getNames() ) {

            String segmentName = sn.getSegmentName();
            for ( String string : strings ) {

                if ( segmentName.contains( string ) ) {

                    return false;

                }

            }

        }

        return true;

    }

    /**
     Determine if any segment contains any of specified {@link Collection} of strings.
     <p>This can be useful for ensuring that calls to {@link #getCanonicalForm()}
     or the {@code getDelimitedPathString} family of methods yield unique values for all
     paths of interest.</p>
     @param strings the {@link Collection} of strings of interest.
     @return {@code true} if any of the this path's segments contain any of the specified string; {@code false} otherwise.
     */

    public boolean hasSegmentContainingString( final @NotNull Collection<String> strings ) {

        for ( SegmentName sn : getNames() ) {

            String segmentName = sn.getSegmentName();
            for ( String string : strings ) {

                if ( segmentName.contains( string ) ) {

                    return false;

                }

            }

        }

        return true;

    }

    /**
     Get this path as a string with segment names delimited by a {@code "/"}.
     <p>A call to this method is exactly equivalent to the following call to the one-parameter version of this method</p>
     <blockquote>{@code getDelimitedPathString( "/" )}</blockquote>
     @return the delimited path.
     */

    @NotNull
    public String getSlashDelimitedPathString() {

        return getDelimitedPathString( "/" );

    }

    /**
     Get this path as a string with segment names delimited by a specified delimiter.
     <p>A call to this method of the form</p>
     <blockquote>{@code getDelimitedPathString( a )}</blockquote>
     is exactly equivalent to the following call to the two-parameter version of this method
     <blockquote>{@code getDelimitedPathString( a, a )}</blockquote>
     @param delimiter the delimiter to appear at the start of the string if this instance is an absolute path and between
     pairs of segment names.
     @return the delimited path.
     */

    @NotNull
    public String getDelimitedPathString( final @NotNull String delimiter ) {

        return getDelimitedPathString( delimiter, delimiter );

    }

    /**
     Get this path as a string with segment names delimited by specified delimiters.
     <p>Should the caller care, it is the caller's responsibility to ensure that the specified delimiters
     do not appear within segment names. This could matter if this method is being used to obtain a
     canonical name which is guaranteed to be unique for any unique path. For example, consider the following
     two distinct absolute paths:</p>
     <blockquote><code>AbsolutePath a = new AbsolutePath( new SegmentName( "a/b" ) );</code>
     <br>
     and
     <br><code>AbsolutePath b = new AbsolutePath( new SegmentName[] { new SegmentName( "a" ), new SegmentName( "b" ) } );</code></blockquote>
     Given the above two paths, the following calls both yield the same result ({@code "/a/b"}):
     <blockquote><code>String aPath = a.getDelimitedPathString( "/", "/" );</code>
     <br>
     and
     <br><code>String bPath = b.getDelimitedPathString( "/", "/" );</code></blockquote>
     @param rootString the delimiter to appear at the start of the string if this instance is an absolute path (ignored otherwise).
     @param separator the delimiter to separate pairs of segment names.
     @return the delimited path.
     */

    @NotNull
    public String getDelimitedPathString( final @NotNull String rootString, final @NotNull String separator ) {

        StringBuilder sb = new StringBuilder();
        boolean firstNonRootSegment = true;
        for ( SegmentName sn : getNames() ) {

            if ( sn.equals( SegmentName.ROOT_SEGMENT ) ) {

                sb.append( rootString );

            } else {

                sb.append( firstNonRootSegment ? "" : separator ).append( sn.getSegmentName() );
                firstNonRootSegment = false;

            }

        }

        return sb.toString();

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper,
            final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        GowingPackableCollection<String> segments = new GowingPackableCollection<>();
        Collections.addAll( segments, getStringPathArray() );

        Logger.logMsg( "RelativePath:  bundling " + SEGMENTS + "=" + segments );
        bundle.addPackableEntityHolder( SEGMENTS, segments, packer, true );
        bundle.addStringHolder( COMPARE_VALUE, _compareValue, true );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        if ( !unPacker.isEntityFinished( _segmentsEntityReference ) ) {

            return false;

        }

        @SuppressWarnings("unchecked") GowingPackableCollection<String> segments =
                (GowingPackableCollection<String>)unPacker.resolveMandatoryReference( _segmentsEntityReference );

        SegmentName[] tmpSegments = new SegmentName[segments.size()];
        boolean isAbsolutePath = false;
        int ix = 0;
        for ( String sn : segments ) {

            if ( SegmentName.ROOT_STRING_NAME.equals( sn ) ) {

                tmpSegments[ix] = SegmentName.ROOT_SEGMENT;
                isAbsolutePath = true;

            } else {

                tmpSegments[ix] = new SegmentName( sn );

            }

            ix += 1;

        }

        String expectedCompareValue = finishConstructor( isAbsolutePath, tmpSegments );

        String ourName = isAbsolutePath ? "AbsolutePath" : "RelativePath";

        if ( !expectedCompareValue.equals( _compareValue ) ) {

            throw new IllegalArgumentException(
                    "RelativePath." + ourName + ":  unpacked compare path " + _compareValue +
                    " not identical to computed compare path " + expectedCompareValue
            );

        }

        return true;

    }

}
