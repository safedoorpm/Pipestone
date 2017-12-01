package com.obtuse.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An enhanced version of the {@link TreeCounter} class that uses long values to accumulate possibly quite large values.
 * <p/>
 * Instances of this class are serializable if the key objects used to create the instance are serializable.
 * <p/>
 * Copyright © 2014 Obtuse Systems Corporation
 */

public class TreeAccumulator<K extends Comparable<K>> implements Accumulator<K>, Serializable {

    private final SortedMap<K,Long> _accumulator;

    private long _grandTotal = 0;

    public TreeAccumulator() {
        super();

        _accumulator = new TreeMap<>();

    }

    @SuppressWarnings("UnusedDeclaration")
    public TreeAccumulator( final Accumulator<K> accumulator ) {
        this();

        for ( K key : accumulator.keySet() ) {

            accumulate( key, accumulator.getCount( key ) );

        }

    }

    public TreeAccumulator( final Counter<K> counter ) {
        this();

        for ( K key : counter.keySet() ) {

            accumulate( key, counter.getCount( key ) );

        }

    }

    /**
     Force a thing's count to have a certain value.
     <p/>This method properly adjusts the grand total for this accumulator to ensure that it still reflects the sum of all the counts.
     This method also deletes the entry for the specified thing if it exists and the new count is 0.
     @param thing which thing's count is to be forced.
     @param newCount the count that the specified thing is to now have.
     @return the thing's old count (zero if the thing was previously unknown).
     @throws IllegalArgumentException if the specified <code>newCount</code> is negative (the state of this instance does not
     change if this exception gets thrown).
     */

    @Override
    public long forceCount( final K thing, final long newCount ) throws IllegalArgumentException {

	if ( newCount < 0 ) {

	    throw new IllegalArgumentException( "specified new count for thing \"" + thing + "\" is negative (" + newCount + ")" );

	}

	long oldCount = 0;
	if ( _accumulator.containsKey( thing ) ) {

	    oldCount = _accumulator.get( thing ).longValue();
	    _grandTotal += newCount - oldCount;

	    if ( newCount == 0 ) {

		_accumulator.remove( thing );

	    } else {

		_accumulator.put( thing, newCount );

	    }

	} else if ( newCount != 0 ) {

	    _grandTotal += newCount;
	    _accumulator.put( thing, newCount );

	}

	return oldCount;

    }

    /**
     Adjust a thing's count by the specified delta.
     The delta can be any value which does not cause the thing's count to drop below zero.
     If a call to this method causes an overflow of a thing's count then the behaviour of this method is explicitly undefined
     (things are likely to get ugly rather quickly).
     @param thing the thing whose count is to be adjusted.
     @param delta the amount by which the specified thing's count is to be adjusted.
     @return the new count for the specified thing.
     @throws IllegalArgumentException if the request would result in a negative count for the thing (the state of this instance does not
     change if this exception gets thrown).
     */

    @Override
    public long accumulate( final K thing, final long delta ) throws IllegalArgumentException {

	long newCount;
        if ( _accumulator.containsKey( thing ) ) {

	    newCount = _accumulator.get( thing ).longValue() + delta;
	    if ( newCount < 0 ) {

		throw new IllegalArgumentException( "adjusted count for thing \"" + thing + "\" is negative (" + newCount + ")" );

	    }

	    _grandTotal += delta;

	    if ( newCount == 0 ) {

		_accumulator.remove( thing );

	    } else {

		_accumulator.put( thing, newCount );

	    }

	    return newCount;

        } else {

	    newCount = delta;
	    if ( newCount > 0 ) {

		_grandTotal += newCount;
		_accumulator.put( thing, newCount );

		return newCount;

	    } else if ( newCount < 0 ) {

		throw new IllegalArgumentException( "adjusted count for first thing \"" + thing + "\" would be negative (" + delta + ")" );

	    } else {

		newCount = 0;

	    }

	}

	return newCount;

    }

    public long increment( final K thing ) {

        return accumulate( thing, 1 );

    }

//    public void countBunch( K thing, int bunchSize ) {
//
//        for ( int i = 0; i < bunchSize; i += 1 ) {
//
//            count( thing );
//
//        }
//
//    }

    public void clear() {

        _accumulator.clear();
        _grandTotal = 0;

    }

    public int size() {

        return _accumulator.size();

    }

    public boolean isEmpty() {

        return _accumulator.isEmpty();

    }

    public Set<K> keySet() {

        return _accumulator.keySet();

    }

    public K firstKey() {

        return _accumulator.firstKey();

    }

    public K lastKey() {

        return _accumulator.lastKey();

    }

    public boolean containsKey( final K thing ) {

        return _accumulator.containsKey( thing );

    }

    public long getCount( final K thing ) {

        Long count = _accumulator.get( thing );

        return count == null ? 0 : count.longValue();

    }

    public String toString() {

        String rval = _accumulator
                .keySet()
                .stream()
                .map( String::valueOf )
                .collect( Collectors.joining( "TreeCounter( ", ", ", " )" ) );
        StringBuilder counts = new StringBuilder( "TreeCounter( " );
        String comma = "";

        for ( K key : _accumulator.keySet() ) {

            counts.append( comma ).append( key ).append( '=' ).append( getCount( key ) );
            comma = ", ";

        }

        return counts.append( " )" ).toString();

    }

    public long getGrandTotal() {

        return _grandTotal;

    }

    public TreeSorter<Long,K> getSortedSums() {

        return getSortedCounts( null );

    }

    public TreeSorter<Long,K> getSortedCounts( final Comparator<Long> comparator ) {

        TreeSorter<Long,K> sortedCounts = comparator == null ? new TreeSorter<>() : new TreeSorter<>( comparator );
        for ( K key : _accumulator.keySet() ) {

            sortedCounts.add( _accumulator.get( key ), key );

        }

        return sortedCounts;

    }

}
