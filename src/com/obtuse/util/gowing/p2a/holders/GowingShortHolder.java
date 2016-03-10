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

    public GowingShortHolder( @NotNull EntityName name, Short v, boolean mandatory ) {
	super( name, GowingConstants.TAG_SHORT, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Short) value ).shortValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
