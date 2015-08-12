package com.obtuse.util.geometry2d;

/*
 * Copyright © 2015 Daniel Boulet
 */

import java.awt.geom.Line2D;

/**
 Useful utility methods.
 */

public class Util {

    /**
     Determine if two double-precision values are approximately equal.
     <p/>REALLY REALLY IMPORTANT: treats the two values as being equal if their absolute difference is less than some threshold.
     Does NOT use any sort of absolute error computation (absolute error is hard; so is nearly equal; using absolute difference is
     good enough for what we are doing here in fairly modest test suites so that's what we do here).
     <p/>I believe that this code handles NaNs and infinities properly (note the use of the word "believe" - don't confuse it with "know").
     @param a first value.
     @param b second value.
     @return true if they are equal to within <code>1e-10</code> in absolute terms.
     */

    public static boolean nearlyEqual( double a, double b ) {

	return nearlyEqual( a, b, 1e-10 );

    }

    public static boolean nearlyEqual( double a, double b, double epsilon ) {

	double diff = Math.abs( a - b );

	return diff < epsilon;

    }

    /**
     Compute the sine of an angle specified in degrees.
     <p/>A call to this method with a parameter value of <code>x</code> is equivalent to
     <blockquote><code>Math.sin( Math.PI * x / 180 )</code></blockquote>
     @param degrees the angle in degrees.
     @return the sine of the specified angle.
     */

    public static double sinDegrees( double degrees ) {

	return Math.sin( Math.PI * degrees / 180 );

    }

    /**
     Compute the cosine of an angle specified in degrees.
     <p/>A call to this method with a parameter value of <code>x</code> is equivalent to
     <blockquote><code>Math.cos( Math.PI * x / 180 )</code></blockquote>
     @param degrees the angle in degrees.
     @return the cosine of the specified angle.
     */

    public static double cosDegrees( double degrees ) {

	return Math.cos( Math.PI * degrees / 180 );

    }

    public static double dotProduct( Line2D.Double a, Line2D.Double b ) {

	MyPoint aVector = new MyPoint( a.x2 - a.x1, a.y2 - a.y1 );
	MyPoint bVector = new MyPoint( b.x2 - b.x1, b.y2 - b.y1 );

	return aVector.x * bVector.x + aVector.y * bVector.y;

    }

}
