package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an integer value.
 */

public class GowingIntegerHolder extends GowingAbstractPackableHolder {

    public GowingIntegerHolder( @NotNull EntityName name, Integer v, boolean mandatory ) {
	super( name, GowingConstants.TAG_INTEGER, v, mandatory );

    }

    public GowingIntegerHolder( @NotNull EntityName name, int[] v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_INTEGER, v, mandatory, true );

    }

    public GowingIntegerHolder( @NotNull EntityName name, Integer[] v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, GowingConstants.TAG_INTEGER, v, mandatory, false );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    switch ( getKind() ) {

		case SCALAR:
		    packer2.emit( ( (Integer) value ).intValue() );
		    break;

		case PRIMITIVE_ARRAY:
		    packer2.emit( ( (int[]) value ) );
		    break;

		case CONTAINER_ARRAY:
		    packer2.emit( ( (Integer[]) value ) );
		    break;

	    }
//		packer2.emit( ( (Integer) value ).intValue() );

	} else {

	    packer2.emitNull();

	}

    }

//    public static IntegerHolder2 parse( @NotNull EntityName name, UnPacker2 unPacker, boolean mandatory ) {
//
//    }

}
