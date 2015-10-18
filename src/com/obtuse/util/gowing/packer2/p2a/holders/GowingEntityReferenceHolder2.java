package com.obtuse.util.gowing.packer2.p2a.holders;

import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPacker2;
import com.obtuse.util.gowing.packer2.p2a.GowingConstants;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an entity reference value.
 */

public class GowingEntityReferenceHolder2 extends GowingAbstractPackableHolder2 {

    public GowingEntityReferenceHolder2(
	    @NotNull EntityName2 name,
	    GowingEntityReference v,
	    @SuppressWarnings("SameParameterValue") boolean mandatory
    ) {
	super( name, GowingConstants.TAG_ENTITY_REFERENCE, v, mandatory );

    }

    public void emitRepresentation( GowingPacker2 packer2 ) {

	Object value = getObjectValue();
	if ( isMandatory() || value != null ) {

	    packer2.emitEntityReference( ( (GowingEntityReference)value ).getTypeId(), ( (GowingEntityReference)value ).getEntityId() );

	} else {

	    packer2.emitNull();

	}

    }

}
