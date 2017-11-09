package com.obtuse.util.geometry2d;

/*
 * Copyright © 2015 Daniel Boulet
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 Represent a circle and provide a few useful circle-related geometry algorithms.
 <p/>
 Instances of this class are immutable.
 */

public class Circle {

    public static final Circle UNIT_CIRCLE = new Circle( new MyPoint( 0.0, 0.0 ), 1.0 );

    private final Point2D.Double _center;

    private final double _radius;

    private final int _hashCode;

    /**
     Describe the 0, 1, 2 or infinite intersections between two circles.
     <p/>
     Instances of this class are immutable.
     */

    public static class IntersectionInfo {

        private final Point2D.Double[] _points;

        /**
         Create a description of the 0, 1, 2 or infinite intersections between two circles.

         @param points the intersection points by specifying either
         <ul>
         <li>a 0, 1 or 2 element array of intersection points</li>
         <li><code>null</code> indicating that the two circles were identical and
         that there are an infinite number of intersection points between them</li>
         </ul>
         If a non-null array of points is provided then the specified array of points,
         including the point instances themselves, are copied into the newly created instance by this constructor.
         Changes to the provided array or the points provided within the array after this constructor
         returns will not have any effect on the state of this instance.
         @throws java.lang.NullPointerException     if there are any null elements in a non-null <code>points</code> array.
         @throws java.lang.IllegalArgumentException if there are any NaNs or infinities used as point coordinates or
         if a non-null <code>points</code> array has more than 2 elements.
         */

        public IntersectionInfo( @Nullable final Point2D.Double[] points ) {

            super();

            if ( points == null ) {

                _points = null;

            } else if ( points.length > 2 ) {

                throw new IllegalArgumentException( "points array must have 0, 1 or 2 elements (it has " + points.length + " elements)" );

            } else {

                for ( int ix = 0; ix < points.length; ix += 1 ) {

                    if ( Double.isNaN( points[ix].x ) || Double.isNaN( points[ix].y ) ) {

                        throw new IllegalArgumentException( "found a NaNs at ix " + ix + ":  " + points[ix] );

                    }

                    if ( Double.isInfinite( points[ix].x ) || Double.isInfinite( points[ix].y ) ) {

                        throw new IllegalArgumentException( "found a infinity at ix " + ix + ":  " + points[ix] );

                    }

                }

                _points = new Point2D.Double[points.length];
                for ( int ix = 0; ix < _points.length; ix += 1 ) {

                    _points[ix] = new Point2D.Double( points[ix].x, points[ix].y );

                }

            }

        }

        /**
         Determine if there are an infinite number of intersection points.

         @return true if there is; false if there are 0, 1 or 2 intersection points.
         */

        public boolean infiniteIntersections() {

            return _points == null;

        }

        /**
         Get the number of intersection points.

         @return 0, 1 or 2. If the return value is 2 then the two points might be arbitrarily close together
         depending on which algorithm is used to compute the intersection points.
         @throws java.lang.IllegalArgumentException if the two circles were identical (i.e. if there were an
         infinite number of intersection points). Ensure that {@link #infiniteIntersections()} returns false
         before calling this method if you'd rather not deal with this exception.
         */

        public int getCount() {

            if ( _points == null ) {

                throw new IllegalArgumentException( "circles are coincident - infinite number of intersection points" );

            } else {

                return _points.length;

            }

        }

        /**
         Return a copy of the array of points which were provided when this instance was created.

         @return a copy of the array of points (both the array and the points within the array will
         be copies which the caller is free to do with as they wish. Each call to this method returns
         a new copy of the array.
         */

        @Nullable
        public MyPoint[] getPoints() {

            if ( _points == null ) {

                return null;

            } else {

                MyPoint[] rval = new MyPoint[_points.length];
                for ( int ix = 0; ix < _points.length; ix += 1 ) {

                    rval[ix] = new MyPoint( _points[ix].x, _points[ix].y );

                }

                return rval;

            }

        }

        /**
         A string representation of the intersection info.

         @return a string representation of this instance.
         */

        public String toString() {

            if ( infiniteIntersections() ) {

                return "{ identical circles }";

            } else {

                return "{ " + Arrays.toString( _points ) + " }";

            }

        }

    }

    /**
     Define a circle centered at a specified location and with a specified radius.
     <p/>Instances of this class are immutable.

     @param center the center of the circle.
     @param radius the radius of the circle.
     */

    public Circle( final Point2D.Double center, final double radius ) {

        super();

        _center = new MyPoint( center.x, center.y );
        _radius = radius;

        _hashCode = new Double( _center.x + _center.y + _radius ).hashCode();

    }

    /**
     Return a hashcode for this instance.
     */

    public int hashCode() {

        return _hashCode;

    }

    /**
     Determine if this circle is exactly equal to some other circle.
     <p/>Double precision IEEE 754 equality operations are used to determine if the two circles
     are at the same location and have the same radius.
     Beware of roundoff error problems . . .
     */

    public boolean equals( final Object rhs ) {

        if ( rhs instanceof Circle ) {

            Circle other = (Circle)rhs;
            return _center.x == other._center.x && _center.y == other._center.y && _radius == other._radius;

        } else {

            return false;

        }

    }

    /**
     Determine if the center of this circle is located at exactly [0.0,0.0].
     <p/>Double precision IEEE 754 equality operators are used to determine if this circle is at the origin.
     Beware of roundoff error problems . . .
     */

    public boolean exactlyAtOrigin() {

        return _center.x == 0.0 && _center.y == 0.0;

    }

    /**
     Get this circle's center.

     @return a newly minted {@link MyPoint} instance specifying the location of this circle's center.
     Note that the return value of this method can be used wherever a {@link Point2D.Double} value is legal since the
     {@link MyPoint} class is derived from the {@link Point2D.Double} class.
     Changes to this returned value by the caller will have no impact on the state of this {@link Circle} instance.
     */

