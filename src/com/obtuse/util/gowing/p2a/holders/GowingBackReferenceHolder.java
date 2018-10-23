package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingInstanceId;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by danny on 2018/10/14.
 */

public class GowingBackReferenceHolder<T> extends GowingAbstractPackableHolder {

    private final GowingInstanceId _instanceId;

    private GowingBackReferenceHolder( @NotNull final EntityName name, @Nullable final GowingBackReferenceable v, boolean mandatory ) {
        super( name, GowingConstants.TAG_BACK_REFERENCE, v, mandatory );

        _instanceId = v == null ? null : v.getInstanceId();

    }

    @Override
    public void emitRepresentation( final GowingPacker packer2 ) {

        Object value = getObjectValue();

        if ( isMandatory() && value == null ) {

            throw new IllegalArgumentException( "mandatory value not provided" );

        }

        packer2.emit( _instanceId );

    }

}
