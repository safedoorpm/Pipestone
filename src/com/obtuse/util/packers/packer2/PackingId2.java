package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Describe a v2 packing id.
 */

public class PackingId2 implements Comparable<PackingId2> {

    private final EntityTypeName2 _entityTypeName;
    private final Long _entityId;

    public PackingId2( EntityTypeName2 entityTypeName, long entityId ) {

	_entityTypeName = entityTypeName;
	_entityId = entityId;

    }

    public EntityTypeName2 getEntityTypeName() {

	return _entityTypeName;

    }

    public long getEntityId() {

	return _entityId;

    }

    public String toString() {

	return "PackingId2( \"" + _entityTypeName + "\", " + _entityId + " )";

    }

    @Override
    public int compareTo( @NotNull PackingId2 rhs ) {

	int rval = _entityTypeName.compareTo( rhs._entityTypeName );
	if ( rval == 0 ) {

	    return _entityId.compareTo( rhs._entityId );

	} else {

	    return rval;

	}

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof PackingId2 && compareTo( (PackingId2)rhs ) == 0;

    }

    @Override
    public int hashCode() {

	return _entityTypeName.hashCode() ^ _entityId.hashCode();

    }

}
