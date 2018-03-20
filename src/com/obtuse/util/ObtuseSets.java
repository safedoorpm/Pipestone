/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import java.util.*;

/**
 Some useful set operations.
 */

public class ObtuseSets {

    /**
     Return a newly created {@link TreeSet} which is the intersection of a {@code SortedSet} of things and a {@link Collection} of things.
     @param set the {@link SortedSet}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code SortedSet} and in the {@code Collection} of things.
     @return the intersection of a {@code SortedSet} of things and a {@code Collection} of things.
     */

    public static <T extends Comparable> TreeSet<T> intersection(
            final SortedSet<T> set,
            final Collection<T> collection
    ) {

        TreeSet<T> rval = new TreeSet<>( set );
        rval.retainAll( collection );

        return rval;

    }

    /**
     Return a new {@link Set} which is the intersection of a {@code Set} of things and a {@link Collection} of things.
     @param set the {@link Set}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code Set} and in the {@code Collection} of things.
     @return the intersection of a {@code Set} of things and a {@code Collection} of things.
     */

    public static <T> HashSet<T> intersection(
            final Set<T> set,
            final Collection<T> collection
    ) {

        HashSet<T> rval = new HashSet<>( set );
        rval.retainAll( collection );

        return rval;

    }

    /**
     Return a newly created {@link TreeSet} which is the union of a {@code SortedSet} of things and a {@link Collection} of things.
     @param set the {@link SortedSet}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code SortedSet} and in the {@code Collection} of things.
     @return the union of a {@code SortedSet} of things and a {@code Collection} of things.
     */

    public static <T extends Comparable> TreeSet<T> union(
            final SortedSet<T> set,
            final Collection<T> collection
    ) {

        TreeSet<T> rval = new TreeSet<>( set );
        rval.addAll( collection );

        return rval;

    }

    /**
     Return a new {@link Set} which is the union of a {@code Set} of things and a {@link Collection} of things.
     @param set the {@link Set}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code Set} and in the {@code Collection} of things.
     @return the union of a {@code Set} of things and a {@code Collection} of things.
     */

    public static <T> HashSet<T> union(
            final Set<T> set,
            final Collection<T> collection
    ) {

        HashSet<T> rval = new HashSet<>( set );
        rval.addAll( collection );

        return rval;

    }

    /**
     Return a newly created {@link TreeSet} which is the set difference of a {@code SortedSet} of things and a {@link Collection} of things.
     <p>The set difference of set S and set T is the set consisting of those elements in S that are not in T.</p>
     @param set the {@link SortedSet}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code SortedSet} and in the {@code Collection} of things.
     @return the set difference of a {@code SortedSet} of things and a {@code Collection} of things.
     */

    public static <T extends Comparable> TreeSet<T> setDifference(
            final SortedSet<T> set,
            final Collection<T> collection
    ) {

        TreeSet<T> rval = new TreeSet<>( set );
        rval.removeAll( collection );

        return rval;

    }

    /**
     Return a new {@link Set} which is the set difference of a {@code Set} of things and a {@link Collection} of things.
     <p>The set difference of set S and set T, often expressed as S - T, is the set consisting of those elements in S that are not in T.</p>
     @param set the {@link Set}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code Set} and in the {@code Collection} of things.
     @return the set difference of a {@code Set} of things and a {@code Collection} of things.
     */

    public static <T> HashSet<T> setDifference(
            final Set<T> set,
            final Collection<T> collection
    ) {

        HashSet<T> rval = new HashSet<>( set );
        rval.removeAll( collection );

        return rval;

    }

    /**
     Return a newly created {@link TreeSet} which is the symmetric set difference of a {@code SortedSet} of things and a {@link Collection} of things.
     <p>The symmetric set difference of set S and set T is the set consisting of the union of S - T and T - S.</p>
     @param set the {@link SortedSet}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code SortedSet} and in the {@code Collection} of things.
     @return the set symmetric difference of a {@code SortedSet} of things and a {@code Collection} of things.
     */

    public static <T extends Comparable> TreeSet<T> symmetricSetDifference(
            final SortedSet<T> set,
            final Collection<T> collection
    ) {

        SortedSet<T> setS = set;
        TreeSet<T> setT = new TreeSet<>( collection );

        TreeSet<T> rval = union( setDifference( setS, setT ), setDifference( setT, setS ) );

        return rval;

    }

    /**
     Return a new {@link Set} which is the set difference of a {@code Set} of things and a {@link Collection} of things.
     <p>The symmetric set difference of set S and set T is the set consisting of the union of S - T and T - S.</p>
     @param set the {@link Set}{@code <T>} of things.
     @param collection the {@code Collection} of things.
     @param <T> the type of things in the {@code Set} and in the {@code Collection} of things.
     @return the symmetric set difference of a {@code Set} of things and a {@code Collection} of things.
     */

    public static <T> HashSet<T> symmetricSetDifference(
            final Set<T> set,
            final Collection<T> collection
    ) {

        Set<T> setS = set;
        HashSet<T> setT = new HashSet<>( collection );

        HashSet<T> rval = union( setDifference( setS, setT ), setDifference( setT, setS ) );

        return rval;

    }

}
