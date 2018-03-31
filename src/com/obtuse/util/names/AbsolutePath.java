/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.names;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 Represent an absolute path/name.
 <p/>Instances of this class are immutable.
 */

public class AbsolutePath extends RelativePath {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( AbsolutePath.class );

    private static final int VERSION = 1;

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

            return new AbsolutePath( unPacker, bundle );

        }

    };

    public static final AbsolutePath ABSOLUTE_ROOT_PATH = new AbsolutePath();

    public AbsolutePath( final @NotNull SegmentName@NotNull[] segments ) {
        super( segments );
    }

    public AbsolutePath( final @NotNull Collection<SegmentName> segments ) {
        super( segments.toArray( new SegmentName[0] ) );

    }

    public AbsolutePath( final @NotNull SegmentName segmentName ) {
        super( new SegmentName[] { segmentName } );

    }

    public AbsolutePath( final @NotNull String... segmentName ) {
        super( segmentName );
//        super( new SegmentName[] { new SegmentName( segmentName ) } );

    }

    public AbsolutePath() {
        super( new SegmentName[] { SegmentName.ROOT_SEGMENT } );

    }

    private AbsolutePath( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {
        super( unPacker, bundle.getSuperBundle() );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        @SuppressWarnings("UnnecessaryLocalVariable") GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleThyself( true, packer ),
                packer.getPackingContext()
        );

        return bundle;

    }

    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        if ( super.finishUnpacking( unPacker ) ) {

            Logger.logMsg( "done unpacking absolute path " + this );

            return true;

        } else {

            Logger.logMsg( "NOT done unpacking absolute path (too dangerous to invoke toString())" );

            return false;

        }

    }

    /**
     Yield a relative path that has the same segments as this absolute path minus the root segment that starts all absolute paths.

     @return a relative path equivalent to this path but without the starting root node.
     */
    public RelativePath makeRelative() {

        List<SegmentName> segments = getNames();
        if ( segments.isEmpty() ) {

            throw new HowDidWeGetHereError( "AbsolutePath.makeRelative:  absolute paths cannot be empty (they start with a root segment) - " + this );

        }

        if ( SegmentName.ROOT_SEGMENT.equals( segments.get(0) ) ) {

            List<SegmentName> relativeSegments = new ArrayList<>();
            for ( int ix = 1; ix < segments.size(); ix += 1 ) {

                relativeSegments.add( segments.get( ix ) );

            }

            RelativePath relativePath = new RelativePath( relativeSegments );

            AbsolutePath newRootPath = concat( ABSOLUTE_ROOT_PATH, relativePath );
            if ( newRootPath.equals( this ) ) {

                Logger.logMsg( "AbsolutePath.makeRelative:  turning " + this + " into a relative path yielded apparently correct result " + relativePath );

                return relativePath;

            } else {

                throw new HowDidWeGetHereError(
                        "AbsolutePath.makeRelative:  path " + this + " yielded path " + relativePath +
                        " that is not equal to this path after concatenating it to an otherwise empty root path" +
                        " (instead, we got " + newRootPath + ")"
                );

            }

        } else {

            throw new HowDidWeGetHereError( "AbsolutePath.makeRelative:  absolute paths most start with a root segment) - " + this );

        }

    }

    public static AbsolutePath concat( final @NotNull AbsolutePath head, final @NotNull SegmentName tail ) {

        return concat( head, new RelativePath( tail ) );

    }

    public static AbsolutePath concat( final @NotNull AbsolutePath head, final @NotNull RelativePath tail ) {

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
