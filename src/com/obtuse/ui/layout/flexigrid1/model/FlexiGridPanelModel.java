/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.*;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 Provide a high level model for manipulating a {@link com.obtuse.ui.layout.flexigrid1.FlexiGridContainer}.
 */

public class FlexiGridPanelModel<SLICE extends FlexiGridModelSlice> {

    public enum Orientation {
        ROWS,
        COLUMNS
    }

    public enum OwnershipExpectation {
        OWNED_BY_THIS_MODEL,
        OWNED_BY_SOME_MODEL,
        OWNED_BY_NO_MODEL,
        OWNED_BY_SOME_OTHER_MODEL
    }

    private final String _name;

    private final FlexiGridContainer _fgContainer;
    private final FlexiGridLayoutManager _fgLayoutManager;
    private final Orientation _orientation;

    private final long _key = RandomCentral.nextLong();

    private FlexiGridLayoutManagerCache _cache = null;
    private long _lastCacheSerialNumber = -1;

    private boolean _msgTraceMode;

    private final List<SLICE> _slices = new LinkedList<>();

    private final Set<SLICE> _slicesSet = new HashSet<>();

    private final List<Component> _titleComponents = new ArrayList<>();

    public FlexiGridPanelModel(
            final @NotNull String name,
            final @NotNull Orientation orientation,
            final boolean msgTraceMode,
            final boolean useLayoutTracer
    ) {
        this( true, name, orientation, msgTraceMode, useLayoutTracer );

    }

    public FlexiGridPanelModel(
            final boolean doubleBuffered,
            final @NotNull String name,
            final @NotNull Orientation orientation,
            final boolean msgTraceMode,
            final boolean useLayoutTracer
    ) {
        super();

        _msgTraceMode = msgTraceMode;

        logMaybe( "FlexiGridPanelModel:  creating model " + name );

        _name = name;

        _fgContainer = new FlexiGridContainer1( doubleBuffered, name, _key, msgTraceMode, useLayoutTracer );
        if ( useLayoutTracer ) {

            _fgContainer.setBorder( BorderFactory.createLineBorder( Color.ORANGE ) );

        }

        _fgLayoutManager = _fgContainer.getFlexiGridLayoutManager();
        _fgLayoutManager.setFlexiGridPanelModel( this );

        _orientation = orientation;

        JLabel component = new JLabel( "This is a test of this thing" );
        _titleComponents.add( component );
        _fgContainer.add( component, new FlexiGridBasicConstraint( "chumbly", -1, 0 ), -1, _key );

    }

    public void setTitleComponents( final Component... titleComponents ) {

        for ( Component c : _titleComponents ) {

            _fgContainer.remove( c, _key );

        }

        _titleComponents.clear();

        int col = 0;
        for ( Component c : titleComponents ) {

            _titleComponents.add( c );
            _fgContainer.add( c, new FlexiGridBasicConstraint( "title@" + col, -1, col ), -1, _key );

            col += 1;

        }

    }

    public void setTitleComponents( final @NotNull List<Component> titleComponents ) {

        setTitleComponents( titleComponents.toArray( new Component[0] ) );

    }

    public boolean isMsgTraceMode() {

        return _msgTraceMode;

    }

    private void logMaybe( final String msg ) {

        if ( isMsgTraceMode() ) {

            Logger.logMsg( msg );

        }

    }

    public int size() {

        return _slices.size();

    }

    public boolean isEmpty() {

        return _slices.isEmpty();

    }

    /**
     Add a new slice before or after a previously marked slice.
     @param newSlice the new slice which is to be added.
     @param addAfter {@code true} if the new slice is to be added after the marked slice; {@code false} if the new slice is to be added after
     the marked slice.
     @param markedSlice the marked slice.
     */

    public void addAtMarker(
            final @NotNull SLICE newSlice,
            final boolean addAfter,
            final @NotNull SLICE markedSlice
    ) {

        checkOwnership( "FlexiGridPanelModel.addAtMarker(newSlice)", newSlice, true, OwnershipExpectation.OWNED_BY_NO_MODEL );
        checkOwnership( "FlexiGridPanelModel.addAtMarker(markedSlice)", markedSlice, true, OwnershipExpectation.OWNED_BY_THIS_MODEL );

        @NotNull Optional<Integer> optMarkerIx = markedSlice.getOptCurrentIndex();
        if ( optMarkerIx.isPresent() ) {

            add( newSlice,
                 optMarkerIx.get().intValue() + ( addAfter ? 1 : 0 ) );

        } else {

            throw new IllegalArgumentException(
                    "FlexiGridPanelModel.addAtMarker:  " +
                    "slice named " + ObtuseUtil.enquoteToJavaString( markedSlice.getName() ) +
                    " and marked " + ObtuseUtil.enquoteToJavaString( markedSlice.getMarkerTag() ) +
                    " has no current index"
            );

        }

    }

