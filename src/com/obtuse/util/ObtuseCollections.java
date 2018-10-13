/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 Some useful {@link Collection} manipulation methods.
 */

@SuppressWarnings("unused")
public class ObtuseCollections {

    /**
     Add things to a sorted set and return the set.
     @param things the existing {@link SortedSet}{@code <T>} of things.
     @param additionalThings the array of things to be added to the set.
     @param <T> the type of things in the existing set and in the array of things to be added to the set.
     @return the sorted set passed in as {@code things}.
     This allows calls to this method to be chained which, to be blunt,
     is the only real advantage of this method over {@link java.util.Collections#addAll}.
     */

    public static <T extends Comparable,E extends SortedSet<T>> E addThings(
            final E things,
            final @NotNull T@NotNull[] additionalThings
    ) {

        Collections.addAll( things, additionalThings );

        return things;

    }

    /**
     Add things to a set and return the set.
     @param things the existing {@link Set}{@code <T>} of things.
     @param additionalThings the array of things to be added to the set.
     @param <T> the type of things in the existing set and in the array of things to be added to the set.
     @return the  set passed in as {@code things}.
     This allows calls to this method to be chained which, to be blunt,
     is the only real advantage of this method over {@link java.util.Collections#addAll}.
     */

    public static <T,E extends Set<T>> E addThings(
            final E things,
            final @NotNull T@NotNull[] additionalThings
    ) {

        Collections.addAll( things, additionalThings );

        return things;

    }

    /**
     Add new things to a list and return the list.
     @param things the existing {@link List}{@code <T>} of things.
     @param additionalThings the array of things to be added to the list.
     Things in this array which are already in the list <b><u>are not added again</u></b> (use {@link #addThings(Collection, T[])}
     if you want things which are already in the list to be added again).
     <p>This method first constructs a {@link Set}{@code <T>} containing all the things in the list.
     It then uses that set to avoid adding duplicate additional things to the list.
     This is almost always faster if not much faster than having to check for duplicates by scanning through the provided list
     (it might not be faster if the provided list starts out really really short and if the provided array also starts out quite short
     but that case is almost certainly inherently quite fast simply because there isn't much work required to get the job done regardless
     of whether or not a tracking set is used).</p>
     @param <T> the type of things in the existing list and in the array of things to be added to the set.
     @return the list passed in as {@code things}.
     This allows calls to this method to be chained which, to be blunt,
     is the only real advantage of this method over {@link java.util.Collections#addAll}.
     */

    @SafeVarargs
    public static <T,E extends Collection<T>> E addNewThings(
            final E things,
            final @NotNull T... additionalThings
    ) {

        Set<T> tracker = new HashSet<>( things );
        for ( T thing : additionalThings ) {

            if ( !tracker.contains( thing ) ) {

                things.add( thing );
                tracker.add( thing );

            }

        }

        return things;

    }

    /**
     Add things to a {@link Collection} and return the {@code Collection}.
     <p>The 'magic' of Java generics allows this class to be used in any context where the developer wants to add things
     to a {@code Collection} and get the collection as the correctly typed return value.
     For example, both of these add values to different types of {@code Collection} without needing to cast the return value
     of this method:
     <blockquote><code>
     ArrayList&lt;String> aList = ObtuseCollections.addThings( new ArrayList<>(), "Hello", "There", "World" );
     ArrayList&lt;String> aList = ObtuseCollections.addThings( new ArrayList<>(), "Hello", "There", "World" );
     </code>
     </blockquote>
     </p>
     @param things the existing {@code Collection <T>} of things.
     @param additionalThings the array of things to be added to the {@code Collection}.
     Things in this array which are already in the {@code Collection} <b><u>are added again</u></b> (use {@link #addNewThings(Collection, T[])}
     if you want things which are already in the list to not be added again).
     @param <T> the type of things in the existing list and in the array of things to be added to the set.
     @return the list passed in as {@code things}.
     This allows calls to this method to be chained which, to be blunt,
     is the only real advantage of this method over {@link java.util.Collections#addAll}.
     */

    @SafeVarargs
    public static <T,E extends Collection<T>> E addThings(
            final E things,
            final @NotNull T... additionalThings
    ) {

        Collections.addAll( things, additionalThings );

        return things;

    }

    public static void xxx() {

        ArrayList<String> aList = ObtuseCollections.addThings( new ArrayList<>(), "Hello", "There", "World" );
        SortedSet<String> sSet = ObtuseCollections.addThings( new TreeSet<>(), "Hello", "There", "World" );

    }

    @SafeVarargs
    public static <T extends Comparable> TreeSet<T> sortedSet( final @NotNull T... array ) {

        TreeSet<T> rval = new TreeSet<>();
        Collections.addAll( rval, array );

        return rval;

    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayList( final @NotNull T... things ) {

        ArrayList<T> rval = new ArrayList<>();
        Collections.addAll( rval, things );

        return rval;

    }

    @SafeVarargs
    public static <T> LinkedList<T> linkedList( final @NotNull T... things ) {

        LinkedList<T> rval = new LinkedList<>();
        Collections.addAll( rval, things );

        return rval;

    }

    @SafeVarargs
    public static <T> Vector<T> vector( final @NotNull T... things ) {

        Vector<T> rval = new Vector<>();
        Collections.addAll( rval, things );

        return rval;

    }

    @SafeVarargs
    public static <T extends Comparable> TreeSet<T> treeSet( final @NotNull T... things ) {

        TreeSet<T> rval = new TreeSet<>();
        Collections.addAll( rval, things );

        return rval;

    }

    @SafeVarargs
    public static <T> HashSet<T> hashSet( final @NotNull T... things ) {

        HashSet<T> rval = new HashSet<>();
        Collections.addAll( rval, things );

        return rval;

    }

    public static @NotNull byte[] copyOf( @NotNull byte[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull short[] copyOf( @NotNull short[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull int[] copyOf( @NotNull int[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull long[] copyOf( @NotNull long[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull float[] copyOf( @NotNull float[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull double[] copyOf( @NotNull double[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static @NotNull boolean[] copyOf( @NotNull boolean[] array ) {

        return Arrays.copyOf( array, array.length );

    }

    public static <T> T[] copyOf( T[] array ) {

        return Arrays.copyOf( array, array.length );

    }

}
