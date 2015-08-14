package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

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

public abstract class EntityFactory1 implements Comparable<EntityFactory1> {

    private final String _entityName;
    private Short _entityTypeId;
    private boolean _locked = false;

    protected EntityFactory1( @NotNull String entityName, short entityTypeId ) {
	super();

	_entityName = entityName;
	_entityTypeId = entityTypeId;

    }

    protected EntityFactory1( @NotNull String entityName ) {
	super();

	_entityName = entityName;
	_entityTypeId = 0;

    }

    public void reconfigTypeId( short newTypeId ) {

	if ( isLockedDown() ) {

	    throw new IllegalArgumentException( "" + this + ":  cannot change type id for factory that is locked" );

	}

	_entityTypeId = newTypeId;

    }

    @NotNull
    public EntityFactory1 lockdown() {

	_locked = true;

	return this;

    }

    public boolean isLockedDown() {

	return _locked;

    }

    public abstract Packable1 createEntity( @NotNull UnPacker1 unPacker );

    @NotNull
    public String getEntityName() {

	return _entityName;

    }

    @NotNull
    public Short getEntityTypeId() {

	return _entityTypeId;

    }

    public int compareTo( EntityFactory1 rhs ) {

	return _entityName.compareTo( rhs._entityName );

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof EntityFactory1 && compareTo( (EntityFactory1)rhs ) == 0;

    }

    public int hashCode() {

	return _entityName.hashCode();

    }

    @NotNull
    public String toString() {

	return "EntityFactory( \"" + _entityName + "\" -> " + _entityTypeId + " )";

    }

}
