/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import hep.wired.heprep.util.NearestPoint;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({ "UnusedDeclaration", "UnnecessaryParentheses" })
public class BezierCurveSequence extends AbstractCurveSequence {

    private List<CubicBezierCurve> _curves = null;

    private GeneralPath _curvesPath = null;
    private GeneralPath _controlsPath = null;

    private double _cachedStrength = -1.0;

    public class PointOnCurve implements PointOnGraphicsElement {

        private int _curveIndex;
        private CubicBezierCurve _cubicBezierCurve;
        private Point2D _point;

        public PointOnCurve( final CubicBezierCurve cubicBezierCurve, final int curveIndex, final Point2D point ) {

            super();

            _curveIndex = curveIndex;
            _cubicBezierCurve = cubicBezierCurve;
            _point = point;

        }

        public CubicBezierCurve getGraphicsElement() {

            return _cubicBezierCurve;

        }

        public Point2D getPoint() {

            return _point;

        }

    }

    /**
     Create an empty curved line.
     */

    public BezierCurveSequence( final GraphicsElement parent ) {

        super( parent );

        flushCachedData();
    }

    protected void flushTypeSpecificCachedData() {

        _curves = null;
        _curvesPath = null;
        _controlsPath = null;
        _cachedStrength = -1.0;

    }

    protected Rectangle2D computeBoundingBox() {

        getCubicCurves();

        Rectangle2D bounds = null;
        for ( CubicBezierCurve curve : _curves ) {

            if ( bounds == null ) {

                bounds = curve.getBounds2D();

            } else {

                bounds.add( curve.getBounds2D() );

            }

        }

        if ( bounds == null ) {

            bounds = new Rectangle2D.Double();

        }

        return bounds;

    }

    /**
     Find the nearest point on one of the curves and return both the point and the curve.

     @param p the point from which we must find the shortest line to the curve from.
     @return the closest point to p (null if the curve sequence is empty).
     */

    public PointOnCurve findNearestPoint( final Point2D p ) {

        getCubicCurves();

        PointOnCurve rval = null;
        double bestDistance = -1.0;
        for ( int i = 0; i < _curves.size(); i += 1 ) {

            Point2D np = new Point2D.Float();
            double distance = NearestPoint.onCurve( _curves.get( i ), p, np );

            if ( bestDistance < 0.0 || distance < bestDistance ) {

                rval = new PointOnCurve( _curves.get( i ), i, np );
                bestDistance = distance;

            }

        }

        return rval;

    }

    public double getSquaredDistanceToNearestPointOnCurve( final Point2D p ) {

        PointOnCurve nearestPointOnCurve = findNearestPoint( p );

        if ( nearestPointOnCurve == null ) {

            //noinspection MagicNumber
            return 1.0e30;

        } else {

            return nearestPointOnCurve.getPoint().distanceSq( p.getX(), p.getY() );

        }

    }

    public List<CubicBezierCurve> getCubicCurves() {

        if ( _curves == null ) {

            flushCachedData();
            getAutoCurvePath();

        }

        return _curves;

    }

    /**
     Construct a sequence of cubic Bezier curves using the current sequence of points.
     <p/>
     This method computes the parameters of a sequence of smoothly connected Bezier
     curves using this instance's current sequence of points.  The method then returns
     a {@link java.awt.geom.GeneralPath} which contains either a sequence of
     connected Bezier curves or a sequence of Bezier curve control bars (the lines with
     handles on them that many user interfaces provide to allow the user to control the
     shape of individual Bezier curves) as specified by this method's <tt>getCurve</tt>
     parameter.
     This method actually computes both the curves
     and the control bars because it is not possible to describe the curves without the
     control bars.

     @param getCurve true if the curve is to be returned; false if the control bars are to be returned.
     @return a shape describing either the curve or the control bars.
     */

