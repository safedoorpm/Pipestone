/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Created by danny on 2018/03/15.
 */

public class Selector implements Comparable<Selector> {

    private static final SortedMap<String, Selector> s_allKnownSelectors = new TreeMap<>();

    public final String selectorName;
    public final String selectorTag;

    public Selector( final @NotNull String selectorName, final @NotNull String selectorTag ) {

        super();

        if ( s_allKnownSelectors.containsKey( selectorTag ) ) {

            throw new IllegalArgumentException( "Selector:  duplicate selector tag " + ObtuseUtil.enquoteToJavaString( selectorTag ) );

        }

        this.selectorName = selectorName;
        this.selectorTag = selectorTag;

        s_allKnownSelectors.put( selectorTag, this );

    }

    public static Optional<Selector> findSelector( final @NotNull String selectorTag ) {

        Selector selector = s_allKnownSelectors.get( selectorTag );

        return Optional.ofNullable( selector );

    }

    public static Selector maybeCreateSelector( final @NotNull String selectorName, final @NotNull String selectorTag ) {

        Optional<Selector> optSelector = findSelector( selectorTag );

        return optSelector.orElseGet( () -> new Selector( selectorName, selectorTag ) );

    }

    public String toString() {

        return this.selectorName;

    }

    public int compareTo( final @NotNull Selector rhs ) {

        return this.selectorTag.compareTo( rhs.selectorTag );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof Selector && compareTo( (Selector)rhs ) == 0;

    }

    public int hashCode() {

        return this.selectorTag.hashCode();

    }

}
