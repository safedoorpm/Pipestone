package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a null value.
 */

public class GowingNullHolder2 extends GowingAbstractPackableHolder2 {

    public GowingNullHolder2( @NotNull EntityName2 name ) {
	super( name, GowingConstants.NULL_VALUE, null, false );

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
