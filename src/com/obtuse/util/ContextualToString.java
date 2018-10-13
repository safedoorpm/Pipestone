package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingPackableEntityHolder;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Provide an easy way to wrap something with a customized {@code toString()} value.
 */

@SuppressWarnings("unused")
public class ContextualToString<T extends Comparable<T>> implements Comparable<ContextualToString<T>>, GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ContextualToString.class );
    private static final int VERSION = 1;
    private static final EntityName G_ITEM = new EntityName( "_item" );
    private static final EntityName G_TO_STRING = new EntityName( "_ts" );

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
        ) {

            return new ContextualToString( unPacker, bundle );

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private T _item;
    private final String _toString;

    private GowingEntityReference _itemReference;

    public ContextualToString( final @NotNull T item, final @NotNull String toString ) {
        super();

        _item = item;

        _toString = toString;

    }

    public ContextualToString( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {
        super();

        _itemReference = bundle.getMandatoryEntityReference( G_ITEM );

        _toString = bundle.MandatoryStringValue( G_TO_STRING );

    }

    public T getItem() {

        return _item;

    }

    public String toString() {

        return _toString;

    }

    @Override
    public int compareTo( @NotNull final ContextualToString<T> o ) {

        return getItem().compareTo( o.getItem() );

    }

    @Override
    public int hashCode() {

        return getItem().hashCode();

    }

    public boolean equals( final @Nullable Object rhs ) {

        if ( rhs instanceof ContextualToString ) {

            @SuppressWarnings("unchecked") ContextualToString<T> cRhs = (ContextualToString<T>)rhs;

            return getItem().equals( cRhs.getItem() );

        }

        return false;

    }

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        if ( _item != null && !(_item instanceof GowingPackable) ) {

            throw new IllegalArgumentException(
                    "ContextualToString.bundleThyself:  unable to bundle ourselves because our wrapped item's class " +
                    _item.getClass().getCanonicalName() + " is not GowingPackable"
            );

        }

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ContextualToString.ENTITY_TYPE_NAME,
                ContextualToString.VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingPackableEntityHolder( G_ITEM, (GowingPackable)_item, packer, true ) );
        bundle.addHolder( new GowingStringHolder( G_TO_STRING, _toString, true ) );

        return bundle;
    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        @SuppressWarnings("unchecked") T tmpItem = (T)unPacker.resolveMandatoryReference( _itemReference );
        _item = tmpItem;

        return true;

    }

}
