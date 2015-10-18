package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPackable2;
import com.obtuse.util.gowing.packer2.GowingInstanceId;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a packable entity.
 */

public class GowingPackableEntityHolder2 extends GowingAbstractPackableHolder2 {

    private final GowingInstanceId _instanceId;

    public GowingPackableEntityHolder2(
	    @NotNull EntityName2 name,
	    GowingPackable2 v,
	    GowingPacker2 packer,
	    @SuppressWarnings("SameParameterValue") boolean mandatory
    ) {
	super( name, GowingConstants.TAG_ENTITY_REFERENCE, v, mandatory );

	_instanceId = packer.queuePackEntity( v );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() && value == null ) {

	    throw new IllegalArgumentException( "mandatory value not provided" );

	}

	packer2.emit( _instanceId );

//	if ( isMandatory() || value != null ) {
//
//	    packer2.emit( _instanceId );
//
//	} else {
//
//	    packer2.emitNull();
//
//	}

    }

    public GowingInstanceId getInstanceId() {

	return _instanceId;

    }

//    public static IntegerHolder2 parse( @NotNull EntityName2 name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
