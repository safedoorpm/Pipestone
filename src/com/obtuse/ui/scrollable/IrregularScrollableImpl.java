/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.scrollable;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.obtuse.util.ObtuseUtil.report;

/**
 A scrollable implementation that supports elements of different sizes.
 <p/>This implementation defines a few terms and makes a few assumptions.
 <ul>
 <li>That the container is <b>oriented</b> vertically or horizontally and that the components within the container are laid out in container order in the container's orientation.
 That the components in a <u>vertically</u> <i>oriented</i> container appear one <u>above</u>
 the other with component 0 above component 1 above component 2, etc.
 Alternatively, the components in a <u>horizontally</u> <i>oriented</i> container appear one <u>beside</u>
 the other with component 0 to the left of component 1 which is to the left of component 2, etc.
 <li>That the <b>length</b> of a component is the distance from the <u>start</u> of the component to the <u>start</u> of the next component in the direction that the components in the container are laid out. The length of the last component in the container is the distance from the start to the end of that component.</li>
 <li>That the <b>breadth</b> of a component is the <u>width</u> of the component if the container is vertically oriented and is the <u>height</u> of the component if the container is horizontally oriented.</li>
 </ul>
 */

public class IrregularScrollableImpl implements Scrollable {

    private final Container _container;

    private final boolean _containerIsVertical;

    public IrregularScrollableImpl( @NotNull Container container, boolean containerIsVertical ) {

        super();

        _container = container;

        _containerIsVertical = containerIsVertical;

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {

        return _container.getPreferredSize();

    }

    public Container getContainer() {

        return _container;

    }

    public boolean isContainerVertical() {

        return _containerIsVertical;

    }

    @Override
    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {


        int rval = 0;

        try {

            // If there's nothing in the container then (pretend to) scroll a modest distance.

            if ( isEmpty() ) {

                rval = 0;
                return rval;

            }

            ScrollableEntitiesManager sem = new ScrollableEntitiesManager( this, visibleRect, orientation, direction );
            rval = getScrollableUnitIncrement( sem );

            return rval;

        } finally {

            ObtuseUtil.report( "### getScrollableUnitIncrement returning " + rval );

        }

    }

    private int getScrollableUnitIncrement( ScrollableEntitiesManager sem ) {

        int topOfWindow = sem.getWindowStart();

        if ( sem.getWindowStart() > ( _containerIsVertical ? _container.getHeight() : _container.getWidth() ) ) {

            throw new HowDidWeGetHereError( "getScrollableUnitIncrement:  huh?" );

        }

        report( "getScrollableUnitIncrement( vr=" +
                ObtuseUtil.fBounds( sem.getVisibleRectangle() ) +
                ", vertical=" + isContainerVertical() +
                ", dir=" + sem.getDirection() +
                " )" );

        sem.showEntities( "getScrollableUnitIncrement" );
        sem.verify();

        ScrollableEntity firstVisibleEntity = sem.findFirstVisibleEntity();

        report( "firstVisibleEntity=" + firstVisibleEntity );

        if ( sem.downwards() ) {

            // If there is still some of the first visible entity which is not yet visible down below
            // then scroll far enough to show the rest of the first visible entity.
            // Limit that scroll by the size of the window.

            int lastLineInWindow = topOfWindow + sem.getWindowLength();

            int topOfFirstVisibleEntity = firstVisibleEntity.getOffset();
            int lineAfterFirstVisibleEntity = topOfFirstVisibleEntity + firstVisibleEntity.getLength();
            int notYetVisibleAmountOfFirstVisibleEntity = lineAfterFirstVisibleEntity - lastLineInWindow;
            if ( notYetVisibleAmountOfFirstVisibleEntity > 0 ) {

                if ( notYetVisibleAmountOfFirstVisibleEntity > sem.getWindowLength() ) {

                    return sem.getWindowLength();

                } else {

                    return notYetVisibleAmountOfFirstVisibleEntity;

                }

            }

            // All of the rest of the first visible entity is visible.
            // If there is no next entity then we're done - return 0.

            if ( firstVisibleEntity.isLastComponent() ) {

                return 0;

            }

            // Bring the top of the next component to the top of the window.

            ScrollableEntity nextEntity = firstVisibleEntity.getNextScrollableEntity();
            int scrollAmount = nextEntity.getOffset() - sem.getWindowStart();

            if ( scrollAmount > 0 ) {

                return scrollAmount;

            } else {

                throw new HowDidWeGetHereError( "IrregularScrollableImpl.getScrollableUnitIncrement:  ended up with a negative scroll amount" );

            }

        } else {

            // Is the visible entity aligned right at the top of the viewport?

            if ( firstVisibleEntity.getOffset() == sem.getWindowStart() ) {

                // If there is a previous entity to scroll up to, move as far as we can while
                // making sure that the tail of the previous entry is entirely visible.
                // Otherwise, do nothing.

                if ( firstVisibleEntity.isFirstComponent() ) {

                    return 0;

                } else {

                    ScrollableEntity previous = firstVisibleEntity.getPreviousScrollableEntity();
                    return Math.min( previous.getLength(), sem.getWindowLength() );

                }

            } else {

                // There must be more of the visible entity to go.
                // Scroll up to align the top of the visible entity at the top of the viewport
                // but don't go up more than one complete viewport.

                return Math.min( sem.getWindowStart() - firstVisibleEntity.getOffset(), sem.getWindowLength() );

            }

        }

    }

    @Override
    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {

        int rval = 0;

        try {

            // If there's nothing in the container then (pretend to) scroll a modest distance.

            if ( isEmpty() ) {

                rval = 0;
                return rval;

            }

            ScrollableEntitiesManager sem =
                    new ScrollableEntitiesManager( this, visibleRect, orientation, direction );

            rval = getScrollableBlockIncrement( sem );
            return rval;

        } finally {

            report( "### getScrollableBlockIncrement returning " + rval );

        }

    }

    private int getScrollableBlockIncrement( ScrollableEntitiesManager sem ) {

        if ( sem.getWindowStart() > ( isContainerVertical() ? _container.getHeight() : _container.getWidth() ) ) {

            LinearLayoutUtil.showStructure( this.getContainer() );

            throw new HowDidWeGetHereError( "getScrollableBlockIncrement:  huh?" );

        }

        report( "getScrollableBlockIncrement( " + sem + " )" ); // visR=" + visibleRect + ", vertical=" + isContainerVertical() + ", dir=" + direction + " )" );

        sem.showEntities( "getScrollableBlockIncrement" );

        // Just unit scroll until the next unit scroll would move us more than a viewport height in total.

        int committedDelta = 0;

        final int topOfOriginalWindow = sem.getWindowStart();
        int curTopOfWindow = topOfOriginalWindow;

        int nominalScrollAmount = 0;

        while ( true ) {

            int curDelta = getScrollableUnitIncrement( sem );
            if ( curDelta == 0 ) {

                return Math.abs( committedDelta );

            }

            int thisDelta = sem.downwards() ? curDelta : -curDelta;
            nominalScrollAmount += curDelta;

            curTopOfWindow += thisDelta;

            if ( nominalScrollAmount > sem.getWindowLength() ) {

                return Math.abs( committedDelta );

            }

            committedDelta += curDelta;

            sem.adjustWindowStart( thisDelta );

        }

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

        return false;

    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

        return false;

    }

    public boolean isEmpty() {

        return getContainer().getComponentCount() == 0;

    }

    public String toString() {

        return "IrregularScrollableImpl( " + ( _containerIsVertical ? "vertical" : "horizontal" ) + ", container=" + _container + " )";

    }

}