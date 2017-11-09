package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a short value.
 */

public class GowingShortHolder extends GowingAbstractPackableHolder {

    public GowingShortHolder( @NotNull final EntityName name, final Short v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_SHORT, v, mandatory );

    }

    public GowingShortHolder( @NotNull final EntityName name, final short[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_SHORT, v, mandatory, true );

    }

    public GowingShortHolder( @NotNull final EntityName name, final Short[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

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
//	    packer2.emit( ( (Short) value ).shortValue() );

        } else {

            packer2.emitNull();

        }

    }

}
