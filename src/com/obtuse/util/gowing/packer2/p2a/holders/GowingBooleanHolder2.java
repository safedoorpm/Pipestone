package com.obtuse.util.gowing.packer2.p2a.holders;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/**
 Pack a boolean value.
 */

public class GowingBooleanHolder2 extends GowingAbstractPackableHolder2 {

    public GowingBooleanHolder2( @NotNull EntityName2 name, Boolean v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_BOOLEAN, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Boolean) value ).booleanValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
