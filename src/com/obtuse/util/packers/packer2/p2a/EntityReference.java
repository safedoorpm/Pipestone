package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Carry around a (type id, entity id, &lt;optional> version) tuple.
 */

public class EntityReference implements Comparable<EntityReference> {

    private final Integer _typeId;
    private final Long _entityId;
    private final Integer _version;

//    private final EntityTypeName2 _typeName;


    public EntityReference( int typeId, long entityId, Integer version ) {

	if ( typeId <= 0 ) {

	    throw new IndexOutOfBoundsException( "type id (" + typeId + ") must be positive" );

	}

	if ( entityId < 0L ) {

	    throw new IndexOutOfBoundsException( "entity id (" + entityId + ") must be non-negative" );

	}

	if ( version != null && version <= 0 ) {

	    throw new IndexOutOfBoundsException( "version (" + version + ") must be positive if it is provided" );

	}

//	_typeName = typeName;

	_typeId = typeId;

	_entityId = entityId;

	_version = version;

    }

//    public EntityTypeName2 getTypeName() {
//
//	return _typeName;
//
//    }

    public int getTypeId() {

	return _typeId;
    }

    public long getEntityId() {

	return _entityId;

    }

    @Nullable
    public Integer getVersion() {

	return _version;

    }

    public int compareTo( @NotNull EntityReference rhs ) {

	int rval = _typeId.compareTo( rhs._typeId );
	if ( rval == 0 ) {

	    rval = _entityId.compareTo( rhs._entityId );

	}

	return rval;

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof EntityReference && compareTo( (EntityReference) rhs ) == 0;

    }

    public int hashCode() {

	return _entityId.hashCode();

    }

    public String toString() {

//	return "EntityTypeReference( typeId=" + _typeId + ", entityId=" + _entityId + ", version=" + _version + " )";

	return "ER( r" + _typeId + ":" + _entityId + ( _version == null ? "" : "v" + _version ) + " )";

    }

}
