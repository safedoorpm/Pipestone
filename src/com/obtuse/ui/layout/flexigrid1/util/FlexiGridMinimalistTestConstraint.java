/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import org.jetbrains.annotations.NotNull;

/**
 A minimalist test FlexiGrid constraint.
 <p>Intended for experimental and testing purposes.
 Do not use this in anything even remotely resembling production software.</p>
 */

public class FlexiGridMinimalistTestConstraint implements FlexiGridConstraint {

    private final String _name;

    private final boolean _enabled;
    private final FlexiGridConstraintCategory _constraintCategory;

    public FlexiGridMinimalistTestConstraint(
            @SuppressWarnings("SameParameterValue") final @NotNull String name,
            final boolean enabled,
            FlexiGridConstraintCategory constraintCategory
    ) {

        super();

        _name = name;

        _enabled = enabled;

        _constraintCategory = constraintCategory;

    }

    @Override
    @NotNull
    public String getName() {

        return _name;

    }

    public boolean isEnabled() {

        return _enabled;

    }

    public FlexiGridConstraintCategory getConstraintCategory() {

        return _constraintCategory;

    }

    public int hashCode() {

        return getName().hashCode();

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof FlexiGridConstraint && compareTo( (FlexiGridConstraint)rhs ) == 0;

    }

    public int compareTo( final FlexiGridConstraint rhs ) {

        return getName().compareTo( rhs.getName() );

    }

    public String toString() {

        return "FlexiGridMinimalistTestConstraint( \"" + getName() + "\", " + isEnabled() + " )";

    }

}
