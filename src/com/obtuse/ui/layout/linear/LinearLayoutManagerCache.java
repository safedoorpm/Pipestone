/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import java.awt.*;

/**
 Describe how a LLM's cache behaves.
 */

public interface LinearLayoutManagerCache {

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