    protected GeneralPath getAutoCurveOrControlsPath( final boolean getCurve ) {

        //noinspection FloatingPointEquality
        if ( getCurve && _curvesPath != null ) {

            return new GeneralPath( _curvesPath );

        }

        //noinspection FloatingPointEquality
        if ( !getCurve && _controlsPath != null ) {

            return new GeneralPath( _controlsPath );

        }

        _curves = new LinkedList<>();

        _curvesPath = new GeneralPath();
        _controlsPath = new GeneralPath();

        _cachedStrength = getStrength();

        Vector<Point2D> pointsVector = getDistinctPoints( getScale() );

        Point2D[] points = pointsVector.toArray( new Point2D[0] );

        if ( points.length <= 1 ) {

            return getCurve ? _curvesPath : _controlsPath;

        }

        Point2D originPoint = points[0];

        Point2D previousPoint = originPoint;
        double prevPointX = previousPoint.getX();
        double prevPointY = previousPoint.getY();

        _curvesPath.moveTo( (float)originPoint.getX(), (float)originPoint.getY() );

        _controlsPath.moveTo( prevPointX - getStrength(), prevPointY - 0.0 );
        _controlsPath.lineTo( prevPointX + getStrength(), prevPointY + 0.0 );

        double controlSlopeX = getStrength();
        double controlSlopeY = 0.0;

        for ( int ix = 1; ix < points.length; ix += 1 ) {

            double thisPointX = points[ix].getX();
            double thisPointY = points[ix].getY();

            double unscaledNextControlSlopeX;
            double unscaledNextControlSlopeY;

            if ( ix == points.length - 1 ) {

                unscaledNextControlSlopeX = getStrength();
                unscaledNextControlSlopeY = 0.0;

            } else {

                double nextPointX = points[ix + 1].getX();
                double nextPointY = points[ix + 1].getY();
                unscaledNextControlSlopeX = nextPointX - prevPointX;
                unscaledNextControlSlopeY = nextPointY - prevPointY;

            }

            // If the previous point is the same as the next point then we need to take a different approach.

            if ( unscaledNextControlSlopeX == 0.0 && unscaledNextControlSlopeY == 0.0 ) {

                // Compute a line from the next/prev point and the current point and rotate it 90 degrees.

                // Don't know which direction to rotate it in.

                throw new IllegalArgumentException( "curve has duplicate points even after duplicates were supposedly stripped out (original is " +
                                                    getPoints() +
                                                    ", stripped is " +
                                                    fmtPoints( points ) +
                                                    ")" );

            }

            double unscaledLength = Math.sqrt(
                    (
                            unscaledNextControlSlopeX * unscaledNextControlSlopeX +
                            unscaledNextControlSlopeY * unscaledNextControlSlopeY
                    )
            );

            double nextControlSlopeX = unscaledNextControlSlopeX * ( getStrength() / unscaledLength );
            double nextControlSlopeY = unscaledNextControlSlopeY * ( getStrength() / unscaledLength );

            double c1x = prevPointX + controlSlopeX;
            double c1y = prevPointY + controlSlopeY;
            double c2x = thisPointX - nextControlSlopeX;
            double c2y = thisPointY - nextControlSlopeY;

            _curves.add( new CubicBezierCurve(
                    this,
                    new CubicCurve2D.Double( prevPointX, prevPointY, c1x, c1y, c2x, c2y, thisPointX, thisPointY )
            ) );

            _curvesPath.curveTo( c1x, c1y, c2x, c2y, thisPointX, thisPointY );

            _controlsPath.moveTo( thisPointX - nextControlSlopeX, thisPointY - nextControlSlopeY );
            _controlsPath.lineTo( thisPointX + nextControlSlopeX, thisPointY + nextControlSlopeY );

            controlSlopeX = nextControlSlopeX;
            controlSlopeY = nextControlSlopeY;
            previousPoint = points[ix];
            prevPointX = previousPoint.getX();
            prevPointY = previousPoint.getY();

        }

        return getCurve ? _curvesPath : _controlsPath;

    }

