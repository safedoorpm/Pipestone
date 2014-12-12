/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.ObtuseUtil;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractCurveSequence implements GraphicsElement {

    private LinkedList<AbstractCurveListener> _listeners = new LinkedList<AbstractCurveListener>();

    private final List<Point2D> _points = new Vector<Point2D>();

    private GeneralPath _linesPath = null;

    private double _scale = 3.0;

    private double _strength = 10.0;

    private Rectangle2D _boundingBox = null;

    private final GraphicsElement _parent;

    /**
     * Something that wants to be informed when an {@link AbstractCurveSequence} instance changes.
     */

    public interface AbstractCurveListener {

        enum ChangeType { POINT_ADDED, POINT_REMOVED, POINT_MOVED, SCALE_CHANGED, STRENGTH_CHANGED }

        void lineChanged( ChangeType changeType );

    }

    protected AbstractCurveSequence( GraphicsElement parent ) {
        super();

        _parent = parent;

    }

    public GraphicsElement getParentElement() {

        return _parent;

    }

    public final void flushCachedData() {

        _boundingBox = null;
        _linesPath = null;

        flushTypeSpecificCachedData();

    }

    public abstract PointOnGraphicsElement findNearestPoint( Point2D p );

    protected abstract void flushTypeSpecificCachedData();

    public void clear() {

        _points.clear();
        fireChangeListeners( AbstractCurveListener.ChangeType.POINT_REMOVED );
        flushCachedData();

    }

    public List<Point2D> getPoints() {

        return Collections.unmodifiableList( _points );

    }

    public final Rectangle2D getBounds() {

        if ( _boundingBox == null ) {

            _boundingBox = computeBoundingBox();

        }

        return _boundingBox;

    }

    protected abstract Rectangle2D computeBoundingBox();

    public abstract double getSquaredDistanceToNearestPointOnCurve( Point2D p );

    protected abstract GeneralPath getAutoCurveOrControlsPath( boolean getCurve );

    public int getPointCount() {

        return _points.size();

    }

    public Point2D getPoint( int ix ) {

        return _points.get( ix );

    }

    /**
     * Get the curve as a {@link java.awt.geom.GeneralPath}.
     * @return The curve as a {@link java.awt.geom.GeneralPath}.
     */

    public GeneralPath getAutoCurvePath() {

        return getAutoCurveOrControlsPath( true );

    }

    /**
     * Get the curve as a sequence of Bezier control bars/handles.
     * @return The curve as a sequence of Bezier control bars.
     */

    public GeneralPath getAutoControlsPath() {

        return getAutoCurveOrControlsPath(false );

    }


    /**
     * Tell anyone who cares that something has changed.
     *
     * @param changeType what has changed.
     */

    protected void fireChangeListeners( AbstractCurveListener.ChangeType changeType ) {

        for ( AbstractCurveListener listener : getChangeListeners() ) {

            listener.lineChanged( changeType );

        }

    }

    public void addChangeListener( AbstractCurveListener listener ) {

        _listeners.add( listener );

    }

    public void removeChangeListener( AbstractCurveListener listener ) {

        _listeners.remove( listener );

    }

    public AbstractCurveListener[] getChangeListeners() {

        return _listeners.toArray( new AbstractCurveListener[_listeners.size()] );

    }

    /**
     * Add a point to the end of the line.
     * @param point the point to be removed.
     * Note that adding the same {@link java.awt.Point} object more than once to the same {@link BezierCurveSequence} will
     * affect how {@link #removePoint(java.awt.geom.Point2D)} behaves in potentially quite mysterious ways.
     */

    public void addPoint( Point2D point ) {

        _points.add( point );

        flushCachedData();

        fireChangeListeners( AbstractCurveListener.ChangeType.POINT_ADDED );

    }

    /**
     * Remove the first occurrence of the specified {@link java.awt.Point} from the line.
     * <p/>
     * Any registered {@link AbstractCurveSequence.AbstractCurveListener} are notified if the call actually results in a point
     * being removed (i.e. registered {@link AbstractCurveSequence.AbstractCurveListener}s are notified if and only if the call eventually returns true).
     * @param point the point to be removed.
     * Note that the actual {@link java.awt.Point} object originally passed to {@link #addPoint} must be specified.
     * @return true if the specified {@link java.awt.Point} object was found and removed; false otherwise.
     */

    public boolean removePoint( Point2D point ) {

        if ( _points.contains( point ) ) {

            _points.remove( point );

            flushCachedData();

            fireChangeListeners( AbstractCurveListener.ChangeType.POINT_REMOVED );

            return true;

        } else {

            return false;

        }

    }

    /**
     * Get the curved line as a sequence of connected straight line segments.
     * @return the curved line as a sequence of connected straight line segments.
     */

    public GeneralPath getLinePath() {

        if ( _linesPath == null ) {

            GeneralPath path = new GeneralPath();
            boolean started = false;

            for ( Point2D p : _points ) {

                if ( started ) {

                    path.lineTo( (float)( _scale * p.getX() ), (float)( _scale * p.getY() ) );

                } else {

                    path.moveTo( (float)( _scale * p.getX() ), (float)( _scale * p.getY() ) );
                    started = true;

                }

            }

            _linesPath = path;

        }

        return new GeneralPath( _linesPath );

    }

    protected String fmtPoint( Point2D p ) {

        return "[" + ObtuseUtil.lpad( p.getX(), 0, 2 ) + ',' + ObtuseUtil.lpad( p.getY(), 0, 2 ) + "]";

    }

    protected String fmtPoints( Point2D[] points ) {

        StringBuilder desc = new StringBuilder();

        desc.append( "{" );

        for ( Point2D p : points ) {

            desc.append( " (" ).append( ObtuseUtil.lpad( p.getX(), 0, 2 ) ).append( ',' ).append( ObtuseUtil.lpad( p.getY(), 0, 2 ) ).append( ")," );

        }

        desc.append( " } )" );

        return desc.toString();

    }

    /**
     * Go through the vector of points describing our line discarding points to avoid having two consecutive identical points.
     * @param scale amount to scale the line by as we go through it (no idea why this method supports this feature).
     * @return the scaled sequence of points without consecutive duplicates.
     */

    protected Vector<Point2D> getDistinctPoints( double scale ) {

        Vector<Point2D> rval = new Vector<Point2D>();
        Point2D lastPoint = null;
        int ix = 0;
        for ( Point2D point : getPoints() ) {

            //noinspection FloatingPointEquality,StatementWithEmptyBody
            if ( lastPoint == null || lastPoint.getX() != point.getX() || lastPoint.getY() != point.getY() ) {

//                Logger.logMsg( "keeping  p[" + ix + "] = (" + point.getX() + "," + point.getY() + ")" );
                rval.add( new Point2D.Float( (float)( point.getX() * scale ), (float)( point.getY() * scale ) ) );
                lastPoint = point;

            } else {

//                Logger.logMsg( "ignoring p[" + ix + "] = (" + lastPoint.getX() + "," + lastPoint.getY() + ")" );

            }

            ix += 1;

        }

        return rval;

    }

    public void setScale( double scale ) {

        _scale = scale;

        if ( _scale != scale ) {

            flushCachedData();

            fireChangeListeners( AbstractCurveListener.ChangeType.SCALE_CHANGED );

        }

    }

    public double getScale() {

        return _scale;

    }

    /**
     * Get a {@link java.awt.geom.GeneralPath} with cross marks at the locations of each point in this curve.
     * @param crossSize size of each cross mark.
     * @return the curve as cross marks.
     */

    public GeneralPath getMarkedPoints( float crossSize ) {

        GeneralPath path = new GeneralPath();

        @SuppressWarnings( { "MagicNumber" } )
        float crossSizeD2 = crossSize / 2.0f;

        for ( Point2D p : getDistinctPoints( _scale ) ) {

            path.moveTo( (float)( _scale * p.getX() ) - crossSizeD2, (float)( _scale * p.getY() ) - crossSizeD2 );
            path.lineTo( (float)( _scale * p.getX() ) + crossSizeD2, (float)( _scale * p.getY() ) + crossSizeD2 );

            path.moveTo( (float)( _scale * p.getX() ) + crossSizeD2, (float)( _scale * p.getY() ) - crossSizeD2 );
            path.lineTo( (float)( _scale * p.getX() ) - crossSizeD2, (float)( _scale * p.getY() ) + crossSizeD2 );

        }

        return path;

    }

    public void setStrength( double strength ) {

        //noinspection FloatingPointEquality
        if ( strength != _strength ) {

            _strength = strength;

            fireChangeListeners( AbstractCurveListener.ChangeType.STRENGTH_CHANGED );
            flushCachedData();

        }

    }

    public double getStrength() {

        return _strength;

    }

}
