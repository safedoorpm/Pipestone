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
 Carry a byte value.
 */

public class GowingByteHolder extends GowingAbstractPackableHolder {

    public GowingByteHolder( @NotNull final EntityName name, @Nullable final Byte v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_BYTE, v, mandatory );

    }

    public GowingByteHolder( @NotNull final EntityName name, final byte@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_BYTE, v, mandatory, true );

    }

    public GowingByteHolder( @NotNull final EntityName name, @Nullable final Byte@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_BYTE, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( ( (Byte)value ).byteValue() );
                    break;

                case PRIMITIVE_ARRAY:
                    packer2.emit( ( (byte[])value ) );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (Byte[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
