package com.obtuse.util.gowing.packer2;

import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.GowingUnPacker2ParsingException;
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

public abstract class GowingEntityFactory2 implements Comparable<GowingEntityFactory2> {

    private final EntityTypeName2 _entityTypeName;

    @SuppressWarnings("WeakerAccess")
    protected GowingEntityFactory2( @NotNull EntityTypeName2 entityTypeName ) {
	super();

	_entityTypeName = entityTypeName;

    }

    public EntityTypeName2 getTypeName() {

	return _entityTypeName;

    }

    public abstract int getOldestSupportedVersion();
    public abstract int getNewestSupportedVersion();

    @NotNull
    public abstract GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) throws GowingUnPacker2ParsingException;

    @NotNull
    public EntityTypeName2 getEntityTypeName() {

	return _entityTypeName;

    }

    public int compareTo( @NotNull GowingEntityFactory2 rhs ) {

	return _entityTypeName.compareTo( rhs._entityTypeName );

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof GowingEntityFactory2 && compareTo( (GowingEntityFactory2)rhs ) == 0;

    }

    public int hashCode() {

	return _entityTypeName.hashCode();

    }

    @NotNull
    public String toString() {

	return "EntityFactory( \"" + _entityTypeName + "\" )";

    }

}