    /**
     Find the intersection between two lines.

     @param a1 a point on the first line.
     @param a2 a different point on the first line.
     @param b1 a point on the second line.
     @param b2 a different point on the second line.
     @return the intersection between the two lines or null if they do not intersect.
     */
    private static Point2D findIntersection(
            final Point2D a1, final Point2D a2, final Point2D b1, final Point2D b2
    ) {

        // Parametric equation for first line is:
        // point = a1 + t( a2 - a1 )
        //
        // Parametric equation for second line is:
        // point = b1 + s( b2 - b1 )
        //
        // Setting the points equal, we get
        // a1 + t( a2 - a1 ) == b1 + s( b2 - b1 )
        //
        // Breaking into two equations (one for x, one for y) we get
        // a1.x + t( a2.x - a1.x ) == b1.x + s( b2.x - b1.x )
        // and
        // a1.y + t( a2.y - a1.y ) == b1.y + s( b2.y - b1.y )
        //
        // Moving the constants to the right hand side, we get
        // t( a2.x - a1.x ) - s( b2.x - b1.x ) == b1.x - a1.x
        // t( a2.y - a1.y ) - s( b2.y - b1.y ) == b1.y - a1.y
        //
        // Multiplying first equation by ( b2.y - b1.y ) and the second equation by -( b2.x - b1.x ), we get
        // t( a2.x - a1.x ) * ( b2.y - b1.y ) - s( b2.x - b1.x ) * ( b2.y - b1.y ) = ( b1.x - a1.x ) * ( b2.y - b1.y )
        // t( a2.y - a1.y ) * -( b2.x - b1.x ) - s( b2.y - b1.y ) * -( b2.x - b1.x ) = ( b1.y - a1.y ) * -( b2.x - b1.x )
        //
        // Adding the equations together we get
        // t( a2.x - a1.x ) * ( b2.y - b1.y ) - t( a2y - a1.y ) * ( b2.x - b1.x ) = ( ( b1.x - a1.x ) * ( b2.y - b1.y ) - ( b1.y - a1.y ) * ( b2.x - b1.x )
        //
        // Applying the distributed law of multiplication over addition to the left hand side we get
        // t( ( a2.x - a1.x ) * ( b2.y - b1.y ) - ( a2y - a1.y ) * ( b2.x - b1.x ) ) = ( ( b1.x - a1.x ) * ( b2.y - b1.y ) - ( b1.y - a1.y ) * ( b2.x - b1.x )
        //
        // Dividing both sides by what is in the parentheses on the left hand side, we get
        // t = ( ( b1.x - a1.x ) * ( b2.y - b1.y ) - ( b1.y - a1.y ) * ( b2.x - b1.x ) ) / ( ( a2.x - a1.x ) * ( b2.y - b1.y ) - ( a2.y - a1.y ) * ( b2.x - b1.x ) )
        //
        // If the denominator is zero then there is no intersection.
        // Otherwise, the intersection is at the just computed value of t.

        // Now we'll actually do that

        //noinspection MagicNumber
        if ( Math.abs( ( a2.getX() - a1.getX() ) * ( b2.getY() - b1.getY() ) - ( a2.getY() - a1.getY() ) * ( b2.getX() - b1.getX() ) ) < 0.0000001 ) {

            return null;

        } else {

            double t = (
                               ( b1.getX() - a1.getX() ) * ( b2.getY() - b1.getY() ) -
                               ( b1.getY() - a1.getY() ) * ( b2.getX() - b1.getX() )
                       ) / (
                               ( a2.getX() - a1.getX() ) * ( b2.getY() - b1.getY() ) -
                               ( a2.getY() - a1.getY() ) * ( b2.getX() - b1.getX() )
                       );
            double x = a1.getX() + t * ( a2.getX() - a1.getX() );
            double y = a1.getY() + t * ( a2.getY() - a1.getY() );

            return new Point2D.Double( x, y );

        }

    }

