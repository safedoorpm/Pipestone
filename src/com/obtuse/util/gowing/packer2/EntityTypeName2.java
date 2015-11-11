package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.holders.GowingStringHolder2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Carry around the type name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity type names.
 */

public class EntityTypeName2 implements /*GowingPackable2,*/ Comparable<EntityTypeName2> {

//    private static EntityTypeName2 ENTITY_TYPE_NAME;
//
//    private static final int VERSION = 1;
//
//    public static final EntityName2 N_NAME = new EntityName2( "_n" );

    private final String _name;

//    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    public EntityTypeName2( Class<? extends GowingPackable2> className ) {
	this( className.getCanonicalName() );
    }
    public EntityTypeName2( String name ) {
	super();

	_name = name;

    }

//    public EntityTypeName2( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
//	this( bundle.getNotNullField( N_NAME ).StringValue() );
//
//    }

//    public static GowingEntityFactory2 getFactory() {
//
//	return new GowingEntityFactory2( new EntityTypeName2( EntityTypeName2.class ) ) {
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
//	    public GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
//
//		return new EntityTypeName2( unPacker, bundle, er );
//
//	    }
//
//	};
//
//    }

    public String getTypeName() {

	return _name;

    }

    public String toString() {

	return _name;

    }

    public int length() {

	return _name.length();

    }

    @Override
    public int compareTo( @NotNull EntityTypeName2 typeName2 ) {

	return _name.compareTo( typeName2._name );

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof EntityTypeName2 && compareTo( (EntityTypeName2)rhs ) == 0;

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

//    @NotNull
//    @Override
//    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {
//
//	if ( ENTITY_TYPE_NAME == null ) {
//
//	    ENTITY_TYPE_NAME = new EntityTypeName2( EntityTypeName2.class );
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
//	rval.addHolder( new GowingStringHolder2( N_NAME, _name, true ) );
//
//	return rval;
//    }

//    @Override
//    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {
//
//	return true;
//
//    }

}
