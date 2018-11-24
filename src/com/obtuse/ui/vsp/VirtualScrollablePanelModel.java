package com.obtuse.ui.vsp;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

/**
 Created by danny on 2018/11/19.
 */

public interface VirtualScrollablePanelModel<E extends VirtualScrollableElement> {

    class CurrentGoals<EV extends VirtualScrollableElement> {

        private final int _elementIndexOfTopRow;
        private final ArrayList<VirtualScrollableElementModel<EV>> _visibleElements;

        public CurrentGoals(
                final int elementIndexOfTopRow,
                @NotNull final List<? extends VirtualScrollableElementModel<EV>> visibleElements
        ) {

            super();

            _elementIndexOfTopRow = elementIndexOfTopRow;
            _visibleElements = new ArrayList<>( visibleElements );

        }

        /**
         Specify which visible element should appear at the top of the inner scrollable window.
         */

        public int elementIndexOfTopRow() {

            return _elementIndexOfTopRow;

        }

        /**
         Provide a list of potentially visible elements.
         <p>An element is potentially visible if the 'right' scrolling operation could bring the element into view.
         In other words, this is essentially 'elements that the human is allowed to see right now'.</p>
         @return the list of potentially visible elements.
         Note that return type is explicitly {@link ArrayList}.
         This provides a guarantee that the cost of accessing an element is constant (and essentially zero).
         */

        public ArrayList<VirtualScrollableElementModel<EV>> visibleElementModels() {

            return _visibleElements;

        }

        public int getVisibleElementCount() {

            return _visibleElements.size();

        }

        public String toString() {

            return "CurrentGoals( " +
                   "elementIndexOfTopRow=" + _elementIndexOfTopRow + ", " +
                   "from " + ObtuseUtil.pluralize( _visibleElements.size(), "visible elements"
            );

        }


    }

    /**
     Launch a new round allocating {@link ElementView} instances.
     <p>Tell the data model that we are about to start asking for a fresh set of {@link ElementView} instances
     and that any that it has provided to us in the past can be recycled if deemed appropriate by this model.</p>
     */

    void allocateElementViews(
            @NotNull Collection<VirtualScrollableElementModel<E>> elementModels,
            @NotNull SortedMap<UniqueID, ElementView<E>> assignedElementViewMapping
    );

    /**
     Tell the view panel mdel that we have actually filled and will render a particular element view.
     This is probably useful in computing the appropriate <em>extent</em> value for the vertical scrollbar
     and could be useful in other ways.
     @param elementView the {link ElementView}{@code <E>} that has just been filled.
     */

    void noteViewFilled( ElementView<E> elementView );

//        /**
//         Get the number of {@link VirtualScrollableElementModel}s which are currently visible.
//         @return the number of currently visible {@code Component}s.
//         */
//
//        @Deprecated
//        int getVisibleElementCount();
//
//        /**
//         Get the index of the topmost currently visible element.
//         @return the index of the topmost currently visible element.
//         */
//
//        @Deprecated
//        int getTopmostVisibleElementIx();
//
//        /**
//         Get the {@link VirtualScrollableElementModel} for a particular currently visible element.
//         @param ix the index of the desired element from within the set of currently visible elements.
//         This value must/will be in the range {@code [0,n)} where {@code n} is the number of currently visible elements.
//         @return the {@link VirtualScrollableElementModel} for a currently visible element.
//         @throws IllegalArgumentException (should be thrown) if {@code ix} is not in the range {@code [0,n)}
//         where {@code n} is the number of currently visible elements
//         */
//
//        @Deprecated
//        VirtualScrollableElementModel<EV> getElementAt( int ix );

//        /**
//         Get the currently visible elements.
//         */
//
//        java.util.List<VirtualScrollableElementModel<E>> getVisibleElements();

//        /**
//         Get an element which really really should appear in the visible part of the scroll view.
//         */

    /**
     Get the current goals.

     @return the current goals.
     */

    @NotNull
    CurrentGoals<E> getCurrentGoals();

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

//        /**
//         Create an empty element view.
//         */
//
//        @SuppressWarnings("unused")
//        ElementView<E> createEmptyElementView();

//        /**
//         Minimum value for vertical scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getVMin();

//        /**
//         Extent value for vertical scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getVExtent();

//        /**
//         Maximum value for vertical scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getVMax();

//        /**
//         Minimum value for horizontal scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getHMin();

//        /**
//         Minimum value for horizontal scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getHExtent();

//        /**
//         Minimum value for horizontal scrollbar.
//         @return typically 0 but anything that satisfies {@code vMin <= vExtent <= vMax} and provides a reasonable
//         amount of resolution works.
//         */
//
//        int getHMax();

}
