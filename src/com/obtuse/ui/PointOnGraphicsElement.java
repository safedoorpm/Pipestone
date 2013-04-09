package com.obtuse.ui;

import java.awt.geom.Point2D;

/*
 * <p/>
 * Copyright © 2010 Daniel Boulet.
 */
public interface PointOnGraphicsElement {

    // Not at all clear that providing an index of the element in the larger entity is a good idea.

//    int getElementIndex();

    GraphicsElement getGraphicsElement();

    Point2D getPoint();

}
