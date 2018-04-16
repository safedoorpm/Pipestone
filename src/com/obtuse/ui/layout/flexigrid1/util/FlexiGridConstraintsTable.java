/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.TreeMap;

/**
 A table of FlexiGrid constraints.
 */

public class FlexiGridConstraintsTable extends TreeMap<FlexiGridConstraintCategory, FlexiGridConstraint> {

    public FlexiGridConstraintsTable() {

        super();

    }

    public FlexiGridConstraintsTable( final FlexiGridConstraint singletonConstraint ) {

        super();

        put( singletonConstraint.getConstraintCategory(), singletonConstraint );

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
