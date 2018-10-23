package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry a File value.
 */

public class GowingFileHolder extends GowingAbstractPackableHolder {

    public GowingFileHolder( final @NotNull EntityName name, final File v, final boolean mandatory ) {

        super( name, GowingConstants.TAG_FILE, v, mandatory );

    }

    public GowingFileHolder( final @NotNull EntityName name, @Nullable final String@NotNull[] v, @SuppressWarnings("SameParameterValue") final boolean mandatory ) {

        super( name, GowingConstants.TAG_STRING, v, mandatory, false );

    }

    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();
        if ( isMandatory() || value != null ) {

            switch ( getKind() ) {

                case SCALAR:
                    packer2.emit( (File)value );
                    break;

                case CONTAINER_ARRAY:
                    packer2.emit( ( (File[])value ) );
                    break;

            }

        } else {

            packer2.emitNull();

        }

    }

}
