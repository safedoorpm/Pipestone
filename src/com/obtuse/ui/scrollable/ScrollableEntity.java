/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.scrollable;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 Describe one of the things that are in the container that we are scrolling.
 */

public class ScrollableEntity {

    private final int _ix;

//    private final boolean _vertical;

    private final Component _component;

    private final Container _container;

//    private final boolean _crossCut;

    private final ScrollableEntitiesManager _sem;

    private final int _effectiveLength;

    private final int _effectiveBreadth;

    /**
     Setup a scrollable entity for a scrolling operation that is in the same orientation as the container.
     @param sem this scrolling operation's manager.
     @param ix this entity's index in the container being scrolled through.
     @param container the container being scrolled through.
     */

    public ScrollableEntity(
            final ScrollableEntitiesManager sem,
            final int ix,
            final Container container
    ) {

        super();

        _sem = sem;

        _effectiveLength = -1;
        _effectiveBreadth = -1;

        _ix = ix;

        _component = sem.getVisibleComponent( ix );

        _container = container;

        if ( ( isContainerVertical() ? _component.getHeight() : _component.getWidth() ) == 0 ) {

            throw new HowDidWeGetHereError(
                    "ScrollableEntity:  cannot cope with zero-length components like " + _component +
                    " (ScrollableEntitiesManager.isEntityVisible fails)"
            );

        }

    }

    /**
     Setup the single scrollable entity used by a scrolling operation which scrolls in the opposite orientation to the container.
     @param sem this scrolling operation's manager.
     @param container the container being scrolled "across the grain"
     */

    public ScrollableEntity(
            final ScrollableEntitiesManager sem,
            final Container container
    ) {
        super();

        _sem = sem;

        _ix = 0;

        _component = null;

        _container = container;

        if ( isContainerVertical() ) {

            _effectiveLength = _container.getWidth();

            _effectiveBreadth = _container.getHeight();

        } else {

            _effectiveLength = _container.getHeight();

            _effectiveBreadth = _container.getWidth();

        }

    }

    public ScrollableEntitiesManager getOurManager() {

         return _sem;

    }

    int getBreadth() {

        if ( isCrossCut() ) {

            return _effectiveBreadth;

        } else {

            return isContainerVertical() ? _component.getWidth() : _component.getHeight();

        }

    }

    int getLength() {

        if ( isCrossCut() ) {

            return _effectiveLength;

        } else {

            int length;
            if ( isLastComponent() ) {

                length = isContainerVertical() ? _component.getHeight() : _component.getWidth();

            } else {

                length = isContainerVertical() ? getNextScrollableEntity().getComponent().getY() - _component.getY() : getNextScrollableEntity().getComponent().getX() - _component.getX();

            }

            return length;

        }

    }

    public boolean isContainerVertical() {

        return _sem.isContainerVertical();

    }

    public boolean isScrollingVertical() {

        return _sem.isScrollingVertical();

    }

    public int getOffset() {

        return isCrossCut() ? 0 : ( isContainerVertical() ? _component.getY() : _component.getX() );

    }

    public boolean isFirstComponent() {

        return _ix == 0;

    }

    public boolean isLastComponent() {

        return isCrossCut() || _ix + 1 == _sem.getVisibleComponentCount();

    }

    public boolean isCrossCut() {

        return isContainerVertical() != isScrollingVertical();

    }

    public Component getComponent() {

        if ( isCrossCut() ) {

            throw new IllegalArgumentException( "ScrollableEntity:  component not available for cross cut entities" );

        }

        return _component;

    }

    public Container getContainer() {

        return _container;

    }

    @NotNull
    public ScrollableEntity getPreviousScrollableEntity() {

        if ( isCrossCut() ) {

            throw new HowDidWeGetHereError( "ScrollableEntity:  cross cut entities do not have a previous component" );

        }

        if ( isFirstComponent() ) {

            throw new HowDidWeGetHereError( "ScrollableEntity:  request for previous component when we are the first component" );

        }

        return _sem.getScrollableEntityByIndex( _ix - 1 );

    }

    @NotNull
    public ScrollableEntity getNextScrollableEntity() {

        if ( isCrossCut() ) {

            throw new HowDidWeGetHereError( "ScrollableEntity:  cross cut entities do not have a next component" );

        }

        if ( isLastComponent() ) {

            throw new HowDidWeGetHereError( "ScrollableEntity:  request for next component when we are the last component" );

        }

        return _sem.getScrollableEntityByIndex( _ix + 1 );

    }

    public int getIx() {

        return _ix;

    }

    public String toString() {

        if ( isCrossCut() ) {

            return "ScrollableEntity( CROSS-CUT, length=" + _effectiveLength + ", breadth=" + _effectiveBreadth + " )";

        } else {

            return "ScrollableEntity( ix=" + _ix + ", off=" + getOffset() + ", br=" + getBreadth() + ", len=" + getLength() + ", e=" + _component +
                   ", last=" + isLastComponent() + " ), bounds=" + ObtuseUtil.fBounds( _component.getBounds() ) +
                   ", container=" + ObtuseUtil.fBounds( _container.getBounds() );

        }

    }

}
