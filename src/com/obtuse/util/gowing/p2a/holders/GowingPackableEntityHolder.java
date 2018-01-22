package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingInstanceId;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import com.obtuse.util.gowing.p2a.GowingUtil;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry a packable entity.
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

        GowingUtil.verifyActuallyPackable( "GowingPackableEntityHolder", name, v );

        _instanceId = packer.queuePackableEntity( v );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() && value == null ) {

            throw new IllegalArgumentException( "mandatory value not provided" );

        }

        packer2.emit( _instanceId );

    }

    public GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    public String toString() {

        return "GowingPackableEntityHolder( instanceId=" + getInstanceId() + " )";

    }

}
