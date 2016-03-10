package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a boolean value.
 */

public class GowingBooleanHolder extends GowingAbstractPackableHolder {

    public GowingBooleanHolder( @NotNull EntityName name, Boolean v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_BOOLEAN, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Boolean) value ).booleanValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
