package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packer2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack an entity reference value.
 */

public class EntityReferenceHolder2 extends AbstractPackableHolder2 {

    public EntityReferenceHolder2( @NotNull EntityName2 name, EntityReference v, boolean mandatory ) {
	super( name, Constants.TAG_ENTITY_REFERENCE, v, mandatory );

    }

    public void emitRepresentation( Packer2 packer2 ) {

	Object value = getObjectValue();
	if ( isMandatory() || value != null ) {

	    packer2.emitEntityReference( ( (EntityReference)value ).getTypeId(), ( (EntityReference)value ).getEntityId() );

	} else {

	    packer2.emitNull();

	}

    }

}
