package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Describe a type in the type index.
 */

public class EntityTypeInfo2 {

    private final int _referenceId;
    private final GowingEntityFactory2 _factory;

    public EntityTypeInfo2( int referenceId, @NotNull GowingEntityFactory2 factory ) {
	super();

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
    public GowingEntityFactory2 getFactory() {

	return _factory;

    }

    public String toString() {

	return "EntityTypeInfo2( \"" + _factory.getTypeName() + "\", " + _referenceId + " )";

    }

}
