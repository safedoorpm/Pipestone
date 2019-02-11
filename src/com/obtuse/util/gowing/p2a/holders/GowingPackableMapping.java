package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.Ix;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide an easy way to pack an arbitrary map and retrieve it during the subsequent unpack operation.
 <p>This class is designed (and hopefully implemented) to pack and unpack instances of any class which implements
 the {@link Map}{@code <K,V>} interface subject to the constraint that both {@code K} and {@code V} must implement
 the {@link GowingPackable} interface or must be instances of one of the following classes:

 <blockquote>Boolean, Byte, Double, Float, Integer, Long, Short, or String</blockquote>
 Note that the map's class itself need not implement the {@link GowingPackable} interface (see example below for more info).
 </p>
 <p>The basic idea is that when packing, the user's {@link GowingPackable#bundleThyself(boolean, GowingPacker)}
 method copies their {@link Map}{@code <K,V>} instance into a {@link GowingPackableMapping}{@code <K,V>} instance and
 adds it to their {@link GowingPackedEntityBundle} {@code bundle} as follows:
 <blockquote>
 <pre> bundle.addHolder(
     new {@link GowingPackableEntityHolder} (
         MAPPED_ENTITY_NAME,
         new GowingPackableMapping<>( _myMapping ),
         packer,
         true  // or false as appropriate
     )
 );
 </pre>
 </blockquote>
</p>
 <p>When the time comes to unpack, the user unbundles the packed mapping's entity reference in the usual fashion:
 <blockquote>
 <pre>_mappingEntityReference = bundle.getMandatoryEntityReference( MAPPED_ENTITY_NAME );
 </pre>
 <p>The user's {@link GowingPackable#finishUnpacking(GowingUnPacker)} method then finishes the job as follows:
 <blockquote>
 <pre> {@link GowingPackable} packedSavedValues = unPacker.resolveReference( _mappingEntityReference );
 if ( !( packedSavedValues instanceof GowingPackableMapping ) ) {

     // get grumpy or switch to some other plan
     // (maybe older packed files contain MAPPED_ENTITY_NAME entities in some other kind of package)

     . . .

 } else {

     &#64;SuppressWarnings("unchecked")
     GowingPackableMapping<MyKeyClass, MyValueClass> mapping = (GowingPackableMapping&lt;MyKeyClass,MyValueClass>)packedSavedValues;

     // This example assumes that the _myMapping instance has been created earlier.

     // Probably a good idea to make sure that the mapping starts out empty.

     _myMapping.clear();

     for ( {@link GowingPackableKeyValuePair}&lt;MyKeyClass, MyValueClass> tuple : mapping.getMappings() ) {

        _savedValues.put( tuple.getKey(), tuple.getValue() );

     }

 }
 </pre>
 </blockquote>
 </p>
 A few notes are probably in order:
 <ul>
 <li>You may have noticed that this class does not actually implement the {@link Map}{@code <K,V>} interface but instead
 carries a collection of the <em>key-value</em> mappings that your {@link Map}{@code <K,V>} entity represents.
 This allows this class to be used to pack and unpack anything that implements the {@link Map}{@code <K,V>} interface
 without having to get involved with very difficult if not impossible to generalize issues which arise when sorted maps
 use unconventional comparators.
 </li>
 <li>Instances of this class should pack into an essentially optimal number of bytes (you may be able to do better yourself
 but the savings is unlikely to be worth the trouble).</li>
 </ul>
 */

