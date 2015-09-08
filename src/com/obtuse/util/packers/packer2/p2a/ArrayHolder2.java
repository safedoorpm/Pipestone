package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packable2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a long value.
 */

public class ArrayHolder2 extends AbstractPackableHolder2 {

    private final char _elementTypeTag;
    private final boolean _areElementsMandatory;

    public ArrayHolder2( @NotNull EntityName2 name, int[] v, boolean mandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_INTEGER;
	_areElementsMandatory = true;

    }

    public ArrayHolder2( @NotNull EntityName2 name, Integer[] v, boolean mandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_INTEGER;
	_areElementsMandatory = false;

    }

    public ArrayHolder2( @NotNull EntityName2 name, long[] v, boolean mandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_LONG;
	_areElementsMandatory = true;

    }

    public ArrayHolder2( @NotNull EntityName2 name, Long[] v, boolean mandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_LONG;
	_areElementsMandatory = false;

    }

    public ArrayHolder2( @NotNull EntityName2 name, String[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_STRING;
	_areElementsMandatory = areElementsMandatory;

    }

    public ArrayHolder2( @NotNull EntityName2 name, Packable2[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, Constants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = Constants.TAG_ENTITY_REFERENCE;
	_areElementsMandatory = areElementsMandatory;

    }

    public void emitRepresentation( Packer2 packer2 ) {

	throw new IllegalArgumentException( "unimplemented" );

//	Object value = getObjectValue();
//
//	if ( isMandatory() || value != null ) {
//
//	    switch ( _elementTypeTag ) {
//
//		case
//	    }
//	    packer2.emit( ( (Long) value ).longValue() );
//
//	} else {
//
//	    packer2.emitNull();
//
//	}

    }

}
