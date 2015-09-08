package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around a (type id, entity id) tuple.
 */

public class EntityTypeReference {

    private final int _typeId;
    private final long _entityId;


    public EntityTypeReference( int typeId, long entityId ) {

	if ( typeId <= 0 ) {

	    throw new IndexOutOfBoundsException( "type id (" + typeId + ") must be positive" );

	}

	if ( entityId <= 0L ) {

	    throw new IndexOutOfBoundsException( "entity id (" + entityId + ") must be positive" );

	}

	_typeId = typeId;

	_entityId = entityId;

    }

    public int getTypeId() {

	return _typeId;
    }

    public long getEntityId() {

	return _entityId;

    }

    public String toString() {

	return "EntityTypeReference( typeId=" + _typeId + ", entityId=" + _entityId + " )";

    }

}
