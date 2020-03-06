/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public class ThreeDimensionalTreeMap<
        T1 extends Comparable<T1>,
        T2 extends Comparable<T2>,
        T3 extends Comparable<T3>,
        V
        > extends GowingAbstractPackableEntity implements Serializable, ThreeDimensionalSortedMap<T1,T2,T3,V> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ThreeDimensionalTreeMap.class );
    private static final int VERSION = 1;

    private static final EntityName OUTER_MAP = new EntityName( "_om" );
    private static final EntityName READONLY = new EntityName( "_um" );

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
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new ThreeDimensionalTreeMap<>( unPacker, bundle );

        }

    };

    /**
     A class that always yields an empty immutable {@link ThreeDimensionalSortedMap}.
     <p>Since that probably guarantees that some will find this useful, here it is.</p>
     @param <A> the first dimension's key type.
     @param <B> the second dimension's key type.
     @param <V> the type of each element (none of which actually exist since the map is empty).
     */

    public static class Empty3DMap<A extends Comparable<A>,B extends Comparable<B>,C extends Comparable<C>,V>
            extends ThreeDimensionalTreeMap<A,B,C,V> {

        private Empty3DMap() {
            super( true );
        }

    }

    /**
     An empty immutable {@link ThreeDimensionalSortedMap}.
     <p>Trying to use {@code EMPTY_MAP3D} in most contexts will yield a warning about unchecked operations.
     I find it to be simpler to get a variable of the exact type that I need and to then use that variable
     as needed.
     For example, you can get a forever empty 2D sorted map which is indexed by {@code Short} and {@code Byte} values
     and which contains no {@code Long} values as follows:
     <blockquote>
     {@code @SuppressWarnings("unchecked") ThreeDimensionalSortedMap<Short,Byte,Long> foreverEmpty = EMPTY_MAP3D;}
     </blockquote>
     </p>
     */

    @SuppressWarnings("rawtypes")
    public static final ThreeDimensionalSortedMap EMPTY_MAP3D = new ThreeDimensionalTreeMap( true );

    /**
     Create an immutable empty 2D sorted map.
     <p>The current implementation of this method always yields the same empty immutable sorted map.
     In other words, {@code empty2DMap() == empty2DMap()} is currently always true.
     This could change in future implementations. Therefore,
     <u><b>do not assume that two different calls to this method each yield distinct empty immutable 2D sorted maps.
     Also, do not assume that two different calls to this map each yield the same empty immutable 2D sorted map instance.</b></u></p>
     @param <A> the first dimension's key type.
     @param <B> the second dimension's key type.
     @param <C> the type of each element.
     @return an empty immutable 2D sorted map of the specified type.
     */

    @SuppressWarnings({ "unchecked", "unused" })
    public static <A extends Comparable<A>,B extends Comparable<B>,C extends Comparable<C>,V>
    ThreeDimensionalSortedMap<A,B,C,V> empty2DMap() {

        return EMPTY_MAP3D;

    }

    private GowingEntityReference _outerMapReference;

    public final boolean _readonly;

    private SortedMap<T1,TwoDimensionalSortedMap<T2,T3,V>> _map = new TreeMap<>();

    public ThreeDimensionalTreeMap() {
        super( new GowingNameMarkerThing() );

        _readonly = false;

    }

    @SuppressWarnings("UnusedDeclaration")
    public ThreeDimensionalTreeMap( final @NotNull ThreeDimensionalSortedMap<T1,T2,T3,V> map, final boolean makeReadonly ) {
        super( new GowingNameMarkerThing() );

        for ( T1 t1 : map.outerKeys() ) {

            TwoDimensionalSortedMap<T2,T3,V> innerMap = map.getInnerMap( t1, false );
            if ( innerMap != null ) {

                _map.put( t1, new TwoDimensionalTreeMap<>( innerMap ) );

            }

        }

        if ( makeReadonly ) {

            _map.replaceAll( ( t, v ) -> new TwoDimensionalTreeMap<>( _map.get( t ), true ) );

            _map = Collections.unmodifiableSortedMap( _map );

        }

        _readonly = makeReadonly;

    }

    public ThreeDimensionalTreeMap( boolean makeReadonly ) {
        this( new ThreeDimensionalTreeMap<>(), makeReadonly );
    }

    @SuppressWarnings("unused")
    public ThreeDimensionalTreeMap( final @NotNull ThreeDimensionalSortedMap<T1,T2,T3,V> map ) {
        this( map, false );

    }

    private ThreeDimensionalTreeMap( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

        _outerMapReference = bundle.getMandatoryEntityReference( OUTER_MAP );
        _readonly = bundle.booleanValue( READONLY );

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        @SuppressWarnings("Duplicates") GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        GowingPackableMapping<T1,TwoDimensionalSortedMap<T2,T3,V>> packedMapping = new GowingPackableMapping<>( _map );

        bundle.addPackableEntityHolder( OUTER_MAP, packedMapping, packer, true );
        bundle.addBooleanHolder( READONLY, _readonly );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        // Our parent class has no finishUnpacking method so we skip the step of letting it finish unpacking.

        // Check if our outer map's instance is ready for use.

        if ( !unPacker.isEntityFinished( _outerMapReference ) ) {

            return false;

        }

        GowingPackable packable = unPacker.resolveMandatoryReference( _outerMapReference );
        if ( ( packable instanceof GowingPackableMapping ) ) {

            // The temporary variable is required in order to make this assignment a declaration which allows
            // the @SuppressWarnings("unchecked") annotation (the annotation is not allowed on a simple assignment statement).
            @SuppressWarnings("unchecked")
            TreeMap<T1, TwoDimensionalSortedMap<T2, T3, V>> tmap =
                    ( (GowingPackableMapping<T1,TwoDimensionalSortedMap<T2,T3,V>>)packable ).rebuildMap( new TreeMap<>() );
            _map = tmap;

            if ( _readonly ) {

                _map.replaceAll( ( t, v ) -> new TwoDimensionalTreeMap<>( _map.get( t ), true ) );

                _map = Collections.unmodifiableSortedMap( _map );

            }

        } else {

            GowingUtil.getGrumpy( "ThreeDimensionalTreeMap", "outer map", GowingPackableMapping.class, packable );

        }

        return true;

    }

    @Override
    public boolean isReadonly() {

        return _readonly;

    }

    public V put( final @NotNull T1 key1, final @NotNull T2 key2, final @NotNull T3 key3, @Nullable final V value ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = getNotNullInnerMap( key1 );

        V rval = innerMap.put( key2, key3, value );

        return rval;

    }

    @Override
    @NotNull
    public TwoDimensionalSortedMap<T2,T3,V> getNotNullInnerMap( final @NotNull T1 key1 ) {

        TwoDimensionalSortedMap<T2, T3, V> innerMap = _map.computeIfAbsent( key1, k -> new TwoDimensionalTreeMap<>() );

        return innerMap;

    }

    @Override
    @Nullable
    public TwoDimensionalSortedMap<T2,T3,V> getInnerMap( final @NotNull T1 key1, final boolean forceCreate ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null && forceCreate ) {

            innerMap = new TwoDimensionalTreeMap<>();
            _map.put( key1, innerMap );

        }

        return innerMap;

    }

    @Override
    @Nullable
    public TwoDimensionalSortedMap<T2,T3,V> removeInnerMap( final @NotNull T1 key ) {

        return _map.remove( key );

    }

    @Override
    @Nullable
    public V get( final @NotNull T1 key1, final @NotNull T2 key2, final @NotNull T3 key3 ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            return null;

        }

        return innerMap.get( key2, key3 );

    }

    @Override
    @Nullable
    public V remove( final @NotNull T1 key1, final @NotNull T2 key2, final @NotNull T3 key3 ) {

        V rval = null;

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap != null ) {

            rval = innerMap.remove( key2, key3 );

            // Note that we cannot remove this inner map if it is now empty since someone may already have a reference
            // to this particular inner map.

        }

        return rval;

    }

    @Override
    public int size() {

        int totalSize = 0;
        for ( TwoDimensionalSortedMap<T2,T3,V> innerMap : innerMaps() ) {

            totalSize += innerMap.size();

        }

        return totalSize;

    }

    @Override
    public boolean isEmpty() {

        // We could just use "return size() == 0" but the short circuiting that we do below makes this approach
        // faster if the map is not empty.

        if ( _map.isEmpty() ) {

            return true;

        }

        for ( TwoDimensionalSortedMap<T2,T3,V> innerMap : innerMaps() ) {

            if ( !innerMap.isEmpty() ) {

                return false;

            }

        }

        return true;

    }

    @Override
    @NotNull
    public Set<T1> outerKeys() {

        return _map.keySet();

    }

    @Override
    @NotNull
    public Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps() {

        return _map.values();

    }

    @Override
    public boolean containsKeys( final T1 key1, final T2 key2, final T3 key3 ) {

        if ( _map.containsKey( key1 ) ) {

            @Nullable TwoDimensionalSortedMap<T2,T3,V> innerMap = getInnerMap( key1, false );
            //noinspection RedundantIfStatement
            if ( innerMap != null && innerMap.containsKeys( key2, key3 ) ) {

                return true;

            }

        }

        return false;
    }

    @Override
    @NotNull
    public Iterator<V> iterator() {

        return new Iterator<>() {

            private final Iterator<T1> _outerIterator;
            private T1 _activeOuterKey;
            private Iterator<V> _innerIterator;

            {

                _outerIterator = _map.keySet()
                                     .iterator();

                findNextNonEmptyInnerMap();

            }

            private void findNextNonEmptyInnerMap() {

                _innerIterator = null;

                while ( _outerIterator.hasNext() ) {

                    _activeOuterKey = _outerIterator.next();

                    TwoDimensionalSortedMap<T2, T3, V> innerMap = getInnerMap( _activeOuterKey, false );
                    //noinspection StatementWithEmptyBody
                    if ( innerMap == null || innerMap.isEmpty() ) {

                        // skip this one

                    } else {

                        _innerIterator = innerMap.iterator();

                        break;

                    }

                }

            }

            public boolean hasNext() {

                return _innerIterator != null && _innerIterator.hasNext();

            }

            @SuppressWarnings("Duplicates")
            public V next() {

                if ( !hasNext() ) {

                    throw new NoSuchElementException( "no more values" );

                }

                V next = _innerIterator.next();
                if ( _innerIterator != null && !_innerIterator.hasNext() ) {

                    findNextNonEmptyInnerMap();

                }

                return next;

            }

            public void remove() {

                throw new UnsupportedOperationException( "unable to remove values via this iterator" );

            }

        };

    }

    @Override
    public void clear() {

        _map.clear();

    }

    public String toString() {

        return "ThreeDimensionalTreeMap( size = " + size() + " )";

    }

}
