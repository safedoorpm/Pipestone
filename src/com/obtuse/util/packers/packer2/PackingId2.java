package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Describe a v2 packing id.
 <p/>A packing id identifies a particular instance of a {@link Packable2} class which is either being packed
 or being unpacked by a packing or unpacking operation. These are used to
 <ol>
 <li>ensure that objects which are referenced more than once in collection of data structures being packed/saved by a packing
 operation are only actually packed/saved once.</li>
 <li>correctly reconnect packed objects as an unpacking operation unfolds.</li>
 </ol>
 */

public class PackingId2 implements Comparable<PackingId2> {

    private final EntityTypeName2 _entityTypeName;

    private final int _typeReferenceId;

    private final Long _entityId;

    public PackingId2( @NotNull EntityTypeName2 entityTypeName, int typeReferenceId, long entityId ) {

	_entityTypeName = entityTypeName;
	_typeReferenceId = typeReferenceId;
	_entityId = entityId;

    }

    public EntityTypeName2 getEntityTypeName() {

	return _entityTypeName;

    }

    public long getEntityId() {

	return _entityId;

    }

    public int getTypeReferenceId() {

	return _typeReferenceId;

    }

    public String toString() {

	return "PackingId2( \"" + _entityTypeName + "\", " + _typeReferenceId + ", " + _entityId + " )";

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
