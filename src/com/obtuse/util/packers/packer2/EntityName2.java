package com.obtuse.util.packers.packer2;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity names.
 */

public class EntityName2 implements Comparable<EntityName2> {

    private final String _typeName;

    public EntityName2( String typeName ) {
	super();

	_typeName = typeName;

    }

    public String getTypeName() {

	return _typeName;

    }

    public String toString() {

	return _typeName;

    }

    public int length() {

	return _typeName.length();

    }

    @Override
    public int compareTo( @NotNull EntityName2 entityName2 ) {

	return _typeName.compareTo( entityName2._typeName );

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof EntityName2 && compareTo( (EntityName2)rhs ) == 0;

    }

    @Override
    public int hashCode() {

	return _typeName.hashCode();

    }

}
