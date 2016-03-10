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
 A derivation of the {@link java.util.Date} class whose instances are immutable.
 <p/>Attempts to call any of the various setters defined in the {@link java.util.Date} class result in an {@link UnsupportedOperationException} being thrown.
 Also, none of the deprecated constructors in the {@link java.util.Date} class exist in this class.
 */

@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
public class ImmutableDate extends Date implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ImmutableDate.class );

    private static final int VERSION = 1;

    protected static final EntityName TIME_MS_NAME = new EntityName( "_tms" );

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    public ImmutableDate() {

	super();

    }

    public ImmutableDate( @NotNull Date date ) {

	super( date.getTime() );

    }

    public ImmutableDate( long date ) {

	super( date );

    }

    public ImmutableDate(
	    GowingUnPacker unPacker,
	    GowingPackedEntityBundle bundle,
	    GowingEntityReference er
    )
	    throws GowingUnPackerParsingException {

	this( bundle.getNotNullField( ImmutableDate.TIME_MS_NAME ).longValue() );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
	    boolean isPackingSuper, GowingPacker packer
    ) {

	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		ImmutableDate.ENTITY_TYPE_NAME,
		ImmutableDate.VERSION,
		null,
		packer.getPackingContext()
	);

	bundle.addHolder( new GowingLongHolder( ImmutableDate.TIME_MS_NAME, getTime(), true ) );

	return bundle;

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker unPacker ) {

	return true;

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

    public void setDate( int date ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setHours( int hours ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMinutes( int minutes ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMonth( int month ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setSeconds( int seconds ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setTime( long time ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setYear( int year ) {

	throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

}
