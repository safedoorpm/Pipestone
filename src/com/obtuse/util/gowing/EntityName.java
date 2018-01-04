package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity names.
 <p/>
 Don't be tempted to make this class {@link GowingPackable}. Life is simpler if instances of this class are packed and
 unpacked as strings.
 The reason is a bit complicated but it boils down to the fact that instances of this class appear in a few places which,
 if this class is packable, create really ugly chicken-and-egg problems.
 Use {@link GowingPackableName} if you want a name that can pack and unpack itself.
 */

public final class EntityName implements Comparable<EntityName> {

    private final String _name;

    public EntityName( @NotNull final String name ) {

        super();

        _name = name.trim();

    }

    @NotNull
    public String getName() {

        return _name;

    }

    public String toString() {

        return _name;

    }

    public int length() {

        return _name.length();

    }

    public boolean isEmpty() {

        return _name.isEmpty();

    }

    @Override
    public int compareTo( @NotNull final EntityName entityName ) {

        return _name.compareTo( entityName._name );

    }

    @Override
    public boolean equals( final Object rhs ) {

        return rhs instanceof EntityName && compareTo( (EntityName)rhs ) == 0;

    }

    @Override
    public int hashCode() {

        return _name.hashCode();

    }

}