    /**
     Add a slice to the end of this instance's slices.
     <p>An attempt to add a slice to a model that it is already owned by is silently ignore.</p>
     @param slice the slice to be added.
     @throws IllegalArgumentException if the slice is already owned by some other model.
     */

    public void add( final @NotNull SLICE slice ) {

        bulkAdd( "FGPM.add(slice)", ObtuseCollections.addThings( new ArrayList<>(), slice ), -1 );

    }

    /**
     Add a slice at a specified index in this instance's slices.
     @param slice the slice to be added.
     @param index the index that the to-be-added slice should after it has been added. A negative index is equivalent to an
     index equal to the number of slices already in this instance (i.e. it adds the slice to the end of this instance's slices).
     @throws IndexOutOfBoundsException if there are not at least {@code index} slices already in this instance's slices.
     */

    public void add( final @NotNull SLICE slice, int index ) {

        bulkAdd( "FGPM.add(slice),index", ObtuseCollections.addThings( new ArrayList<>(), slice ), index );

    }

    /**
     Add an array of slices to the end of this instance's slices.
     @param slices the slices to be added.
     @return {@code true} if any slices where actually added (i.e. if {@code slices.length > 0}); {@code false} otherwise.
     */

    public boolean addAll( final @NotNull SLICE @NotNull [] slices ) {

        return bulkAdd(
                "FGPM.addAll(slice[])",
                ObtuseCollections.addThings( new ArrayList<>(), slices ),
                -1
        );

    }

    /**
     Add an array of slices to this instance's slices at a specified index.
     @param slices the slices to be added.
     @param index the index at which the slices should be added.
     The result is that this instance will contain a collection of slices which is conceptually equivalent to
     <blockquote>the {@code index} slices previously in this instance followed by the slices in the {@code slices} array
     followed by the rest of the slices previously in this instance.</blockquote>
     A negative index is equivalent to an
     index equal to the number of slices already in this instance (i.e. it adds the slices to the end of this instance's slices).
     @return {@code true} if any slices where actually added (i.e. if {@code slices.length > 0}); {@code false} otherwise.
     @throws IndexOutOfBoundsException if there are not at least {@code index} slices already in this instance's slices.
     */

    public boolean addAll( final @NotNull SLICE @NotNull [] slices, int index ) {

        return bulkAdd(
                "FGPM.addAll(slice[],index)",
                ObtuseCollections.addThings( new ArrayList<>(), slices ),
                index
        );

    }

    /**
     Add a collection of slices to the end of this instance's slices.
     @param slices the slices to be added.
     @return {@code true} if any slices where actually added (i.e. if {@code slices.size() > 0}); {@code false} otherwise.
     */

    public boolean addAll( final @NotNull Collection<SLICE> slices ) {

        return bulkAdd(
                "FGPM.addAll(Collection(slices))",
                slices,
                -1
        );

    }

    /**
     Add a collection of slices to this instances slices at a specified index.
     @param slices the slices to be added.
     @param index the index at which the slices should be added.
     The result is that this instance will contain a collection of slices which is conceptually equivalent to
     <blockquote>the {@code index} slices previously in this instance followed by the slices in the {@code slices} collection
     followed by the rest of the slices previously in this instance.</blockquote>
     A negative index is equivalent to an
     index equal to the number of slices already in this instance (i.e. it adds the slices to the end of this instance's slices).
     @return {@code true} if any slices where actually added (i.e. if {@code slices.size() > 0}); {@code false} otherwise.
     @throws IndexOutOfBoundsException if there are not at least {@code index} slices already in this instance's slices.
     */

    public boolean addAll( final @NotNull Collection<SLICE> slices, int index ) {

        return bulkAdd(
                "FGPM.addAll(Collection(slices),index)",
                slices,
                index
        );

    }

    /**
     Add a collection of slices to this instance's slices at a specified index.
     @param who the name of the method that our user called (used in trace and (possibly) in thrown exceptions.
     @param slices the slices to be added.
     @param index the index at which the slices are to be added.
     @return {@code true} if any slices where actually added (i.e. if {@code slices.size() > 0}); {@code false} otherwise.
     @throws IndexOutOfBoundsException if there are not at least {@code index} slices already in this instance's slices.
     @throws IllegalArgumentException if any of the {@link FlexiGridModelSlice} instances in the specified collection are {@code null}.
     There may be other situations which result in an IllegalArgumentException being thrown (sorry but there's a lot of code involved).
     */

