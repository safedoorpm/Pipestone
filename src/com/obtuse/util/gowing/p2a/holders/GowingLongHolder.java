package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a long value.
 */

public class GowingLongHolder extends GowingAbstractPackableHolder {

    public GowingLongHolder( @NotNull EntityName name, Long v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory );

    }

    public GowingLongHolder( @NotNull EntityName name, long[] v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory, true );

    }

    public GowingLongHolder( @NotNull EntityName name, Long[] v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory, false );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( ( (Long)value ).longValue() );
                    break;

                case PRIMITIVE_ARRAY:
                    packer2.emit( ( (long[])value ) );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (Long[])value ) );
                    break;

            }
//	    packer2.emit( ( (Long) value ).longValue() );

        } else {

            packer2.emitNull();

        }

    }

}
