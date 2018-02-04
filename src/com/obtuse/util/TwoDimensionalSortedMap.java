/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.GowingPackable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;

/**
 Describe how a two dimensional sorted map behaves.
 */

@SuppressWarnings("unused")
public interface TwoDimensionalSortedMap<T1, T2, V> extends Iterable<V>, GowingPackable, Serializable {

    /**
     Put a value into the map.

     @param key1  the first key dimension's value.
     @param key2  the second key dimension's value.
     @param value the value.
     */

    V put( T1 key1, T2 key2, V value );

    /**
     Get a particular inner map.

     @param key1        the key for the desired inner map.
     @param forceCreate true if the specified inner map should be created if it does not already exist.
     @return the requested inner map or null if it does not exist and <tt>forceCreate</tt> is false.
     Note that if a non-null map is returned then it is the actual inner map used by this instance for the specified key.
     Consequently, changes to the returned map change this instance.
     */

    @Nullable
    SortedMap<T2, V> getInnerMap( T1 key1, boolean forceCreate );

    /**
     Get a particular inner map which is guaranteed to not be {@code null}.
     <p/>This method is exactly equivalent to
     <blockquote>
     <pre>{@link #getInnerMap}( key1, true )</pre>
     </blockquote>

     @param key1 the key for the desired inner map.
     @return the requested inner map (created and inserted into this instance if it does not already exist).
     Note that the returned inner map is the actual inner map used by this instance for the specified key.
     Consequently, changes to the returned map change this instance.
     */

    @NotNull
    SortedMap<T2, V> getNotNullInnerMap( final T1 key1 );

    /**
     Determine if this map is readonly.
     @return {@code true} if it is readonly; {@code false} if it modifiable.
     */

    boolean isReadonly();

    /**
     Empty the map.
     */

    void clear();

    /**
     Get a particular value.

     @param key1 the first key dimension's value.
     @param key2 the second key dimension's value.
     @return the requested value (will be null if this combination of key values leads to a null value or does not
     correspond to a value in the tree).
     */


    @Nullable
    V get( T1 key1, T2 key2 );

    /**
     Remove a particular inner map.
     <p/>Warning: Care is advised when using them method as any previously obtained references to the to-be-deleted inner
     map will
     become stale in the sense that they will now reference an inner map which is no longer associated with this tree.

     @param key the key for the to-be-removed inner map.
     @return the just deleted inner map (will be null if the key does not have an inner map currently associated with it).
     */

    @Nullable
    SortedMap<T2, V> removeInnerMap( T1 key );

    /**
     Remove a particular value.

     @param key1 the first key dimension's value.
     @param key2 the second key dimension's value.
     @return the requested value (will be null if this combination of key values leads to a null value or does not
     correspond to a value in the tree).
     */

    @Nullable
    V remove( T1 key1, T2 key2 );

    /**
     Get the outer keys (also known as the first dimension's keys).

     @return the first dimension's keys.
     Note that this is the actual set of keys used by this instance.
     Consequently, changes to the returned set of keys change this instance.
     */

    @NotNull
    Set<T1> outerKeys();

    /**
     Get all of the inner maps.

     @return all of the inner maps.
     Note that this is the actual set of inner maps used by this instance.
     Consequently, changes to the returned maps changes this instance.
     */

    @NotNull
    Collection<SortedMap<T2, V>> innerMaps();

    /**
     Determine if a value has ever been associated with the specified keys.
     <p>Note that the return value of this method generally becomes meaningless if {@link #removeInnerMap(Object)} has ever been invoked.</p>
     @param key1 the first key.
     @param key2 the second key.
     @return generally meaningless if {@link #removeInnerMap(Object)} has ever been invoked;
     otherwise, {@code true} if {@link #removeInnerMap(Object)} has never been invoked and a (possibly {@code null})
     value has ever been associated with the specified keys; otherwise, {@code false}.
     */

    boolean containsKeys( T1 key1, T2 key2 );

    /**
     Get an iterator that iterates across all the values in the tree.
     Values are returned sorted by the T1 key and then by the T2 key and finally by the T3 key.

     @return an iterator that iterates across all the values in the tree.
     */

    @NotNull
    Iterator<V> iterator();

    /**
     If the specified keys are not already associated with a non-null value, associate the specified keys with the specified {@code newValue}.
     <p>See {@link java.util.Map#putIfAbsent(Object, Object)} for more information
     (obviously, {@link java.util.Map#putIfAbsent(Object, Object)} deals with only one key and this method
     deals with two keys but this and the {@link java.util.Map#putIfAbsent(Object, Object)} methods accomplish conceptually the same thing).</p>
     @param key1 the first key (where {@code T1} is the first 'type' parameter to this class).
     @param key2 the second key (where {@code T2} is the second 'type' parameter to this class).
     @param newValue the new value to be associated with the keys if they are not already associated with a non-null value.
     @return the value associated with the two keys prior to this method 'doing its thing'.
     If the keys were already associated with a non-null value then this return value will be that non-null value.
     Otherwise, this return value will be {@code null}.
     */

    default V putIfAbsent( final T1 key1, final T2 key2, final V newValue ) {

        V previousValue = get( key1, key2 );
        if ( previousValue == null ) {
            previousValue = put( key1, key2, newValue );
        }

        return previousValue;

    }

    /**
     If the specified keys are not already associated with a non-null value, invoke the specified mapping function and
     associate its return value with the specified keys.
     <p>See {@link java.util.Map#computeIfAbsent(Object, java.util.function.Function)} for more information
     (obviously, {@link java.util.Map#computeIfAbsent(Object, java.util.function.Function)} deals with only one key and this method
     deals with two keys but the two accomplish essentially the same thing).</p>
     @param key1 the first key (where {@code T1} is the first 'type' parameter to this class).
     @param key2 the second key (where {@code T2} is the second 'type' parameter to this class).
     @param mappingFunction the mapping function.
     @return the non-null value associated with the two keys (either the non-null value that was 'there' before this
     method was called or the non-null value that invoking the specified mapping function yielded and is now 'there').
     */

    default V computeIfAbsent(
            final T1 key1,
            final T2 key2,
            final BiFunction<? super T1, ? super T2, ? extends V> mappingFunction
    ) {

        Objects.requireNonNull( mappingFunction );
        V existingValue;

        if ( ( existingValue = get( key1, key2 ) ) == null ) {

            V newValue;
            if ( ( newValue = mappingFunction.apply( key1, key2 ) ) != null ) {

                put( key1, key2, newValue );

                return newValue;

            }

        }

        return existingValue;

    }

    int size();

    boolean isEmpty();

}
