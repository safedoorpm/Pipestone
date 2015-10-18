package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an integer value.
 */

public class GowingIntegerHolder2 extends GowingAbstractPackableHolder2 {

    public GowingIntegerHolder2( @NotNull EntityName2 name, Integer v, boolean mandatory ) {
	super( name, GowingConstants.TAG_INTEGER, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

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
