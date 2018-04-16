/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.ui.layout.flexigrid1.*;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.util.ObtuseCollections;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.RandomCentral;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 Provide a high level model for manipulating a {@link com.obtuse.ui.layout.flexigrid1.FlexiGridContainer}.
 */

public class FlexiGridPanelModel {

    public enum Orientation {
        ROWS,
        COLUMNS
    }

    private final String _name;

    private final FlexiGridContainer _fgContainer;
    private final FlexiGridLayoutManager _fgLayoutManager;
    private final Orientation _orientation;

    private final long _key = RandomCentral.nextLong();

    private FlexiGridLayoutManagerCache _cache = null;
    private long _lastCacheSerialNumber = -1;

    private final List<FlexiGridModelSlice> _slices = new LinkedList<>();

    public FlexiGridPanelModel( final @NotNull String name, final @NotNull Orientation orientation ) {
        this( true, name, orientation );

    }

    public FlexiGridPanelModel( final boolean doubleBuffered, final @NotNull String name, final @NotNull Orientation orientation ) {
        super();

        _name = name;

        _fgContainer = new FlexiGridContainer1( doubleBuffered, name, _key );

        _fgLayoutManager = (FlexiGridLayoutManager)_fgContainer.getAsContainer().getLayout();

        _orientation = orientation;

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
            final @NotNull FlexiGridModelSlice newSlice,
            final boolean addAfter,
            final @NotNull FlexiGridModelSlice markedSlice
    ) {

        @NotNull Optional<Integer> optMarkerIx = markedSlice.getOptCurrentIndex();
        if ( optMarkerIx.isPresent() ) {

            add( newSlice, optMarkerIx.get() + ( addAfter ? 1 : 0 ) );

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
     @param slice the slice to be added.
     */

    public void add( final @NotNull FlexiGridModelSlice slice ) {

        bulkAdd( "add(slice)", ObtuseCollections.addThings( new ArrayList<>(), slice ), -1 );

    }

    /**
     Add a slice at a specified index in this instance's slices.
     @param slice the slice to be added.
     @param index the index that the to-be-added slice should after it has been added. A negative index is equivalent to an
     index equal to the number of slices already in this instance (i.e. it adds the slice to the end of this instance's slices).
     @throws IndexOutOfBoundsException if there are not at least {@code index} slices already in this instance's slices.
     */

    public void add( final @NotNull FlexiGridModelSlice slice, int index ) {

        bulkAdd( "add(slice),index", ObtuseCollections.addThings( new ArrayList<>(), slice ), index );

    }

    /**
     Add an array of slices to the end of this instance's slices.
     @param slices the slices to be added.
     @return {@code true} if any slices where actually added (i.e. if {@code slices.length > 0}); {@code false} otherwise.
     */

    public boolean addAll( final @NotNull FlexiGridModelSlice @NotNull [] slices ) {

        return bulkAdd(
                "addAll(slice[])",
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

    public boolean addAll( final @NotNull FlexiGridModelSlice @NotNull [] slices, int index ) {

        return bulkAdd(
                "addAll(slice[],index)",
                ObtuseCollections.addThings( new ArrayList<>(), slices ),
                index
        );

    }

    /**
     Add a collection of slices to the end of this instance's slices.
     @param slices the slices to be added.
     @return {@code true} if any slices where actually added (i.e. if {@code slices.size() > 0}); {@code false} otherwise.
     */

    public boolean addAll( final @NotNull Collection<FlexiGridModelSlice> slices ) {

        return bulkAdd(
                "addAll(Collection(slices))",
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

    public boolean addAll( final @NotNull Collection<FlexiGridModelSlice> slices, int index ) {

        return bulkAdd(
                "addAll(Collection(slices),index)",
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

    private boolean bulkAdd( final @NotNull String who, final @NotNull Collection<FlexiGridModelSlice> slices, int index ) {

        if ( slices.isEmpty() ) {

            return false;

        }

        checkForNullSlices( who, slices );

        if ( index < 0 ) {

            _slices.addAll( slices );

        } else {

            _slices.addAll( index, slices );

        }

        for ( FlexiGridModelSlice slice : slices ) {

            SortedMap<Integer, FlexiGridItemInfo> dataMap = slice.getDataMap();

            for ( int ix : dataMap.keySet() ) {

                _fgContainer.add( dataMap.get( ix ).component(), dataMap.get( ix ).getInfo(), -1, _key );

            }

        }

        renumber( who );

        return true;

    }

    private void checkForNullSlices( final @NotNull String who, final @NotNull Collection<FlexiGridModelSlice> slices ) {

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

                throw new IllegalArgumentException( "FlexiGridPanelModel." + who + ":  element " + nullIndices.get( 0 ) + " in collection is null" );

            } else if ( nullIndices.size() > 10 ) {

                throw new IllegalArgumentException(
                        "FlexiGridPanelModel." + who + ":  " + nullIndices.size() + " elements in collection are null" +
                        " (first 10 null indices are " + nullIndices.subList( 0, 9 ) + ")"
                );

            } else {

                throw new IllegalArgumentException( "FlexiGridPanelModel." + who + ":  elements " + nullIndices + " in collection are null" );

            }

        }

    }

    /**
     Force the renumbering of this instance's slices in the model and in the underlying {@link FlexiGridContainer}.
     When this method returns, this instance's slices will have indices starting at 0 and incrementing by 1.
     This instance's corresponding elements in the underlying {@code FlexiGridContainer} will have row or column values,
     depending on the orientation of this model, that are equal to their corresponding slices in this model.
     @param who the name of the invoking method (used it trace messages and possibly in thrown exceptions).
     @throws IllegalArgumentException if something goes wrong (sorry but there's a lot of code involved).
     */

    public void renumber( final @NotNull String who ) {

        int newIx = 0;
        boolean rowOrientation = getOrientation() == Orientation.ROWS;

        for ( FlexiGridModelSlice slice : _slices ) {

            slice.setCurrentIndex( newIx );
            for ( int ix : slice.getDataMap().keySet() ) {

                FlexiGridItemInfo element = slice.getDataMap().get( ix );
                FlexiGridBasicConstraint constraint = _fgLayoutManager.getMandatoryBasicConstraint( element.component() );
                if ( rowOrientation ) {

                    constraint.changeRow( constraint.getRow(), newIx );

                } else {

                    constraint.changeColumn( constraint.getCol(), newIx );

                }

                slice.setCurrentIndex( newIx );

                newIx += 1;

            }

        }

        _fgContainer.getAsContainer().revalidate();

        _fgLayoutManager.flushCache( who + "/renumber" );

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
