package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a null value.
 */

public class NullHolder2 extends AbstractPackableHolder2 {

    public NullHolder2( @NotNull EntityName2 name ) {
	super( name, Constants.NULL_VALUE, null, false );

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
