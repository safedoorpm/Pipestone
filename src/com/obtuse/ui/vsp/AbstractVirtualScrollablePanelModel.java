package com.obtuse.ui.vsp;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.RandomCentral;
import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Created by danny on 2018/11/20.
 */

public abstract class AbstractVirtualScrollablePanelModel<E extends VirtualScrollableElement>
        implements VirtualScrollablePanelModel<E> {

    private SortedMap<UniqueID, ElementView<E>> _currentlyAssignedElementViewsMap = new TreeMap<>();

    private java.util.List<ElementView<E>> _unusedElementViews = new ArrayList<>();

//    private final java.util.List<VirtualScrollableElementModel<E>> _elementModelsList = new ArrayList<>();
//    private final SortedMap<UniqueID, VirtualScrollableElementModel<E>> _elementModelsMap = new TreeMap<>();
    private final ElementView.ElementViewFactory<E> _elementViewFactory;

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
//            @NotNull Collection<VirtualScrollableElementModel<E>> elements,
            @NotNull final ElementView.ElementViewFactory<E> elementViewFactory
    ) {
        super();

        _elementViewFactory = elementViewFactory;

//        addElementModels( elements );

    }

//    public void addElementModel( final VirtualScrollableElementModel<E> elementModel ) {
//
//        if ( !_elementModelsMap.containsKey( elementModel.getUniqueID() ) ) {
//
//            _elementModelsList.add( elementModel );
//            _elementModelsMap.put( elementModel.getUniqueID(), elementModel );
//
//        }
//
//    }
//
//    public void addElementModels( @NotNull final Collection<VirtualScrollableElementModel<E>> elementModels ) {
//
//        for ( VirtualScrollableElementModel<E> elementModel : elementModels ) {
//
//            elementModel.setPanelModel( this );
//            addElementModel( elementModel );
//
//        }
//
//    }

    @Override
    public void allocateElementViews(
            @NotNull Collection<VirtualScrollableElementModel<E>> elementModels,
            @NotNull SortedMap<UniqueID, ElementView<E>> assignedElementViewMapping
    ) {

        java.util.List<VirtualScrollableElementModel<E>> needViews = new ArrayList<>( elementModels );
        java.util.List<ElementView<E>> availableElementViews = new ArrayList<>();

        availableElementViews.addAll( _currentlyAssignedElementViewsMap.values() );
        availableElementViews.addAll( _unusedElementViews );
        _currentlyAssignedElementViewsMap.clear();

        // Assign views to models.
        // If we have recycled views to assign, we select them randomly to maximize the chances
        // that any failure in the fill method to fill the entire view gets noticed.

        for ( VirtualScrollableElementModel<E> elementModel : needViews ) {

            ElementView<E> availableView;
            if ( availableElementViews.isEmpty() ) {

                availableView = _elementViewFactory.createInstance( elementModel );

            } else {

                int ix = RandomCentral.nextInt( availableElementViews.size() );
                availableView = availableElementViews.remove( ix );
                availableView.setElementModel( elementModel );
            }

            _currentlyAssignedElementViewsMap.put( elementModel.getUniqueID(), availableView );
            assignedElementViewMapping.put( elementModel.getUniqueID(), availableView );

        }

        _unusedElementViews.clear();
        _unusedElementViews.addAll( availableElementViews );

        _currentlyAssignedElementViewsMap.values().forEach(
                eElementView -> eElementView.freshAssignment()
        );

        // We are DONE!

        ObtuseUtil.doNothing();

    }

//        // If there are STILL ids needing views then give them bright and shiny new views.
//
//        while ( !needViews.isEmpty() ) {
//
//            VirtualScrollableElementModel<E> elementModel = needViews.remove( 0 );
//
//            UniqueID id = stillNeedViews.remove( 0 );
//            ElementView<E> newView = _elementViewFactory.createInstance( id, idToElementModelMap.get( id ) );
//
//            // Make sure that the id and element model get initialized.
//
//            newView.setElementModel( idToElementModelMap.get( id ) );
//
//            _currentlyAssignedElementViewsMap.put( id, newView );
//            assignedElementViewMapping.put( id, newView );
//
//        }


//            ElementView<E> availableView = _currentlyAssignedElementViewsMap.remove( oldId );
//            _currentlyAssignedElementViewsMap.put( id, availableView );
//            assignedElementViewMapping.put( id, availableView );
//                assignedElementViews.add( availableView );

//        }

//        _unusedElementViews.addAll( _currentlyAssignedElementViewsMap.values() );
//        // We're starting a new round of deciding what to actually render.
//
//        _nElementViewsFilledAndRendered = 0;
//
////            java.util.List<ElementView<E>> assignedElementViews = new ArrayList<>();
//
//
//        HashMap<UniqueID, VirtualScrollableElementModel<E>> idToElementModelMap = new HashMap<>();
//
//        // Nobody has a view officially assigned to them.
//
//        assignedElementViewMapping.clear();
//
//        // Need a list of the ids that were previously assigned element views.
//
//        List<UniqueID> oldIdsSet = new ArrayList<>( _currentlyAssignedElementViewsMap.keySet() );
//
//        // Remember how many elements are in the _currentlyAssignedElementViewsMap map.
//
//        int existingAssignmentCount = _currentlyAssignedElementViewsMap.size();
//
//        // Initialize the list of models that need views.
//
//        stillNeedViews.addAll( elementModels );
//
//        ObtuseUtil.doNothing();

