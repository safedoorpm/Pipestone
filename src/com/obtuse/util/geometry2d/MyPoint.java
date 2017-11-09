package com.obtuse.util.geometry2d;

import java.awt.geom.Point2D;

/*
 * Copyright © 2015 Daniel Boulet
 */

/**
 A slightly improved extension of the {@link java.awt.geom.Point2D.Double} class.
 <p/>
 This class provides a {@link #toString} method which produces a 'prettier' result that the {@link java.awt.geom.Point2D.Double#toString} method produces.
 It also provides a few useful methods. which yield new instances of this class.
 <p/>
 Note that since this class is derived from the {@link java.awt.geom.Point2D.Double} class, instances of this class may be used anywhere
 an instance of the {@link java.awt.geom.Point2D.Double} class may appear.
 <p/>
 Warning: <b>Instances of this class are NOT immutable as this class is derived from the very mutable {@link java.awt.geom.Point2D.Double} class.</b>
 */

public class MyPoint extends Point2D.Double {

    /**
     Create a new point at the origin.
     */

    public MyPoint() {

	super();

    }

    /**
     Create a new point at the specified coordinates.
     */

    public MyPoint( final double x, final double y ) {

	super( x, y );

    }

    public static String pToString( final Double p ) {

	return "[" + p.x + "," + p.y + "]";

    }

    /**
     Provide a string representation of this point.
     <p/>
     It will take the form
     <blockquote><code>[<i>x value</i>,<i>y value</i>]</code></blockquote>
     For example, a point at the origin would yield <code>[0.0,0.0]</code>.
     The precision of the values is whatever {@link java.lang.Double#toString()} yields for the values in question.
     */

    public String toString() {

	return pToString( this );

    }

    public MyPoint rotateDegrees( final double degrees ) {

	return rotateTheta( Math.PI * degrees / 180 );

    }

    public MyPoint rotateTheta( final double theta ) {

	double cosTheta = Math.cos( theta );
	double sinTheta = Math.sin( theta );

//	    Logger.logMsg( "point theta = " + theta + ", cosTheta = " + cosTheta + ", sinTheta = " + sinTheta );

	MyPoint rotated = new MyPoint(
		cosTheta * x - sinTheta * y,
		sinTheta * x + cosTheta * y
	);

	return rotated;

    }

    public MyPoint move( final double adjX, final double adjY ) {

	return new MyPoint( x + adjX, y + adjY );

    }

    public boolean nearlyEquals( final Double rhs ) {

	return Util.nearlyEqual( x, rhs.x ) && Util.nearlyEqual( y, rhs.y );

    }

}
