package com.obtuse.ui.vsp;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 Created by danny on 2018/11/19.
 */

public interface VirtualScrollablePanelModel<E extends VirtualScrollableElement> {

    class CurrentGoals<EV extends VirtualScrollableElement> {

        private final int _firstVisibleElementNumber;
        private final int _firstProvidedElementNumber;
        private final int _scrollableElementsCount;
        private final ArrayList<VirtualScrollableElementModel<EV>> _providedElementModels;

        public CurrentGoals(
                final int firstVisibleElementNumber,
                final int firstProvidedElementNumber,
                final int scrollableElementsCount,
                @NotNull final List<? extends VirtualScrollableElementModel<EV>> providedElementModels
        ) {

            super();

            _firstVisibleElementNumber = firstVisibleElementNumber;
            _firstProvidedElementNumber = firstProvidedElementNumber;
            _scrollableElementsCount = scrollableElementsCount;
            _providedElementModels = new ArrayList<>( providedElementModels );

        }

        public boolean isAnythingVisible() {

            //noinspection RedundantIfStatement
            if ( _firstVisibleElementNumber < _scrollableElementsCount && !_providedElementModels.isEmpty() ) {

                return true;

            } else {

                return false;

            }

        }

        /**
         Determine which actual element should appear at the top of the inner scrollable window.
         */

        public int getFirstVisibleElementNumber() {

            return _firstVisibleElementNumber;

        }

        /**
         Determine which element is the first one actually provided by this instance.
         @return the index of the first element that the creator of this instance provided.
         */

        public int getFirstProvidedElementNumber() {

            return _firstProvidedElementNumber;

        }

        /**
         Determine which element is the last one actually provided by this instance.
         @return the index of the last element that the creator of this instance provided.
         */

        public int getLastProvidedElementNumber() {

            return _firstProvidedElementNumber + _providedElementModels.size() - 1;

        }

        /**
         Determine how many elements are provided by this instance.
         <p>Do NOT confuse this with the total number of elements which currently exist
         and which the human might be able to see by scrolling
         (see {@link #getScrollableElementsCount()} for that number).</p>
         @return the number of elements provided by this instance.
         */

        public int getProvidedElementCount() {

            return _providedElementModels.size();

        }

        /**
         Determine how many elements the human can see (some of which might require scrolling to actually see).
         @return the number of elements which the human can see by scrolling.
         */

        public int getScrollableElementsCount() {

            return _scrollableElementsCount;

        }

        /**
         Get the element model for a specified element in the list of all elements.
         @param elementIx the index of the requested element.
         @return the element model for the specified element.
         */

        public VirtualScrollableElementModel<EV> getElementAt( int elementIx ) {

            int ixWithinProvidedElements = elementIx - _firstProvidedElementNumber;

            if ( ixWithinProvidedElements < 0 ) {

                throw new IllegalArgumentException(
                        "CurrentGoals.getElementAt(" + elementIx + "):  " +
                        "element less than first available element ix=" + _firstProvidedElementNumber );

            }

            if ( ixWithinProvidedElements >= _providedElementModels.size() ) {

                throw new IllegalArgumentException(
                        "CurrentGoals.getElementAt(" + elementIx + "):  " +
                        "element greater than last provide element ix=" + ( _providedElementModels.size() - 1 ) );

            }

            return _providedElementModels.get( ixWithinProvidedElements );

        }

        public int getVisibleElementCount() {

            return _providedElementModels.size();

        }

        public String toString() {

            return "CurrentGoals( " +
                   "elementIndexOfTopRow=" + _firstVisibleElementNumber + ", " +
                   "from " + ObtuseUtil.pluralize( _providedElementModels.size(), "visible elements"
            );

        }

    }

    /**
     Check if there's an update to the selection result.
     <p>This is the only time that the selection result can change.</p>
     @return {@code true} if the selection result has changed; {@code false} otherwise.
     */

    @SuppressWarnings("UnusedReturnValue")
    boolean checkForUpdates();

    ElementView.ElementViewFactory<E> getElementViewFactory();

    /**
     Tell _vModel that we are starting a new round of allocating element views.
     This allows the _vModel to recycle old views if they so choose.
     */

    void startNewElementViewAllocationRound();

    /**
     Allocate an element view for an element model that the {@link VirtualScrollablePanel} intends to display.
     @param elementModel the element model.
     @return the element view.
     */

    ElementView<E> createInstance( VirtualScrollableElementModel<E> elementModel );

        /**
         Tell the view panel mdel that we have actually filled and will render a particular element view.
         This is probably useful in computing the appropriate <em>extent</em> value for the vertical scrollbar
         and could be useful in other ways.
         @param elementView the {link ElementView}{@code <E>} that has just been filled.
         */

    void noteViewFilled( ElementView<E> elementView );

    /**
     Get the current goals.

     @return the current goals.
     */

    @NotNull
    CurrentGoals<E> getCurrentGoals(
            final int firstVisibleRowNumber,
            @NotNull final Dimension viewportSize
    );

    /**
     Configure the vertical scrollbar.
     <p>This will typically involve little more than setting the min, extent and max attributes.</p>
     */

    @SuppressWarnings("UnusedReturnValue")
    boolean configureVerticalScrollBar(
            @NotNull JScrollBar verticalScrollBar,
            int nRenderedElementViews,
            int innerScrollableWindowHeight
    );

    /**
     Configure the vertical scrollbar.
     <p>This will typically involve little more than setting the min, extent and max attributes.</p>
     */

    @SuppressWarnings("UnusedReturnValue")
    boolean configureHorizontalScrollBar(
            @NotNull JScrollBar horizontalScrollBar,
            int widestRenderedElementView,
            final int innerScrollableWindowWidth
    );

    /**
     Get the approximate height in pixels of an element.
     <p>This only needs to be a reasonable estimate. The actual height of an element will be
     'discovered' when the first element is encountered by our layout manager.
     It is also entirely possible that this method will never be called (depends on how this critter works).</p>
     <p>This facility is currently designed to work with elements which are all the same size.
     Hopefully I will explore supporting elements of varying sizes eventually.</p>
     */

    @SuppressWarnings("unused")
    int getApproximateElementHeight();

    /**
     Get the maximum width of an element.
     <p>This needs to be a pretty good estimate as the value is used to respond to changes in the horizontal
     scrollbar. That said, the {@link VirtualScrollablePanel} class uses the value provided by this method as a
     starting point and will adjust its working value of this attribute upwards as wider elements are encountered.
     </p>
     */

    @SuppressWarnings("unused")
    int getApproximateElementWidth();

}
