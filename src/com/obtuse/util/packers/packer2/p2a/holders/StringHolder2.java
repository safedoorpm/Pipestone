package com.obtuse.util.packers.packer2.p2a.holders;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import com.obtuse.util.packers.packer2.p2a.Constants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a String value.
 */

public class StringHolder2 extends AbstractPackableHolder2 {

    public StringHolder2( @NotNull EntityName2 name, String v, boolean mandatory ) {
	super( name, Constants.TAG_STRING, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();
	if ( isMandatory() || value != null ) {

	    packer2.emit( (String) value );

	} else {

	    packer2.emitNull();

	}

    }

}
