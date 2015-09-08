package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Describe a type in the type index.
 */

public class EntityTypeInfo2 {

//    private final EntityTypeName2 _typeName;
    private final int _referenceId;
    private final EntityFactory2 _factory;

    public EntityTypeInfo2( /*EntityTypeName2 typeName,*/ int referenceId, @NotNull EntityFactory2 factory ) {
	super();

//	_typeName = typeName;

	_referenceId = referenceId;

	_factory = factory;

    }

    @NotNull
    public EntityTypeName2 getTypeName() {

	return getFactory().getTypeName();

    }

    public int getReferenceId() {

	return _referenceId;

    }

    @NotNull
    public EntityFactory2 getFactory() {

	return _factory;

    }

    public String toString() {

	return "EntityTypeInfo2( \"" + _factory.getTypeName() + "\", " + _referenceId + " )";

    }

}
