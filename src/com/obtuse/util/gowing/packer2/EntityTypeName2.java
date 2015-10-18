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

public class EntityTypeName2 implements GowingPackable2, Comparable<EntityTypeName2> {

    private static EntityTypeName2 ENTITY_TYPE_NAME;

//    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( EntityTypeName2.class.getCanonicalName() );
    private static final int VERSION = 1;

    public static final EntityName2 N_NAME = new EntityName2( "_n" );

    private final String _name;

    private GowingInstanceId _instanceId;

    public EntityTypeName2( Class<? extends GowingPackable2> className ) {
	this( className.getCanonicalName() );
    }
    public EntityTypeName2( String name ) {
	super();

//	if ( !EntityTypeName2.class.getCanonicalName().equals( name ) && !EntityName2.class.getCanonicalName().equals( name ) ) {
//
//	    _instanceId = new InstanceId( new EntityTypeName2( EntityTypeName2.class.getCanonicalName() ) );
//
//	}

	_instanceId = new GowingInstanceId( EntityTypeName2.class.getCanonicalName() );

	_name = name;

    }

    public EntityTypeName2( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
	this( bundle.getNotNullField( N_NAME ).StringValue() );

//	_instanceId = new InstanceId( ENTITY_TYPE_NAME );
//	_name = bundle.getNotNullField( N_NAME ).StringValue();

    }

    public static GowingEntityFactory2 getFactory() {

	return new GowingEntityFactory2( new EntityTypeName2( EntityTypeName2.class ) ) {

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
	    public GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {

		return new EntityTypeName2( unPacker, bundle, er );

	    }

	};

    }

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

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

//	if ( _instanceId == null ) {
//
//	    _instanceId = new InstanceId( new EntityTypeName2( _name ) );
//
//	}

	return _instanceId;

    }

    protected final void setInstanceId( GowingInstanceId instanceId ) {

	_instanceId = instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {

	if ( ENTITY_TYPE_NAME == null ) {

	    ENTITY_TYPE_NAME = new EntityTypeName2( EntityTypeName2.class );

	}

	GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	rval.addHolder( new GowingStringHolder2( N_NAME, _name, true ) );

	return rval;
    }

    @Override
    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {

	return true;

    }

//    private final String _typeName;
//
//    public EntityTypeName2( Class<? extends Packable2> className ) {
//	super();
//
//	_typeName = className.getCanonicalName();
//
//    }
//
//    public EntityTypeName2( String typeName ) {
//	super();
//
//	_typeName = typeName;
//
//    }
//
//    public String getTypeName() {
//
//	return _typeName;
//
//    }
//
//    public String toString() {
//
//	return _typeName;
//
//    }
//
//    @Override
//    public int compareTo( @NotNull EntityTypeName2 entityTypeName2 ) {
//
//	return _typeName.compareTo( entityTypeName2._typeName );
//
//    }
//
//    @Override
//    public boolean equals( Object rhs ) {
//
//	return rhs instanceof EntityTypeName2 && compareTo( (EntityTypeName2)rhs ) == 0;
//
//    }
//
//    @Override
//    public int hashCode() {
//
//	return _typeName.hashCode();
//
//    }

}
