package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a double value.
 */

public class GowingDoubleHolder extends GowingAbstractPackableHolder {

    public GowingDoubleHolder( @NotNull EntityName name, Double v, boolean mandatory ) {
	super( name, GowingConstants.TAG_DOUBLE, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Double) value ).doubleValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