    public MyPoint getCenter() {

        return new MyPoint( _center.x, _center.y );

    }

    /**
     Get this circle's radius.

     @return this circle's radius.
     */

    public double getRadius() {

        return _radius;

    }

    public IntersectionInfo computeCircleCircleIntersection( final Circle other ) {

        return computeCircleCircleIntersection( other, false );

    }

    public IntersectionInfo computeCircleCircleIntersection( final Circle other, final boolean testMode ) {

        return computeCircleCircleIntersection(
                _center.x,
                _center.y,
                _radius,
                other._center.x,
                other._center.y,
                other._radius,
                testMode
        );

    }

    public static IntersectionInfo computeCircleCircleIntersection(
            final double x0,
            final double y0,
            final double r0,
            final double x1,
            final double y1,
            final double r1
    ) {

        return computeCircleCircleIntersection( x0, y0, r0, x1, y1, r1, false );

    }

    // One of the two circle-circle intersection algorithms is derived from following code
    // found at http://paulbourke.net/geometry/circlesphere/tvoght.c
    // Uses algorithm described at http://paulbourke.net/geometry/circlesphere/
    // Found 2015/03/16
    // Note the public domain disclaimer below. It existed in the tvoght.c source file when it was copied to here on 2015/03/16.
    //
    ///* circle_circle_intersection() *
    // * Determine the points where 2 circles in a common plane intersect.
    // *
    // * int circle_circle_intersection(
    // *                                // center and radius of 1st circle
    // *                                double x0, double y0, double r0,
    // *                                // center and radius of 2nd circle
    // *                                double x1, double y1, double r1,
    // *                                // 1st intersection point
    // *                                double *xi, double *yi,
    // *                                // 2nd intersection point
    // *                                double *xi_prime, double *yi_prime)
    // *
    // * This is a public domain work. 3/26/2005 Tim Voght
    // *
    // */
    //    #include <stdio.h>
    //    #include <math.h>
    //
    //    int circle_circle_intersection(double x0, double y0, double r0,
    //				   double x1, double y1, double r1,
    //				   double *xi, double *yi,
    //				   double *xi_prime, double *yi_prime)
    //    {
    //	double a, dx, dy, d, h, rx, ry;
    //	double x2, y2;
    //
    //  /* dx and dy are the vertical and horizontal distances between
    //   * the circle centers.
    //   */
    //	dx = x1 - x0;
    //	dy = y1 - y0;
    //
    //  /* Determine the straight-line distance between the centers. */
    //	//d = sqrt((dy*dy) + (dx*dx));
    //	d = hypot(dx,dy); // Suggested by Keith Briggs
    //
    //  /* Check for solvability. */
    //	if (d > (r0 + r1))
    //	{
    //    /* no solution. circles do not intersect. */
    //	    return 0;
    //	}
    //	if (d < fabs(r0 - r1))
    //	{
    //    /* no solution. one circle is contained in the other */
    //	    return 0;
    //	}
    //
    //  /* 'point 2' is the point where the line through the circle
    //   * intersection points crosses the line between the circle
    //   * centers.
    //   */
    //
    //  /* Determine the distance from point 0 to point 2. */
    //	a = ((r0*r0) - (r1*r1) + (d*d)) / (2.0 * d) ;
    //
    //  /* Determine the coordinates of point 2. */
    //	x2 = x0 + (dx * a/d);
    //	y2 = y0 + (dy * a/d);
    //
    //  /* Determine the distance from point 2 to either of the
    //   * intersection points.
    //   */
    //	h = sqrt((r0*r0) - (a*a));
    //
    //  /* Now determine the offsets of the intersection points from
    //   * point 2.
    //   */
    //	rx = -dy * (h/d);
    //	ry = dx * (h/d);
    //
    //  /* Determine the absolute intersection points. */
    //	*xi = x2 + rx;
    //	*xi_prime = x2 - rx;
    //	*yi = y2 + ry;
    //	*yi_prime = y2 - ry;
    //
    //	return 1;
    //    }
    //
    //    #define TEST
    //
    //    #ifdef TEST
    //
    //    void run_test(double x0, double y0, double r0,
    //		  double x1, double y1, double r1)
    //    {
    //	double x3, y3, x3_prime, y3_prime;
    //
    //	printf("x0=%F, y0=%F, r0=%F, x1=%F, y1=%F, r1=%F :\n",
    //	       x0, y0, r0, x1, y1, r1);
    //	circle_circle_intersection(x0, y0, r0, x1, y1, r1,
    //	&x3, &y3, &x3_prime, &y3_prime);
    //	printf("  x3=%F, y3=%F, x3_prime=%F, y3_prime=%F\n",
    //	       x3, y3, x3_prime, y3_prime);
    //    }
    //
    //    int main(void)
    //    {
    //  /* Add more! */
    //	run_test(-1.0, -1.0, 1.5, 1.0, 1.0, 2.0);
    //	run_test(1.0, -1.0, 1.5, -1.0, 1.0, 2.0);
    //	run_test(-1.0, 1.0, 1.5, 1.0, -1.0, 2.0);
    //	run_test(1.0, 1.0, 1.5, -1.0, -1.0, 2.0);
    //	exit(0);
    //    }
    //    #endif

