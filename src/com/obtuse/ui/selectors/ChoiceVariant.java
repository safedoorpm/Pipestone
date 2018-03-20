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

public class ChoiceVariant implements Comparable<ChoiceVariant> {

    private static final SortedMap<String, ChoiceVariant> s_allKnownVariants = new TreeMap<>();

    public final String variantName;
    public final String variantTag;

    public ChoiceVariant( final @NotNull String variantName, final @NotNull String variantTag ) {

        super();

        if ( s_allKnownVariants.containsKey( variantTag ) ) {

            throw new IllegalArgumentException( "ChoiceVariant:  duplicate variant tag " + ObtuseUtil.enquoteToJavaString( variantTag ) );

        }

        this.variantName = variantName;
        this.variantTag = variantTag;

        s_allKnownVariants.put( variantTag, this );

    }

    public static Optional<ChoiceVariant> findVariant( final @NotNull String variantTag ) {

        ChoiceVariant variant = s_allKnownVariants.get( variantTag );

        return Optional.ofNullable( variant );

    }

    public static ChoiceVariant maybeCreateVariant( final @NotNull String variantName, final @NotNull String variantTag ) {

        Optional<ChoiceVariant> optVariant = findVariant( variantTag );

        return optVariant.orElseGet( () -> new ChoiceVariant( variantName, variantTag ) );

    }

    public String toString() {

        return this.variantName;

    }

    public int compareTo( final @NotNull ChoiceVariant rhs ) {

        return this.variantTag.compareTo( rhs.variantTag );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof ChoiceVariant && compareTo( (ChoiceVariant)rhs ) == 0;

    }

    public int hashCode() {

        return this.variantTag.hashCode();

    }

}
