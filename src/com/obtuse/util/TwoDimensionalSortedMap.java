/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

/**
 * Describe how a two dimensional sorted map behaves.
 */

public interface TwoDimensionalSortedMap<T1,T2,V> extends Iterable<V>, Serializable {

    /**
     * Put a value into the map.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param value the value.
     */

    void put( T1 key1, T2 key2, V value );

    /**
     * Get a particular inner map.
     * @param key1 the key for the desired inner map.
     * @param forceCreate true if the specified inner map should be created if it does not already exist.
     * @return the requested inner map or null if it does not exist and <tt>forceCreate</tt> is false.
     * Note that if a non-null map is returned then it is the actual inner map used by this instance for the specified key.
     * Consequently, changes to the returned map change this instance.
     */

    @Nullable
    SortedMap<T2,V> getInnerMap( T1 key1, boolean forceCreate );

    /**
     * Get a particular inner map which is guaranteed to not be {@code null}.
     * <p/>This method is exactly equivalent to
     * <blockquote>
     *     <pre>{@link #getInnerMap}( key1, true )</pre>
     </blockquote>
     * @param key1 the key for the desired inner map.
     * @return the requested inner map (created and inserted into this instance if it does not already exist).
     * Note that the returned inner map is the actual inner map used by this instance for the specified key.
     * Consequently, changes to the returned map change this instance.
     */

    @NotNull
    SortedMap<T2,V> getNotNullInnerMap( final T1 key1 );

    /**
     * Get a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    @Nullable
    V get( T1 key1, T2 key2 );

    /**
     * Remove a particular inner map.
     * <p/>Warning: Care is advised when using them method as any previously obtained references to the to-be-deleted inner map will
     * become stale in the sense that they will now reference an inner map which is no longer associated with this tree.
     * @param key the key for the to-be-removed inner map.
     * @return the just deleted inner map (will be null if the key does not have an inner map currently associated with it).
     */

    @Nullable
    SortedMap<T2,V> removeInnerMap( T1 key );

    /**
     * Remove a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    @Nullable
    V remove( T1 key1, T2 key2 );

    /**
     * Get the outer keys (also known as the first dimension's keys).
     * @return the first dimension's keys.
     * Note that this is the actual set of keys used by this instance.
     * Consequently, changes to the returned set of keys change this instance.
     */

    @NotNull
    Set<T1> outerKeys();

    /**
     * Get all of the inner maps.
     * @return all of the inner maps.
     * Note that this is the actual set of inner maps used by this instance.
     * Consequently, changes to the returned maps changes this instance.
     */

    @NotNull
    Collection<SortedMap<T2,V>> innerMaps();

    /**
     * Get an iterator that iterates across all the values in the tree.
     * Values are returned sorted by the T1 key and then by the T2 key and finally by the T3 key.
     * @return an iterator that iterates across all the values in the tree.
     */

    @NotNull
    Iterator<V> iterator();

    int size();

    boolean isEmpty();

}
