package com.obtuse.util.packers.packer2;

import com.obtuse.util.packers.packer1.Packable1;
import com.obtuse.util.packers.packer1.UnPacker1;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Create an entity of some type.
 <p/>
 IMPORTANT:
 Things will get really ugly if the name of an entity type is changed once instances of the entity have been
 saved into save files or transmitted to other applications. Consequently, the name of each entity type must be
 immutable once application development reaches the point where the application is being used in scenarios where
 old save files must be restorable and old versions of application components must be able to communicate with current
 or even future versions of the same or different application components.
 */

public abstract class EntityFactory2 implements Comparable<EntityFactory2> {

    private EntityTypeName2 _entityTypeName;

    protected EntityFactory2( @NotNull EntityTypeName2 entityTypeName ) {
	super();

	_entityTypeName = entityTypeName;

    }

//    public void reconfigTypeId( short newTypeId ) {
//
//	if ( isLockedDown() ) {
//
//	    throw new IllegalArgumentException( "" + this + ":  cannot change type id for factory that is locked" );
//
//	}
//
//	_entityTypeId = newTypeId;
//
//    }
//
//    @NotNull
//    public EntityFactory2 lockdown() {
//
//	_locked = true;
//
//	return this;
//
//    }
//
//    public boolean isLockedDown() {
//
//	return _locked;
//
//    }

    public abstract Packable2 createEntity( @NotNull UnPacker2 unPacker, PackableState state );

    @NotNull
    public EntityTypeName2 getEntityTypeName() {

	return _entityTypeName;

    }

    public int compareTo( EntityFactory2 rhs ) {

	return _entityTypeName.compareTo( rhs._entityTypeName );

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof EntityFactory2 && compareTo( (EntityFactory2)rhs ) == 0;

    }

    public int hashCode() {

	return _entityTypeName.hashCode();

    }

    @NotNull
    public String toString() {

	return "EntityFactory( \"" + _entityTypeName + "\" )";

    }

}
