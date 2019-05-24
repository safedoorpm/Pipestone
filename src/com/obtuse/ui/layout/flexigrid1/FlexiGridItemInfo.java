/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 An element in the {@link GridArray} that is used to manage components involved in the layout process.
 */

public class FlexiGridItemInfo extends GridArray.ItemInfo<FlexiGridConstraintsTable> {

    public interface FlexiItemInfoFactory {

        FlexiGridItemInfo createInstance(
                int row,
                int col,
                final @NotNull Component component,
                final @NotNull FlexiGridConstraintsTable constraintsTable
        );

    }

    public FlexiGridItemInfo(
            final @NotNull String name,
            final int row,
            final int col,
            final @NotNull Component component,
            final FlexiGridConstraintsTable info
    ) {

        super( name, row, col, component, info );

    }

    public FlexiGridItemInfo(
            final int row,
            final int col,
            final @NotNull Component component, final FlexiGridConstraintsTable info
    ) {

        super( row, col, component, info );

    }

    public FlexiGridItemInfo( final int row, final int col, final @NotNull Component component ) {

        super( row, col, component );

    }

}
