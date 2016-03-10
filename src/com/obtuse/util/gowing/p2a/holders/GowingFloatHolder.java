package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a float value.
 */

public class GowingFloatHolder extends GowingAbstractPackableHolder {

    public GowingFloatHolder( @NotNull EntityName name, Float v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_FLOAT, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Float) value ).floatValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
