package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a short value.
 */

public class GowingShortHolder2 extends GowingAbstractPackableHolder2 {

    public GowingShortHolder2( @NotNull EntityName2 name, Short v, boolean mandatory ) {
	super( name, GowingConstants.TAG_SHORT, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Short) value ).shortValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
