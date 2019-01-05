package com.obtuse.ui.vsp;

import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Created by danny on 2018/11/20.
 */

public abstract class AbstractVirtualScrollablePanelModel<E extends VirtualScrollableElement>
        implements VirtualScrollablePanelModel<E> {

    private SortedMap<UniqueId, ElementView<E>> _currentlyAssignedElementViewsMap = new TreeMap<>();

//    private java.util.List<ElementView<E>> _unusedElementViews = new ArrayList<>();

    private ElementView.ElementViewFactory<E> _elementViewFactory;

    /**
     The number of visible elements that the most recently obtained {@link CurrentGoals} instance reported.
     */

    private int _nVisibleElements = 0;

    /**
     The number of element views that were actually filled and rendered during the latest (re-)layout of the inner
     scrollable panel.
     */

    private int _nElementViewsFilledAndRendered = 0;

    public AbstractVirtualScrollablePanelModel(
            @NotNull final ElementView.ElementViewFactory<E> elementViewFactory
    ) {
        super();

        _elementViewFactory = elementViewFactory;

    }

    public ElementView.ElementViewFactory<E> getElementViewFactory() {

        return _elementViewFactory;

    }

    @Override
    public final ElementView<E> createInstance( VirtualScrollableElementModel<E> elementModel ) {

        return getElementViewFactory().createInstance( elementModel );

    }

    @Override
    public void noteViewFilled( final ElementView<E> elementView ) {

        _nElementViewsFilledAndRendered += 1;

    }

    @NotNull
    public final CurrentGoals<E> getCurrentGoals(
            final int firstVisibleRowNumber,
            @NotNull final Dimension viewportSize
    ) {

        @NotNull CurrentGoals<E> rval = getActualCurrentGoals( firstVisibleRowNumber, viewportSize );
        _nVisibleElements = rval.getVisibleElementCount();

        return rval;

    }

    @NotNull
    public abstract CurrentGoals<E> getActualCurrentGoals(
            int firstVisibleElementIx,
            @NotNull final Dimension viewportSize
    );

    @Override
    public boolean configureVerticalScrollBar(
            @NotNull final JScrollBar verticalScrollBar,
            final int nRenderedElementViews,
            final int actualScrollableElements
    ) {

        int min = 0;
        @SuppressWarnings("UnnecessaryLocalVariable") int extent = nRenderedElementViews;
        @SuppressWarnings("UnnecessaryLocalVariable") int max = actualScrollableElements;
        boolean didSomething = false;
        if ( verticalScrollBar.getMinimum() != min ) {

            didSomething = true;

        }

        if ( verticalScrollBar.getMaximum() != max ) {

            didSomething = true;

        }

        if ( verticalScrollBar.getBlockIncrement() != extent ) {

            didSomething = true;

        }

        if ( didSomething ) {

            verticalScrollBar.getModel()
                             .setRangeProperties(
                                     verticalScrollBar.getValue(),
                                     extent,
                                     min,
                                     max,
                                     false
                             );
            verticalScrollBar.setBlockIncrement( nRenderedElementViews );
            verticalScrollBar.setUnitIncrement( 1 );

        }

        return didSomething;

    }

    @Override
    public boolean configureHorizontalScrollBar(
            @NotNull final JScrollBar horizontalScrollBar,
            final int widestRenderedElementView,
            final int innerScrollableWindowWidth
    ) {

        int min = 0;
        int extent = Math.min( widestRenderedElementView, innerScrollableWindowWidth );
        int max = Math.max( widestRenderedElementView, innerScrollableWindowWidth );
        boolean didSomething = false;
        if ( horizontalScrollBar.getMinimum() != min ) {

            horizontalScrollBar.setMinimum( min );
            didSomething = true;

        }

        if ( horizontalScrollBar.getMaximum() != max ) {

            horizontalScrollBar.setMaximum( max );
            didSomething = true;

        }

        if ( horizontalScrollBar.getBlockIncrement() != extent ) {

            horizontalScrollBar.setBlockIncrement( extent );
            didSomething = true;

        }

        return didSomething;

    }

    @Override
    public int getApproximateElementHeight() {

        return 10;

    }

    @Override
    public int getApproximateElementWidth() {

        return 50;

    }

    public String toString() {

        return "AbstractVirtualScrollablePanelModel()"; // size=" + _elementModelsList.size() + " )";

    }

}
