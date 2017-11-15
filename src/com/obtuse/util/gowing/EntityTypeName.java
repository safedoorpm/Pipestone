package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the type name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity type names.
 <p/>
 Don't be tempted to make this class {@link GowingPackable}. Life is simpler if instances of this class are packed and unpacked as strings.
 The reason is a bit complicated but it boils down to the fact that instances of this class appear in a few places which, if this class is packable, it creates really ugly chicken-and-egg problems.
 Use {@link GowingPackableName} if you want a name that can pack and unpack itself.
 */

public final class EntityTypeName implements Comparable<EntityTypeName> {

    private final String _name;

    public EntityTypeName( final Class<? extends GowingPackable> className ) {

        this( className.getCanonicalName() );
    }

    public EntityTypeName( final String name ) {

        super();

        _name = name;

    }

    public String getTypeName() {

        return _name;

    }

    public String toString() {

        return _name;

    }

    public int length() {

        return _name.length();

    }

    @Override
    public int compareTo( @NotNull final EntityTypeName typeName2 ) {

        return _name.compareTo( typeName2._name );

    }

    @Override
    public boolean equals( final Object rhs ) {

        return rhs instanceof EntityTypeName && compareTo( (EntityTypeName)rhs ) == 0;

    }

    @Override
    public int hashCode() {

        return _name.hashCode();

    }

}
