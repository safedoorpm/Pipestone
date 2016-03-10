package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe a type in the type index.
 */

public class EntityTypeInfo {

    private final int _referenceId;
    private final GowingEntityFactory _factory;

    public EntityTypeInfo( int referenceId, @NotNull GowingEntityFactory factory ) {
	super();

	_referenceId = referenceId;

	_factory = factory;

    }

    @NotNull
    public EntityTypeName getTypeName() {

	return getFactory().getTypeName();

    }

    public int getReferenceId() {

	return _referenceId;

    }

    @NotNull
    public GowingEntityFactory getFactory() {

	return _factory;

    }

    public String toString() {

	return "EntityTypeInfo( \"" + _factory.getTypeName() + "\", " + _referenceId + " )";

    }

}
