/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing.p2a;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

/**
 A String that is {@link GowingPackable}.
 <p>While it is almost always simplest to store a string directly, I have found a few situations where wrapping a string
 inside of something that is {@link GowingPackable} is useful.
 <p>For example, you might be packing an abstract generic class where one of the fields is actually an {@link Object}
 because the generic declaration is very broad. You happen to know that the class is only implemented in a very controlled
 situation where you <b><u>KNOW</u></b> that it is always either a string or some {@code GowingPackable} class.
 Being able to pack it as a {@code GowingPackable} instance simplifies unpacking.</p>
 <p>There will probably soon be variants on this class that wrap the other primitive wrapper Java types including
 {@code byte}, {@code Byte},
 {@code double}, {@code Double},
 {@code float}, {@code Float},
 {@code int}, {@code Integer},
 {@code long}, and {@code Long}.
 I, Danny, want to gain some experience with this class first to make sure that I get this family of classes right the first time (famous last words).
 </p>
 <p>Instances of this class are immutable. This class' {@link #hashCode()}, {@link #equals(Object)}, and {@link #compareTo(GowingString)} methods are
 consistent with each other.</p>
 */

public class GowingString implements GowingBackReferenceable, Comparable<GowingString> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingString.class );

    private static final int VERSION = 1;

    private static final EntityName G_STRING = new EntityName( "_s" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @SuppressWarnings("RedundantThrows")
        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            return new GowingString( bundle.MandatoryStringValue( G_STRING ) );

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    @NotNull
    public final String string;

    public GowingString( final @NotNull String string ) {

        super();

        this.string = string;

    }

    @NotNull
    public String getString() {

        return this.string;

    }

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                GowingString.ENTITY_TYPE_NAME,
                GowingString.VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( G_STRING, string, true ) );

        return bundle;
    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

    @Override
    public int hashCode() {

        return string.hashCode();

    }

    @Override
    public int compareTo( final @NotNull GowingString rhs ) {

        return string.compareTo( rhs.string );

    }

    @Override
    public boolean equals( final Object rhs ) {

        return rhs instanceof GowingString && compareTo( (GowingString)rhs ) == 0;

    }

    public String toString() {

        return string;

    }

}
