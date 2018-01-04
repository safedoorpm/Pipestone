package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry a null value.
 */

public class GowingNullHolder extends GowingAbstractPackableHolder {

    public GowingNullHolder( @NotNull final EntityName name ) {

        super( name, GowingConstants.NULL_VALUE, null, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            packer2.emit( ( (Integer)value ).intValue() );

        } else {

            packer2.emitNull();

        }

    }

}
