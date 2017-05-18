package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Associate zero or more names with an entity.
 */

public class EntityNames {

//    public static final EntityName ANON_ENTITY = new EntityName( "<anon>" );

    private final SortedSet<EntityName> _entityNames = new TreeSet<>();

    private final GowingPackable _entity;

    public EntityNames( @NotNull Collection<EntityName> entityNames, @NotNull GowingPackable entity ) {
	super();

	addAll( entityNames );

	_entity = entity;

    }

    @NotNull
    public Collection<EntityName> getEntityNames() {

	return _entityNames;

    }

    @NotNull
    public GowingPackable getEntity() {

	return _entity;

    }

    public void add( EntityName name ) {

	if ( name == null ) {

	    throw new IllegalArgumentException( "cannot add a null name" );

	}

	_entityNames.add( name );

    }

    public void addAll( Collection<EntityName> moreNames ) {

	for ( EntityName entityName : moreNames ) {

	    if ( entityName == null ) {

		throw new IllegalArgumentException( "cannot add null names" );

	    }

	    _entityNames.add( entityName );

	}

    }

    public String toString() {

	return "EntityNames( names=" + getEntityNames() + ", entity=" + getEntity() + " )";

    }

//    @Override
//    public int compareTo( EntityNames rhs ) {
//
//	return _entityName.compareTo( rhs._entityName );
//
//    }
//
//    public int hashCode() {
//
//	return _entityName.hashCode();
//
//    }
//
//    public boolean equals( Object rhs ) {
//
//	return rhs instanceof EntityNames && compareTo( (EntityNames)rhs ) == 0;
//
//    }

}