    private boolean bulkAdd( final @NotNull String who, final @NotNull Collection<SLICE> slices, int index ) {

        if ( slices.isEmpty() ) {

            return false;

        }

        verify( "bulkAdd@" + who, "/start" );

        checkForNullSlices( who, slices );

        checkOwnership( who, slices, true, OwnershipExpectation.OWNED_BY_NO_MODEL );

        List<SLICE> newSlices = new ArrayList<>();

        for ( SLICE slice : slices ) {

            if ( _slicesSet.add( slice ) ) {

                newSlices.add( slice );

            }

        }

        if ( newSlices.isEmpty() ) {

            return false;

        }

        if ( index < 0 ) {

            _slices.addAll( newSlices );

        } else {

            _slices.addAll( index, newSlices );

        }

        for ( FlexiGridModelSlice slice : slices ) {

            slice.setOwner( this, false );

        }

        // Just add the components in the new slices that are marked as visible to the container
        // (we don't add the components from invisible slices until/if they become visible later).
        //
        // Note that it really doesn't matter where they land in the container's list/array of components since our renumber method decides
        // where they land on the visible screen based on their location in our list of slices.

        boolean visibleChanges = false;
        for ( SLICE slice : newSlices ) {

            if ( slice.isVisible() ) {

                flipVisibility( who, slice, false );

                visibleChanges = true;

            }

        }

        if ( visibleChanges ) {

            _fgContainer.validate();

        } else {

            verify( "bulkAdd@" + who, "/end" );

        }

        return true;

    }

    /* package private */ void flipVisibility( final @NotNull String who, final FlexiGridModelSlice slice, final boolean renumber ) {

        @SuppressWarnings("unchecked") SLICE xSlice = (SLICE)slice;

        checkOwnership( who, xSlice, true, OwnershipExpectation.OWNED_BY_THIS_MODEL );

        if ( slice.isVisible() ) {

            SortedMap<Integer, FlexiGridItemInfo> dataMap = slice.getDataMap();

            for ( int ix : dataMap.keySet() ) {

                _fgContainer.add( dataMap.get( ix ).component(), dataMap.get( ix ).getInfo(), -1, _key );

            }

            logMaybe( who + ":  slice made visible - " + slice );

        } else {

            SortedMap<Integer, FlexiGridItemInfo> dataMap = slice.getDataMap();

            for ( int ix : dataMap.keySet() ) {

                _fgContainer.remove( dataMap.get( ix ).component(), _key );

            }

            logMaybe( who + ":  slice made invisible - " + slice );

        }

        _fgContainer.validate();

    }

    public void remove( final @NotNull SLICE slice ) {

        checkOwnership( "FlexiGridPanelModel.flipVisibility", slice, true, OwnershipExpectation.OWNED_BY_THIS_MODEL );

        if ( _slicesSet.remove( slice ) ) {

            _slices.remove( slice );

            // If the slice is currently visible, remove its components from the container.

            if ( slice.isVisible() ) {

                flipVisibility( "remove", slice, true );

            }

        }

    }

    private void checkForNullSlices( final @NotNull String who, final @NotNull Collection<SLICE> slices ) {

        List<Integer> nullIndices = new ArrayList<>();
        int ix = 0;
        for ( FlexiGridModelSlice slice : slices ) {

            if ( slice == null ) {

                nullIndices.add( ix );

            }

            ix += 1;

        }

        if ( !nullIndices.isEmpty() ) {

            if ( nullIndices.size() == 1 ) {

                throw new IllegalArgumentException( who + ":  element " + nullIndices.get( 0 ) + " in collection is null" );

            } else if ( nullIndices.size() > 10 ) {

                throw new IllegalArgumentException(
                        who + ":  " + nullIndices.size() + " elements in collection are null" +
                        " (first 10 null indices are " + nullIndices.subList( 0, 9 ) + ")"
                );

            } else {

                throw new IllegalArgumentException( who + ":  elements " + nullIndices + " in collection are null" );

            }

        }

    }

