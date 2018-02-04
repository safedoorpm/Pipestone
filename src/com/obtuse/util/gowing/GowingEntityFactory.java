package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Create a packable entity of some type.
 <p/>
 IMPORTANT:
 Things will get really ugly if the name of an entity type is changed once instances of the entity have been
 saved into save files or transmitted to other applications. Consequently, the name of each entity type must be
 immutable once application development reaches the point where the application is being used in scenarios where
 old save files must be restorable and old versions of application components must be able to communicate with current
 or even future versions of the same or different application components.
 <p>This isn't quite true. It is possible to safely rename a class for which there are already packed instances on long-term storage
 by providing two factories, one for the old class and one for the new, which both create instances of the new class.</p>
 */

public abstract class GowingEntityFactory implements Comparable<GowingEntityFactory> {

    private final EntityTypeName _entityTypeName;

    /**
     Create a factory that creates instance of a {@link GowingPackable} class.
     <p>Note that while unusual, it is acceptable for the {@link EntityTypeName} provided to this constructor to be incorrect.
     This subterfuge allows one to support the renaming of a {@link GowingPackable} class which already has packed instances in long-term
     storage (by having two factories for the class, one created using this constructor with the old name of the class and one
     created using this constructor with the class's new name, it will be possible to recover packed instances of the
     old class by having the factory defined to create instances of the old name to actually create instances of the newly renamed class)
     (put another way, the two factories will be 100% identical except for the name of the class that was passed to this constructor when
     they were created).
     Note that in this situation, the two factories really should be identical except for the name of the class that was passed to this constructor.
     This includes each factory's {@link #getTypeName()} method returning the name of the class for which they claim to be responsible
     as this maintains the <em>illusion</em> that each factory is actually creating instances of different classes.</p>
     @param entityTypeName the {@link EntityTypeName} canonical name of the class which is to be encapsulated.
     */

    @SuppressWarnings("WeakerAccess")
    protected GowingEntityFactory( @NotNull final EntityTypeName entityTypeName ) {

        super();

        _entityTypeName = entityTypeName;

    }

    public EntityTypeName getTypeName() {

        return _entityTypeName;

    }

    public abstract int getOldestSupportedVersion();

    public abstract int getNewestSupportedVersion();

    @NotNull
    public abstract GowingPackable createEntity(
            @NotNull GowingUnPacker unPacker,
            @NotNull GowingPackedEntityBundle bundle,
            @NotNull GowingEntityReference er
    ) throws GowingUnpackingException;

    @NotNull
    public EntityTypeName getEntityTypeName() {

        return _entityTypeName;

    }

    public final int compareTo( @NotNull final GowingEntityFactory rhs ) {

        return _entityTypeName.compareTo( rhs._entityTypeName );

    }

    public final boolean equals( final Object rhs ) {

        return rhs instanceof GowingEntityFactory && compareTo( (GowingEntityFactory)rhs ) == 0;

    }

    public final int hashCode() {

        return _entityTypeName.hashCode();

    }

    @NotNull
    public String toString() {

        return "EntityFactory( \"" + _entityTypeName + "\" )";

    }

}
