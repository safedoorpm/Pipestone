package com.obtuse.util.packers.packer2.p2a.util;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import com.obtuse.util.packers.packer2.p2a.EntityReference;
import com.obtuse.util.packers.packer2.p2a.holders.PackableEntityHolder2;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide an easy way to pack an arbitrary map and retrieve it during the subsequent unpack operation.
 */

public class Packable2Mapping<K,V> implements Packable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( Packable2Mapping.class.getCanonicalName() );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final InstanceId _instanceId;

    private final List<Packable2KeyValuePair<K,V>> _keyValuePairs;

    private List<EntityReference> _kvpReferences;

    public static final EntityFactory2 FACTORY = new EntityFactory2( ENTITY_TYPE_NAME ) {

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

	    return new Packable2Mapping( unPacker, bundle, er );

	}

    };

    public Packable2Mapping() {
	super();

	_instanceId = new InstanceId( ENTITY_TYPE_NAME );

	_keyValuePairs = new FormattingLinkedList<Packable2KeyValuePair<K,V>>();

    }

    public Packable2Mapping( Map<? extends K,? extends V> map ) {
	super();

	_instanceId = new InstanceId( ENTITY_TYPE_NAME );

	_keyValuePairs = new FormattingLinkedList<Packable2KeyValuePair<K,V>>();

	for ( K key : map.keySet() ) {

	    V value = map.get( key );

	    Packable2KeyValuePair<K,V> kvp = new Packable2KeyValuePair<K, V>( key, value );

	    _keyValuePairs.add( kvp );

	}

    }

    public Packable2Mapping( UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {
	super();

	_instanceId = new InstanceId( ENTITY_TYPE_NAME );

	if ( bundle.getVersion() != VERSION ) {

	    throw new IllegalArgumentException( Packable2Mapping.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	}

	int ix = 0;
	_keyValuePairs = new FormattingLinkedList<Packable2KeyValuePair<K,V>>();
	_kvpReferences = new FormattingLinkedList<EntityReference>();
	while ( true ) {

	    Packable2ThingHolder2 holder = bundle.get( new EntityName2( "_" + ix ) );
	    if ( holder == null ) {

		break;

	    }

	    _kvpReferences.add( (EntityReference)holder.getObjectValue() );

	    ix += 1;

	}

	ObtuseUtil.doNothing();

    }

    @Override
    public boolean finishUnpacking( UnPacker2 unPacker ) {

	ObtuseUtil.doNothing();

	for ( EntityReference er : _kvpReferences ) {

	    if ( !unPacker.isEntityFinished( er ) ) {

		return false;

	    }

	}

	for ( EntityReference er : _kvpReferences ) {

	    Packable2KeyValuePair<K, V> kvp = (Packable2KeyValuePair<K, V>) unPacker.resolveReference( er );
	    Logger.logMsg( "kvp = " + kvp );
	    _keyValuePairs.add( kvp );

	}

	_kvpReferences = null;

	return true;

    }

    public List<Packable2KeyValuePair<K, V>> getMappings() {

	return Collections.unmodifiableList( _keyValuePairs );

    }

    @Override
    public InstanceId getInstanceId() {

	return _instanceId;

    }

    @NotNull
    @Override
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	PackedEntityBundle rval = new PackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	int ix = 0;

	for ( Packable2KeyValuePair kvp : _keyValuePairs ) {

	    rval.addHolder( new PackableEntityHolder2( new EntityName2( "_" + ix ), kvp, packer, true ) );
//	    Packable2KeyValuePair.packObj( rval, new EntityName2( "_" + ix ), kvp, packer );

	    ix += 1;

	}

	return rval;

    }

}
