package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 Associate zero or more names with an entity.
 */

public class EntityNames {

//    public static final EntityName2 ANON_ENTITY = new EntityName2( "<anon>" );

    private final SortedSet<EntityName2> _entityNames = new TreeSet<EntityName2>();

    private final GowingPackable2 _entity;

    public EntityNames( @NotNull Collection<EntityName2> entityNames, @NotNull GowingPackable2 entity ) {
	super();

	addAll( entityNames );

	_entity = entity;

    }

    @NotNull
    public Collection<EntityName2> getEntityNames() {

	return _entityNames;

    }

    @NotNull
    public GowingPackable2 getEntity() {

	return _entity;

    }

    public void add( EntityName2 name ) {

	if ( name == null ) {

	    throw new IllegalArgumentException( "cannot add a null name" );

	}

	_entityNames.add( name );

    }

    public void addAll( Collection<EntityName2> moreNames ) {

	for ( EntityName2 entityName : moreNames ) {

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
