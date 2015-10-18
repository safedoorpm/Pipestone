package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a byte value.
 */

public class GowingByteHolder2 extends GowingAbstractPackableHolder2 {

    public GowingByteHolder2( @NotNull EntityName2 name, Byte v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_BYTE, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Byte) value ).byteValue() );

	} else {

	    packer2.emitNull();

	}

    }

//    public static IntegerHolder2 parse( @NotNull EntityName2 name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
