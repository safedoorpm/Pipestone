package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.p2a.StringHolder2;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 A reasonable base class for something that is packable.
 */

public abstract class AbstractPackableEntity2 implements Packable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( AbstractPackableEntity2.class );

    private static int VERSION = 1;

    private final InstanceId _instanceId;

    protected AbstractPackableEntity2( EntityTypeName2 typeName ) {
	super();

	_instanceId = new InstanceId( typeName );

    }

    public InstanceId getInstanceId() {

	return _instanceId;

    }

    private static Random _rng = new Random();

    @NotNull
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	PackedEntityBundle rval = new PackedEntityBundle( ENTITY_TYPE_NAME, VERSION, null, packer.getPackingContext() );

	if ( _rng.nextBoolean() ) {

	    rval.addHolder( new StringHolder2( new EntityName2( "_hello" ), _rng.nextBoolean() ? "hello" : "world", true ) );

	}

	return rval;

    }

}
