package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.p2a.StringHolder2;
import org.jetbrains.annotations.NotNull;

/**
 A reasonable base class for something that is packable.
 */

public abstract class AbstractPackableEntity2 implements Packable2 {

    public static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( AbstractPackableEntity2.class );

    private final InstanceId _instanceId;

    protected AbstractPackableEntity2( EntityTypeName2 typeName ) {
	super();

	_instanceId = new InstanceId( typeName );

    }

    public InstanceId getInstanceId() {

	return _instanceId;

    }

    @NotNull
    public PackedEntityBundle bundleThyself( PackingId2 packingId, boolean isPackingSuper, Packer2 packer ) {

	PackedEntityBundle rval = new PackedEntityBundle( ENTITY_TYPE_NAME, isPackingSuper ? 0L : packingId.getEntityId(), null, packer.getPackingContext() );

	rval.add( new StringHolder2( new EntityName2( "_hello" ), "hello", true ) );

	return rval;

    }

}
