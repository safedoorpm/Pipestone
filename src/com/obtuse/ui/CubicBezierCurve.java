package com.obtuse.ui;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/*
 * <p/>
 * Copyright © 2010 Daniel Boulet.
 */

public class CubicBezierCurve implements GraphicsElement {

    private final BezierCurveSequence _parent;

    private final CubicCurve2D _cubicCurve;

    public CubicBezierCurve( BezierCurveSequence parent, CubicCurve2D cubicCurve ) {
        super();

        _parent = parent;

        _cubicCurve = cubicCurve;

    }

    public BezierCurveSequence getParentElement() {

        return _parent;

    }

    public Rectangle2D getBounds2D() {

        return _cubicCurve.getBounds2D();

    }

    public Point2D getP1() {

        return _cubicCurve.getP1();

    }

    public Point2D getP2() {

        return _cubicCurve.getP2();

    }

    public Point2D getCtrlP1() {

        return _cubicCurve.getCtrlP1();

    }

    public Point2D getCtrlP2() {

        return _cubicCurve.getCtrlP2();

    }

}
