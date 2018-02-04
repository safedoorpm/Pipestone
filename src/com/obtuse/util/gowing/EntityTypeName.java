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
     @param classInstance the {@link GowingPackable} class to be encapsulated.
     */

    public EntityTypeName( @NotNull final Class<? extends GowingPackable> classInstance ) {
        super();

        _name = classInstance.getCanonicalName();

    }

    /**
     Encapsulate the name of a {@link GowingPackable} type.
     <p>It is almost surely safer to use the {@link #EntityTypeName(Class)} as it is much more difficult to introduce a typo into the game.
     This constructor exists to cover those rare situations where there is not actually a Java class with the name that an instance of
     this class is needed to specify (note sure when that situation might arise but there seems little harm in providing this constructor
     even if it is somewhat like a knife that is sharp at both ends).</p>
     @param canonicalClassName the {@link String} canonical name of the class which is to be encapsulated.
     */

    public EntityTypeName( @NotNull final String canonicalClassName ) {
        super();

        _name = canonicalClassName;

    }

    /**
     Encapsulate the name of a {@link GowingPackable} type's {@link String} name.
     <p>This constructor is intended to be used internally by Gowing (specifically, Gowing unpackers).
     Using this constructor anywhere else is likely to lead to a somewhat painful "learning opportunity".</p>
     @param typeNameToken a {@link StdGowingTokenizer.GowingToken2} instance containing the name of a {@link GowingPackable} type.
     */

    public EntityTypeName( final StdGowingTokenizer.GowingToken2 typeNameToken ) {
        super();

        _name = typeNameToken.stringValue();

    }

    /**
     Get the {@link String} name of the {@link GowingPackable} class for which this factory is responsible.
     @return the {@link String} name of the class that this factory is responsible for.
     */

    public String getTypeName() {

        return _name;

    }

    /**
     The proverbial {@link Object#toString()} method.
     @return the {@link String} name of the class that this factory is responsible for.
     */

    public String toString() {

        return _name;

    }

    /**
     The proverbial {@link Comparable#compareTo(Object)} method.
     @param rhs the other {@code EntityTypeName} instance.
     @return the result of comparing this instance's name with the other instance's name using
     the standard {@link String#compareTo(String)} method which is equivalent to {@code this.getName().compareTo( rhs.getName() )}
     */

    @Override
    public int compareTo( @NotNull final EntityTypeName rhs ) {

        return _name.compareTo( rhs._name );

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