    @SuppressWarnings({ "UseOfSystemOutOrSystemErr" })
    public BezierCurveSequence getOffsetCurve( final float offset ) {

        BezierCurveSequence rval = new BezierCurveSequence( null );
        rval.setScale( getScale() );

        Vector<Point2D> pointsVector = getDistinctPoints( 1.0 );

        Point2D[] points = pointsVector.toArray( new Point2D[0] );

        for ( int ix = 0; ix < points.length; ix += 1 ) {

            Point2D previousPoint, nextPoint;
            if ( ix == 0 ) {

                System.out.print( "***" );
                previousPoint = new Point2D.Float( 0.0f, 0.0f );
                nextPoint = new Point2D.Float( 1.0f, 0.0f );

            } else if ( ix == points.length - 1 ) {

                previousPoint = new Point2D.Float( 0.0f, 0.0f );
                nextPoint = new Point2D.Float( 1.0f, 0.0f );

            } else {

                previousPoint = points[ix - 1];
                nextPoint = points[ix + 1];

            }

            Point2D thisPoint = points[ix];

            float unscaledNextControlSlopeX = (float)nextPoint.getX() - (float)previousPoint.getX();
            float unscaledNextControlSlopeY = (float)nextPoint.getY() - (float)previousPoint.getY();

            float unscaledLength = (float)Math.sqrt(
                    (double)(
                            unscaledNextControlSlopeX * unscaledNextControlSlopeX +
                            unscaledNextControlSlopeY * unscaledNextControlSlopeY
                    )
            );

            float nextControlSlopeX = unscaledNextControlSlopeX * ( offset / unscaledLength );
            float nextControlSlopeY = unscaledNextControlSlopeY * ( offset / unscaledLength );

            Point2D rotatedPoint = rotatePoint90( nextControlSlopeX, nextControlSlopeY );

            Point2D offsetPoint =
                    new Point2D.Float( (float)thisPoint.getX() + (float)rotatedPoint.getX(), (float)thisPoint.getY() + (float)rotatedPoint.getY() );
            if ( ix == 1 ) {
                Logger.logMsg( "at offset " +
                               ObtuseUtil.lpad( offset, 0, 2 ) +
                               " point " +
                               fmtPoint( points[0] ) +
                               " rotated using " +
                               fmtPoint( new Point2D.Float( unscaledNextControlSlopeX, unscaledNextControlSlopeY ) ) +
                               " / " +
                               fmtPoint( new Point2D.Float( nextControlSlopeX, nextControlSlopeY ) ) +
                               " and " +
                               fmtPoint( rotatedPoint ) +
                               " to " +
                               fmtPoint( offsetPoint ) );
            }

            rval.addPoint( offsetPoint );

        }

        return rval;

    }

    static {

        for ( Point2D p : new Point2D[]{
                new Point2D.Float( 1.0f, 0.0f ),
                new Point2D.Float( 0.0f, 1.0f ),
                new Point2D.Float( -1.0f, 0.0f ),
                new Point2D.Float( 0.0f, -1.0f )
        } ) {

            //noinspection ObjectToString
            Logger.logMsg( "point " + p + " rotates to " + rotatePoint90( (float)p.getX(), (float)p.getY() ) );

        }

    }

    private static Point2D rotatePoint90( final float x, final float y ) {

        return new Point2D.Float( -y, x );

    }

    public String toString() {

        StringBuilder desc = new StringBuilder();

        desc.append( "CurvedLine( points = {" );

        for ( Point2D p : getPoints() ) {

            desc.append( " (" ).append( ObtuseUtil.lpad( p.getX(), 0, 2 ) ).append( ',' ).append( ObtuseUtil.lpad( p.getY(), 0, 2 ) ).append( ")," );

        }

        desc.append( " } )" );

        return desc.toString();

    }

}