    public void verify( final @NotNull String who, final @NotNull String why ) {

        Component[] components = _fgContainer.getAsContainer().getComponents();

        Set<Component> allComponentsSet = Collections.unmodifiableSet( ObtuseCollections.addThings( new HashSet<Component>(), components ) );
        Set<Component> unseenComponentsSet = ObtuseCollections.addThings( new HashSet<Component>(), components );

        Map<Component,FlexiGridModelSlice> componentSlice = new HashMap<>();

        int errCount = 0;
        for ( FlexiGridModelSlice slice : _slices ) {

            if ( slice.isVisible() ) {

                SortedMap<Integer, FlexiGridItemInfo> dataMap = slice.getDataMap();
                for ( int ix : dataMap.keySet() ) {

                    FlexiGridItemInfo info = dataMap.get( ix );
                    Component component = info.component();

                    if ( !allComponentsSet.contains( component ) ) {

                        Logger.logErr( who + ":  ERROR encountered component not actually in container - " + LinearLayoutUtil.describeComponent( component ) );
                        errCount += 1;

                    }

                    if ( unseenComponentsSet.contains( component ) ) {

                        unseenComponentsSet.remove( component );

                    } else {

                        Logger.logErr( who + ":  ERROR encountered component more than once - " + LinearLayoutUtil.describeComponent( component ) );
                        errCount += 1;

                    }

                    componentSlice.put( component, slice );

                }

            } else {

                SortedMap<Integer, FlexiGridItemInfo> dataMap = slice.getDataMap();
                for ( int ix : dataMap.keySet() ) {

                    FlexiGridItemInfo info = dataMap.get( ix );
                    Component component = info.component();

                    if ( allComponentsSet.contains( component ) ) {

                        Logger.logErr( who + "/invisible:  ERROR encountered component is in container - " + LinearLayoutUtil.describeComponent( component ) );
                        errCount += 1;

                    }

                    componentSlice.put( component, slice );

                }
            }

        }

        for ( Component titleComponent : _titleComponents ) {

            unseenComponentsSet.remove( titleComponent );

        }

        if ( !unseenComponentsSet.isEmpty() ) {

            List<String> unseenComponentDescriptions = getComponentDescriptions( unseenComponentsSet );

            Logger.logErr( who + ":  ERROR some components were never seen - " + unseenComponentDescriptions );
            errCount += 1;

        }

        if ( errCount == 0 ) {

            logMaybe( who + ":  no errors found in model " + getName() );

            ObtuseUtil.doNothing();

        } else {

            throw new HowDidWeGetHereError( who + ":  " + errCount + " error" + ( errCount == 1 ? "" : "s" ) + " found in model " + getName() );

        }

        ObtuseUtil.doNothing();

    }

