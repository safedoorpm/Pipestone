package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingConstants;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an entity reference value.
 */

public class GowingEntityReferenceHolder extends GowingAbstractPackableHolder {

    public GowingEntityReferenceHolder(
	    @NotNull EntityName name,
	    GowingEntityReference v,
	    @SuppressWarnings("SameParameterValue") boolean mandatory
				      ) {
	super( name, GowingConstants.TAG_ENTITY_REFERENCE, v, mandatory );

    }

    public void emitRepresentation( GowingPacker packer2 ) {

	Object value = getObjectValue();
	if ( isMandatory() || value != null ) {

	    packer2.emitEntityReference( ( (GowingEntityReference)value ).getTypeId(), ( (GowingEntityReference)value ).getEntityId() );

	} else {

	    packer2.emitNull();

	}

    }

}
