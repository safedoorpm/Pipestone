package com.obtuse.util.packers.packer2.examples.simple;

import com.obtuse.util.packers.packer2.*;
import com.obtuse.util.packers.packer2.p2a.StringHolder2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public class SimpleEntity extends AbstractPackableEntity2 {

    public static final EntityTypeName2 ENTITY_NAME = new EntityTypeName2( SimpleEntity.class );

    private static final int VERSION = 1;

    private String _payload;

    public SimpleEntity( @NotNull String payload ) {
	super( ENTITY_NAME );

	_payload = payload;

    }

    public SimpleEntity( UnPacker2 unPacker, PackedEntityBundle bundle ) {
	super( ENTITY_NAME );

	if ( bundle.getVersion() != VERSION ) {

	    throw new IllegalArgumentException( SimpleEntity.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	}

	throw new IllegalArgumentException( "unimplemented" );

    }

    @Override
    @NotNull
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

//	SortedSet<Packable2ThingHolder2> rval = new TreeSet<Packable2ThingHolder2>();
	PackedEntityBundle rval = new PackedEntityBundle(
		ENTITY_NAME,
		VERSION,
		super.bundleThyself( true, packer ),
		packer.getPackingContext()
	);
	rval.addHolder( new StringHolder2( new EntityName2( "_payload" ), _payload, false ) );

	return rval;

    }

    @Override
    public void finishUnpacking( UnPacker2 unPacker ) {

	throw new IllegalArgumentException( "unimplemented" );

    }

//    public static void main( String[] args ) {
//
//	TypeIndex2 typeIndex = new TypeIndex2( "SimpleEntity test type index" );
//	typeIndex.addFactory(
//		new EntityFactory2( ENTITY_TYPE_NAME ) {
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
