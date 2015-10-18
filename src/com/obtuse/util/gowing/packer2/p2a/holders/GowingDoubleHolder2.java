package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a double value.
 */

public class GowingDoubleHolder2 extends GowingAbstractPackableHolder2 {

    public GowingDoubleHolder2( @NotNull EntityName2 name, Double v, boolean mandatory ) {
	super( name, GowingConstants.TAG_DOUBLE, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Double) value ).doubleValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
