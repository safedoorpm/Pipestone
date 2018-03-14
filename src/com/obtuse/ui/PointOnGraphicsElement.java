/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import java.awt.geom.Point2D;

@SuppressWarnings("UnusedDeclaration")
public interface PointOnGraphicsElement {

    // Not at all clear that providing an index of the element in the larger entity is a good idea.

    GraphicsElement getGraphicsElement();

    Point2D getPoint();

}
