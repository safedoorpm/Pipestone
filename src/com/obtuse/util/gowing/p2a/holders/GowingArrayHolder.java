package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a collection containing an arbitrary combination of zero or more {@link GowingPackable} instances, {@link String} instances,
 or Java container class instances ({@link Integer}, {@link Byte}, {@link Boolean}, etc).
 <p/>
 Not completely implemented (yet).
 */

public class GowingArrayHolder extends GowingAbstractPackableHolder {

    private final char _elementTypeTag;
    private final boolean _areElementsMandatory;

    public GowingArrayHolder( @NotNull EntityName name, int[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_INTEGER;
	_areElementsMandatory = true;

    }

    public GowingArrayHolder( @NotNull EntityName name, Integer[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_INTEGER;
	_areElementsMandatory = false;

    }

    public GowingArrayHolder( @NotNull EntityName name, long[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_LONG;
	_areElementsMandatory = true;

    }

    public GowingArrayHolder( @NotNull EntityName name, Long[] v, boolean mandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_LONG;
	_areElementsMandatory = false;

    }

    public GowingArrayHolder( @NotNull EntityName name, String[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_STRING;
	_areElementsMandatory = areElementsMandatory;

    }

    public GowingArrayHolder( @NotNull EntityName name, GowingPackable[] v, boolean mandatory, boolean areElementsMandatory ) {
	super( name, GowingConstants.TAG_ARRAY, v, mandatory );

	_elementTypeTag = GowingConstants.TAG_ENTITY_REFERENCE;
	_areElementsMandatory = areElementsMandatory;

    }

    public void emitRepresentation( GowingPacker packer2 ) {

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
