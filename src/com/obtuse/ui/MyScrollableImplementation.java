package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.Trace;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

public class MyScrollableImplementation implements Scrollable {

    private final Container _container;

    private final boolean _vertical;

    private static boolean s_traceOnly = false;

    public MyScrollableImplementation( @NotNull Container container, boolean vertical ) {
	super();

	_container = container;

	_vertical = vertical;

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {

	return _container.getPreferredSize();

    }

    public Container getContainer() {

        return _container;

    }

    public boolean isVertical() {

        return _vertical;

    }

    @Override
    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {

        return getScrollableUnitIncrement( new MyScrollableImplementation.ScrollableEntitiesManager( this ), visibleRect, orientation, direction );

    }

    private int getScrollableUnitIncrement( MyScrollableImplementation.ScrollableEntitiesManager sem, Rectangle visibleRect, int orientation, int direction ) {

        if ( visibleRect.y >= ( _vertical ? _container.getHeight() : _container.getWidth() ) ) {

            throw new HowDidWeGetHereError( "getScrollableUnitIncrement:  huh?" );

	}

	if ( isVertical() ) {

	    if ( orientation != SwingConstants.VERTICAL ) {

		return 25;

	    }

	} else {

	    return 25;

//            if ( orientation != SwingConstants.HORIZONTAL ) {
//
//                return 25;
//
//	    }

	}

	boolean downwards = direction > 0;

	report( "getScrollableUnitIncrement( vr=" + visibleRect + ", vertical=" + isVertical() + ", dir=" + direction + " )" );

//	MyScrollableImplementation.ScrollableEntitiesManager sem = new MyScrollableImplementation.ScrollableEntitiesManager( this );

	sem.showEntities( "getScrollableUnitIncrement" );

	MyScrollableImplementation.ScrollableEntity firstVisible = sem.findFirstVisibleEntity( visibleRect );
//        ScrollableEntity lastVisible = sem.findLastVisibleEntity( visibleRect );

	report( "firstVisible=" + firstVisible );

//        if ( firstVisible.getIx() == lastVisible.getIx() ) {

//	if ( ObtuseUtil.never() && ( !downwards || firstVisible.getNextTop() >= visibleRect.y + visibleRect.height || firstVisible.isLast() ) ) {
//
//            // There is only one entity visible.
//
//	    if ( downwards ) {
//
//	        // Moving downwards.
//
//		// Is there is enough of the visible entity beyond the bottom of our viewport to fill another viewport?
//
//	        if ( firstVisible.getNextTop() >= visibleRect.y + visibleRect.height + visibleRect.height ) {
//
//	            // Yes - just scroll down one viewport.
//
//	            return visibleRect.height;
//
//		} else {
//
//	            // No. Is the visible entity's bottom aligned at the bottom of the viewport?
//
//		    if ( firstVisible.getNextTop() == visibleRect.y + visibleRect.height ) {
//
//		        // Yes. If the visible entity is the last entity then do nothing.
//			// Otherwise, put the next entity at the top of the viewport.
//
//			if ( firstVisible.isLast() ) {
//
//			    return 0;
//
//			} else {
//
//			    return visibleRect.height;
//
////			    return firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height );
//
//			}
//
//		    } else {
//
//		        // No. Force the visible entity's bottom to be aligned with the bottom of the viewport.
//			// Another way of saying this is that we'll scroll down just enough to reveal that which
//			// is still hidden.
//
//			int hiddenBelow = firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height );
//
//			return hiddenBelow;
//
//		    }
//
//		    //	            int delta = firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height );
////
////	            if ( delta == 0 ) {
////
////	                return visibleRect.height;
////
////		    } else {
////
////			return delta;
////
////		    }
//
//		}
//
//	    } else {
//
//	        // Going up.
//
//		// Is the visible entity aligned right at the top of the viewport?
//
//		if ( firstVisible.getOffset() == visibleRect.y ) {
//
//		    // If there is a previous entity to scroll up to, move as far as we can while
//		    // making sure that the tail of the previous entry is entirely visible.
//		    // Otherwise, do nothing.
//
//		    ScrollableEntity previous =  sem.getByIndex( firstVisible.getIx() - 1 );
//
//		    if ( previous == null ) {
//
//		        return 0;
//
//		    }
//
//		    return Math.min( previous.getEntityLength(), visibleRect.height );
//
//		} else {
//
//		    // There must be more of the visible entity to go.
//		    // Scroll up to align the top of the visible entity at the top of the viewport
//		    // but don't go up more than one complete viewport.
//
//		    return Math.min( visibleRect.y - firstVisible.getOffset(), visibleRect.height );
//
//		}
//
//	    }
//
//	} else {

//            // More than one is visible.

	if ( downwards ) {

	    // Advance to place the next entity at the top of the viewport.

//		ScrollableEntity next = sem.getByIndex( firstVisible.getIx() + 1 );
//		if ( next == null ) {
//
//		    // IMPOSSIBLE!
//
//		    throw new HowDidWeGetHereError( "getScrollableUnitIncrement:  downwards, more than one entity visible, no next entity after the first visible" );
//
//		}

	    // As a first approximation, scroll down by enough to advance to put the next entity at the top of the viewport.

	    int nextTop = firstVisible.getNextTop() - visibleRect.y;

	    // Don't go further than is required to get the next entity to align with the bottom of the viewport.

	    int nt = firstVisible.getNextTop();
	    if ( nt > visibleRect.y + visibleRect.height ) {

		report( "" + nextTop + " is too far, limiting to " + firstVisible.getNextTop() + " - ( " + visibleRect.y + " + " +
			     visibleRect.height + " ) == " + ( firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height ) ) );
		nextTop = Math.min( nextTop, firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height ) );

	    }

	    // Don't go further than a full viewport height (this seems superfluous).
	    if ( nextTop > visibleRect.height ) {

		report( "" + nextTop + " is too far, limiting to height of " + visibleRect.height );

		nextTop = visibleRect.height;

	    }

//		// Scroll down by the smaller of:
//		// - distance to top of next entity
//		// - distance to bottom of next entity
//		// - viewport height
//
//		int nt2 = Math.min(
//			firstVisible.getNextTop() - visibleRect.y,
//			Math.min(
//				firstVisible.getNextTop() - ( visibleRect.y + visibleRect.height ),
//				visibleRect.height
//			)
//		);
//		if ( nt2 != nextTop ) {
//
//		    // Something went wrong.
//
//		    throw new HowDidWeGetHereError( "nextTop=" + nextTop + " but nt2=" + nt2 );
//
//		}

	    return nextTop;

	} else {

	    // Is the visible entity aligned right at the top of the viewport?

	    if ( firstVisible.getOffset() == visibleRect.y ) {

		// If there is a previous entity to scroll up to, move as far as we can while
		// making sure that the tail of the previous entry is entirely visible.
		// Otherwise, do nothing.

		MyScrollableImplementation.ScrollableEntity previous = sem.getByIndex( firstVisible.getIx() - 1 );

		if ( previous == null ) {

		    return 0;

		}

		return Math.min( previous.getEntityLength(), visibleRect.height );

	    } else {

		// There must be more of the visible entity to go.
		// Scroll up to align the top of the visible entity at the top of the viewport
		// but don't go up more than one complete viewport.

		return Math.min( visibleRect.y - firstVisible.getOffset(), visibleRect.height );

	    }

	}

//
//	    if ( downwards ) {
//
//	        // Advance to the next entity.
//
//		return
//	    }
//	    return 25;

//	}

//        int firstInViewportIx = -1;
//        int lastInViewportIx = -1;
//        int currentVerticalOffset = 0;
//        int currentHorizontalOffset = 0;
//
//        for ( ScrollableEntity entity : entities ) {
//
//            if ( getOrientation() == LinearOrientation.VERTICAL ) {
//
//
//	    }
//	}

    }

    @Override
    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {

	if ( visibleRect.y >= ( _vertical ? _container.getHeight() : _container.getWidth() ) ) {

	    throw new HowDidWeGetHereError( "getScrollableBlockIncrement:  huh?" );

	}

	if ( isVertical() ) {

	    if ( orientation != SwingConstants.VERTICAL ) {

		return 25;

	    }

	} else {

	    return 25;

//            if ( orientation != SwingConstants.HORIZONTAL ) {
//
//                return 25;
//
//	    }

	}

	boolean downwards = direction > 0;

	report( "getScrollableUnitIncrement( visR=" + visibleRect + ", vertical=" + isVertical() + ", dir=" + direction + " )" );

	MyScrollableImplementation.ScrollableEntitiesManager sem = new MyScrollableImplementation.ScrollableEntitiesManager( this );

	sem.showEntities( "getScrollableUnitIncrement" );

//	ScrollableEntity firstVisible = sem.findFirstVisibleEntity( visibleRect );
//
//	int maxScrollDelta = visibleRect.height;

	// Just unit scroll until the next unit scroll would move us more than a viewport height in total.

	int committedDelta = 0;
	Rectangle vr = new Rectangle( visibleRect );
	if ( vr.y < 0 || vr.y >= ( _vertical ? _container.getHeight() : _container.getWidth() ) ) {

	    throw new HowDidWeGetHereError( "oops" );

	}

	while ( true ) {

	    int curDelta = getScrollableUnitIncrement( sem, vr, orientation, direction );
	    if ( curDelta == 0 ) {

		report( "got unit delta of zero - we're done" );

		break;

	    }

	    int thisDelta = downwards ? curDelta : -curDelta;

	    int newY = vr.y + thisDelta;
	    if ( newY < 0 || newY >= visibleRect.y + visibleRect.height ) {

	        report( "getScrollableBlockIncrement(1):  vr=" + vr + ", thisDelta=" + thisDelta + ", newY=" + newY );

	        break;

//	        return vr.height - vr.y;

//		throw new HowDidWeGetHereError( "oops" );

	    }

	    report( "getScrollableBlockIncrement(2): vr=" + vr + ", thisDelta=" + thisDelta + ", newY=" + newY );

	    if ( downwards && newY >= visibleRect.y + visibleRect.height ) {

		report( "downwards is done, committed delta is " + committedDelta );

		break;

	    }

	    if ( !downwards && newY < visibleRect.y - visibleRect.height ) {

		report( "upwards is done, committed delta is " + committedDelta );

		break;

	    }

	    vr.y = newY;
	    committedDelta += thisDelta;

	}

	report( "block increment returning " + committedDelta );

	return Math.abs( committedDelta );

//	// Is the first visible entity the only visible entity?
//
//	if ( downwards && ( firstVisible.isLast() || firstVisible.getNextTop() >= visibleRect.y + visibleRect.height ) ) {
//
//	    // The first visible entity is the only visible entity.
//	    // Treat this like a unit scroll downwards.
//
//	    return getScrollableUnitIncrement( visibleRect, orientation, direction );
//
//	} else if ( !downwards && )

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

	return false;

    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

	return false;

    }

    static class ScrollableEntity {

	private final int _offset;

	private final boolean _vertical;

	private final int _ix;

	private final boolean _last;

	private final int _breadth;

	private final int _length;

	private final Component _component;

	private final Container _container;

	private ScrollableEntity(
		boolean vertical,
		int offset,
		int breadth,
		int length,
		Component component,
		int ix,
		boolean last,
		Container container
	) {
	    super();

	    _offset = offset;

	    _vertical = vertical;

	    _breadth = breadth;
	    _length = length;

	    _component = component;

	    _ix = ix;

	    _last = last;

	    _container = container;

	}

	@SuppressWarnings("unused")
	int getEntityBreadth() {

	    return _breadth;

	}

	int getEntityLength() {

	    return _length;

	}

	public int getOffset() {

	    return _offset;

	}

	public int getNextTop() {

	    return _offset + _length;

	}

	@SuppressWarnings("unused")
	public int getLastLine() {

	    return getNextTop() - 1;

	}

	public boolean isFirst() {

	    return _ix == 0;

	}

	@SuppressWarnings("unused")
	public boolean isLast() {

	    return _last;

	}

	Component getComponent() {

	    return _component;

	}

	int getIx() {

	    return _ix;

	}

	public boolean overlaps( Rectangle visibleRect ) {

	    if ( visibleRect.y >= ( _vertical ? _container.getHeight() : _container.getWidth() ) ) {

		throw new HowDidWeGetHereError( "huh?" );

	    }

	    boolean result = _offset < visibleRect.y + visibleRect.height && _offset + _length > visibleRect.y;
	    report( "of=" + _offset + ", y+h=" + ( visibleRect.y + visibleRect.height ) + "; of+l=" + ( _offset + _length ) + ", y=" +
			 visibleRect.y );
	    report(
		    "[" + _offset + "," + ( _offset + _length ) + ") vs [" + visibleRect.y + ", " + ( visibleRect.y + visibleRect.height ) +
		    ") yields " + result );

	    return result;

	}

	public String toString() {

	    return "ScrollableEntity( ix=" + _ix + ", off=" + _offset + ", br=" + _breadth + ", len=" + _length + ", e=" + _component +
		   ", last=" + _last + " )";

	}

    }

    static class ScrollableEntitiesManager {

	private final MyScrollableImplementation _msi;

//	private final LinearOrientation _orientation;

	private Vector<MyScrollableImplementation.ScrollableEntity> _entities = new Vector<>();

	public ScrollableEntitiesManager( @NotNull MyScrollableImplementation msi ) {
	    super();

	    _msi = msi;

//	    _orientation = lc.getOrientation();

	    int offset = 0;
	    int ix = 0;
	    int size = msi.getContainer().getComponentCount();
	    for ( Component c : msi.getContainer().getComponents() ) {

		MyScrollableImplementation.ScrollableEntity se;
		if ( _msi.isVertical() ) {

		    se = new MyScrollableImplementation.ScrollableEntity(
			    _msi.isVertical(),
			    offset,
			    c.getWidth(), c.getHeight(), c, ix, ix + 1 == size, _msi.getContainer()
		    );

		} else {

		    se = new MyScrollableImplementation.ScrollableEntity(
			    _msi.isVertical(),
			    offset,
			    c.getHeight(), c.getWidth(), c, ix, ix + 1 == size, _msi.getContainer()
		    );

		}

		_entities.add( se );

		offset += se.getEntityLength();
		ix += 1;

	    }

	    showEntities( "initialized" );

	}

	public boolean isVertical() {

	    return _msi.isVertical();

	}

	@NotNull
	MyScrollableImplementation.ScrollableEntity findFirstVisibleEntity( @NotNull Rectangle visibleRect ) {

	    for ( MyScrollableImplementation.ScrollableEntity se : _entities ) {

		if ( se.overlaps( visibleRect ) ) {

		    return se;

		}

	    }

	    throw new HowDidWeGetHereError( "findFirstVisibleEntity:  no entity is visible within the viewport" );

//            if ( lastSe.getOffset() + lastSe.getEntityLength() <= visibleRect.y ) {
//
//                throw new HowDidWeGetHereError( "findFirstVisibleEntity:  no entity is visible in the " )
//	    }

	}

	@SuppressWarnings("unused")
	@NotNull
	MyScrollableImplementation.ScrollableEntity findLastVisibleEntity( Rectangle visibleRect ) {

	    // report( "vr=" + visibleRect );
	    showEntities( "findLastVisibleEntity(vr=" + visibleRect + ")" );
	    MyScrollableImplementation.ScrollableEntity lastSe = null;
	    for ( MyScrollableImplementation.ScrollableEntity se : _entities ) {

		report( "se=" + se );

		if ( se.overlaps( visibleRect ) ) {

		    lastSe = se;

		} else if ( lastSe != null ) {

		    report( "inner lastSe=" + lastSe );
		    return lastSe;

		}

//		    if ( se.getOffset() + se.getEntityLength() >= visibleRect.y + visibleRect.height ) {
//
//		        if ( lastSe == null ) {
//
//		            throw new HowDidWeGetHereError( "findLastVisibleEntity:  no entity visible within the viewport (before end)" );
//
//			}
//
//			return lastSe;
//
//		    }
//
//		    lastSe = se;
//
//		}

	    }

	    if ( lastSe != null ) {

		report( "terminal lastSe=" + lastSe );
		return lastSe;

	    }

	    throw new HowDidWeGetHereError( "findFirstVisibleEntity:  no entity is visible within the viewport (at end)" );

	}

	@SuppressWarnings("unused")
	public void showEntities() {

	    showEntities( "all" );

	}

	public void showEntities( String why ) {

	    report( "selectable entities (" + why + ")" );
	    report( "  ix length  start    end component" );
	    for ( MyScrollableImplementation.ScrollableEntity se : _entities ) {

		report(
			ObtuseUtil.lpad( se.getIx(), 4 ) + ' ' +
			ObtuseUtil.lpad( se.getEntityLength(), 6 ) + ' ' +
			ObtuseUtil.lpad( se.getOffset(), 6 ) + ' ' +
			ObtuseUtil.lpad( se.getOffset() + se.getEntityLength(), 6 ) + ' ' +
			se.getComponent()
		);

	    }

	}

	public MyScrollableImplementation.ScrollableEntity getByIndex( int ix ) {

	    if ( ix < 0 || ix >= _entities.size() ) {

		return null;

	    } else {

		return _entities.get( ix );

	    }

	}

    }

    private static void report( String msg ) {

        if ( s_traceOnly ) {

	    Trace.event( msg );

	} else {

            Logger.logMsg( msg );

	}
    }
}