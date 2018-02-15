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
 Carry a short value.
 */

public class GowingShortHolder extends GowingAbstractPackableHolder {

    public GowingShortHolder( final @NotNull EntityName name, @Nullable final Short v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_SHORT, v, mandatory );

    }

    public GowingShortHolder( final @NotNull EntityName name, final short@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_SHORT, v, mandatory, true );

    }

    public GowingShortHolder( final @NotNull EntityName name, @Nullable final Short@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_SHORT, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( ( (Short)value ).shortValue() );
                    break;

                case PRIMITIVE_ARRAY:
                    packer2.emit( ( (short[])value ) );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (Short[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
