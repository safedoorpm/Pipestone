/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.Serializable;
import java.util.*;

public class ThreeDimensionalTreeMap<T1,T2,T3,V> implements Serializable, ThreeDimensionalSortedMap<T1,T2,T3,V> {

    private SortedMap<T1,TwoDimensionalSortedMap<T2,T3,V>> _map = new TreeMap<>();

    public ThreeDimensionalTreeMap() {
        super();

    }

    @SuppressWarnings("UnusedDeclaration")
    public ThreeDimensionalTreeMap( ThreeDimensionalSortedMap<T1,T2,T3,V> map ) {
        super();

        for ( T1 t1 : map.outerKeys() ) {

            TwoDimensionalSortedMap<T2,T3,V> innerMap = map.getInnerMap( t1, false );
            _map.put( t1, new TwoDimensionalTreeMap<>( innerMap ) );

        }

    }

    public void put( T1 key1, T2 key2, T3 key3, V value ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = getInnerMap( key1, true );

        innerMap.put( key2, key3, value );

    }

    public TwoDimensionalSortedMap<T2,T3,V> getInnerMap( T1 key1, boolean forceCreate ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null && forceCreate ) {

            innerMap = new TwoDimensionalTreeMap<>();
            _map.put( key1, innerMap );

        }

        return innerMap;

    }

    public TwoDimensionalSortedMap<T2,T3,V> removeInnerMap( T1 key ) {

        return _map.remove( key );

    }

    public V get( T1 key1, T2 key2, T3 key3 ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            return null;

        }

        return innerMap.get( key2, key3 );

    }

    public V remove( T1 key1, T2 key2, T3 key3 ) {

        V rval = null;

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap != null ) {

            rval = innerMap.remove( key2, key3 );

            // Note that we cannot remove this inner map if it is now empty since someone may already have a reference
            // to this particular inner map.

        }

        return rval;

    }

    public int size() {

        int totalSize = 0;
        for ( TwoDimensionalSortedMap<T2,T3,V> innerMap : innerMaps() ) {

            totalSize += innerMap.size();

        }

        return totalSize;

    }

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

    public Set<T1> outerKeys() {

        return _map.keySet();

    }

    public Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps() {

        return _map.values();

    }

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

                    TwoDimensionalSortedMap<T2,T3,V> innerMap = getInnerMap( _activeOuterKey, false );
                    //noinspection StatementWithEmptyBody
                    if ( innerMap.isEmpty() ) {

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

    public String toString() {

        return "ThreeDimensionalTreeMap( size = " + size() + " )";

    }

}
