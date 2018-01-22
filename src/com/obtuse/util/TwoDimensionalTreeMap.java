/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public class TwoDimensionalTreeMap<T1,T2,V> implements Serializable, TwoDimensionalSortedMap<T1,T2,V> {

    private SortedMap<T1,SortedMap<T2,V>> _map = new TreeMap<>();

    public TwoDimensionalTreeMap() {
        super();

    }

    public TwoDimensionalTreeMap( final TwoDimensionalSortedMap<T1,T2,V> map ) {
        super();

        for ( T1 t1 : map.outerKeys() ) {

            SortedMap<T2,V> innerMap = map.getInnerMap( t1, false );
            if ( innerMap != null ) {

                for ( T2 t2 : innerMap.keySet() ) {

                    put( t1, t2, innerMap.get( t2 ) );

                }

            }

        }

    }

    @Override
    public V put( final T1 key1, final T2 key2, final V value ) {

        SortedMap<T2,V> innerMap = getNotNullInnerMap( key1 );

        @SuppressWarnings("UnnecessaryLocalVariable") V rval = innerMap.put( key2, value );

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

        @SuppressWarnings("UnnecessaryLocalVariable") SortedMap<T2, V> innerMap = _map.computeIfAbsent( key1, k -> new TreeMap<>() );

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

        return new Iterator<V>() {

            private final Iterator<T1> _outerIterator;
            private T1 _activeOuterKey;
            private Iterator<V> _innerIterator;

            {

                _outerIterator = _map.keySet().iterator();

                findNextNonEmptyInnerMap();

            }

            private void findNextNonEmptyInnerMap() {

                _innerIterator = null;

                while ( _outerIterator.hasNext() ) {

                    _activeOuterKey = _outerIterator.next();

                    SortedMap<T2,V> innerMap = getInnerMap( _activeOuterKey, false );
                    //noinspection StatementWithEmptyBody
                    if ( innerMap == null || innerMap.isEmpty() ) {

                        // skip this one

                    } else {

                        _innerIterator = innerMap.values().iterator();
                        break;

                    }

                }

            }

            public boolean hasNext() {

                return _innerIterator != null && _innerIterator.hasNext();

            }

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

    @NotNull
    public String toString() {

        return "TwoDimensionalTreeMap( size = " + size() + " )";

    }

}