    public static IntersectionInfo computeCircleCircleIntersection(
            final double x0,
            final double y0,
            final double r0,
            final double x1,
            final double y1,
            final double r1,
            final boolean testMode
    ) {

        if ( x0 == x1 && y0 == y1 && r0 == r1 ) {

            return new IntersectionInfo( null );

        }

        // dx and dy are the vertical and horizontal distances between the circle centers.

        double dx = x1 - x0;
        double dy = y1 - y0;

        // Determine the straight-line distance between the centers.

        double d = Math.hypot( dx, dy ); // Suggested by Keith Briggs

        // Check for solvability.

//	if ( d > ( r0 + r1 ) ) {
        if ( ( testMode ? d * 0.999999 : d ) > ( r0 + r1 ) ) {

            // no solution. circles do not intersect.

            return new IntersectionInfo( new MyPoint[0] );

        }

//	if ( d < Math.abs( r0 - r1 ) ) {
        if ( d < ( testMode ? Math.abs( r0 - r1 ) * 0.999999 : Math.abs( r0 - r1 ) ) ) {

            // no solution. one circle is contained in the other.

            return new IntersectionInfo( new MyPoint[0] );

        }

        // 'point 2' is the point where the line through the circle intersection points crosses the line between the circle centers.

        // Determine the distance from point 0 to point 2.

        double a = ( ( r0 * r0 ) - ( r1 * r1 ) + ( d * d ) ) / ( 2.0 * d );

        // Determine the coordinates of point 2.

        double x2 = x0 + ( dx * a / d );
        double y2 = y0 + ( dy * a / d );

        // Determine the distance from point 2 to either of the intersection points.

        double deltaR = ( r0 * r0 ) - ( a * a );
        double h = Math.sqrt( deltaR >= 0 ? deltaR : 0 );

        // Now determine the offsets of the intersection points from point 2.

        double rx = -dy * ( h / d );
        double ry = dx * ( h / d );

        // Determine the absolute intersection points.

        IntersectionInfo rval = new IntersectionInfo(
                new MyPoint[]{
                        new MyPoint( x2 + rx, y2 + ry ),
                        new MyPoint( x2 - rx, y2 - ry )
                }
        );

        return rval;

    }

    /**
     An alternative algorithm which computes the intersection between this instance and another {@link Circle} instance.
     <p/>Found in the "dead tree" book <i>Computational Geometry in C</i> by
     Joseph O'Rourke, Copyright &copy; Cambridge University Press 1994, ISBN 0-521-44034-3.

     @param other the other circle.
     @return {@link com.obtuse.util.geometry2d.Circle.IntersectionInfo} instance indicating which intersection points, if any, were found.
     */

    @NotNull
    public IntersectionInfo intersects( @NotNull final Circle other ) {

        if ( exactlyAtOrigin() ) {

            return originCircleIntersects( other );

        }

        Circle usAtOrigin = new Circle( new MyPoint( 0.0, 0.0 ), _radius );
        Circle themAdjusted = new Circle( new MyPoint( other._center.x - _center.x, other._center.y - _center.y ), other._radius );

        IntersectionInfo info = usAtOrigin.originCircleIntersects( themAdjusted );
        if ( !info.infiniteIntersections() ) {

            Point2D.Double[] before = info._points;
            Point2D.Double[] after = new Point2D.Double[before.length];
            for ( int ix = 0; ix < before.length; ix += 1 ) {

                after[ix] = new MyPoint( before[ix].x + _center.x, before[ix].y + _center.y );

            }

            info = new IntersectionInfo( after );

        }

        return info;

    }

    /**
     Assuming that our circle is centered at the origin, compute the intersection[s]] with our circle and some other circle.

     @param other the other circle.
     @return the intersection information which will indicate no intersection, one intersection and what it is,
     two intersections and what they are, or that there are an infinite number of intersections.
     @throws java.lang.IllegalArgumentException if our circle is not <b>exactly</b> centered at the origin.
     */

    public IntersectionInfo originCircleIntersects( @NotNull final Circle other ) {

        if ( !exactlyAtOrigin() ) {

            throw new IllegalArgumentException( "this instance's circle is not at origin (it is as " + _center + ")" );

        }

//	Logger.logMsg( "origin circle " + this + " vs " + other );
        double distanceSq = _center.distanceSq( other._center );        // distance between the two centers squared
        double sumRadiiSq = ( _radius + other._radius ) * ( _radius + other._radius );  // sum of the two radii squared
        double diffRadiiSq = ( _radius - other._radius ) * ( _radius - other._radius ); // difference of the two radii squared

//	Logger.logMsg( "" + this + " vs " + other + ":  distanceSq=" + distanceSq + ", sumRadiiSq=" + sumRadiiSq + ", diffRadiiSq=" + diffRadiiSq );

        // Are the two circles too far apart or two close together to intersect?

        if ( distanceSq > sumRadiiSq || distanceSq < diffRadiiSq ) {

            return new IntersectionInfo( new Point2D.Double[0] );

        }

        // Do the two circles just barely touch with neither inside the other?

//	if ( distanceSq == sumRadiiSq ) {
        if ( Util.nearlyEqual( distanceSq, sumRadiiSq, Math.max( distanceSq, sumRadiiSq ) / 1e8 ) ) {

            double f = _radius / ( _radius + other._radius );
            Point2D.Double intersect = new MyPoint( f * other._center.x, f * other._center.y );

//	    Logger.logMsg( "circles externally touch:  f=" + f + ", intersect=" + intersect );

            return new IntersectionInfo( new Point2D.Double[]{ intersect } );

        }

        // Do the circles just barely touch with one inside the other?

//	if ( distanceSq == diffRadiiSq ) {
        if ( Util.nearlyEqual( distanceSq, diffRadiiSq, Math.max( Math.abs( distanceSq ), Math.abs( diffRadiiSq ) ) / 1e5 ) ) {

            // Do the two circles coincide?

            if ( Math.abs( diffRadiiSq ) < 1e-5 ) {

                return new IntersectionInfo( null );

            }

            // The two circles just touch at a single point with one circle inside the other.

            double f = _radius / ( _radius - other._radius );
            Point2D.Double intersect = new MyPoint( f * other._center.x, f * other._center.y );

//	    Logger.logMsg( "circles internally touch:  f=" + f + ", intersect=" + intersect );

            return new IntersectionInfo( new Point2D.Double[]{ intersect } );

        }

        // There are exactly two points of intersection.

        return computeTwoIntersections( other );

    }