public class GowingPackableMapping<K, V> implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableMapping.class );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final List<GowingPackableKeyValuePair<K, V>> _keyValuePairs;

    private GowingEntityReference[] _kvpReferences;
    private int _nextKvpReferenceIx = 0;

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
        public GowingPackable createEntity( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle, final @NotNull GowingEntityReference er ) {

            return new GowingPackableMapping( unPacker, bundle, er );

        }

    };

    public GowingPackableMapping() {

        super();

        _keyValuePairs = new FormattingLinkedList<>();

    }

    public GowingPackableMapping( final Map<? extends K, ? extends V> map ) {

        this();

        for ( K key : map.keySet() ) {

            V value = map.get( key );

            addMapping( key, value );

        }

    }

    public void addMapping( final K key, final V value ) {

        @SuppressWarnings("unchecked")
        V value2 = ( value instanceof Collection && !( value instanceof GowingPackableCollection ) )
                ?
                (V)( new GowingPackableCollection( (Collection)value ) )
                :
                value;

        GowingPackableKeyValuePair<K, V> kvp = new GowingPackableKeyValuePair<>( key, value2 );

        addMapping( kvp );

    }

    public void addMapping( final GowingPackableKeyValuePair<K, V> kvp ) {

        _keyValuePairs.add( kvp );

    }

    public GowingPackableMapping(
            @SuppressWarnings("unused") final @NotNull GowingUnPacker unPacker,
            final @NotNull GowingPackedEntityBundle bundle,
            @SuppressWarnings("unused") final @NotNull GowingEntityReference er
    ) {

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
        FormattingLinkedList<GowingEntityReference> tmpKvpReferences = new FormattingLinkedList<>();
        while ( true ) {

            GowingPackableThingHolder holder = bundle.get( new EntityName( "_" + ix ) );
            if ( holder == null ) {

                break;

            }

            tmpKvpReferences.add( (GowingEntityReference)holder.getObjectValue() );

            ix += 1;

        }

        _kvpReferences = tmpKvpReferences.toArray( new GowingEntityReference[0] );

        ObtuseUtil.doNothing();

    }

    /**
     Finish unpacking.
     <p>VERY IMPORTANT POINT: we're not finished until every single {@link GowingPackableKeyValuePair} instance that we refer to is finished.
     Violating this rule will result in all sorts of chaos because those who reference this instance will need to be able to
     use our {@link GowingPackableKeyValuePair} instances to rebuild their maps.</p>
     @param unPacker the {@link GowingUnPacker} that running this circus.
     @return {@code true} if we are finished; {@code false} otherwise.
     */

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        ObtuseUtil.doNothing();

        for ( int ix = _nextKvpReferenceIx; ix < _kvpReferences.length; ix += 1 ) {

            GowingEntityReference er = _kvpReferences[ix];

            if ( !unPacker.isEntityFinished( er ) ) {

                _nextKvpReferenceIx = ix;
                return false;

            }

        }

        _nextKvpReferenceIx = _kvpReferences.length;

        ObtuseUtil.doNothing();

        for ( GowingEntityReference er : _kvpReferences ) {

            // The _kvpReferences list only contains GowingPackableKeyValuePair<K,V> instances so the following cast is actually quite safe.

            @SuppressWarnings("unchecked")
            GowingPackableKeyValuePair<K, V> kvp = (GowingPackableKeyValuePair<K, V>)unPacker.resolveMandatoryReference( er );
            addMapping( kvp );

        }

        _kvpReferences = null;

        return true;

    }

    /**
     Get the raw key-value pairs.
     There's not much use for this except possibly debugging what's actually going on.
     @return the key-value pairs as they came out of the bundle.
     */

    public List<GowingPackableKeyValuePair<K, V>> getMappings() {

        return Collections.unmodifiableList( _keyValuePairs );

    }

    public <W extends Map<K,V>> W rebuildMap( final @NotNull W map ) {

        for ( GowingPackableKeyValuePair<K,V> pair : _keyValuePairs ) {

            map.put( pair.getKey(), pair.getValue() );

        }

        return map;

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        for ( Ix<GowingPackableKeyValuePair<K, V>> kvp : Ix.arrayList( _keyValuePairs ) ) {

            bundle.addPackableEntityHolder( new EntityName( "_" + kvp.ix ), kvp.item, packer, true );

        }

        return bundle;

    }

    public String toString() {

        return "GowingPackableMapping( " +
               "kVP size is " + ( _keyValuePairs == null ? "null" : _keyValuePairs.size() ) + ", " +
               "kvpR size is " + ( _kvpReferences == null ? "null" : _kvpReferences.length ) +
               " )";

    }

}
