package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a long value.
 */

public class LongHolder2 extends AbstractPackableHolder2 {

    public LongHolder2( @NotNull EntityName2 name, Long v, boolean mandatory ) {
	super( name, Constants.TAG_LONG, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();

	if ( isMandatory() || value != null ) {

	    packer2.emit( ( (Long) value ).longValue() );

	} else {

	    packer2.emitNull();

	}

    }

}