    private IntersectionInfo computeTwoIntersections( @NotNull final Circle other ) {

        if ( !exactlyAtOrigin() ) {

            throw new IllegalArgumentException( "this instance's circle is not at origin (it is as " + _center + ")" );

        }

        double a2 = _center.distance( other._center );
        double cosTheta = other._center.x / a2;
        double sinTheta = other._center.y / a2;

//	Logger.logMsg( "cTT " + this + " vs " + other + ":  a2=" + a2 + ", cosTheta=" + cosTheta + ", sinTheta=" + sinTheta );

        IntersectionInfo info = computeRotatedIntersections( new Circle( new MyPoint( a2, 0.0 ), other._radius ) );

        if ( info.getCount() != 2 ) {

            throw new HowDidWeGetHereError(
                    "did not find two intersections when logic requires that there be two intersections (us=" +
                    this +
                    ", them=" +
                    other +
                    ")"
            );

        }

        Point2D.Double[] points = info._points;
        Point2D.Double[] rPoints = new Point2D.Double[points.length];
        for ( int ix = 0; ix < points.length; ix += 1 ) {

            rPoints[ix] = new MyPoint(
                    cosTheta * points[ix].x - sinTheta * points[ix].y,
                    sinTheta * points[ix].x + cosTheta * points[ix].y
            );

        }

        return new IntersectionInfo( rPoints );

    }

    private IntersectionInfo computeRotatedIntersections( @NotNull final Circle other ) {

        if ( !exactlyAtOrigin() ) {

            throw new IllegalArgumentException( "this instance's circle is not at origin (it is as " + _center + ")" );

        }

        if ( other._center.y != 0.0 ) {

            throw new IllegalArgumentException( "other instance's circle is not on X axis (it is at " + other._center + ")" );

        }

        double xIntersection = ( other._center.x + ( _radius * _radius - other._radius * other._radius ) / other._center.x ) / 2.0;
        double yIntersection = Math.sqrt( _radius * _radius - xIntersection * xIntersection );

//	Logger.logMsg( "cRI " + this + " vs " + other + ":  xI=" + xIntersection + ", yI=" + yIntersection );

        return new IntersectionInfo(
                new Point2D.Double[]{
                        new MyPoint( xIntersection, yIntersection ),
                        new MyPoint( xIntersection, -yIntersection )
                }
        );

    }

    /**
     Use Thales theorem to compute the two tangent lines to a circle from a point located outside the circle.
     <p/>
     We use the following:
     <ol>
     <li>Consider a circle C centered at location O with radius R and a point P which is located anywhere outside the circle C.</li>
     <li>A tangent line from point P to circle C touches circle C at exactly one point.
     <li>There are exactly two distinct tangent lines each of which pass through P and touch circle C at distinct points T1 and T2.</li>
     <li>These two points of contact, one on each distinct tangent line, are the only points on any of the infinite number of lines which pass through P and which intersect or touch
     circle C at point X where the line PX is perpendicular to the radius line OX.</li>
     <li>Draw a circle D which passes through both O and P and which is centered on Q, the midpoint of the line segment OP.
     Since this circle D touches point P which is outside the circle C and point O which is inside the circle C, this circle D intersects circle C at exactly two distinct points.
     Call these points S1 and S2.</li>
     <li>The line through P and S1 is perpendicular to the line through O and S1 because these two lines are inscribed
     within the semicircle which is the portion of D which is located on S1's side of the line segment OP.
     In other words, the line PS1 passes through P and touches circle C at a point where the line PS1 is perpendicular to the radius line OS1.
     <li>Since there are exactly two such points (see point 4 above), points S1 and S2 are also the two points T1 and T2 from above.</li>
     <li>Consequently, the lines PT1 and PT2 are the two tangent lines from P to circle C.</li>
     </ol>
     */

    @NotNull
    public MyPoint[] computeTangentLines( final Point2D.Double p ) {

        double distancePtoO = _center.distance( p );
        if ( distancePtoO <= _radius ) {

            throw new IllegalArgumentException( "point P at " + p + " is outside the circle " + this );

        }

        // Compute the circle D centered on the midpoint between the center of circle C and point P.

        Circle d = new Circle( new MyPoint( ( _center.x + p.x ) / 2, ( _center.y + p.y ) / 2 ), distancePtoO / 2 );

        // Compute the points of intersection between circles C and D.

        IntersectionInfo info = computeCircleCircleIntersection( d );

        if ( info == null ) {

            throw new HowDidWeGetHereError( "circle D " +
                                            d +
                                            " is identical to circle C " +
                                            this +
                                            " when computing tangent lines from point P " +
                                            p +
                                            " to circle C (should be impossible)" );

        }

        if ( info.getCount() != 2 ) {

            throw new HowDidWeGetHereError( "found " +
                                            info.getCount() +
                                            " intersection point" +
                                            ( info.getCount() == 1 ? "" : "s" ) +
                                            " between circle C " +
                                            this +
                                            " and circle D " +
                                            d +
                                            " when computing tangent lines from point P " +
                                            p +
                                            " to circle C (should be impossible)" );

        }

        return info.getPoints();

    }

    /**
     Create a new circle which is a scaled copy of this circle.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance,
     the radius of the new circle will be
     <blockquote>
     <code>c.getRadius() * scaleFactor</code>
     </blockquote>
     The location of the new circle will be the same as the location of this circle.

     @param scaleFactor the scale factor to be applied to this circle to yield the new circle.
     @return the scaled copy of this circle.
     */

    public Circle scale( final double scaleFactor ) {

        return new Circle( _center, _radius * scaleFactor );

    }

