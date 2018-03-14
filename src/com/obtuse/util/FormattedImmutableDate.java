/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingLongHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 An immutable date which caches certain formatted variants.
 */

@SuppressWarnings({ "InstanceVariableNamingConvention", "InstanceMethodNamingConvention", "UnusedDeclaration" })
public class FormattedImmutableDate extends ImmutableDate implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( FormattedImmutableDate.class );

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

        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            return new FormattedImmutableDate( unPacker, bundle );

        }

    };

    private String _yyyy_mm_dd = null;

    private String _yyyy_mm_dd_hh_mm_ss = null;

    private String _yyyy_mm_dd_hh_mm = null;

    /**
     Create an instance which represents the current time.
     */

    public FormattedImmutableDate() {

        super();

    }

    /**
     Create an instance which represents the time of a specified {@link Date} instance.

     @param date the specified {@link Date} instance.
     @throws IllegalArgumentException if <code>date</code> is <code>null</code>.
     */

    public FormattedImmutableDate( final @NotNull Date date ) {

        super( date );

    }

    /**
     Create an instance which represents a time specified in milliseconds since the Java epoch.

     @param timeMs the time since the Java epoch that this instance should represent.
     */

    public FormattedImmutableDate( final long timeMs ) {

        super( timeMs );

    }

    /**
     Recover an instance from a {@link GowingPackedEntityBundle} instance.

     @param unPacker the active Gowing unpacker.
     @param bundle   the bundle representing the instance to be recovered.
     @throws GowingUnpackingException if something goes wrong.
     */

    public FormattedImmutableDate(
            final GowingUnPacker unPacker,
            final GowingPackedEntityBundle bundle
    ) {

        this( bundle.getNotNullField( ImmutableDate.TIME_MS_NAME ).longValue() );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                FormattedImmutableDate.ENTITY_TYPE_NAME,
                FormattedImmutableDate.VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingLongHolder( ImmutableDate.TIME_MS_NAME, getTime(), true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        return true;

    }

    public String getYYYY_MM_DD() {

        if ( _yyyy_mm_dd == null ) {

            _yyyy_mm_dd = DateUtils.formatYYYY_MM_DD( this );

        }

        return _yyyy_mm_dd;

    }

    public String getYYYY_MM_DD_HH_MM_SS() {

        if ( _yyyy_mm_dd_hh_mm_ss == null ) {

            _yyyy_mm_dd_hh_mm_ss = DateUtils.formatYYYY_MM_DD_HH_MM_SS( this );

        }

        return _yyyy_mm_dd_hh_mm_ss;

    }

    public String getYYYY_MM_DD_HH_MM() {

        if ( _yyyy_mm_dd_hh_mm == null ) {

            _yyyy_mm_dd_hh_mm = DateUtils.formatYYYY_MM_DD_HH_MM( this );

        }

        return _yyyy_mm_dd_hh_mm;

    }

    /**
     Format this instance.
     @return the value returned by calling {@link #getYYYY_MM_DD_HH_MM_SS()}.
     */

    public String toString() {

        return getYYYY_MM_DD_HH_MM_SS();

    }

}