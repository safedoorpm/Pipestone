package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
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

public class GowingPackableMapping<K, V> implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableMapping.class.getCanonicalName() );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final List<GowingPackableKeyValuePair<K, V>> _keyValuePairs;

    private List<GowingEntityReference> _kvpReferences;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

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
        public GowingPackable createEntity( @NotNull GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle, GowingEntityReference er ) {

            return new GowingPackableMapping( unPacker, bundle, er );

        }

    };

    public GowingPackableMapping() {

        super();

        _keyValuePairs = new FormattingLinkedList<>();

    }

    public GowingPackableMapping( Map<? extends K, ? extends V> map ) {

        super();

        _keyValuePairs = new FormattingLinkedList<>();

        for ( K key : map.keySet() ) {

            V value = map.get( key );

            addMapping( key, value );

        }

    }

    public void addMapping( K key, V value ) {

        GowingPackableKeyValuePair<K, V> kvp = new GowingPackableKeyValuePair<>( key, value );

        addMapping( kvp );

    }

    public void addMapping( GowingPackableKeyValuePair<K, V> kvp ) {

        _keyValuePairs.add( kvp );

    }

    public GowingPackableMapping( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {

        super();

        if ( bundle.getVersion() != VERSION ) {

            throw new IllegalArgumentException( GowingPackableMapping.class.getCanonicalName() +
                                                ":  expected version " +
                                                VERSION +
                                                " but received version " +
                                                bundle.getVersion() );

        }

        int ix = 0;
        _keyValuePairs = new FormattingLinkedList<>();
        _kvpReferences = new FormattingLinkedList<>();
        while ( true ) {

            GowingPackableThingHolder holder = bundle.get( new EntityName( "_" + ix ) );
            if ( holder == null ) {

                break;

            }

            _kvpReferences.add( (GowingEntityReference)holder.getObjectValue() );

            ix += 1;

        }

        ObtuseUtil.doNothing();

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker unPacker ) {

        ObtuseUtil.doNothing();

        for ( GowingEntityReference er : _kvpReferences ) {

            if ( !unPacker.isEntityFinished( er ) ) {

                return false;

            }

        }

        for ( GowingEntityReference er : _kvpReferences ) {

            // The _kvpReferences list only contains GowingPackableKeyValuePair<K,V> instances so the following cast is actually quite safe.

            @SuppressWarnings("unchecked")
            GowingPackableKeyValuePair<K, V> kvp = (GowingPackableKeyValuePair<K, V>)unPacker.resolveReference( er );
            Logger.logMsg( "kvp = " + kvp );
            addMapping( kvp );

        }

        _kvpReferences = null;

        return true;

    }

    public List<GowingPackableKeyValuePair<K, V>> getMappings() {

        return Collections.unmodifiableList( _keyValuePairs );

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {

        GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                null,
                packer.getPackingContext()
        );

        int ix = 0;

        for ( GowingPackableKeyValuePair kvp : _keyValuePairs ) {

            rval.addHolder( new GowingPackableEntityHolder( new EntityName( "_" + ix ), kvp, packer, true ) );
//	    Packable2KeyValuePair.packObj( rval, new EntityName( "_" + ix ), kvp, packer );

            ix += 1;

        }

        return rval;

    }

}
