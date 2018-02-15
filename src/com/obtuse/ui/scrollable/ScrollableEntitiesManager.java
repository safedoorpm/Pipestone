/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.scrollable;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static com.obtuse.util.ObtuseUtil.report;

/**
 Gather into one place all of the relevant knowledge about the thing that we are scrolling.
 */

public class ScrollableEntitiesManager {

    private final IrregularScrollableImpl _msi;

    private final Rectangle _visibleRectangle;
    private final int _direction;
    private int _windowStart;
    private final int _windowLength;
    private final ScrollableEntity[] _entities;
    private final ArrayList<Component> _visibleComponents = new ArrayList<>();
    private final boolean _scrollingVertical;

    public ScrollableEntitiesManager( final @NotNull IrregularScrollableImpl msi, final Rectangle visibleRectangle, final int orientation, final int direction ) {

        super();

        _msi = msi;

        // We don't deal with empty containers as they get handled before we get constructed.

        if ( isEmpty() ) {

            throw new HowDidWeGetHereError( "ScrollableEntitiesManager:  constructor invoked with empty container" );

        }

        _visibleRectangle = visibleRectangle;
        _direction = direction;

        if ( orientation == SwingConstants.HORIZONTAL ) {

            _scrollingVertical = false;

            _windowStart = visibleRectangle.x;
            _windowLength = visibleRectangle.width;

        } else {

            _scrollingVertical = true;

            _windowStart = visibleRectangle.y;
            _windowLength = visibleRectangle.height;

        }

        // If we are scrolling across rather than along our orientation then setup a very simple config that consists of a single scrollable entity.

        if ( orientation == ( _msi.isContainerVertical() ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL ) ) {

            _entities = new ScrollableEntity[1];
            _entities[0] = new ScrollableEntity( this, /*isContainerVertical(),*/ msi.getContainer() );

        } else {

            Component[] components = msi.getContainer().getComponents();

            // Gather together the components that are visible and of non-zero length.

            for ( Component c : components ) {

                if ( c.isVisible() ) {

                    // It is entirely possible if not logical that {@link Component#isVisible()} returns false
                    // when the component is of zero length (in the direction of scrolling). On the other hand,
                    // it is simpler and safer to just leave such components out of our calculations
                    // Also, the ScrollableEntity constructor will have a fit if it is asked to manage such a component.

                    if ( ( isContainerVertical() ? c.getHeight() : c.getWidth() ) > 0 ) {

                        _visibleComponents.add( c );

                    }

                }

            }

            _entities = new ScrollableEntity[_visibleComponents.size()];
            for ( int ix = 0; ix < _visibleComponents.size(); ix += 1 ) {

                _entities[ix] = new ScrollableEntity(
                        this,
                        ix,
                        _msi.getContainer()
                );

            }

            showEntities( "initialized" );

        }

        verify();

    }

    public boolean isContainerVertical() {

        return _msi.isContainerVertical();

    }

    public boolean isScrollingVertical() {

        return _scrollingVertical;

    }

    public boolean isCrossCut() {

        return isContainerVertical() != isScrollingVertical();

    }

    public void verify() {

        if ( isCrossCut() ) {

            return;

        }

        int ix = 0;
        int offset = 0;

        for ( ScrollableEntity se : _entities ) {

            String tagString = "IrregularScrollableImpl.verify(" + ix + " of " + size() +
                               "/" + ( isContainerVertical() ? "v" : "h" ) +
                               ( ix == _entities.length - 1 ? "/last" : "" ) +
                               "):  ";

            if ( se.getIx() != ix ) {

                showEntities( "verify failed" );

                throw new HowDidWeGetHereError(
                        tagString +
                        "expected ix=" + ix + " " +
                        "but found ix=" + se.getIx() + " " +
                        "(" + se + ")" );

            }

            if ( se.getOffset() != offset ) {

                showEntities( "verify failed" );

                throw new HowDidWeGetHereError(
                        tagString +
                        "expected offset=" + offset + " " +
                        "but found offset=" + se.getOffset() + " " +
                        "(" + se + ")"
                );

            }

            if ( se.isLastComponent() ) {

                int length = se.isScrollingVertical() ? se.getComponent().getHeight() : se.getComponent().getWidth();
                if ( se.getLength() != length ) {

                    showEntities( "verify failed" );

                    throw new HowDidWeGetHereError(
                            tagString +
                            "expected length=" + length + " " +
                            "but found length=" + se.getLength() + " " +
                            "(" + se + ")"
                    );

                }

            } else {

                int length;
                if ( se.isScrollingVertical() ) {

                    length = se.getNextScrollableEntity().getComponent().getY() - se.getComponent().getY();

                } else {

                    length = se.getNextScrollableEntity().getComponent().getX() - se.getComponent().getX();

                }

                if ( se.getLength() != length ) {

                    showEntities( "verify failed" );

                    throw new HowDidWeGetHereError(
                            tagString +
                            "expected length=" + length + " " +
                            "but found length=" + se.getLength() + " " +
                            "(" + se + ")"
                    );

                }

            }

            if ( se.getIx() != se.getContainer().getComponentCount() - 1 ) {

                if ( se.isLastComponent() ) {

                    showEntities( "verify failed" );

                    throw new HowDidWeGetHereError(
                            tagString +
                            "expected not last but found is last" +
                            "(" + se + ")"
                    );

                }

            } else {

                if ( !se.isLastComponent() ) {

                    showEntities( "verify failed" );

                    throw new HowDidWeGetHereError(
                            tagString +
                            "expected is last but found not last" +
                            "(" + se + ")"
                    );

                }

            }

            ix += 1;
            offset += se.getLength();

        }

    }

