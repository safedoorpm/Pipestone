/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 A table of FlexiGrid constraints.
 */

public class FlexiGridConstraintsTable extends TreeMap<FlexiGridConstraintCategory, FlexiGridConstraint> {

    public FlexiGridConstraintsTable() {

        super();

    }

    public FlexiGridConstraintsTable( final @NotNull FlexiGridConstraint... constraints ) {

        super();

        setConstraints( constraints );

    }

    @Nullable
    public FlexiGridConstraint setConstraint( final @NotNull FlexiGridConstraint constraint ) {

        FlexiGridConstraint oldValue = get( constraint.getConstraintCategory() );
        put( constraint.getConstraintCategory(), constraint );

        return oldValue;

    }

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public SortedMap<FlexiGridConstraintCategory,FlexiGridConstraint> setConstraints( final @NotNull FlexiGridConstraint... constraints ) {

        SortedMap<FlexiGridConstraintCategory,FlexiGridConstraint> rval = new TreeMap<>();
        for ( FlexiGridConstraint constraint : constraints ) {

            FlexiGridConstraint oldValue = setConstraint( constraint );
            if ( oldValue != null && !rval.containsKey( constraint.getConstraintCategory() ) ) {

                rval.put( constraint.getConstraintCategory(), constraint );

            }

        }

        return rval;

    }

    @NotNull
    public FlexiGridBasicConstraint getBasicConstraint() {

        FlexiGridConstraint rval = get( FlexiGridConstraintCategory.BASIC );

        return (FlexiGridBasicConstraint)rval;

    }

    public Insets getMargins() {

        return getBasicConstraint().getMargins();

    }

    public FlexiGridBasicConstraint.HJustification getHorizontalJustification() {

        return getBasicConstraint().getHorizontalJustification();

    }

    public FlexiGridBasicConstraint.VJustification getVerticalJustification() {

        return getBasicConstraint().getVerticalJustification();

    }

}
