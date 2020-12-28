package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry a String value.
 */

public class GowingStringHolder extends GowingAbstractPackableHolder {

    public GowingStringHolder( final @NotNull EntityName name, final String v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_STRING, v, mandatory );

    }

    public GowingStringHolder( final @NotNull EntityName name, @Nullable final String@Nullable[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_STRING, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();
        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( (String)value );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (String[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
