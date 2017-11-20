/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.names;

import org.jetbrains.annotations.NotNull;

/**
 Represent a segment of an absolute or relative path.
 <p/>Instances of this class are immutable.
 */

public class SegmentName implements Comparable<SegmentName> {

    /**
     The string name of the root segment.
     */

    public static final String ROOT_STRING_NAME = "[root]";

    /**
     The root segment.
     <p/>There is EXACTLY one root segment in the visible universe (unlikely to ever change although it is still probably not a good idea to rely on this fact).
     */

    public static final SegmentName ROOT_SEGMENT = new SegmentName( ROOT_STRING_NAME );

    /**
     The string name of an empty segment.
     */

    public static final String EMPTY_STRING_NAME = "[empty]";

    /**
     A segment named {@code "[empty]"}.
     <p/>Feel free to use this anywhere that you want an empty segment (I cannot imagine what empty segments are good for but I cannot convince myself to ban them).
     Alternatively, feel free to create an empty segment yourself using <blockquote>{@code new SegmentName("")}</blockquote>
     Note that unlike the {@link #ROOT_SEGMENT}, there can be any number of segments named {@code "[empty]"} in the visible universe.
     */

    public static final SegmentName EMPTY_NAME = new SegmentName( EMPTY_STRING_NAME );

    /**
     The separator used when converting a path into its string representation via {@code toString()}.
     */

    public static final String SEPARATOR = "->";

    private final String _segmentName;

    private static boolean s_haveRootNode = false;

    /**
     Create a segment with a particular name.
     @param segmentName the segment's name. If the specified name is {@code ""} then {@code "[empty]"} is used instead.
     @throws IllegalArgumentException
     <ul>
     <li>if the segment name starts with a {@code '['} and ends with a {@code ']'} but is called something
     other than one of the special names {@code "[root]"} or {@code "[empty]"}.</li>
     <li>if the segment name is {@code "[root]"} after the special segment {@link #ROOT_SEGMENT} has already been created (there is only room in this universe for one root node).</li>
     </ul>
     */

    public SegmentName( @NotNull final String segmentName ) {
        super();

        String trimmedSegmentName = segmentName.trim();
        if ( ROOT_STRING_NAME.equals( trimmedSegmentName ) ) {

            synchronized ( SegmentName.class ) {

                if ( s_haveRootNode ) {

                    // There's only room in this universe for one ROOT node.

                    throw new IllegalArgumentException( "SegmentName:  there can only be one true root thus thou art an evil usurper!!!" );

                } else {

                    s_haveRootNode = true;

                }

            }

        } else if ( trimmedSegmentName.isEmpty() ) {

            trimmedSegmentName = "[empty]";

        }

        if ( trimmedSegmentName.startsWith( "[" ) && trimmedSegmentName.endsWith( "]" ) ) {

            if ( !ROOT_STRING_NAME.equals( trimmedSegmentName ) && !EMPTY_STRING_NAME.equals( trimmedSegmentName ) ) {

                throw new IllegalArgumentException(
                        "SegmentName:  illegal segment name \"" + trimmedSegmentName + "\"" +
                        " (looks like an internal segment name like \"" + ROOT_STRING_NAME + "\")"
                );

            }

        }

        _segmentName = trimmedSegmentName;

    }

    /**
     Get this segment's name.
     @return this segment's name.
     */

    @NotNull
    public String getSegmentName() {

        return _segmentName;

    }

    /**
     Return this segment as the only segment in a relative path.
     @return this segment as the only segment in a relative path.
     @throws IllegalArgumentException if this method is invoked on the {@link #ROOT_SEGMENT}.
     */

    public RelativePath asRelativePath() {

        return new RelativePath( this );

    }

    /**
     Return this segment's name (exactly equivalent to {@link #getSegmentName()}).
     @return this segment's name.
     */

    @NotNull
    public String toString() {

        return _segmentName;

    }

    /**
     Determine if this instance has the same name as some other instance of this class.
     @param rhs any other object or {@code null}.
     @return {@code true} if {@code rhs} is an instance of this class with the same name as this class; {@code false} otherwise.
     */

    public boolean equals( final Object rhs ) {

        return rhs instanceof SegmentName && _segmentName.compareTo( ((SegmentName)rhs)._segmentName ) == 0;

    }

    /**
     Compare this instance to a specified instance of this class.
     @param rhs the specified instance.
     @return the result of comparing this instance's name with the specified instance's name.
     In other words, exactly equivalent to
     <blockquote>{@code getSegmentName().compareTo( rhs.getSegmentName() )}</blockquote>
     This method returns 0 if-and-only-if {@code equals( rhs )} would return true.
     */

    public int compareTo( @NotNull final SegmentName rhs ) {

        return _segmentName.compareTo( rhs._segmentName );

    }

    /**
     Compute a hashcode on this instance.
     <p/>Exactly equivalent to <blockquote>{@code getSegmentName().hashCode()}</blockquote>
     @return a hashcode value for this instance.
     */

    public int hashCode() {

        return _segmentName.hashCode();

    }

}