    /**
     Create a new circle which is a moved copy of this circle.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance,
     location of the new circle will be
     <blockquote>
     <code>[c.getCenter().x + delta.x, c.getCenter().y + delta.y]</code>
     </blockquote>
     The radius of the new circle will be the same as the radius of this circle.

     @param delta how the circle is to be moved.
     @return the new circle.
     */

    public Circle moveRelative( final Point2D.Double delta ) {

        return new Circle( new MyPoint( _center.x + delta.x, _center.y + delta.y ), _radius );

    }

    /**
     Create a new circle which is a moved copy of this circle.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance,
     location of the new circle will be
     <blockquote>
     <code>[c.getCenter().x + xDelta, c.getCenter().y + yDelta]</code>
     </blockquote>
     The radius of the new circle will be the same as the radius of this circle.

     @param xDelta the amount that the new circle's X coordinate should be greater than this circle's X coordinate.
     @param yDelta the amount that the new circle's Y coordinate should be greater than this circle's Y coordinate.
     @return the new circle.
     */

    public Circle moveRelative( final double xDelta, final double yDelta ) {

        return new Circle( new MyPoint( _center.x + xDelta, _center.y + yDelta ), _radius );

    }

    /**
     Create a new circle which is a moved copy of this circle.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance,
     location of the new circle will be
     <blockquote>
     <code>[newLocation.x, newLocation.y]</code>
     </blockquote>
     The radius of the new circle will be the same as the radius of this circle.

     @param newLocation where the circle is to be moved to.
     @return the new circle.
     */

    public Circle moveTo( final Point2D.Double newLocation ) {

        return new Circle( new MyPoint( newLocation.x, newLocation.y ), _radius );

    }

    /**
     Create a new circle which is a moved copy of this circle.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance,
     location of the new circle will be
     <blockquote>
     <code>[xLocation, yLocation]</code>
     </blockquote>
     The radius of the new circle will be the same as the radius of this circle.

     @param xLocation the new circle's X coordinate.
     @param yLocation the new circle's Y coordinate.
     @return the new circle.
     */

    public Circle moveTo( final double xLocation, final double yLocation ) {

        return new Circle( new MyPoint( _center.x + xLocation, _center.y + yLocation ), _radius );

    }

    /**
     Create a new circle which is this circle rotated counter-clockwise around the origin by the specified amount in degrees.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance, this method is equivalent to
     <blockquote>
     new Circle( c.getCenter().rotateDegrees( degrees ), c.getRadius() )
     </blockquote>
     For example, if this circle is a unit circle located at [1,0] then invoking this method on this instance with
     a <code>degrees</code> value of 90 degrees would yield a new unit circle at [0,1]
     (this circle would remain unchanged as instances of this class are immutable).

     @param degrees the amount that this circle is to be rotated around the origin in degrees.
     @return the rotated circle.
     */

    public Circle rotateDegrees( final double degrees ) {

        return rotateTheta( Math.PI * degrees / 180 );

    }

    /**
     Create a new circle which is this circle rotated counter-clockwise around the origin by the specified amount in radians.
     <p/>Assuming that the Java variable <code>c</code> refers to this instance, this method is equivalent to
     <blockquote>
     new Circle( c.getCenter().rotateTheta( theta ), c.getRadius() )
     </blockquote>
     For example, if this circle is a unit circle located at [1,0] then invoking this method on this instance with
     a <code>theta</code> value of <code>Math.PI / 2</code> would
     yield a new unit circle at [0,1] (this circle would remain unchanged as instances of this class are immutable).

     @param theta the amount that this circle is to be rotated around the origin in theta.
     @return the rotated circle.
     */

    public Circle rotateTheta( final double theta ) {

        double cosTheta = Math.cos( theta );
        double sinTheta = Math.sin( theta );

        MyPoint rotated = new MyPoint(
                cosTheta * _center.x - sinTheta * _center.y,
                sinTheta * _center.x + cosTheta * _center.y
        );

        return new Circle( rotated, _radius );

    }

    /**
     Get a string description of this instance.
     <p/>The returned value will take the form
     <blockquote><code>"{ [<i>x coordinate</i>,<i>y coordinate</i>} : <i>radius</i> }"</code></blockquote>.
     For example, a unit circle located at the origin would yield
     <blockquote><code>"{ [0.0,0.0] : 1.0 }"</code></blockquote>
     The precision of the values in the description is whatever {@link java.lang.Double#toString()} yields for the values in question.

     @return a string description of this instance.
     */

    public String toString() {

        return "{ " + MyPoint.pToString( _center ) + " : " + _radius + " }";

    }

    private static int s_errorCount;

