package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a packable entity.
 */

public class PackableEntityHolder2 extends AbstractPackableHolder2 {

    private final InstanceId _instanceId;

    public PackableEntityHolder2( @NotNull EntityName2 name, Packable2 v, Packer2 packer, boolean mandatory ) {
	super( name, Constants.TAG_ENTITY_REFERENCE, v, mandatory );

	_instanceId = packer.queuePackEntity( v );

    }

    public void emitRepresentation( Packer2 packer2 ) {

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

    public InstanceId getInstanceId() {

	return _instanceId;

    }

//    public static IntegerHolder2 parse( @NotNull EntityName2 name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
