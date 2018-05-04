/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import org.jetbrains.annotations.NotNull;

/**
 An arbitrary FlexiGrid constraint.
 */

public interface FlexiGridConstraint {

    @NotNull String getName();

    FlexiGridConstraintCategory getConstraintCategory();

}
