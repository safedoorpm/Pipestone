package com.obtuse.util.packers.packer2;

import com.obtuse.util.packers.packer2.p2a.EntityReference;
import com.obtuse.util.packers.packer2.p2a.holders.StringHolder2;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the name of an entity.
 This primarily exists to allow type-checking of parameters that are supposed to be entity names.
 */

public class EntityName2 implements Packable2, Comparable<EntityName2> {

    private static EntityTypeName2 ENTITY_NAME;

    private static final int VERSION = 1;

//    public static final EntityFactory2 FACTORY = new EntityFactory2( ENTITY_NAME ) {
//
//	@Override
//	public int getOldestSupportedVersion() {
//
//	    return VERSION;
//	}
//
//	@Override
//	public int getNewestSupportedVersion() {
//
//	    return VERSION;
//	}
//
//	@Override
//	@NotNull
//	public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle ) {
//
//	    return new EntityName2( unPacker, bundle );
//
//	}
//
//    };

    public static final EntityName2 N_NAME = new EntityName2( "_n" );

    private final String _name;

    private final InstanceId _instanceId; // = new InstanceId( ENTITY_NAME );

    public EntityName2( String name ) {
	super();

//	if ( !EntityTypeName2.class.getCanonicalName().equals( name ) && !EntityName2.class.getCanonicalName().equals( name ) ) {
//
//	    _instanceId = new InstanceId( new EntityTypeName2( EntityTypeName2.class.getCanonicalName() ) );
//
//	}

	_instanceId = new InstanceId( EntityName2.class.getCanonicalName() );

	_name = name;

    }

    public EntityName2( UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {
	this( bundle.getNotNullField( N_NAME ).StringValue() );

    }

    public static EntityFactory2 getFactory() {

	return new EntityFactory2( new EntityTypeName2( EntityName2.class ) ) {

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
	    public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {

		return new EntityName2( unPacker, bundle, er );

	    }

	};

    }

    public String getName() {

	return _name;

    }

    public String toString() {

	return _name;

    }

    public int length() {

	return _name.length();

    }

    @Override
    public int compareTo( @NotNull EntityName2 entityName2 ) {

	return _name.compareTo( entityName2._name );

    }

    @Override
    public boolean equals( Object rhs ) {

	return rhs instanceof EntityName2 && compareTo( (EntityName2)rhs ) == 0;

    }

    @Override
    public int hashCode() {

	return _name.hashCode();

    }

    @Override
    public InstanceId getInstanceId() {

//	if ( _instanceId == null ) {
//
//	    _instanceId = new InstanceId( new EntityTypeName2( _name ) );
//
//	}

	return _instanceId;

    }

    @NotNull
    @Override
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	if ( ENTITY_NAME == null ) {

	    ENTITY_NAME = new EntityTypeName2( EntityName2.class );

	}

	PackedEntityBundle rval = new PackedEntityBundle(
		ENTITY_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	rval.addHolder( new StringHolder2( N_NAME, _name, true ) );

	return rval;
    }

    @Override
    public boolean finishUnpacking( UnPacker2 unPacker ) {

	return true;

    }

}
