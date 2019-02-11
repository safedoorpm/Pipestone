/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackedEntityGroup;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class TwoDimensionalTreeMap<
        T1 extends Comparable<T1>,
        T2 extends Comparable<T2>,
        V
        > extends GowingAbstractPackableEntity implements Serializable, TwoDimensionalSortedMap<T1,T2,V> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( TwoDimensionalTreeMap.class );
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

            return new TwoDimensionalTreeMap( unPacker, bundle );

        }

    };

    /**
     A class that always yields an empty immutable {@link TwoDimensionalSortedMap}.
     <p>Since that probably guarantees that some will find this useful, here it is.</p>
     @param <A> the first dimension's key type.
     @param <B> the second dimension's key type.
     @param <V> the type of each element (none of which actually exist since the map is empty).
     */

    public static class Empty2DMap<A extends Comparable<A>,B extends Comparable<B>,V>
            extends TwoDimensionalTreeMap<A,B,V> {

        private Empty2DMap() {
            super( true );
        }

    }

    /**
     An empty immutable {@link TwoDimensionalSortedMap}.
     <p>Trying to use {@code EMPTY_2DMAP} in most contexts will yield a warning about unchecked operations.
     I find it to be simpler to get a variable of the exact type that I need and to then use that variable
     as needed.
     For example, you can get a forever empty 2D sorted map which is indexed by {@code Short} and {@code Byte} values
     and which contains no {@code Long} values as follows:
     <blockquote>
     {@code @SuppressWarnings("unchecked") TwoDimensionalSortedMap<Short,Byte,Long> foreverEmpty = EMPTY_2DMAP;}
     </blockquote>
     </p>
      */

    public static final TwoDimensionalSortedMap EMPTY_2DMAP = new TwoDimensionalTreeMap( true );

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
    public static <A extends Comparable<A>,B extends Comparable<B>,C>
    TwoDimensionalSortedMap<A,B,C> empty2DMap() {

        return EMPTY_2DMAP;

//        return new TwoDimensionalTreeMap<>(true );

    }

    private GowingEntityReference _outerMapReference;

    private final boolean _readonly;

    private SortedMap<T1,SortedMap<T2,V>> _map = new TreeMap<>();

    public TwoDimensionalTreeMap() {
        super( new GowingNameMarkerThing() );

        _readonly = false;

    }

    public TwoDimensionalTreeMap( boolean makeReadonly ) {
        this( new TwoDimensionalTreeMap<>(), makeReadonly );
    }

    public TwoDimensionalTreeMap( final @NotNull TwoDimensionalSortedMap<T1,T2,V> map, final boolean makeReadonly ) {
        super( new GowingNameMarkerThing() );

        addAll( map );

        if ( makeReadonly ) {

            for ( T1 t1 : _map.keySet() ) {

                _map.put( t1, Collections.unmodifiableSortedMap( _map.get( t1 ) ) );

            }

            _map = Collections.unmodifiableSortedMap( _map );

        }

        _readonly = makeReadonly;

    }

    public void addAll( final @NotNull TwoDimensionalSortedMap<T1, T2, V> map ) {

        for ( T1 t1 : map.outerKeys() ) {

            SortedMap<T2,V> innerMap = map.getInnerMap( t1, false );
            if ( innerMap != null ) {

                for ( T2 t2 : innerMap.keySet() ) {

                    put( t1, t2, innerMap.get( t2 ) );

                }

            }

        }

    }

    public TwoDimensionalTreeMap( final @NotNull TwoDimensionalSortedMap<T1,T2,V> map ) {
        this( map, false );

    }

    private TwoDimensionalTreeMap( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

        _outerMapReference = bundle.getMandatoryEntityReference( OUTER_MAP );

        _readonly = bundle.booleanValue( READONLY );

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        TreeMap<T1,GowingPackableMapping<T2,V>> fullMapping = new TreeMap<>();
        for ( T1 key1 : _map.keySet() ) {

            SortedMap<T2, V> inner = _map.get( key1 );
            if ( !inner.isEmpty() ) {

                SortedMap<T2,V> condensedInner = new TreeMap<>();
                for ( T2 key2 : inner.keySet() ) {

                    V value = inner.get( key2 );
                    if ( value != null ) {

                        condensedInner.put( key2, value );

                    }

                }

                if ( !condensedInner.isEmpty() ) {

                    GowingPackableMapping<T2, V> packedInner = new GowingPackableMapping<>( condensedInner );
                    fullMapping.put( key1, packedInner );

                }

            }

        }

        GowingPackableMapping<T1,GowingPackableMapping<T2,V>> packedMapping = new GowingPackableMapping<>( fullMapping );

        bundle.addPackableEntityHolder( OUTER_MAP, packedMapping, packer, true );
        bundle.addBooleanHolder( READONLY, _readonly, true );

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
            @SuppressWarnings("unchecked") GowingPackableMapping<T1, GowingPackableMapping<T2, V>> packable1 =
                    (GowingPackableMapping<T1, GowingPackableMapping<T2, V>>)packable;
            for ( GowingPackableKeyValuePair<T1, GowingPackableMapping<T2, V>> outer : packable1.getMappings() ) {

                ObtuseUtil.doNothing();

                for ( GowingPackableKeyValuePair<T2, V> inner : outer.getValue().getMappings() ) {

                    put( outer.getKey(), inner.getKey(), inner.getValue() );

                    ObtuseUtil.doNothing();

                }

            }

        } else {

            GowingUtil.getGrumpy( "TwoDimensionalTreeMap", "outer map", GowingPackableMapping.class, packable );

        }

        return true;

    }

    @Override
    public boolean isReadonly() {

        return _readonly;

    }

    @Override
    public V put( final T1 key1, final T2 key2, final V value ) {

        SortedMap<T2,V> innerMap = getNotNullInnerMap( key1 );

        V rval = innerMap.put( key2, value );

        return rval;

    }

    @Override
    @Nullable
    public SortedMap<T2,V> getInnerMap( final T1 key1, final boolean forceCreate ) {

        SortedMap<T2,V> innerMap = _map.get( key1 );
        if ( innerMap == null && forceCreate ) {

            innerMap = new TreeMap<>();
            _map.put( key1, innerMap );

        }

        return innerMap;

    }

    @Override
    @NotNull
    public SortedMap<T2,V> getNotNullInnerMap( final T1 key1 ) {

        SortedMap<T2, V> innerMap = _map.computeIfAbsent( key1, k -> new TreeMap<>() );

        return innerMap;

    }

    @Override
    @Nullable
    public SortedMap<T2,V> removeInnerMap( final T1 key ) {

        return _map.remove( key );

    }

    @Override
    @Nullable
    public V get( final T1 key1, final T2 key2 ) {

        SortedMap<T2,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            return null;

        }

        return innerMap.get( key2 );

    }

    @Override
    @Nullable
    public V remove( final T1 key1, final T2 key2 ) {

        V rval = null;

        SortedMap<T2,V> innerMap = _map.get( key1 );
        if ( innerMap != null ) {

            rval = innerMap.remove( key2 );

            // Note that we cannot remove this inner map if it is now empty since someone may already have a reference
            // to this particular inner map.

        }

        return rval;

    }

    @Override
    public int size() {

        int totalSize = 0;
        for ( SortedMap<T2,V> innerMap : innerMaps() ) {

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

        for ( SortedMap<T2,V> innerMap : innerMaps() ) {

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
    public Collection<SortedMap<T2,V>> innerMaps() {

        return _map.values();

    }

    @Override
    public boolean containsKeys( final T1 key1, final T2 key2 ) {

        if ( _map.containsKey( key1 ) ) {

            @Nullable SortedMap<T2, V> innerMap = getInnerMap( key1, false );
            //noinspection RedundantIfStatement
            if ( innerMap != null && innerMap.containsKey( key2 ) ) {

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

                    SortedMap<T2, V> innerMap = getInnerMap( _activeOuterKey, false );
                    //noinspection StatementWithEmptyBody
                    if ( innerMap == null || innerMap.isEmpty() ) {

                        // skip this one

                    } else {

                        _innerIterator = innerMap.values()
                                                 .iterator();
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

    @NotNull
    public String toString() {

        return "TwoDimensionalTreeMap( size = " + size() + " )";

    }

    @SuppressWarnings("Duplicates")
    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "ObtuseUtil", "testing", null );

        TwoDimensionalSortedMap<Integer,String,String> originalMap = new TwoDimensionalTreeMap<>();
        originalMap.put( 1, "hello", "1-hello" );
        originalMap.put( 2, "hello", "2-hello" );
        originalMap.put( 3, "hello", "3-hello" );
        originalMap.put( 1, "world", "1-world" );
        originalMap.put( 2, "world", "2-world" );

        displayMap( "originalMap", originalMap );

        EntityName en = new EntityName( "eName" );
        File testFile = new File( "2dsortedMap-test.packed" );
        ObtuseUtil.packQuietly( en, originalMap, testFile, false );
        try ( Measure ignored = new Measure( "TwoDimensionalTreeMap unpack main" ) ){

            GowingUnPackedEntityGroup unpackedEntities = ObtuseUtil.unpack(
                    testFile,
                    new GowingEntityFactory[0]
            );

            GowingUtil.logUnpackResults( "TwoDimensionalTreeMap test", unpackedEntities );

            if ( unpackedEntities.getNamedClasses()
                                 .containsKey( en ) ) {

                @NotNull List<GowingPackable> interestingStuff =
                        unpackedEntities.getNamedClasses()
                                        .getValues( en );

                if ( interestingStuff.size() == 1 ) {

                    GowingPackable first = interestingStuff.get( 0 );
                    if ( first instanceof TwoDimensionalSortedMap ) {

                        @SuppressWarnings("unchecked") TwoDimensionalTreeMap<Integer, String, String> recoveredMap =
                                ( (TwoDimensionalTreeMap<Integer, String, String>)interestingStuff.get( 0 ) );

                        displayMap( "recoveredMap", recoveredMap );

                    } else {

                        throw new HowDidWeGetHereError(
                                "LancotMediaLibraryRoot.readImportBundle:  " +
                                "read yielded a " +
                                first.getClass()
                                     .getCanonicalName() +
                                " when we expected a " +
                                TwoDimensionalSortedMap.class.getCanonicalName()
                        );

                    }

                } else {

                    throw new HowDidWeGetHereError(
                            "LancotMediaLibraryRoot.readImportBundle:  read yielded " +
                            ObtuseUtil.pluralize( interestingStuff.size(), "element", "elements" ) +
                            " when we expected one element"
                    );

                }

            } else {

                Logger.logMsg(
                        "2dsortedMap-test(unpack):  " +
                        "did not find a " + en + " in " + unpackedEntities
                );

            }

        } catch ( GowingUnpackingException e ) {

            Logger.logErr( "com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException caught", e );

        }

    }

    private static void displayMap(
            final String title,
            final TwoDimensionalSortedMap map
    ) {

        Logger.logMsg( title );
        for ( Object ixO : map.outerKeys() ) {

            int ix = ((Integer)ixO).intValue();

            @SuppressWarnings("unchecked") SortedMap notNullInnerMap = map.getNotNullInnerMap( ix );
            for ( Object sx : notNullInnerMap.keySet() ) {

                Logger.logMsg( "    map(" + ix + "," + sx + ") = " + notNullInnerMap.get( sx ) );

            }

        }

    }

}