    private static int s_trialCount;

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "geometry2d-Circle", null );

        testIntersection1();

        testTangent();

    }

    public static void testTangent() {

        double[] offsets = { 0.99999, 1.00001, Math.sqrt( 2 ), Math.sqrt( 3 ), Math.PI };

        s_trialCount = 0;
        s_errorCount = 0;

        for ( double offset1 : offsets ) {

            for ( int sign1 = -1; sign1 <= 1; sign1 += 2 ) {

                double xLocation = offset1 * sign1;

                for ( double offset2 : offsets ) {

                    for ( int sign2 = -1; sign2 <= 1; sign2 += 2 ) {

                        double yLocation = offset2 * sign2;

                        doTangentScaling( UNIT_CIRCLE, new MyPoint( xLocation, yLocation ) );

                    }

                }

            }

        }

        Logger.logMsg( "###1 " + s_trialCount + " trials, " + s_errorCount + " error" + ( s_errorCount == 1 ? "" : "s" ) );

    }

    private static void doTangentScaling( final Circle c, final MyPoint p ) {

//	Logger.logMsg( "" + s_trialCount + ":  " + c + " vs " + p );

        double[] scales = { 0.3, 1.0, 1.01, 7 };

        for ( double scale : scales ) {

            doTangentRotations( c.scale( scale ), new MyPoint( p.x * scale, p.y * scale ) );

        }

    }

    private static void doTangentRotations( final Circle c, final MyPoint p ) {

        double[] rotationsTheta = { 0, .3467834678, 0.5 * Math.PI, Math.PI, 1.5 * Math.PI, 2.0 * Math.PI, 17.777 * Math.PI };
        for ( double theta : rotationsTheta ) {

            doTangentXlates( c, p.rotateTheta( theta ) );

        }

    }

    public static void doTangentXlates( final Circle c, final MyPoint p ) {

        double[] offsets = { 0.0, 0.0001, 0.99999, 1.00001, Math.sqrt( 2 ), Math.sqrt( 3 ), Math.PI };

        for ( double offset1 : offsets ) {

            for ( int sign1 = -1; sign1 <= 1; sign1 += 2 ) {

                double xLocation = offset1 * sign1;

                for ( double offset2 : offsets ) {

                    for ( int sign2 = -1; sign2 <= 1; sign2 += 2 ) {

                        double yLocation = offset2 * sign2;

                        doOneTangent( c.moveRelative( xLocation, yLocation ), new MyPoint( p.x + xLocation, p.y + yLocation ) );

                    }

                }

            }

        }

    }

    private static void doOneTangent( final Circle c, final MyPoint p ) {

//	Logger.logMsg( "--- " + s_trialCount );

        boolean isInside = p.distanceSq( c.getCenter() ) <= c.getRadius() * c.getRadius();

        s_trialCount += 1;

        try {

            MyPoint[] tangentPoints = c.computeTangentLines( p );

            if ( isInside ) {

                Logger.logMsg( "*** expected IAE computing tangent lines from " + p + " to " + c + " (p is not outside c)" );
                s_errorCount += 1;

            } else if ( tangentPoints.length != 2 ) {

                Logger.logMsg( "*** got " +
                               tangentPoints.length +
                               " tangent point" +
                               ( tangentPoints.length == 1 ? "" : "s" ) +
                               " computing tangent lines from " +
                               p +
                               " to " +
                               c );
                s_errorCount += 1;

            } else {

                if ( !checkTangentPoint( c, p, tangentPoints[0] ) ) {

                    s_errorCount += 1;

                }

            }

        } catch ( IllegalArgumentException e ) {

            if ( isInside ) {

                ObtuseUtil.doNothing();

            } else {

                Logger.logMsg( "*** got an IAE point " + p + " is not inside circle c " + c );
                s_errorCount += 1;

            }

        }

    }

    private static boolean checkTangentPoint( final Circle c, final MyPoint p, final MyPoint tangentPoint ) {

//	Logger.logMsg( "checking " + c + " vs " + p + " at " + tangentPoint );

        if ( Util.nearlyEqual( c.getCenter().distance( tangentPoint ), c.getRadius() ) ) {

            Line2D.Double pT = new Line2D.Double( p, tangentPoint );
            Line2D.Double oT = new Line2D.Double( c.getCenter(), tangentPoint );
            if ( Util.nearlyEqual( Util.dotProduct( pT, oT ), 0 ) ) {

//		Logger.logMsg( "ok" );

                return true;

            } else {

                Logger.logMsg( "*** point " +
                               tangentPoint +
                               " is not a tangent point of point p " +
                               p +
                               " and circle c " +
                               c +
                               " (tangent line not perpendicular to radius line)" );

                return false;

            }

        } else {

            Logger.logMsg( "*** point " +
                           tangentPoint +
                           " is not a tangent point of point p " +
                           p +
                           " and circle c " +
                           c +
                           " (tangent point not on circle)" );

            return false;

        }

    }

    public static void testIntersection1() {

        Circle uc = UNIT_CIRCLE;

        s_trialCount = 0;
        s_errorCount = 0;

//	doSet( uc.move( -1, -1 ).scale( 1.5 ), uc.move( 1, 1 ).scale( 2 ), null );

//	Logger.logMsg( "" + uc + " vs " + uc.move( 1, 0 ) + " yielded " + uc.intersects( uc.move( 1, 0 ) ) );

//	doSet(
//		uc,
//		uc.move( 1, 0 ),
//		new MyPoint[]{ new MyPoint( 0.5, Math.sqrt( 1 - 0.5 * 0.5 ) ), new MyPoint( 0.5, -Math.sqrt( 1 - 0.5 * 0.5 ) ) }
//	);

//	Circle unitCircleAtx2 = new Circle( new MyPoint( 2.0, 0.0 ), 1.0 );

//	doit( uc, uc.move( 2.0, 0.0 ), new MyPoint[]{ new MyPoint( 1.0, 0.0 ) } );

//	System.exit( 0 );

//	if ( System.currentTimeMillis() != 0 ) {
//
//	    return;
//
//	}

        // Two unit circles both centered at the origin.
        // These yield an infinite number of intersections.

        doRotations( uc, uc.scale( 1.0 ), null );

        // A unit circle at the origin and another unit circle on the X-axis at the point where the two circles
        // intersect at [sinDegrees(45),cosDegrees(45)] and [sinDegrees(45),-cosDegrees(45)].

        doRotations(
                uc,
                uc.moveRelative( 2 * Util.sinDegrees( 45 ), 0.0 ),
                new MyPoint[]{
                        new MyPoint( Util.sinDegrees( 45 ), -Util.cosDegrees( 45 ) ),
                        new MyPoint( Util.sinDegrees( 45 ), Util.cosDegrees( 45 ) )
                }
        );

        // Two unit circles that are easily far enough apart that they do not intersect.

        doRotations( uc, uc.moveRelative( 0, 3 ), new MyPoint[0] );

        // A unit circle at the origin with a half-size unit circle close enough to the origin that it is entirely within
        // the first unit circle (no points intersect).

        doRotations( uc, uc.scale( 0.5 ).moveRelative( 0.25, 0 ), new MyPoint[0] );

        // A series of circles which alternate between exactly touching at [1,0] and not quite touching at [1,0].
        // The first circle in each pair is a unit circle at the origin.
        // The second circle in each pair is on the inside of the unit circle at the origin.
        // All circles are on the X-axis (we rely on the rotations in doRotations() to test the software at various angles).

        for ( double offset : new double[]{ 0.01, 0.1, 0.5, 0.9, 0.99 } ) {

            doRotations( uc, uc.scale( offset ).moveRelative( 1.0 - offset, 0 ), new MyPoint[]{ new MyPoint( 1, 0 ) } );

            doRotations( uc, uc.scale( offset ).moveRelative( ( 1.0 - offset ) * 0.999, 0 ), new MyPoint[0] );

        }

        // A series of circles which alternate between exactly touching at [1,0] and not quite touching at [1,0].
        // The first circle in each pair is a unit circle at the origin.
        // The second circle in each pair is to the right of the unit circle at the origin (some touch, some don't quite touch).
        // All circles are on the X-axis (we rely on the rotations in doRotations() to test the software at various angles).

        for ( double offset : new double[]{ 1.01, 1.1, Math.E, 375.8893483 } ) {

            doRotations( uc, uc.scale( offset ).moveRelative( -1.0 - offset, 0 ), new MyPoint[]{ new MyPoint( -1, 0 ) } );

            doRotations( uc, uc.scale( offset ).moveRelative( ( -1.0 - offset ) * 1.001, 0 ), new MyPoint[0] );

        }

        Logger.logMsg( "###3 " + s_trialCount + " trials, " + s_errorCount + " error" + ( s_errorCount == 1 ? "" : "s" ) );

    }

    /**
     Rotate a pair of circles around the origin at various angles which are intended to ensure that the software works regardless of
     the orientation of the pair of circles in space.

     @param circle1              the first circle.
     @param circle2              the second circle.
     @param correctIntersections an array of where they intersect (null if they are identical circles with identical locations such that they
     intersect at an infinite number of points).
     */

    private static void doRotations( @NotNull final Circle circle1, @NotNull final Circle circle2, @Nullable final MyPoint[] correctIntersections ) {

        double[] rotationsTheta = { 0, .3467834678, 0.5 * Math.PI, Math.PI, 1.5 * Math.PI, 2.0 * Math.PI, 17.777 * Math.PI };
        for ( double theta : rotationsTheta ) {

            doMoves( circle1.rotateTheta( theta ), circle2.rotateTheta( theta ), rotateIntersectionsTheta( correctIntersections, theta ) );

        }

    }

    /**
     Move a pair of circles to a variety of locations to ensure that the software works regardless of where the circles are located.

     @param circle1              the first circle.
     @param circle2              the second circle.
     @param correctIntersections an array of where they intersect (null if they are identical circles with identical locations such that they
     intersect at an infinite number of points).
     */

    private static void doMoves( @NotNull final Circle circle1, @NotNull final Circle circle2, @Nullable final MyPoint[] correctIntersections ) {

        double[] adjustments = { 0, Math.sqrt( 2 ), Math.sqrt( 3 ), Math.PI };

        for ( double adj1 : adjustments ) {

            for ( int sign1 = -1; sign1 <= 1; sign1 += 2 ) {

                double adjX = adj1 * sign1;

                for ( double adj2 : adjustments ) {

                    for ( int sign2 = -1; sign2 <= 1; sign2 += 2 ) {

                        double adjY = adj2 * sign2;

                        doOneSet(
                                circle1.moveRelative( adjX, adjY ),
                                circle2.moveRelative( adjX, adjY ),
                                moveIntersections( correctIntersections, adjX, adjY )
                        );

                    }

                }
            }

        }

    }

    /**
     Produce an adjusted copy of the array of correct intersection points which accounts for the circles having been moved some
     combination of left/right and/or up/down.

     @param correctIntersections an array of where they intersect (null if they are identical circles with identical locations such that they
     intersect at an infinite number of points).
     @param adjX                 the X adjustment (positive to the right, negative to the left).
     @param adjY                 the Y adjustment (positive upwards, negative downwards).
     @return the adjusted array.
     */

    private static MyPoint[] moveIntersections( @Nullable final MyPoint[] correctIntersections, final double adjX, final double adjY ) {

        if ( correctIntersections == null ) {

            return null;

        }

        MyPoint[] rval = new MyPoint[correctIntersections.length];
        for ( int ix = 0; ix < rval.length; ix += 1 ) {

            rval[ix] = correctIntersections[ix].move( adjX, adjY );

        }

        return rval;

    }

    /**
     Produce an adjusted copy of the array of correct intersection points which has all of the points rotated around the origin by a
     specified angle in degrees.

     @param correctIntersections an array of where they intersect (null if they are identical circles with identical locations such that they
     intersect at an infinite number of points).
     @param degrees              the amount to rotate the points around the origin in degrees (positive rotates counter-clockwise, negative rotates clockwise).
     @return the adjusted array.
     */

    private static MyPoint[] rotateIntersectionsDegrees( @Nullable final MyPoint[] correctIntersections, final double degrees ) {

        return rotateIntersectionsTheta( correctIntersections, Math.PI * degrees / 180 );

    }

    /**
     Produce an adjusted copy of the array of correct intersection points which has all of the points rotated around the origin by a
     specified angle in radians.

     @param correctIntersections an array of where they intersect (null if they are identical circles with identical locations such that they
     intersect at an infinite number of points).
     @param theta                the amount to rotate the points around the origin in radians (positive rotates counter-clockwise, negative rotates clockwise).
     @return the adjusted array.
     */

    private static MyPoint[] rotateIntersectionsTheta( @Nullable final MyPoint[] correctIntersections, final double theta ) {

        if ( correctIntersections == null ) {

            return null;

        }

        MyPoint[] rval = new MyPoint[correctIntersections.length];
        for ( int ix = 0; ix < rval.length; ix += 1 ) {

            rval[ix] = correctIntersections[ix].rotateTheta( theta );

        }

        return rval;

    }

    /**
     Test a single pair of circles.

     @param circle1              the first circle.
     @param circle2              the second circle.
     @param correctIntersections where they should intersect (null if the two circles are the same size and location such that they
     intersect at an infinite number of points).
     */

    private static void doOneSet( @NotNull final Circle circle1, @NotNull final Circle circle2, @Nullable final MyPoint[] correctIntersections ) {

        validate( circle1, circle2, circle1.computeCircleCircleIntersection( circle2, true ), correctIntersections );

    }

    private static void validate(
            @NotNull final Circle circle1,
            @NotNull final Circle circle2,
            @NotNull final IntersectionInfo info,
            @Nullable final MyPoint[] correctIntersections
    ) {

        if ( !info.infiniteIntersections() &&
             info.getCount() == 2 &&
             Util.nearlyEqual( info.getPoints()[0].x, info.getPoints()[1].x, 1e-5 ) &&
             Util.nearlyEqual( info.getPoints()[0].y, info.getPoints()[1].y, 1e-5 ) ) {

            validate(
                    circle1,
                    circle2,
                    new IntersectionInfo(
                            new Point2D.Double[]{
                                    new Point2D.Double(
                                            ( info.getPoints()[0].x + info.getPoints()[1].x ) / 2,
                                            ( info.getPoints()[0].y + info.getPoints()[1].y ) / 2
                                    )
                            }
                    ),
                    correctIntersections
            );

            return;

        }

        s_trialCount += 1;

        if ( correctIntersections == null ) {

            if ( info.infiniteIntersections() ) {

                Logger.logMsg( "    " + circle1 + " correctly determined to be exactly equal to " + circle2 );

            } else {

                s_errorCount += 1;
                Logger.logMsg(
                        "*** " +
                        circle1 +
                        " is exactly equal to " +
                        circle2 +
                        " but we found " +
                        info.getCount() +
                        " intersection" +
                        ( info.getCount() == 1 ? "" : "s: " + info )
                );

            }

            return;

        } else {

            if ( info.infiniteIntersections() ) {

                s_errorCount += 1;
                Logger.logMsg( "*** " + circle1 + " is not exactly equal to " + circle2 + " but we concluded that it was" );

                return;

            }

            if ( correctIntersections.length != info.getCount() ) {

                s_errorCount += 1;
                Logger.logMsg(
                        "*** " + circle1 + " intersects " + circle2 + " " + correctIntersections.length +
                        " time" + ( correctIntersections.length == 1 ? "" : "s" ) +
                        " at " + Arrays.toString( correctIntersections ) + " but we found " +
                        info.getCount() + " intersection" + ( info.getCount() == 1 ? "" : "s" ) +
                        " at " + Arrays.toString( info.getPoints() )
                );

            } else {

                switch ( info.getCount() ) {

                    case 0:

                        Logger.logMsg( "    we correctly determined that " + circle1 + " does not intersect " + circle2 );

                        return;

                    case 1:

                        if ( correctIntersections[0].nearlyEquals( info.getPoints()[0] ) ) {

                            Logger.logMsg(
                                    "    we correctly determined that " +
                                    circle1 +
                                    " intersects " +
                                    circle2 +
                                    " once at " +
                                    correctIntersections[0]
                            );

                        } else {

                            s_errorCount += 1;
                            Logger.logMsg(
                                    "*** " +
                                    circle1 +
                                    " intersects " +
                                    circle2 +
                                    " at " +
                                    correctIntersections[0] +
                                    " but we found that it intersects at " +
                                    info.getPoints()[0]
                            );

                        }

                        return;

                    case 2:

                        if ( correctIntersections[0].nearlyEquals( info.getPoints()[0] ) &&
                             correctIntersections[1].nearlyEquals( info.getPoints()[1] ) ) {

                            Logger.logMsg(
                                    "    we correctly determined that " +
                                    circle1 +
                                    " intersects " +
                                    circle2 +
                                    " at " +
                                    info.getPoints()[0] +
                                    " and at " +
                                    info.getPoints()[1]
                            );

                        } else if ( correctIntersections[1].nearlyEquals( info.getPoints()[0] ) &&
                                    correctIntersections[0].nearlyEquals( info.getPoints()[1] ) ) {

                            Logger.logMsg(
                                    "    we correctly except for order determined that " +
                                    circle1 +
                                    " intersects " +
                                    circle2 +
                                    " at " +
                                    info.getPoints()[0] +
                                    " and at " +
                                    info.getPoints()[1]
                            );

                        } else {

                            s_errorCount += 1;
                            Logger.logMsg(
                                    "*** we determined that " +
                                    circle1 +
                                    " intersects " +
                                    circle2 +
                                    " at " +
                                    info.getPoints()[0] +
                                    " and at " +
                                    info.getPoints()[1] +
                                    " but they actually intersect at " +
                                    correctIntersections[0] +
                                    " and at " +
                                    correctIntersections[1]
                            );

                        }

                        return;

                    default:

                        s_errorCount += 1;
                        Logger.logMsg(
                                "??? c1=" +
                                circle1 +
                                ", c2=" +
                                circle2 +
                                ", correct=" +
                                correctIntersections.length +
                                "@" +
                                Arrays.toString( correctIntersections ) +
                                ", computed=" +
                                info.getCount() +
                                "@" +
                                Arrays.toString( info.getPoints() )
                        );

                        return;

                }

            }

        }

    }

}