//        // Reconfirm element views that were previously assigned to the ids we've been asked to get views for.
//
//        for ( VirtualScrollableElementModel<E> elementModel : elementModels ) {
//
//            UniqueID id = elementModel.getUniqueID();
//            idToElementModelMap.put( id, elementModel );
//            ElementView<E> previouslyAssigned = _currentlyAssignedElementViewsMap.get( id );
//            if ( previouslyAssigned == null ) {
//
//                stillNeedViews.add( id );
//
//            } else {
//
//                assignedElementViewMapping.put( id, previouslyAssigned );
////                    assignedElementViews.add( previouslyAssigned );
//                oldIdsSet.remove( id );
//
//            }
//
//        }
//
//        ObtuseUtil.doNothing();

//        // If we already have them, assign completely empty views to ids that don't yet have views.
//
//        for ( UniqueID id : List.copyOf( stillNeedViews ) ) {
//
//            if ( _emptyElementViews.isEmpty() ) {
//
//                break;
//
//            } else {
//
//                ElementView<E> availableView = _emptyElementViews.remove( 0 );
//                UniqueID firstId = stillNeedViews.remove( 0 );
//                if ( firstId.equals( id ) ) {
//
//                    availableView.setElementModel( id, idToElementModelMap.get( id ) );
//                    assignedElementViewMapping.put( id, availableView );
////                        assignedElementViews.add( availableView );
//
//                } else {
//
//                    throw new HowDidWeGetHereError(
//                            "AbstractVirtualScrollablePanelModel.allocateElementViews:  " +
//                            "expected first id to be " + firstId.format() + " but it was " + id.format()
//                    );
//
//                }
//
//            }
//
//        }
//
//        ObtuseUtil.doNothing();
//
//        // Reassign any views that we've used in the past.
//
//        List<UniqueID> oldIdsList = new ArrayList<>( oldIdsSet );
//        while ( !stillNeedViews.isEmpty() && !oldIdsList.isEmpty() ) {
//
//            int ix = RandomCentral.nextInt( oldIdsList.size() );
//            UniqueID oldId = oldIdsList.remove( ix );
//            ElementView<E> availableView = _currentlyAssignedElementViewsMap.remove( oldId );
//            UniqueID id = stillNeedViews.remove( 0 );
//            availableView.setElementModel( idToElementModelMap.get( id ) );
//            _currentlyAssignedElementViewsMap.put( id, availableView );
//            assignedElementViewMapping.put( id, availableView );
////                assignedElementViews.add( availableView );
//
//        }
//
//        // We should still have EXACTLY the same number of views in the _currentlyAssignedElementViewsMap map.
//
//        if ( existingAssignmentCount != _currentlyAssignedElementViewsMap.size() ) {
//
//            // Oops!!!
//
//            throw new HowDidWeGetHereError(
//                    "AbstractVirtualScrollablePanelModel.allocateElementViews:  " +
//                    "size of _currentlyAssignedElementViewsMap changed from " + existingAssignmentCount +
//                    " to " + _currentlyAssignedElementViewsMap.size()
//            );
//
//        }
//
//        // If there are any old views that we have still not used, mark them as unused.
//
//        for ( UniqueID id : oldIdsList ) {
//
//            VirtualScrollableElementModel<E> oldView = idToElementModelMap.get( id );
//            oldView.setPanelModel( null );
//
//        }
//
//        ObtuseUtil.doNothing();
//
//        // If there are STILL ids needing views then give them bright and shiny new views.
//
//        while ( !stillNeedViews.isEmpty() ) {
//
//            UniqueID id = stillNeedViews.remove( 0 );
//            ElementView<E> newView = _elementViewFactory.createInstance( id, idToElementModelMap.get( id ) );
//
//            // Make sure that the id and element model get initialized.
//
//            newView.setElementModel( idToElementModelMap.get( id ) );
//
//            _currentlyAssignedElementViewsMap.put( id, newView );
//            assignedElementViewMapping.put( id, newView );
//
//        }
//
//    }

    @Override
    public void noteViewFilled( final ElementView<E> elementView ) {

        _nElementViewsFilledAndRendered += 1;

    }

    @NotNull
    public final CurrentGoals<E> getCurrentGoals( @NotNull final Dimension viewportSize ) {

        @NotNull CurrentGoals<E> rval = getActualCurrentGoals( viewportSize );
        _nVisibleElements = rval.getVisibleElementCount();

        return rval;

    }

    @NotNull
    public abstract CurrentGoals<E> getActualCurrentGoals( @NotNull final Dimension viewportSize );

    @Override
    public boolean configureVerticalScrollBar(
            @NotNull final JScrollBar verticalScrollBar,
            final int nRenderedElementViews,
            final int innerScrollableWindowHeight
    ) {

        int min = 0;
        @SuppressWarnings("UnnecessaryLocalVariable") int extent = nRenderedElementViews;
        int max = _nVisibleElements;
        boolean didSomething = false;
        if ( verticalScrollBar.getMinimum() != min ) {

//                verticalScrollBar.setMinimum( min );
            didSomething = true;

        }

        if ( verticalScrollBar.getMaximum() != max ) {

//                verticalScrollBar.setMaximum( max );
            didSomething = true;

        }

        if ( verticalScrollBar.getBlockIncrement() != extent ) {

//                verticalScrollBar.setBlockIncrement( extent );
//                verticalScrollBar.getModel().setExtent( extent );
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

//        Logger.logMsg( "model is now " + verticalScrollBar.getModel() );
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
