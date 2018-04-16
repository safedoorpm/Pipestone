/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import com.obtuse.ui.layout.flexigrid1.GridArray;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 A factory for creating {@link com.obtuse.ui.layout.flexigrid1.GridArray.ItemInfo} instances.
 */

public interface FlexiGridItemInfoFactory<E> {

    GridArray.ItemInfo<E> createInstance(
            int row,
            int col,
            final @NotNull Component component,
            final @NotNull FlexiGridConstraintsTable constraintsTable
    );

}
