/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.names;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 Represent an absolute path/name.
 <p/>Instances of this class are immutable.
 */

public class AbsolutePath extends RelativePath {

    public static final AbsolutePath ABSOLUTE_ROOT_PATH = new AbsolutePath();

    public AbsolutePath( @NotNull final SegmentName[] segments ) {
        super( segments );
    }

    public AbsolutePath( @NotNull final Collection<SegmentName> segments ) {
        super( segments.toArray( new SegmentName[segments.size()] ) );

    }

    public AbsolutePath( @NotNull final SegmentName segmentName ) {
        super( new SegmentName[] { segmentName } );

    }

    public AbsolutePath( @NotNull final String segmentName ) {
        super( new SegmentName[] { new SegmentName( segmentName ) } );

    }

    public AbsolutePath() {
        super( new SegmentName[] { SegmentName.ROOT_SEGMENT } );

    }

    public static AbsolutePath concat( @NotNull final AbsolutePath head, @NotNull final SegmentName tail ) {

        return concat( head, new RelativePath( tail ) );

    }

    public static AbsolutePath concat( @NotNull final AbsolutePath head, @NotNull final RelativePath tail ) {

        RelativePath rval = concat( (RelativePath)head, tail );

        // rval simply must be an AbsolutePath since the combined path will have started with a ROOT_SEGMENT segment.
        if ( rval instanceof AbsolutePath ) {

            return (AbsolutePath)rval;

        } else {

            throw new HowDidWeGetHereError( "AbsolutePath concat( " + head + ", " + tail + " ) yielded a non-absolute result " + rval );

        }

    }

    public String toString() {

        return super.toString();

    }

}
