package com.obtuse.util.gowing.packer2.p2a.util;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.packer2.*;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.holders.GowingPackableEntityHolder2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide an easy way to pack an arbitrary map and retrieve it during the subsequent unpack operation.
 */

public class GowingPackable2Mapping<K,V> implements GowingPackable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( GowingPackable2Mapping.class.getCanonicalName() );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private GowingInstanceId _instanceId;

    private final List<GowingPackable2KeyValuePair<K,V>> _keyValuePairs;

    private List<GowingEntityReference> _kvpReferences;

    public static final GowingEntityFactory2 FACTORY = new GowingEntityFactory2( ENTITY_TYPE_NAME ) {

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

	    return new GowingPackable2Mapping( unPacker, bundle, er );

	}

    };

    public GowingPackable2Mapping() {
	super();

	_instanceId = new GowingInstanceId( ENTITY_TYPE_NAME );

	_keyValuePairs = new FormattingLinkedList<GowingPackable2KeyValuePair<K,V>>();

    }

    public GowingPackable2Mapping( Map<? extends K, ? extends V> map ) {
	super();

	_instanceId = new GowingInstanceId( ENTITY_TYPE_NAME );

	_keyValuePairs = new FormattingLinkedList<GowingPackable2KeyValuePair<K,V>>();

	for ( K key : map.keySet() ) {

	    V value = map.get( key );

	    GowingPackable2KeyValuePair<K,V> kvp = new GowingPackable2KeyValuePair<K, V>( key, value );

	    _keyValuePairs.add( kvp );

	}

    }

    public GowingPackable2Mapping( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
	super();

	_instanceId = new GowingInstanceId( ENTITY_TYPE_NAME );

	if ( bundle.getVersion() != VERSION ) {

	    throw new IllegalArgumentException( GowingPackable2Mapping.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	}

	int ix = 0;
	_keyValuePairs = new FormattingLinkedList<GowingPackable2KeyValuePair<K,V>>();
	_kvpReferences = new FormattingLinkedList<GowingEntityReference>();
	while ( true ) {

	    GowingPackable2ThingHolder2 holder = bundle.get( new EntityName2( "_" + ix ) );
	    if ( holder == null ) {

		break;

	    }

	    _kvpReferences.add( (GowingEntityReference)holder.getObjectValue() );

	    ix += 1;

	}

	ObtuseUtil.doNothing();

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {

	ObtuseUtil.doNothing();

	for ( GowingEntityReference er : _kvpReferences ) {

	    if ( !unPacker.isEntityFinished( er ) ) {

		return false;

	    }

	}

	for ( GowingEntityReference er : _kvpReferences ) {

	    GowingPackable2KeyValuePair<K, V> kvp = (GowingPackable2KeyValuePair<K, V>) unPacker.resolveReference( er );
	    Logger.logMsg( "kvp = " + kvp );
	    _keyValuePairs.add( kvp );

	}

	_kvpReferences = null;

	return true;

    }

    public List<GowingPackable2KeyValuePair<K, V>> getMappings() {

	return Collections.unmodifiableList( _keyValuePairs );

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

    public final void setInstanceId( GowingInstanceId instanceId ) {

	_instanceId = instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {

	GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	int ix = 0;

	for ( GowingPackable2KeyValuePair kvp : _keyValuePairs ) {

	    rval.addHolder( new GowingPackableEntityHolder2( new EntityName2( "_" + ix ), kvp, packer, true ) );
//	    Packable2KeyValuePair.packObj( rval, new EntityName2( "_" + ix ), kvp, packer );

	    ix += 1;

	}

	return rval;

    }

}
