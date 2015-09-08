package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an integer value.
 */

public class IntegerHolder2 extends AbstractPackableHolder2 {

    public IntegerHolder2( @NotNull EntityName2 name, Integer v, boolean mandatory ) {
	super( name, Constants.TAG_INTEGER, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Integer) value ).intValue() );

	} else {

	    packer2.emitNull();

	}

    }

//    public static IntegerHolder2 parse( @NotNull EntityName2 name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
