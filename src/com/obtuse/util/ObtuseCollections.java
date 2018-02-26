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

    public static <T extends Comparable> SortedSet<T> addThings(
            final SortedSet<T> things,
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

    public static <T> Set<T> addThings(
            final Set<T> things,
            final @NotNull T@NotNull[] additionalThings
    ) {

        Collections.addAll( things, additionalThings );

        return things;

    }

    /**
     Add new things to a list and return the list.
     @param things the existing {@link List}{@code <T>} of things.
     @param additionalThings the array of things to be added to the list.
     Things in this array which are already in the list <b><u>are not added again</u></b> (use {@link #addThings(List, T[])}
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
    public static <T> List<T> addNewThings(
            final List<T> things,
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
     Add things to a list and return the list.
     @param things the existing {@link List}{@code <T>} of things.
     @param additionalThings the array of things to be added to the list.
     Things in this array which are already in the list <b><u>are added again</u></b> (use {@link #addNewThings(List, T[])}
     if you want things which are already in the list to not be added again).
     @param <T> the type of things in the existing list and in the array of things to be added to the set.
     @return the list passed in as {@code things}.
     This allows calls to this method to be chained which, to be blunt,
     is the only real advantage of this method over {@link java.util.Collections#addAll}.
     */

    @SafeVarargs
    public static <T> List<T> addThings(
            final List<T> things,
            final @NotNull T... additionalThings
    ) {

        Collections.addAll( things, additionalThings );

        return things;

    }

}
