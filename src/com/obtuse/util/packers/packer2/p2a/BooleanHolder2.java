package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/**
 Pack a boolean value.
 */

public class BooleanHolder2 extends AbstractPackableHolder2 {

    public BooleanHolder2( @NotNull EntityName2 name, Boolean v, boolean mandatory ) {
	super( name, Constants.TAG_BOOLEAN, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Boolean) value ).booleanValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
