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
 Carry a long value.
 */

public class GowingLongHolder extends GowingAbstractPackableHolder {

    public GowingLongHolder( final @NotNull EntityName name, @Nullable final Long v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory );

    }

    public GowingLongHolder( final @NotNull EntityName name, final long v ) {

        super( name, GowingConstants.TAG_LONG, v, true );

    }

    public GowingLongHolder( final @NotNull EntityName name, final long@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory, true );

    }

    public GowingLongHolder( final @NotNull EntityName name, @Nullable final Long@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_LONG, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

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

        } else {

            packer2.emitNull();

        }

    }

}
