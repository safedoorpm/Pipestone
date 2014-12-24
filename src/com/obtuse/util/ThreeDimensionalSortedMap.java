/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Describe how a three dimensional sorted map behaves.
 */

public interface ThreeDimensionalSortedMap<T1,T2,T3,V> extends Serializable {

    /**
     * Put a value into the map.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @param value the value.
     */

    void put( T1 key1, T2 key2, T3 key3, V value );

    /**
     * Get a particular inner map.
     * @param key1 the key for the desired inner map.
     * @param forceCreate true if the specified inner map should be created if it does not already exist.
     * @return the requested inner map or null if it does not exist and <tt>forceCreate</tt> is false.
     * Note that if a non-null map is returned then it is the actual inner map used by this instance for the specified key.
     * Consequently, changes to the returned map change this instance.
     */

    TwoDimensionalSortedMap<T2,T3,V> getInnerMap( T1 key1, boolean forceCreate );

    /**
     * Remove a particular inner map.
     * <p/>Warning: Care is advised when using them method as any previously obtained references to the to-be-deleted inner map will
     * become stale in the sense that they will now reference an inner map which is no longer associated with this tree.
     * @param key the key for the to-be-removed inner map.
     * @return the just deleted inner map (will be null if the key does not have an inner map currently associated with it).
     */

    TwoDimensionalSortedMap<T2,T3,V> removeInnerMap( T1 key );

    /**
     * Get a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    V get( T1 key1, T2 key2, T3 key3 );

    /**
     * Remove a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    V remove( T1 key1, T2 key2, T3 key3 );

    /**
     * Get the outer keys (also known as the first dimension's keys).
     * @return the first dimension's keys.
     * Note that this is the actual set of keys used by this instance.
     * Consequently, changes to the returned set of keys change this instance.
     */

    Set<T1> outerKeys();

    /**
     * Get all of the inner maps.
     * @return all of the inner maps.
     * Note that this is the actual set of inner maps used by this instance.
     * Consequently, changes to the returned maps changes this instance.
     */

    Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps();

    /**
     * Get an iterator that iterates across all the values in the tree.
     * Values are returned sorted by the T1 key and then by the T2 key and finally by the T3 key.
     * @return an iterator that iterates across all the values in the tree.
     */

    Iterator<V> iterator();

    /**
     * Return the number of values in this map.
     * <p/>This method returns <tt>0</tt> if and only if {@link #isEmpty} returns false.
     * @return the number of values in this map (possibly but not necessarily including any values which have been explicitly set to null).
     */

    int size();

    /**
     * Determine if this map is null.
     * <p/>This method returns <tt>true</tt> if and only if {@link #size} returns 0.
     * <p/>This method is often faster than <tt>size() == 0</tt> if a call to <tt>size()</tt> would currently return a positive value.
     * @return true if this map has no values; false otherwise.
     * The presence of any null values may result in a return value of false.
     */

    boolean isEmpty();

}
