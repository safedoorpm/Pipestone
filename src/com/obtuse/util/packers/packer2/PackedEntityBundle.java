package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;

/**
 Carry around the packed form of an entity.
 */

public class PackedEntityBundle extends TreeSet<Packable2ThingHolder2> {

    private final EntityTypeName2 _typeName;

    private final PackedEntityBundle _superBundle;

    private final PackingId2 _packingId;

    public PackedEntityBundle( @NotNull EntityTypeName2 typeName, long entityId, @Nullable PackedEntityBundle superBundle, @NotNull PackingContext2 context ) {
	super();

	_typeName = typeName;
	_superBundle = superBundle;
	_packingId = new PackingId2( typeName, context.getTypeReferenceId( typeName ), entityId );

    }

    public boolean hasSuper() {

	return _superBundle != null;

    }

    @NotNull
    public PackingId2 getPackingId() {

	return _packingId;

    }

    @Nullable
    public PackedEntityBundle getSuperBundle() {

	return _superBundle;

    }

    public EntityTypeName2 getTypeName() {

	return _typeName;

    }

    public String toString() {

	return super.toString();
//	return "PackedEntityBundle<" + getTypeName() + ">( super = " + getSuperBundle() + ", " + size() + " values )";

    }

}
