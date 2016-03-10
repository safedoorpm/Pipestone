package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity names.
 */

public class EntityName implements /*GowingPackable,*/ Comparable<EntityName> {

//    private static EntityTypeName ENTITY_TYPE_NAME;
//
//    private static final int VERSION = 1;
//
//    public static final EntityName N_NAME = new EntityName( "_n" );

    private final String _name;

//    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    public EntityName( @NotNull String name ) {
	super();

	_name = name.trim();

    }

//    public EntityName( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
//	this( bundle.getNotNullField( N_NAME ).StringValue() );
//
//    }

//    public static GowingEntityFactory getFactory() {
//
//	return new GowingEntityFactory( new EntityTypeName( EntityName.class ) ) {
//
//	    @Override
//	    public int getOldestSupportedVersion() {
//
//		return VERSION;
//	    }
//
//	    @Override
//	    public int getNewestSupportedVersion() {
//
//		return VERSION;
//	    }
//
//	    @Override
//	    @NotNull
//	    public GowingPackable createEntity( @NotNull GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
//
//		return new EntityName( unPacker, bundle, er );
//
//	    }
//
//	};
//
//    }

    @NotNull
    public String getName() {

	return _name;

    }

    public String toString() {

	return _name;

    }

    public int length() {

	return _name.length();

    }

    public boolean isEmpty() {

	return _name.isEmpty();

    }

    @Override
    public int compareTo( @NotNull EntityName entityName ) {

	return _name.compareTo( entityName._name );

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof EntityName && compareTo( (EntityName)rhs ) == 0;

    }

    @Override
    public int hashCode() {

	return _name.hashCode();

    }

//    @Override
//    @NotNull
//    public final GowingInstanceId getInstanceId() {
//
//	return _instanceId;
//
//    }
//
//    @NotNull
//    @Override
//    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {
//
//	if ( ENTITY_TYPE_NAME == null ) {
//
//	    ENTITY_TYPE_NAME = new EntityTypeName( EntityName.class );
//
//	}
//
//	GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
//		ENTITY_TYPE_NAME,
//		VERSION,
//		null,
//		packer.getPackingContext()
//	);
//
//	rval.addHolder( new GowingStringHolder( N_NAME, _name, true ) );
//
//	return rval;
//    }
//
//    @Override
//    public boolean finishUnpacking( GowingUnPacker unPacker ) {
//
//	return true;
//
//    }

}
