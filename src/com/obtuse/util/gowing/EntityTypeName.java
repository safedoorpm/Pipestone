package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.StdGowingTokenizer;
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

    /**
     Encapsulate the name of a {@link GowingPackable} type.
     @param className the {@link GowingPackable} type to be encapsulated.
     */

    public EntityTypeName( final Class<? extends GowingPackable> className ) {
        super();

        _name = className.getCanonicalName();

    }

    /**
     Encapsulate the name of a {@link GowingPackable} type's {@link String} name.
     <p>This constructor is intended to be used internally by Gowing (specifically, Gowing unpackers).
     Using this constructor anywhere else is likely to lead to a somewhat painful "learning opportunity".</p>
     @param typeNameToken the {@link String} name of a {@link GowingPackable} type.
     */

    public EntityTypeName( final StdGowingTokenizer.GowingToken2 typeNameToken ) {
        super();

        _name = typeNameToken.stringValue();

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
