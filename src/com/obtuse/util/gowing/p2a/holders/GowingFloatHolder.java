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
 Carry a float value.
 */

public class GowingFloatHolder extends GowingAbstractPackableHolder {

    public GowingFloatHolder( final @NotNull EntityName name, @Nullable final Float v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_FLOAT, v, mandatory );

    }

    public GowingFloatHolder( final @NotNull EntityName name, final float v ) {

        super( name, GowingConstants.TAG_FLOAT, v, true );

    }

    public GowingFloatHolder( final @NotNull EntityName name, final float@Nullable[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_FLOAT, v, mandatory, true );

    }

    public GowingFloatHolder( final @NotNull EntityName name, @Nullable final Float@Nullable[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_FLOAT, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( ( (Float)value ).floatValue() );
                    break;

                case PRIMITIVE_ARRAY:
                    packer2.emit( ( (float[])value ) );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (Float[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
