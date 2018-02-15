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

    private final SortedSet<EntityName> _entityNames = new TreeSet<>();

    private final GowingPackable _entity;

    public EntityNames( final @NotNull Collection<EntityName> entityNames, final @NotNull GowingPackable entity ) {

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

    public void add( final EntityName name ) {

        if ( name == null ) {

            throw new IllegalArgumentException( "cannot add a null name" );

        }

        _entityNames.add( name );

    }

    public void addAll( final Collection<EntityName> moreNames ) {

        for ( EntityName entityName : moreNames ) {

            if ( entityName == null ) {

                throw new IllegalArgumentException( "cannot add null names" );

            }

            _entityNames.add( entityName );

        }

    }

    public String getPrintableDescription() {

        return "" + ( getEntityNames().isEmpty() ? "unknown" : getEntityNames() );

    }

    public String toString() {

        return "EntityNames( names=" + getEntityNames() + ", entity=" + getEntity() + " )";

    }

}
