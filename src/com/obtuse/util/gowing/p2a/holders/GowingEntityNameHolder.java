package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a boolean value.
 */

public class GowingEntityNameHolder extends GowingAbstractPackableHolder {

    public GowingEntityNameHolder( @NotNull EntityName name, EntityName v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {

        super( name, GowingConstants.TAG_ENTITY_NAME, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            packer2.emit( ( (EntityName)value ).getName() );

        } else {

            packer2.emitNull();

        }

    }

}
