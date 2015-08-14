package com.obtuse.util.packers.packer1.examples.simple;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer1.*;
import org.jetbrains.annotations.NotNull;

/**
 %%% Something clever goes here.
 */
public class SimpleEntity implements Packable1 {

    public static final String ENTITY_NAME = "com.obtuse.util.packer.examples.simple.SimpleEntity";

    private final PackingId1 _packingId;

    private String _payload;

    public SimpleEntity( PackingContext1 context, @NotNull String payload ) {
	super();

	_packingId = context.allocatePackingId( context.getTypeId( ENTITY_NAME ) );

	_payload = payload;

    }

    public SimpleEntity( UnPacker1 unPacker ) {
	super();

	throw new IllegalArgumentException( "unimplemented" );

    }

    @Override
    public void finishUnpacking( UnPacker1 unPacker ) {

    }

    @Override
    public void packThyself( Packer1 packer ) {

	TypeIndex1 typeIndex = new TypeIndex1( "SimpleEntity test type index" );
	typeIndex.appendFactory(
		new EntityFactory1( ENTITY_NAME ) {

		    @Override
		    public Packable1 createEntity( @NotNull UnPacker1 unPacker ) {

			return new SimpleEntity( unPacker );
		    }
		}
	);
	PackingContext1 packingContext = new StdPackingContext1( typeIndex );
	Packable1 entity = new SimpleEntity( packingContext, "hello world" );
	packer.startPackingEntity( entity );
	packer.packMandatoryString( _payload );

    }

}
