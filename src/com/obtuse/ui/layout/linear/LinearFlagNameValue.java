/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import org.jetbrains.annotations.NotNull;

/**
 %%% Something clever goes here.
 */

public class LinearFlagNameValue extends GowingPackableAttribute {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( LinearFlagNameValue.class.getCanonicalName() );
    private static final int VERSION = 1;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

	@Override
	public int getOldestSupportedVersion() {

	    return VERSION;
	}

	@Override
	public int getNewestSupportedVersion() {

	    return VERSION;
	}

	@Override
	@NotNull
	public GowingPackable createEntity( @NotNull GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle, GowingEntityReference er )
		throws GowingUnPackerParsingException {

	    return new LinearFlagNameValue( unPacker, bundle, er );

	}

    };

    protected LinearFlagNameValue( LinearFlagName key, Object value, GowingPackableType type, boolean computed ) {
	super( key, value, type, computed );

    }

//    protected LinearFlagNameValue( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
//	super( unPacker, bundle, er );
//
//    }

    public LinearFlagNameValue( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er )
	    throws GowingUnPackerParsingException {
	super(
		unPacker,
		bundle.getSuperBundle(),
		er
	);

    }

    @NotNull
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {

	@SuppressWarnings("UnnecessaryLocalVariable")
	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		LinearFlagNameValue.ENTITY_TYPE_NAME,
		LinearFlagNameValue.VERSION,
		super.bundleThyself( true, packer ),
		packer.getPackingContext()
	);

	return bundle;

    }

}
