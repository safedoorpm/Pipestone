package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a String value.
 */

public class GowingStringHolder extends GowingAbstractPackableHolder {

    public GowingStringHolder( @NotNull final EntityName name, final String v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_STRING, v, mandatory );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();
        if ( isMandatory() || value != null ) {

            packer2.emit( (String)value );

        } else {

            packer2.emitNull();

        }

    }

}
