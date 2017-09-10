/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.ui.layout.ComponentSizeRequirements;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 Encapsulate the cached sizing data about our target container.
 */

public class LinearCache3 implements LayoutImplCache {

//    private final boolean _trace;
//
//    private final LinearOrientation _orientation;
//
//    private final LinearContainer2 _container;

    private final ComponentSizeRequirements[] _breadthSizes;
    private final ComponentSizeRequirements[] _lengthSizes;

    private final SizeRequirements _breadthTotal;

    private final SizeRequirements _lengthTotal;

    private final LinearLayoutManager3 _linearLayoutManager;

    private final LinearContainer3 _target;

    private int[] _xOffsets;

    private int[] _xSpans;

    private int[] _yOffsets;

    private int[] _ySpans;

    private boolean[] _trackParentBreadths;

//    private Insets _insets;

    public LinearCache3(
            LinearLayoutManager3 linearLayoutManager,
            LinearContainer3 target,
            @NotNull Hashtable<Component, LinearLayoutManager3.ConstraintsTable> allConstraints
    ) {

        super();

        _linearLayoutManager = linearLayoutManager;

        _target = target;

        boolean tracingContainer = LinearLayoutUtil.isContainerOnWatchlist( target );
        //noinspection EmptyFinallyBlock
        try {

            Vector<Component> visibleComponents = new Vector<>();
            Arrays.stream( target.getComponents() )
                  .filter( Component::isVisible )
                  .filter(
                          component -> {

                              if ( LinearLayoutUtil.isContainerOnWatchlist( target ) ) {

                                  LinearLayoutUtil.describeGuiEntity( 0, component, false, false );

                              }

                              return true;

                          }
                  )
                  .forEach( visibleComponents::add );

            if ( LinearLayoutUtil.isContainerOnWatchlist( target ) ) {

                ObtuseUtil.doNothing();

            }

            int n = visibleComponents.size();

            java.util.List<ComponentSizeRequirements> xSizes = new LinkedList<>();
            java.util.List<ComponentSizeRequirements> ySizes = new LinkedList<>();
            java.util.List<Boolean> trackParentBreadth = new LinkedList<>();

            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            LinearLayoutManager3.ConstraintsTable emptyConstraintsTable = new LinearLayoutManager3.ConstraintsTable();
            LinearLayoutManager3.SimpleConstraint disabledTrackParentsBreadthConstraint =
                    new LinearLayoutManager3.SimpleConstraint( LinearLayoutManager3.TRACK_PARENTS_BREADTH_CONSTRAINT_TITLE, false );

            // Find the space sponge (where extra length space comes from).
            // If there's more than one, we'll use the last one (we'll get extra space on a shared basis some day).

            boolean hasSpaceSponge = false;
            for ( Component c : visibleComponents ) {

                if ( c instanceof LinearLayoutUtil.SpaceSponge ) {

                    hasSpaceSponge = true;
                    break;

                }

            }

            for ( Component c : visibleComponents ) {

                if ( LinearLayoutUtil.isComponentOnWatchlist( c ) ) {

                    workingOnWatchlistComponent( "collecting sizes", c );

                }

                LinearLayoutManager3.ConstraintsTable componentConstraints = allConstraints.getOrDefault( c, emptyConstraintsTable );
                LinearLayoutManager3.Constraint trackParentsBreadthConstraint = componentConstraints.getOrDefault(
                        LinearLayoutManager3.TRACK_PARENTS_BREADTH_CONSTRAINT_TITLE,
                        disabledTrackParentsBreadthConstraint
                );
                boolean componentTrackParentsBreadth = trackParentsBreadthConstraint.isEnabled();

                if ( componentTrackParentsBreadth ) {

                    Logger.logMsg( "track parent's breadth" );

                }

                Dimension min = c.getMinimumSize();
                Dimension pref = c.getPreferredSize();
                Dimension max = c.getMaximumSize();

                Logger.logMsg( "LC3@" + c + ":  min=" + min + ", pref=" + pref + ", max=" + max );

                if ( componentTrackParentsBreadth ) {

                    Logger.logMsg(
                            "*** track parent's breadth:  " +
                            ObtuseUtil.fDim( "min", min ) + ", " +
                            ObtuseUtil.fDim( "pref", pref ) + ", " +
                            ObtuseUtil.fDim( "max", max )
                    );

                }

                if ( c instanceof LinearContainer ) {

                    LinearContainer lc = (LinearContainer)c;

                    if ( LinearLayoutUtil.isContainerOnWatchlist( (Container)c ) ) {

                        ObtuseUtil.doNothing();

                    }

                    if ( lc.isVertical() ) {

                        min.width = lc.applyBreadthConstraints( min.width );
                        pref.width = lc.applyBreadthConstraints( pref.width );
                        max.width = lc.applyBreadthConstraints( max.width );

                        min.height = lc.applyLengthConstraints( min.height );
                        pref.height = lc.applyLengthConstraints( pref.height );
                        max.height = lc.applyLengthConstraints( max.height );

                    } else {

                        min.width = lc.applyLengthConstraints( min.width );
                        pref.width = lc.applyLengthConstraints( pref.width );
                        max.width = lc.applyLengthConstraints( max.width );

                        min.height = lc.applyBreadthConstraints( min.height );
                        pref.height = lc.applyBreadthConstraints( pref.height );
                        max.height = lc.applyBreadthConstraints( max.height );

                    }

                }

                trackParentBreadth.add( componentTrackParentsBreadth );

                if ( isHorizontal() ) {

                    if ( componentTrackParentsBreadth && max.height < 32767 ) {

                        max.height = 32767;

                    }

                } else {

                    if ( componentTrackParentsBreadth && max.width < 32767 ) {

                        max.width = 32767;

                    }

                }

                ComponentSizeRequirements xReq = new ComponentSizeRequirements(
                        c,
                        min.width,
                        pref.width,
                        ( isHorizontal() && hasSpaceSponge && !( c instanceof LinearLayoutUtil.SpaceSponge ) ? pref.width : max.width ),
                        0f
                );
                ComponentSizeRequirements yReq = new ComponentSizeRequirements(
                        c,
                        min.height,
                        pref.height,
                        ( isVertical() && hasSpaceSponge && !( c instanceof LinearLayoutUtil.SpaceSponge ) ? pref.height : max.height ),
                        0f
                );

                xSizes.add( xReq );
                ySizes.add( yReq );

            }

            if ( isVertical() ) {

                _breadthSizes = xSizes.toArray( new ComponentSizeRequirements[xSizes.size()] );
                _lengthSizes = ySizes.toArray( new ComponentSizeRequirements[ySizes.size()] );

            } else {

                _breadthSizes = ySizes.toArray( new ComponentSizeRequirements[ySizes.size()] );
                _lengthSizes = xSizes.toArray( new ComponentSizeRequirements[xSizes.size()] );

            }

            _trackParentBreadths = new boolean[trackParentBreadth.size()];
            int ix = 0;
            for ( boolean b : trackParentBreadth ) {

                _trackParentBreadths[ix] = b;
                ix += 1;

            }

            _breadthTotal = ComponentSizeRequirements.getAlignedSizeRequirements( _breadthSizes );
            _lengthTotal = ComponentSizeRequirements.getTiledSizeRequirements( _lengthSizes );
            if ( _lengthTotal.maximum < 32767 ) {

                _lengthTotal.maximum = 32767;

            }

            if ( LinearLayoutUtil.isContainerOnWatchlist( target ) ) {

                boolean forced = ObtuseUtil.never();
//		logIfWatched( "_breadthTotal alignment is " + _breadthTotal.alignment + ", _lengthTotal alignment is " + _lengthTotal.alignment + ( forced ? " both FORCED to 0" : "" ));

                if ( forced ) {

                    _breadthTotal.alignment = 0f;
                    _lengthTotal.alignment = 0f;

                }

            }

        } finally {

//	    if ( tracingContainer ) {
//
//		Logger.popNestingLevel( "inner cache" );
//
//		Logger.popNestingLevel( "cache constructor" );
//
//	    }

        }

    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean logIfWatched( String msg ) {

        if ( isWatched() ) {

            Logger.logMsg( "LinearCache3.logIfWatched:  " + msg );

            return true;

        } else {

            return false;

        }

    }

    public boolean isWatched() {

        return _target.isWatched();

    }

    private void workingOnWatchlistComponent(
            @SuppressWarnings({ "SameParameterValue", "unused" }) String what,
            @SuppressWarnings("unused") Component c
    ) {

//	logIfWatched( "working on " + LinearLayoutUtil.fullName( c ) );

    }

    private String showAlignment( float alignmentX, float alignmentY ) {

        return "alignment( " + alignmentX + ", " + alignmentY + " )";

    }

    @NotNull
    public LinearContainer3 getTarget() {

        return _target;

    }

    public int getVisibleComponentCount() {

        return _breadthSizes.length;

    }

    public Component getVisibleComponent( int ix ) {

        return _breadthSizes[ix].component;

    }

    public LinearOrientation getOrientation() {

        return _linearLayoutManager.getOrientation();

    }

    public boolean isVertical() {

        return getOrientation() == LinearOrientation.VERTICAL;

    }

    public boolean isHorizontal() {

        return getOrientation() == LinearOrientation.HORIZONTAL;

    }

    public Dimension getMinimumSize() {

        Dimension size = isVertical()
                ?
                new Dimension( _breadthTotal.minimum, _lengthTotal.minimum )
                :
                new Dimension( _lengthTotal.minimum, _breadthTotal.minimum );

        addInsets( size );

        size = isVertical()
                ?
                new Dimension( _target.applyBreadthConstraints( size.width ), _target.applyLengthConstraints( size.height ) )
                :
                new Dimension( _target.applyLengthConstraints( size.width ), _target.applyBreadthConstraints( size.height ) );

        return size;

    }

    public Dimension getPreferredSize() {

        Dimension size = isVertical() ?
                new Dimension( _breadthTotal.preferred, _lengthTotal.preferred ) :
                new Dimension( _lengthTotal.preferred, _breadthTotal.preferred );

        addInsets( size );

        size = isVertical()
                ?
                new Dimension( _target.applyBreadthConstraints( size.width ), _target.applyLengthConstraints( size.height ) )
                :
                new Dimension( _target.applyLengthConstraints( size.width ), _target.applyBreadthConstraints( size.height ) );

        return size;

    }

    public Dimension getMaximumSize() {

        Dimension size = isVertical() ?
                new Dimension( _breadthTotal.maximum, _lengthTotal.maximum ) :
                new Dimension( _lengthTotal.maximum, _breadthTotal.maximum );

        addInsets( size );

        size = isVertical()
                ?
                new Dimension( _target.applyBreadthConstraints( size.width ), _target.applyLengthConstraints( size.height ) )
                :
                new Dimension( _target.applyLengthConstraints( size.width ), _target.applyBreadthConstraints( size.height ) );

        return size;

    }

//    public Dimension getMinimumSize() {
//
//	Dimension size = isVertical() ? new Dimension( _breadthTotal.minimum, _lengthTotal.minimum ) : new Dimension( _lengthTotal.minimum, _breadthTotal.minimum );
//
//	addInsets( size );
//
//	return size;
//
//    }
//
//    public Dimension getPreferredSize() {
//
//	Dimension size = isVertical() ? new Dimension( _breadthTotal.preferred, _lengthTotal.preferred ) : new Dimension( _lengthTotal.preferred, _breadthTotal.preferred );
//
//	addInsets( size );
//
//	return size;
//
//    }
//
//    public Dimension getMaximumSize() {
//
//	Dimension size = isVertical() ? new Dimension( _breadthTotal.maximum, _lengthTotal.maximum ) : new Dimension( _lengthTotal.maximum, _breadthTotal.maximum );
//
//	addInsets( size );
//
//	return size;
//
//    }

    private void addInsets( Dimension size ) {

        Insets insets = _target.getInsets();

        size.width = (int)Math.min( (long)size.width + (long)insets.left + (long)insets.right, Integer.MAX_VALUE );
        size.height = (int)Math.min( (long)size.height + (long)insets.top + (long)insets.bottom, Integer.MAX_VALUE );

    }

    public float getLayoutAlignmentX() {

        @SuppressWarnings("UnnecessaryLocalVariable")
        float alignmentX = isVertical() ? _breadthTotal.alignment : _lengthTotal.alignment;

        return alignmentX;

    }

    public float getLayoutAlignmentY() {

        @SuppressWarnings("UnnecessaryLocalVariable")
        float alignmentY = isVertical() ? _lengthTotal.alignment : _breadthTotal.alignment;

        return alignmentY;

    }

    public void computePositions() {

        int n = _breadthSizes.length;

        _xOffsets = new int[n];
        _xSpans = new int[n];
        _yOffsets = new int[n];
        _ySpans = new int[n];

        Dimension space = _target.getSize();
        Insets in = _target.getInsets();

        Dimension adjSpace = new Dimension( space.width - ( in.left + in.right ), space.height - ( in.top + in.bottom ) );
//	logIfWatched( "space=" + ObtuseUtil.fDim( space ) + ", insets=" + ObtuseUtil.fInsets( in ) + ", adjSpace=" + ObtuseUtil.fDim( adjSpace ) );

//	space.width -= in.left + in.right;
//	space.height -= in.top + in.bottom;

        if ( isWatched() ) {

            ObtuseUtil.doNothing();

        }

//	if ( ObtuseUtil.always() || LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {
//
//	    logIfWatched(
//	    	( isVertical() ? "compute vertical positions(" : "compute horizontal positions(" ) + LinearLayoutUtil.fullName( _target ) + "):" +
//		"  space=" + ObtuseUtil.fDim( space ) +
//		", insets=" + ObtuseUtil.fInsets( in ) +
//		", adjSpace=" + ObtuseUtil.fDim( adjSpace )
//	    );
//
//	    for ( int i = 0; i < _breadthSizes.length; i += 1 ) {
//
//		logIfWatched( "    bS[" + i + "] = " + _breadthSizes[i] );
//		logIfWatched( "    lS[" + i + "] = " + _lengthSizes[i] );
//
//	    }
//
//	}

//	String levelName = "adjusting " + getTarget().getName();

        try {

//	    Logger.pushNesting( levelName );

//	    Logger.logMsg( getTarget().getName() + "'s adjSpace is " + ObtuseUtil.fDim( adjSpace ) );

            if ( isVertical() ) {

                if ( LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {

                    ObtuseUtil.doNothing();

                }

                calculateAlignedPositions(
                        adjSpace.width,
                        _breadthTotal,
                        _breadthSizes,
                        _xOffsets,
                        _xSpans,
                        true,
                        _trackParentBreadths
                );

                calculateTiledPositions(
                        adjSpace.height,
                        _lengthTotal,
                        _lengthSizes,
                        _yOffsets,
                        _ySpans,
                        true
                );

            } else {

                calculateTiledPositions(
                        adjSpace.width,
                        _lengthTotal,
                        _lengthSizes,
                        _xOffsets,
                        _xSpans,
                        true
                );

                calculateAlignedPositions(
                        adjSpace.height,
                        _breadthTotal,
                        _breadthSizes,
                        _yOffsets,
                        _ySpans,
                        true,
                        _trackParentBreadths
                );

            }

        } finally {

//	    Logger.popNestingLevel( levelName );

        }

//	if ( ObtuseUtil.always() || LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {
//
//	    for ( int ix = 0; ix < n; ix += 1 ) {
//
//		Rectangle r = computeComponentBoundingRectangle( ix );
//
//		logIfWatched( "L3C.computePositions:  [" + ix + "] " + LinearLayoutUtil.fullName( _target, _breadthSizes[ix].component ) + " will be at " + ObtuseUtil.fBounds( r ) );
//
//	    }
//
//	}

    }

    private void calculateAlignedPositions(
            int allocated,
            SizeRequirements total,
            SizeRequirements[] children,
            int[] offsets,
            int[] spans,
            @SuppressWarnings("SameParameterValue") boolean normal,
            boolean[] trackParentBreadth
    ) {

        if ( isWatched() ) {

            ObtuseUtil.doNothing();

        }

//	logIfWatched( "calculateAlignedPositions( " + allocated + ", " + total + ", " + ( children == _breadthSizes ? "_breadthSizes" : ( children == _lengthSizes ? "_lengthSizes" : "?" ) ) + " ... )" );
        float totalAlignment = normal ? total.alignment : 1.0f - total.alignment;

        int totalAscent = (int)( allocated * totalAlignment );

        int totalDescent = allocated - totalAscent;

//	logIfWatched( "total alignment=" + totalAlignment + ", totalAscent=" + totalAscent + ", totalDescent=" + totalDescent );
        for ( int i = 0; i < children.length; i++ ) {

//	    if ( trackParentBreadth[i] ) {
//
//	        Logger.logMsg( "track parent's breadth" );
//
//	    } else {
//
//	        Logger.logMsg( "ignore parent's breadth" );
//
//	    }

            SizeRequirements req = children[i];
            float alignment = normal ? req.alignment : 1.0f - req.alignment;
            int maxAscent = (int)( req.maximum * alignment );
            int maxDescent = req.maximum - maxAscent;
            int ascent = Math.min( totalAscent, maxAscent );
            int descent = Math.min( totalDescent, maxDescent );

            offsets[i] = totalAscent - ascent;
            spans[i] = (int)Math.min( (long)ascent + (long)descent, Integer.MAX_VALUE );
            if ( trackParentBreadth[i] ) {

                if ( offsets[i] + spans[i] < allocated ) {

                    int newSpan = (int)Math.min( (long)allocated - offsets[i], totalAscent );
//		    Logger.logMsg( "oldSpan=" + spans[i] + ", new span=" + newSpan );

                    ObtuseUtil.doNothing();

                }

            }
//	    logIfWatched( "  [" + i + "]  alignment=" + alignment + ", maxAscent=" + maxAscent + ", maxDescent=" + maxDescent + ", ascent=" + ascent + ", descent=" + descent + ", offset=" + offsets[i] + ", span=" + spans[i] );

        }

    }

    private void calculateTiledPositions(
            int allocated,
            SizeRequirements total,
            SizeRequirements[] children,
            int[] offsets,
            int[] spans,
            @SuppressWarnings("SameParameterValue") boolean forward
    ) {

        if ( isWatched() ) {

            ObtuseUtil.doNothing();

        }

//	logIfWatched( "calculateTiledPositions( " + total + ", " + children.length + " children, forward = " + forward + " )" );

        // The total argument turns out to be a bad idea since the
        // total of all the children can overflow the integer used to
        // hold the total.  The total must therefore be calculated and
        // stored in long variables.
        long min = 0;
        long pref = 0;
        long max = 0;
//	StringBuilder buf = new StringBuilder();
//	String comma = "";

        for ( SizeRequirements child : children ) {

//	    buf.append( comma ).append( children[i].minimum ).append( '/' ).append( children[i].preferred ).append( '/' ).append( children[i].maximum );
//	    comma = ", ";

            min += child.minimum;
            pref += child.preferred;
            max += child.maximum;

        }

//	logIfWatched( buf.toString() );

//	String levelName = "adjusting " + getTarget().getName() + " - allocated=" + allocated + ", pref=" + pref;
//	Logger.pushNesting( levelName );

        try {

//	    Logger.logMsg( "allocated=" + allocated + ", pref=" + pref );

            if ( allocated >= pref ) {

                expandedTile( allocated, min, pref, max, children, offsets, spans, forward );

            } else {

                compressedTile( allocated, min, pref, max, children, offsets, spans, forward );

            }

        } finally {

//	    Logger.popNestingLevel( levelName );

        }

    }

    private void compressedTile(
            int allocated, long min, long pref, long max,
            SizeRequirements[] request,
            int[] offsets, int[] spans,
            boolean forward
    ) {

        if ( isWatched() ) {

            ObtuseUtil.doNothing();

        }

        logIfWatched( "compressedTile:  " + getTarget().getName() + " - " + ObtuseUtil.fDim( getTarget().getSize() ) );

        // ---- determine what we have to work with ----
        float totalPlay = Math.min( pref - allocated, pref - min );
        float factor = ( pref - min == 0 ) ? 0.0f : totalPlay / ( pref - min );

        // ---- make the adjustments ----
        int totalOffset;
        if ( forward ) {

            // lay out with offsets increasing from 0
            totalOffset = 0;
            for ( int i = 0; i < spans.length; i++ ) {

                offsets[i] = totalOffset;
                SizeRequirements req = request[i];
                float play = factor * ( req.preferred - req.minimum );
                spans[i] = (int)( req.preferred - play );
                totalOffset = (int)Math.min( (long)totalOffset + (long)spans[i], Integer.MAX_VALUE );

            }

        } else {

            // lay out with offsets decreasing from the end of the allocation
            totalOffset = allocated;
            for ( int i = 0; i < spans.length; i++ ) {

                SizeRequirements req = request[i];
                float play = factor * ( req.preferred - req.minimum );
                spans[i] = (int)( req.preferred - play );
                offsets[i] = totalOffset - spans[i];
                totalOffset = (int)Math.max( (long)totalOffset - (long)spans[i], 0 );

            }

        }

    }

    private void expandedTile(
            int allocated, long min, long pref, long max,
            SizeRequirements[] request,
            int[] offsets, int[] spans,
            boolean forward
    ) {

        if ( isWatched() ) {

            ObtuseUtil.doNothing();

        }

        logIfWatched( "  expandedTile:  " + getTarget().getName() + " - " + ObtuseUtil.fDim( getTarget().getSize() ) );

        // ---- determine what we have to work with ----
        float totalPlay = Math.min( allocated - pref, max - pref );
        float factor = ( max - pref == 0 ) ? 0.0f : totalPlay / ( max - pref );

        // ---- make the adjustments ----
        int totalOffset;
        if ( forward ) {

            // lay out with offsets increasing from 0
            totalOffset = 0;
            for ( int i = 0; i < spans.length; i++ ) {

                offsets[i] = totalOffset;
                SizeRequirements req = request[i];
                int play = (int)( factor * ( req.maximum - req.preferred ) );
                spans[i] = (int)Math.min( (long)req.preferred + (long)play, Integer.MAX_VALUE );
                totalOffset = (int)Math.min( (long)totalOffset + (long)spans[i], Integer.MAX_VALUE );

            }

        } else {

            // lay out with offsets decreasing from the end of the allocation
            totalOffset = allocated;
            for ( int i = 0; i < spans.length; i++ ) {

                SizeRequirements req = request[i];
                int play = (int)( factor * ( req.maximum - req.preferred ) );
                spans[i] = (int)Math.min( (long)req.preferred + (long)play, Integer.MAX_VALUE );
                offsets[i] = totalOffset - spans[i];
                totalOffset = (int)Math.max( (long)totalOffset - (long)spans[i], 0 );

            }

        }

    }

    public void setComponentBounds() {

        for ( int ix = 0; ix < _breadthSizes.length; ix += 1 ) {

            Component c = _breadthSizes[ix].component;

//	    if ( ObtuseUtil.always() || c instanceof Container && LinearLayoutUtil.isContainerOnWatchlist( (Container)c ) ) {
//
//		logIfWatched( "L3C.setComponentBounds:  about to compute bounds of " + LinearLayoutUtil.fullName( _target, c ) );
//
//	    }

            Rectangle r = computeComponentBoundingRectangle( ix );

//	    if ( ObtuseUtil.always() || c instanceof Container && LinearLayoutUtil.isContainerOnWatchlist( (Container)c ) ) {
//
//		logIfWatched( "L3C.setComponentBounds: bounds of " + LinearLayoutUtil.fullName( _target, c ) + " set to " + ObtuseUtil.fBounds( r ) );
//
//	    }

//	    Logger.logMsg( "setComponentBounds on " + c.getName() + ObtuseUtil.fBounds( r ) + "<<<" );

            try {

//	        Logger.pushNesting( "setComponentBounds on " + c.getName() + ObtuseUtil.fBounds( r ) );

                c.setBounds( r );

            } finally {

//	        Logger.popNestingLevel( "setComponentBounds on " + c.getName() + ObtuseUtil.fBounds( r ) );

            }

//	    Logger.logMsg( "setComponentBounds on " + c.getName() + ObtuseUtil.fBounds( r ) + ">>>" );

        }

    }

    @NotNull
    public Rectangle computeComponentBoundingRectangle( int ix ) {

        Insets in = _target.getInsets();
        return new Rectangle(
                (int)Math.min( ( (long)in.left + _xOffsets[ix] ), Integer.MAX_VALUE ),
                (int)Math.min( ( (long)in.top + _yOffsets[ix] ), Integer.MAX_VALUE ),
                _xSpans[ix],
                _ySpans[ix]
        );

    }

}
