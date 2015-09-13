package com.obtuse.util.packers.packer2.p2a.holders;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import com.obtuse.util.packers.packer2.p2a.Constants;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a float value.
 */

public class FloatHolder2 extends AbstractPackableHolder2 {

    public FloatHolder2( @NotNull EntityName2 name, Float v, @SuppressWarnings("SameParameterValue") boolean mandatory ) {
	super( name, Constants.TAG_FLOAT, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Float) value ).floatValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
