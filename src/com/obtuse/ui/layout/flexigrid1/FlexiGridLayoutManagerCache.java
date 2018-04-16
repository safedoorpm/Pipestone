/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 Describe how a FlexiGrid layout manager's cache behaves.
 */

public interface FlexiGridLayoutManagerCache {

    Dimension getMinimumSize();

    Dimension getPreferredSize();

    Dimension getMaximumSize();

    void computePositions();

    void setComponentBounds();

    long getSerialNumber();

    @NotNull GridArray<FlexiGridItemInfo> getGrid();

    int getVisibleComponentCount();

    Component getVisibleComponent( int ix );

}
