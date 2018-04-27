/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Count occurrences of things using a sorted mapping.
 * <p/>
 * Instances of this class are serializable if the key objects used to create the instance are serializable.
 */

public class TreeCounter<K extends Comparable<K>> implements Counter<K>, Serializable {

    private final SortedMap<K,Integer> _counts;

    private int _grandTotal = 0;

    public TreeCounter() {
        super();

        _counts = new TreeMap<>();

    }

    @SuppressWarnings("UnusedDeclaration")
    public TreeCounter( final Counter<K> counter ) {
        super();

        _counts = new TreeMap<>();
        for ( K key : counter.keySet() ) {

            _counts.put( key, counter.getCount( key ) );

        }

        _grandTotal = counter.getGrandTotal();

    }

    public void count( final K thing ) {

        _grandTotal += 1;

        if ( _counts.containsKey( thing ) ) {

            _counts.put( thing, _counts.get( thing ).intValue() + 1 );

        } else {

            _counts.put( thing, 1 );

        }

    }

    public int size() {

        return _counts.size();

    }

    public boolean isEmpty() {

        return _counts.isEmpty();

    }

    public Set<K> keySet() {

        return _counts.keySet();

    }

    public K firstKey() {

        return _counts.firstKey();

    }

    public K lastKey() {

        return _counts.lastKey();

    }

    public boolean containsKey( final K thing ) {

        return _counts.containsKey( thing );

    }

    public int getCount( final K thing ) {

        Integer count = _counts.get( thing );

        return count == null ? 0 : count.intValue();

    }

    public String toString() {

        StringBuilder counts = new StringBuilder( "TreeCounter( " );
        String comma = "";

        for ( K key : _counts.keySet() ) {

            counts.append( comma ).append( key ).append( '=' ).append( getCount( key ) );
            comma = ", ";

        }

        return counts.append( " )" ).toString();

    }

    public int getGrandTotal() {

        return _grandTotal;

    }

}
