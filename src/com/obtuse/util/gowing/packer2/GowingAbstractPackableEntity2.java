package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 A reasonable base class for something that is packable.
 */

public abstract class GowingAbstractPackableEntity2 implements GowingPackable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( GowingAbstractPackableEntity2.class );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private GowingInstanceId _instanceId;

    protected GowingAbstractPackableEntity2() {
	super();

	setInstanceId( new GowingInstanceId( new EntityTypeName2( getClass().getCanonicalName() ) ) );

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

    protected final void setInstanceId( GowingInstanceId instanceId ) {

	_instanceId = instanceId;

    }

//    @Nullable
//    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {
//
//	return null;
//
//    }

    private static Random _rng = new Random();

//    @NotNull
//    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {
//
//	PackedEntityBundle rval = new PackedEntityBundle( ENTITY_TYPE_NAME, VERSION, null, packer.getPackingContext() );
//
//	if ( _rng.nextBoolean() ) {
//
//	    rval.addHolder( new StringHolder2( new EntityName2( "_hello" ), _rng.nextBoolean() ? "hello" : "world", true ) );
//
//	}
//
//	return rval;
//
//    }

}
