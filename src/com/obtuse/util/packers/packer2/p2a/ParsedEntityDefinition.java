package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.util.packers.packer2.PackedEntityBundle;
import com.obtuse.util.packers.packer2.PackingId2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the definition.
 */

public class ParsedEntityDefinition {

    private final PackingId2 _packingId;

    private final PackedEntityBundle _bundle;

    public ParsedEntityDefinition( PackingId2 packingId, PackedEntityBundle bundle ) {

	super();

	_packingId = packingId;
	_bundle = bundle;

    }

    public PackingId2 getPackingId() {

	return _packingId;

    }

    public PackedEntityBundle getBundle() {

	return _bundle;

    }

    public String toString() {

	return "ParsedEntityDefinition( " + _packingId + ", " + _bundle + " )";

    }

}
