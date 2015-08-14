package com.obtuse.util.packers.packer2.examples.simple;

import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */
public class SimpleEntity implements Packable2 {

    public static final EntityTypeName2 ENTITY_NAME = new EntityTypeName2( "com.obtuse.util.packer.examples.simple.SimpleEntity" );

    private final PackingId2 _packingId;

    private String _payload;

    public SimpleEntity( PackingContext2 context, @NotNull String payload ) {
	super();

	_packingId = context.allocatePackingId( ENTITY_NAME );

	_payload = payload;

    }

    public SimpleEntity( UnPacker2 unPacker, PackableState state ) {
	super();

	throw new IllegalArgumentException( "unimplemented" );

    }

    @Override
    public void packThyself( Packer2 packer ) {

	throw new IllegalArgumentException( "unimplemented" );

    }
    @Override
    public void finishUnpacking( UnPacker2 unPacker ) {

	throw new IllegalArgumentException( "unimplemented" );

    }

    public PackingId2 getPackingId() {

	return _packingId;

    }

//    public static void main( String[] args ) {
//
//	TypeIndex2 typeIndex = new TypeIndex2( "SimpleEntity test type index" );
//	typeIndex.addFactory(
//		new EntityFactory2( ENTITY_NAME ) {
//
//		    @Override
//		    public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackableState state ) {
//
//			return new SimpleEntity( unPacker, state );
//		    }
//		}
//	);
//	PackingContext2 packingContext = new StdPackingContext2( typeIndex );
//	Packable2 entity = new SimpleEntity( packingContext, "hello world" );
//	packer.packEntity( entity );
//
//    }

}
