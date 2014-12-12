/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

/**
 * Describe how a two dimensional sorted map behaves.
 */

public interface TwoDimensionalSortedMap<T1,T2,V> extends Serializable {

    void put( T1 key1, T2 key2, V value );

    SortedMap<T2,V> getInnerMap( T1 key1, boolean forceCreate );

    V get( T1 key1, T2 key2 );

    SortedMap<T2,V> removeInnerMap( T1 key );

    V remove( T1 key1, T2 key2 );

    Set<T1> outerKeys();

    Collection<SortedMap<T2,V>> innerMaps();

    Iterator<V> iterator();

    int size();

    boolean isEmpty();

}
