/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingEntityFactory;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.p2a.GowingUnPackedEntityGroup;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.GowingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * Describe how a three dimensional sorted map behaves.
 */

@SuppressWarnings("unused")
public interface ThreeDimensionalSortedMap<T1,T2,T3,V> extends Iterable<V>, Serializable, GowingPackable {

    /**
     * Put a value into the map.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @param value the value.
     */

    V put( @NotNull T1 key1, @NotNull T2 key2, @NotNull T3 key3, @Nullable V value );

    /**
     * Get a particular inner map.
     * @param key1 the key for the desired inner map.
     * @param forceCreate true if the specified inner map should be created if it does not already exist.
     * @return the requested inner map or null if it does not exist and <tt>forceCreate</tt> is false.
     * Note that if a non-null map is returned then it is the actual inner map used by this instance for the specified key.
     * Consequently, changes to the returned map change this instance.
     */

    @Nullable
    TwoDimensionalSortedMap<T2,T3,V> getInnerMap( @NotNull T1 key1, boolean forceCreate );

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
    TwoDimensionalSortedMap<T2,T3,V> getNotNullInnerMap( @NotNull T1 key1 );

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
     * Remove a particular inner map.
     * <p/>Warning: Care is advised when using them method as any previously obtained references to the to-be-deleted inner map will
     * become stale in the sense that they will now reference an inner map which is no longer associated with this tree.
     * @param key the key for the to-be-removed inner map.
     * @return the just deleted inner map (will be null if the key does not have an inner map currently associated with it).
     */

    @Nullable
    TwoDimensionalSortedMap<T2,T3,V> removeInnerMap( @NotNull T1 key );

    /**
     * Get a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    @Nullable
    V get( @NotNull T1 key1, @NotNull T2 key2, @NotNull T3 key3 );

    /**
     * Remove a particular value.
     * @param key1 the first key dimension's value.
     * @param key2 the second key dimension's value.
     * @param key3 the third key dimension's value.
     * @return the requested value (will be null if this combination of key values leads to a null value or does not correspond to a value in the tree).
     */

    @Nullable
    V remove( @NotNull T1 key1, @NotNull T2 key2, @NotNull T3 key3 );

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
    Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps();

    /**
     Determine if a value has ever been associated with the specified keys.
     <p>Note that the return value of this method generally becomes meaningless if {@link #removeInnerMap(Object)}
     has ever been invoked on this instance or if {@link TwoDimensionalSortedMap#removeInnerMap(Object)} has ever
     been invoked on the return value of {@link #getInnerMap(Object, boolean)} or {@link #getNotNullInnerMap(Object)}.</p>
     @param key1 the first key.
     @param key2 the second key.
     @param key3 the third key.
     @return generally meaningless if {@link #removeInnerMap(Object)}
     has ever been invoked on this instance or if {@link TwoDimensionalSortedMap#removeInnerMap(Object)} has ever
     been invoked on the return value of {@link #getInnerMap(Object, boolean)} or {@link #getNotNullInnerMap(Object)};
     otherwise, {@code true} if a (possibly {@code null})
     value has ever been associated with the specified keys; otherwise, {@code false}.
     */

    boolean containsKeys( T1 key1, T2 key2, T3 key3 );

    /**
     * Get an iterator that iterates across all the values in the tree.
     * Values are returned sorted by the T1 key and then by the T2 key and finally by the T3 key.
     * @return an iterator that iterates across all the values in the tree.
     */

    @NotNull
    Iterator<V> iterator();

    /**
     If the specified keys are not already associated with a non-null value, associate the specified keys with the specified {@code newValue}.
     <p>See {@link java.util.Map#putIfAbsent(Object, Object)} for more information
     (obviously, {@link java.util.Map#putIfAbsent(Object, Object)} deals with only one key and this method
     deals with three keys but this and the {@link java.util.Map#putIfAbsent(Object, Object)} methods accomplish conceptually the same thing).</p>
     @param key1 the first key (where {@code T1} is the first 'type' parameter to this class).
     @param key2 the second key (where {@code T2} is the second 'type' parameter to this class).
     @param newValue the new value to be associated with the keys if they are not already associated with a non-null value.
     @return the value associated with the three keys prior to this method 'doing its thing'.
     If the keys were already associated with a non-null value then this return value will be that non-null value.
     Otherwise, this return value will be {@code null}.
     */

    default V putIfAbsent( final T1 key1, final T2 key2, final T3 key3, final V newValue ) {

        V previousValue = get( key1, key2, key3 );
        if ( previousValue == null ) {
            previousValue = put( key1, key2, key3, newValue );
        }

        return previousValue;

    }

