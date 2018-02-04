package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry an entity name value.
 */

public class GowingEntityNameHolder extends GowingAbstractPackableHolder {

    public GowingEntityNameHolder( @NotNull final EntityName name, final EntityName v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_ENTITY_NAME, v, mandatory );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            packer2.emitUsersEntityName( (EntityName)value );

        } else {

            packer2.emitNull();

        }

    }

}
