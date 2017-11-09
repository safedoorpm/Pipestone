package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
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
 */

public abstract class GowingEntityFactory implements Comparable<GowingEntityFactory> {

    private final EntityTypeName _entityTypeName;

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
	    GowingEntityReference er
    ) throws GowingUnPackerParsingException;

    @NotNull
    public EntityTypeName getEntityTypeName() {

	return _entityTypeName;

    }

    public int compareTo( @NotNull final GowingEntityFactory rhs ) {

	return _entityTypeName.compareTo( rhs._entityTypeName );

    }

    public boolean equals( final Object rhs ) {

	return rhs instanceof GowingEntityFactory && compareTo( (GowingEntityFactory)rhs ) == 0;

    }

    public int hashCode() {

	return _entityTypeName.hashCode();

    }

    @NotNull
    public String toString() {

	return "EntityFactory( \"" + _entityTypeName + "\" )";

    }

}
