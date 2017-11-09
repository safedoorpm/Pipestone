/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import com.obtuse.util.gowing.p2a.holders.GowingLongHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 An immutable date which caches certain formatted versions.
 */

@SuppressWarnings({ "InstanceVariableNamingConvention", "InstanceMethodNamingConvention", "UnusedDeclaration" })
public class FormattedImmutableDate extends ImmutableDate implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( FormattedImmutableDate.class );

    private static final int VERSION = 1;

    public static GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

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
	public GowingPackable createEntity( @NotNull final GowingUnPacker unPacker, @NotNull final GowingPackedEntityBundle bundle, final GowingEntityReference er )
		throws GowingUnPackerParsingException {

	    return new FormattedImmutableDate( unPacker, bundle, er );

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

    public FormattedImmutableDate( @NotNull final Date date ) {
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
     @param bundle the bundle representing the instance to be recovered.
     @param er the entity reference for the instance being recovered.
     @throws GowingUnPackerParsingException if something goes wrong.
     */

    public FormattedImmutableDate(
            final GowingUnPacker unPacker,
            final GowingPackedEntityBundle bundle,
            final GowingEntityReference er
    )
	    throws GowingUnPackerParsingException {

	this( bundle.getNotNullField( ImmutableDate.TIME_MS_NAME ).longValue() );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final GowingPacker packer
    ) {

	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		FormattedImmutableDate.ENTITY_TYPE_NAME,
		FormattedImmutableDate.VERSION,
		null,
		packer.getPackingContext()
	);

	bundle.addHolder( new GowingLongHolder( ImmutableDate.TIME_MS_NAME, getTime(), true ) );

	return bundle;

    }

    @Override
    public boolean finishUnpacking( final GowingUnPacker unPacker ) {

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

}