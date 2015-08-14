package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Carry around the type name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity type names.
 */

public class EntityTypeName2 implements Comparable<EntityTypeName2> {

    private final String _typeName;

    public EntityTypeName2( String typeName ) {
	super();

	_typeName = typeName;

    }

    public String getTypeName() {

	return _typeName;

    }

    public String toString() {

	return _typeName;

    }

    @Override
    public int compareTo( @NotNull EntityTypeName2 entityTypeName2 ) {

	return _typeName.compareTo( entityTypeName2._typeName );

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof EntityTypeName2 && compareTo( (EntityTypeName2)rhs ) == 0;

    }

    @Override
    public int hashCode() {

	return _typeName.hashCode();

    }

}
