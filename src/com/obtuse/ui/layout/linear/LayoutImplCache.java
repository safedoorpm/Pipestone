/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import java.awt.*;

/**
 %%% Something clever goes here.
 */
public interface LayoutImplCache {

    float getLayoutAlignmentX();

    float getLayoutAlignmentY();

    Dimension getMinimumSize();

    Dimension getPreferredSize();

    Dimension getMaximumSize();

    void computePositions();

    void setComponentBounds();

    int getVisibleComponentCount();

    Component getVisibleComponent( int ix );

}