    @NotNull
    public List<String> getComponentDescriptions( final Collection<Component> unseenComponentsSet ) {

        List<String> unseenComponentDescriptions = new FormattingVector<>();
        for ( Component component : unseenComponentsSet ) {

            unseenComponentDescriptions.add( LinearLayoutUtil.describeComponent( component ) );

        }
        return unseenComponentDescriptions;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<List<SLICE>> checkOwnership(
            final @NotNull String who,
            final @NotNull SLICE slice,
            final boolean boomOnError,
            final @NotNull OwnershipExpectation ownershipExpectation
    ) {

        return checkOwnership( who, ObtuseCollections.addThings( new ArrayList<>(), slice ), boomOnError, ownershipExpectation );

    }

    @NotNull
    public Optional<List<SLICE>> checkOwnership(
            final @NotNull String who,
            final @NotNull Collection<SLICE> slices,
            final boolean boomOnError,
            final @NotNull OwnershipExpectation ownershipExpectation
    ) {

        List<SLICE> bogusSlices = null;
        for ( SLICE slice : slices ) {

            @NotNull Optional<FlexiGridPanelModel> optOwner = slice.getOptOwner();
            String msg = null;
            if ( optOwner.isPresent() ) {

                FlexiGridPanelModel owner = optOwner.get();
                switch ( ownershipExpectation ) {

                    case OWNED_BY_THIS_MODEL:

                        if ( owner != this ) {

                            msg = "slice is already owned by a different model - " + slice;

                        }

                        break;

                    case OWNED_BY_SOME_OTHER_MODEL:

                        if ( owner == this ) {

                            msg = "slice is owned by this model - " + slice;

                        }

                        break;

                    case OWNED_BY_NO_MODEL:

                        msg = "slice is owned by model - " + slice;

                        break;

                    case OWNED_BY_SOME_MODEL:

                        // Life is wonderful.

                }

            } else {

                switch ( ownershipExpectation ) {

                    case OWNED_BY_THIS_MODEL:

                        msg = "slice is not owned by any model - " + slice;

                        break;

                    case OWNED_BY_SOME_OTHER_MODEL:

                        msg = "slice is not owned by this model - " + slice;

                        break;

                    case OWNED_BY_NO_MODEL:

                        break;

                    case OWNED_BY_SOME_MODEL:

                        msg = "slice is owned by a model - " + slice;

                }

            }

            if ( msg != null ) {

                if ( boomOnError ) {

                    throw new IllegalArgumentException( msg );

                } else {

                    if ( bogusSlices == null ) {

                        bogusSlices = new ArrayList<>();

                    }

                    bogusSlices.add( slice );

                }

            }

        }

        return Optional.ofNullable( bogusSlices );

    }

    public boolean isSliceVisible( final int row, final int column ) {

        try {

            FlexiGridModelSlice slice = _slices.get( _orientation == Orientation.ROWS ? row : column );

            return slice.isVisible();

        } catch ( Exception e ) {

            e.printStackTrace();
            System.err.flush();

            logSlices( "FlexiGridPanelModel.isSliceVisible" );

            throw e;

        }

    }

    public void logSlices( final @NotNull String who ) {

        logMaybe( who + ":  slices in " + getName() );
        int ix = 0;
        for ( FlexiGridModelSlice slice : _slices ) {

            logMaybe( who + " - slice[" + ix + "]@" + slice.getOptCurrentIndex() + " = " + slice );
            ix += 1;

        }

    }

    public List<SLICE> getSlicesInOrder() {

        return Collections.unmodifiableList( _slices );

    }

    public List<? super SLICE> getVisibleSlices() {

        List<SLICE> visibleSlices = new ArrayList<>();
        for ( SLICE slice : getSlicesInOrder() ) {

            if ( slice.isVisible() ) {

                visibleSlices.add( slice );

            }

        }

        return Collections.unmodifiableList( visibleSlices );

    }

    public Set<SLICE> getSlicesSet() {

        return Collections.unmodifiableSet( _slicesSet );

    }

    /**
     Force the renumbering of this instance's slices in the model and in the underlying {@link FlexiGridContainer}.
     When this method returns, this instance's slices will have indices starting at 0 and incrementing by 1.
     This instance's corresponding elements in the underlying {@code FlexiGridContainer} will have row or column values,
     depending on the orientation of this model, that are equal to their corresponding slices in this model.
     @param who the name of the invoking method (used it trace messages and possibly in thrown exceptions).
     @throws IllegalArgumentException if something goes wrong (sorry but there's a lot of code involved).
     */

    public void renumber( final @NotNull String who, boolean invalidate ) {

        verify( "renumber@" + who, "/start" );

        int newIx = 0;
        boolean rowOrientation = getOrientation() == Orientation.ROWS;

        for ( FlexiGridModelSlice slice : _slices ) {

            if ( slice.isVisible() ) {

                slice.setCurrentIndex( newIx );
                for ( int ix : slice.getDataMap().keySet() ) {

                    FlexiGridItemInfo element = slice.getDataMap().get( ix );
                    logMaybe( LinearLayoutUtil.describeComponent( element.component() ) + " set to index " + newIx );
                    FlexiGridBasicConstraint constraint = _fgLayoutManager.getMandatoryBasicConstraint( element.component() );
                    if ( rowOrientation ) {

                        constraint.changeRow( constraint.getRow(), newIx );

                    } else {

                        constraint.changeColumn( constraint.getCol(), newIx );

                    }

                }

                newIx += 1;

            }

        }

        if ( invalidate ) {

            _fgContainer.validate();

        }

        _fgLayoutManager.flushCache( who + "/renumber" );

        verify( "renumber@" + who, "/end" );

    }

    /**
     Get this model's orientation as specified when this instance was created.
     @return {@link Orientation#ROWS} or {@link Orientation#COLUMNS} as appropriate.
     */

    public Orientation getOrientation() {

        return _orientation;

    }

    /**
     Get this instance's underlying {@link FlexiGridContainer} instance.
     @return this instance's underlying {@link FlexiGridContainer} instance.
     */

    @NotNull
    public FlexiGridContainer getFlexiGridContainer() {

        return _fgContainer;

    }

    /**
     Get this instance's underlying {@link FlexiGridLayoutManager} instance.
     @return this instance's underlying {@link FlexiGridLayoutManager} instance.
     */

    @NotNull
    public FlexiGridLayoutManager getFlexiGridLayoutManager() {

        return _fgLayoutManager;

    }

    @NotNull
    public String getName() {

        return _name;

    }

    public String toString() {

        return "FlexiGridPanelModel( " + ObtuseUtil.enquoteToJavaString( getFlexiGridContainer().getName() ) + " )";

    }

}