    /**
     If the specified keys are not already associated with a non-null value, invoke the specified mapping function and
     associate its return value with the specified keys.
     <p>See {@link java.util.Map#computeIfAbsent(Object, Function)} for more information
     (obviously, {@link java.util.Map#computeIfAbsent(Object, Function)} deals with only one key and this method
     deals with three keys but the two accomplish essentially the same thing).</p>
     @param key1 the first key (where {@code T1} is the first 'type' parameter to this class).
     @param key2 the second key (where {@code T2} is the second 'type' parameter to this class).
     @param key3 the third key (where {@code T3} is the third 'type' parameter to this class).
     @param mappingFunction the mapping function.
     @return the non-null value associated with the three keys (either the non-null value that was 'there' before this
     method was called or the non-null value that invoking the specified mapping function yielded and is now 'there').
     */

    default V computeIfAbsent(
            final T1 key1,
            final T2 key2,
            final T3 key3,
            final ObtuseFunction3<? super T1, ? super T2, ? super T3, ? extends V> mappingFunction
    ) {

        Objects.requireNonNull( mappingFunction );
        V v;
        if ( ( v = get( key1, key2, key3 ) ) == null ) {
            V newValue;
            if ( ( newValue = mappingFunction.apply( key1, key2, key3 ) ) != null ) {
                put( key1, key2, key3, newValue );
                return newValue;
            }
        }

        return v;
    }

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

    static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "ObtuseUtil", "testing", null );

        ThreeDimensionalSortedMap<Integer,String,Boolean,String> originalMap = new ThreeDimensionalTreeMap<>();
        originalMap.put( 1, "hello", true, "1-hello-t" );
        originalMap.put( 2, "hello", false, "2-hello-f" );
        originalMap.put( 3, "hello", true, "3-hello-t" );
        originalMap.put( 1, "world", false, "1-world-f" );
        originalMap.put( 2, "world", false, "2-world-f" );

        displayMap( "originalMap", originalMap );

        EntityName en = new EntityName( "eName" );
        File testFile = new File( "2dsortedMap-test.packed" );
        ObtuseUtil.packQuietly( en, originalMap, testFile, false );
        try ( Measure ignored = new Measure( "ThreeDimensionalSortedMap unpack main" ) ) {

            GowingUnPackedEntityGroup unpackedEntities = ObtuseUtil.unpack(
                    testFile,
                    new GowingEntityFactory[0]
            );

            GowingUtil.logUnpackResults( "3D map", unpackedEntities );

            if ( unpackedEntities.getNamedClasses()
                                 .containsKey( en ) ) {

                @NotNull List<GowingPackable> interestingStuff =
                        unpackedEntities.getNamedClasses()
                                        .getValues( en );

                if ( interestingStuff.size() == 1 ) {

                    GowingPackable first = interestingStuff.get( 0 );
                    if ( first instanceof ThreeDimensionalSortedMap ) {

                        @SuppressWarnings("unchecked")
                        ThreeDimensionalTreeMap<Integer, String, Boolean, String> recoveredMap =
                                ( (ThreeDimensionalTreeMap<Integer, String, Boolean, String>)interestingStuff.get( 0 ) );

                        displayMap( "recoveredMap", recoveredMap );

                    } else {

                        throw new HowDidWeGetHereError(
                                "LancotMediaLibraryRoot.readImportBundle:  " +
                                "read yielded a " +
                                first.getClass()
                                     .getCanonicalName() +
                                " when we expected a " +
                                ThreeDimensionalSortedMap.class.getCanonicalName()
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

//        } catch ( IOException e ) {
//
//            Logger.logErr( "java.io.IOException caught", e );

        } catch ( GowingUnpackingException e ) {

            Logger.logErr( "com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException caught", e );

        }

    }

    static void displayMap(
            final String title,
            final ThreeDimensionalSortedMap<Integer, String, Boolean, String> map
    ) {

        Logger.logMsg( title );
        for ( int ix : map.outerKeys() ) {

            TwoDimensionalSortedMap<String, Boolean, String> innerMap = map.getNotNullInnerMap( ix );

            for ( String sx : innerMap.outerKeys() ) {

                @NotNull SortedMap<Boolean, String> twoDmap = innerMap.getNotNullInnerMap( sx );
                for ( Boolean bx : twoDmap.keySet() ) {
                    Logger.logMsg( "    map(" + ix + "," + sx + "," + bx + ") = " + map.get( ix, sx, bx ) );

                }

            }

        }

    }

}
