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
 Carry a double value.
 */

public class GowingDoubleHolder extends GowingAbstractPackableHolder {

    public GowingDoubleHolder( final @NotNull EntityName name, @Nullable final Double v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_DOUBLE, v, mandatory );

    }

    public GowingDoubleHolder( final @NotNull EntityName name, final double v ) {

        super( name, GowingConstants.TAG_DOUBLE, v, true );

    }

    public GowingDoubleHolder( final @NotNull EntityName name, final double@Nullable[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_DOUBLE, v, mandatory, true );

    }

    public GowingDoubleHolder( final @NotNull EntityName name, @Nullable final Double@Nullable[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_DOUBLE, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( ( (Double)value ).doubleValue() );
                    break;

                case PRIMITIVE_ARRAY:
                    packer2.emit( ( (double[])value ) );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (Double[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
