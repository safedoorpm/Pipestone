package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingInstanceId;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a packable entity.
 */

public class GowingPackableEntityHolder extends GowingAbstractPackableHolder {

    private final GowingInstanceId _instanceId;

    public GowingPackableEntityHolder(
            @NotNull final EntityName name,
            final GowingPackable v,
            final GowingPacker packer,
            @SuppressWarnings("SameParameterValue") final boolean mandatory
    ) {

        super( name, GowingConstants.TAG_ENTITY_REFERENCE, v, mandatory );

        _instanceId = packer.queuePackableEntity( v );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

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

//    public static IntegerHolder2 parse( @NotNull EntityName name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
