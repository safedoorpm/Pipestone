package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPackable2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a collection containing an arbitrary combination of zero or more {@link GowingPackable2} instances, {@link String} instances,
 or Java container class instances ({@link Integer}, {@link Byte}, {@link Boolean}, etc).
 <p/>
 Not completely implemented (yet).
 */

public class GowingArrayHolder2 extends GowingAbstractPackableHolder2 {

    private final char _elementTypeTag;
    private final boolean _areElementsMandatory;

    public GowingArrayHolder2( @NotNull EntityName2 name, int[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_INTEGER;
	_areElementsMandatory = true;

    }

    public GowingArrayHolder2( @NotNull EntityName2 name, Integer[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_INTEGER;
	_areElementsMandatory = false;

    }

    public GowingArrayHolder2( @NotNull EntityName2 name, long[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_LONG;
	_areElementsMandatory = true;

    }

    public GowingArrayHolder2( @NotNull EntityName2 name, Long[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_LONG;
	_areElementsMandatory = false;

    }

    public GowingArrayHolder2( @NotNull EntityName2 name, String[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_STRING;
	_areElementsMandatory = areElementsMandatory;

    }

    public GowingArrayHolder2( @NotNull EntityName2 name, GowingPackable2[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_ENTITY_REFERENCE;
	_areElementsMandatory = areElementsMandatory;

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

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