    public int getDirection() {

        return _direction;

    }

    public void adjustWindowStart( final int adjustment ) {

        _windowStart += adjustment;

    }

    public int getWindowStart() {

        return _windowStart;

    }

    public int getWindowLength() {

        return _windowLength;

    }

    public boolean downwards() {

        return _direction > 0;

    }

    public boolean isEntityVisible( final ScrollableEntity se ) {

        return ObtuseUtil.overlapsLength( _windowStart, _windowLength, se.getOffset(), se.getLength() - 1 );

    }

    @NotNull
    ScrollableEntity findFirstVisibleEntity() {

        for ( ScrollableEntity se : _entities ) {

            if ( isEntityVisible( se ) ) {

                report( "first visible se is " + se );

                return se;

            }

        }

        throw new HowDidWeGetHereError( "findFirstVisibleEntity:  no entity is visible within the viewport" );

    }

    @SuppressWarnings("unused")
    public void showEntities() {

        showEntities( "all" );

    }

    public void showEntities( final String why ) {

        if ( !isCrossCut() ) {

            report( "selectable entities (" + why + ")" );
            report( "  ix length  start    end last component" );
            for ( ScrollableEntity se : _entities ) {

                report(
                        ObtuseUtil.lpad( se.getIx(), 4 ) + ' ' +
                        ObtuseUtil.lpad( se.getLength(), 6 ) + ' ' +
                        ObtuseUtil.lpad( se.getOffset(), 6 ) + ' ' +
                        ObtuseUtil.lpad( se.getOffset() + se.getLength(), 6 ) + ' ' +
                        ' ' + ( se.isLastComponent() ? "y" : "n" ) + "   " +
                        se.getComponent()
                );

            }

        }

    }

    public int size() {

        return _entities.length;

    }

    public boolean isEmpty() {

        return _msi.isEmpty();

    }

    @NotNull
    public ScrollableEntity getScrollableEntityByIndex( final int ix ) {

        if ( ix < 0 || ix >= _entities.length ) {

            throw new HowDidWeGetHereError( "ScrollableEntitiesManager:  request for entity " + ix + " when only entities 0 through " + ( _entities.length - 1 ) + " exist" );

        } else {

            return _entities[ix];

        }

    }

    public int getVisibleComponentCount() {

        if ( isCrossCut() ) {

            throw new HowDidWeGetHereError( "ScrollableEntitiesManager:  cross cut operations do not have a visible component count" );

        }

        return _visibleComponents.size();

    }

    @NotNull
    public Component getVisibleComponent( final int ix ) {

        if ( isCrossCut() ) {

            throw new HowDidWeGetHereError( "ScrollableEntitiesManager:  cross cut operations do not have available visible components" );

        }

        if ( ix < 0 || ix >= _entities.length ) {

            throw new HowDidWeGetHereError( "ScrollableEntitiesManager:  request for visible component " + ix + " when only components 0 through " + ( _visibleComponents.size() - 1 ) + " exist" );

        } else {

            return _visibleComponents.get( ix );

        }

    }

    @NotNull
    public java.util.List<Component> getVisibleComponents() {

        return Collections.unmodifiableList( _visibleComponents );

    }

    public Rectangle getVisibleRectangle() {

        return _visibleRectangle;

    }

    public String toString() {

        return "ScrollableEntitiesManager( msi=" + _msi + ", windowStart=" + getWindowStart() + ", windowLength=" + getWindowLength() + " )";

    }

}
