package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an float value.
 */

public class FloatHolder2 extends AbstractPackableHolder2 {

    public FloatHolder2( @NotNull EntityName2 name, Float v, boolean mandatory ) {
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
