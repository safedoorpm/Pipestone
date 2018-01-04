package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 The packable name of something.
 */

public class GowingPackableName implements GowingPackable, Comparable<GowingPackableName> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableName.class );

    private static final int VERSION = 1;
    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( GowingPackableName.ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return GowingPackableName.VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return GowingPackableName.VERSION;

        }

        @NotNull
        @Override
        public GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                final GowingEntityReference er
        ) {

            return new GowingPackableName( unPacker, bundle );

        }

    };
    private static final EntityName NAME_NAME = new EntityName( "_ban" );
    private static SortedMap<String, GowingPackableName> s_knownAttributeNames = new TreeMap<>();
    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );
    private String _name;

    public GowingPackableName( @NotNull final String name ) {

        super();

        _name = name;

    }

    public GowingPackableName( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle ) {

        this( bundle.getNotNullField( NAME_NAME ).StringValue() );

    }

    @NotNull
    public String getName() {

        return _name;

    }

    @Override
    public int compareTo( @NotNull final GowingPackableName rhs ) {

        return getName().compareTo( rhs.getName() );

    }

    @Override
    public boolean equals( final Object rhs ) {

        return rhs instanceof GowingPackableName && compareTo( (GowingPackableName)rhs ) == 0;

    }

    @Override
    public int hashCode() {

        return getName().hashCode();

    }

    @NotNull
    @Override
    public GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, @NotNull final GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( NAME_NAME, getName(), true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

}
