/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 A derivation of the {@link java.util.Date} class whose instances are immutable.
 <p/>Attempts to call any of the various setters defined in the {@link java.util.Date} class result in an
 {@link UnsupportedOperationException} being thrown.
 Also, none of the deprecated constructors in the {@link java.util.Date} class exist in this class.
 */

@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
public class ImmutableDate extends Date implements GowingBackReferenceable {

    protected static final EntityName TIME_MS_NAME = new EntityName( "_tms" );
    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ImmutableDate.class );
    private static final int VERSION = 1;
    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    public ImmutableDate() {

        super();

    }

    public ImmutableDate( final @NotNull Date date ) {

        super( date.getTime() );

    }

    public ImmutableDate( final long date ) {

        super( date );

    }

    public ImmutableDate(
            final GowingUnPacker unPacker,
            final GowingPackedEntityBundle bundle,
            final GowingEntityReference er
    ) {

        this( bundle.getNotNullField( ImmutableDate.TIME_MS_NAME ).longValue() );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ImmutableDate.ENTITY_TYPE_NAME,
                ImmutableDate.VERSION,
                packer.getPackingContext()
        );

        bundle.addLongHolder( ImmutableDate.TIME_MS_NAME, getTime() );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        return true;

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    public void setDate( final int date ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setHours( final int hours ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMinutes( final int minutes ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMonth( final int month ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setSeconds( final int seconds ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setTime( final long time ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setYear( final int year ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public String toString() {

        return "ImmutableDate( " + super.toString() + " )";

    }

}
