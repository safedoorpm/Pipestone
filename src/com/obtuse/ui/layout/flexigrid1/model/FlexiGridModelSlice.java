/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.FlexiGridCache1;
import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.FormattingVector;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Describe a row or a column of components managed by a {@link FlexiGridPanelModel}.
 */

public class FlexiGridModelSlice implements Comparable<FlexiGridModelSlice> {

    private static final SimpleUniqueIntegerIdGenerator s_idGenerator =
            new SimpleUniqueIntegerIdGenerator( FlexiGridCache1.class.getCanonicalName() + " - marker id generator" );

    private final int _id = s_idGenerator.getUniqueId();

    @NotNull
    private final String _name;
    private final FlexiGridPanelModel.Orientation _orientation;
    private int _currentIndex = -1;
    private FlexiGridPanelModel<? extends FlexiGridModelSlice> _owner;
    private final SortedMap<Integer,FlexiGridItemInfo> _dataMap;
    private boolean _markerTagCrystallized;
    private String _markerTag;
    private boolean _visible = true;

    public FlexiGridModelSlice(
            final @NotNull String name,
            final @NotNull FlexiGridPanelModel.Orientation orientation,
            final @NotNull SortedMap<Integer,FlexiGridItemInfo> dataMap
    ) {
        super();

        _name = name;

        _orientation = orientation;

        _dataMap = new TreeMap<>( dataMap );

    }

    public FlexiGridModelSlice(
            final @NotNull String name,
            final @NotNull FlexiGridPanelModel.Orientation orientation
    ) {
        this( name, orientation, new TreeMap<>() );

    }

    public FlexiGridPanelModel.Orientation getOrientation() {

        return _orientation;

    }

    /**
     Make a slice visible or invisible.
     <p>If this call actually changes the visibility of this slice then this slice's components will be
     added or removed from the underlying container depending on whether they are made visible or invisible by this call.
     This is almost certainly an irrelevant implementation detail unless any of this slice's components are being managed
     in ways which change depending upon whether or not the component is actually in a container.</p>
     @param visible {@code true} if this slice is to be made visible; {@code false} otherwise.
     @return {@code true} if this call actually changed this slice's visibility; {@code false} otherwise.
     */

    public boolean setVisible( final boolean visible ) {

        verifySlice( ".setVisible( " + visible + ")", "/before" );

        if ( visible == _visible ) {

            return false;

        } else {

            if ( hasOwner() ) {

                verifySlice( ".setVisible( " + visible + ")", ( visible ? "visible" : "invisible" ) + "/before" );

                _visible = visible;

                _owner.flipVisibility( "FGMS.setVisible", this, true );

            } else {

                _visible = visible;

            }

            verifySlice( ".setVisible( " + visible + ")", ( visible ? "visible" : "invisible" ) + "/after" );

            return true;

        }

    }

    @SuppressWarnings("unused")
    @NotNull
    public java.util.List<String> getComponentDescriptions() {

        java.util.List<String> unseenComponentDescriptions = new FormattingVector<>();
        for ( FlexiGridItemInfo info : _dataMap.values() ) {

            unseenComponentDescriptions.add( LinearLayoutUtil.describeComponent( info.component() ) + "@" + getWhere( info ) );

        }

        return unseenComponentDescriptions;

    }

    @NotNull
    public static String getWhere( final FlexiGridItemInfo info ) {

        String where;
        try {

            where = info.getInfo().getBasicConstraint().locationString();

        } catch ( Throwable e ) {

            where = "<<caught an exception>>";

        }
        return where;
    }

    public void verifySlice( final @NotNull String methodName, final @NotNull String why ) {

        @NotNull Optional<FlexiGridPanelModel> optOwner = getOptOwner();
        optOwner.ifPresent(
                flexiGridPanelModel ->
                        flexiGridPanelModel.verify(
                                "FlexiGridModelSlice(" +
                                ObtuseUtil.enquoteToJavaString( getName() ) +
                                ")" +
                                methodName,
                                why + "/slice"
                        )
        );

    }

    public boolean isVisible() {

        return _visible;

    }

    public void setComponent(
            final int ix,
            final @Nullable Component component,
            final @NotNull FlexiGridBasicConstraint.HJustification hJustification,
            final @NotNull FlexiGridBasicConstraint.VJustification vJustification
    ) {

        if ( component == null ) {

            setComponent( ix, null );

            return;

        }

        boolean rowOrientation = getOrientation().isRowOrientation();

        int row = rowOrientation ? _currentIndex : ix;
        int col = rowOrientation ? ix : _currentIndex;
        FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                new FlexiGridBasicConstraint( _name + " @ " + ix, row, col )
                        .setHorizontalJustification( hJustification )
                        .setVerticalJustification( vJustification )
        );

        setComponent( ix, new FlexiGridItemInfo( row, col, component, constraintsTable ) );

    }

    public void setComponent( final int ix, final @Nullable FlexiGridItemInfo itemInfo ) {

        boolean changed;

        FlexiGridItemInfo oldItemInfo = _dataMap.get( ix );
        if ( oldItemInfo == itemInfo ) {

            return;

        }

        boolean wasVisible = isVisible() && hasOwner();
        if ( wasVisible ) {

            setVisible( false );

        }

        if ( itemInfo == null ) {

            changed = _dataMap.remove( ix ) != null;

        } else {

            changed = _dataMap.put( ix, itemInfo ) != itemInfo;

        }

        if ( !changed ) {

            throw new HowDidWeGetHereError(
                    "FlexiGridModelSlice.setComponent:  " +
                    "changing dataMap[" + ix + "] from " + oldItemInfo + " to " + itemInfo + " was a null change"
            );

        }

        if ( wasVisible ) {

            setVisible( true );

        }

    }

    /**
     Set this instance's marker tag.
     <p>A marker tag can be used to indicate where in a {@link FlexiGridPanelModel} and new slice should be placed.
     An instance's marker tag is crystallized (becomes impossible to change) once it has been set or queried.</p>
     @param markerTag this instance's marker tag.
     @return this instance.
     @throws IllegalArgumentException if this instance's marker tag is already crystallized.
     */

    @SuppressWarnings("UnusedReturnValue")
    public FlexiGridModelSlice setMarkerTag( final @NotNull String markerTag ) {

        if ( _markerTagCrystallized ) {

            throw new IllegalArgumentException( "FlexiGridModelSlice.setMarkerTag:  marker tag is already crystalized to " + ObtuseUtil.enquoteJavaObject( _markerTag ) );

        }

        _markerTagCrystallized = true;

        _markerTag = markerTag;

        return this;

    }

    /**
     Get this instance's marker tag.
     <p>The act of getting an instance's marker tag crystallizes it to {@code null} if it has not already been set.</p>
     @return this instance's marker tag.
     */

    @Nullable
    public String getMarkerTag() {

        _markerTagCrystallized = true;

        return _markerTag;

    }

    /**
     Determine if this instance's marker tag has crystallized.
     @return {@code true} if this instance's marker tag has crystallized (become impossible to change); {@code false} otherwise.
     */

    @SuppressWarnings("unused")
    public boolean isMarkerTagCrystallized() {

        return _markerTagCrystallized;

    }

    /**
     Get this instance's unique id.
     No two {@code FlexiGridModelSlice} instances in this JVM will ever share the same tag.
     @return this instance's unique id.
     */

    public final int getId() {

        return _id;

    }

    /**
     Get a reference to this instance's data map.
     @return a reference to this instance's data map.
     */

    @NotNull
    public SortedMap<Integer,FlexiGridItemInfo> getDataMap() {

        return Collections.unmodifiableSortedMap( _dataMap );

    }

    /* package private */ void setOwner( final @NotNull FlexiGridPanelModel<? extends FlexiGridModelSlice> owner, @SuppressWarnings("SameParameterValue") final boolean verify ) {

        if ( _owner == null ) {

            _owner = owner;
            _currentIndex = -1;

            if ( verify ) {

                verifySlice( ".setOwner( " + owner.getName() + ")", ( isVisible() ? "visible" : "invisible" ) );

            }

        } else {

            throw new IllegalArgumentException( "FlexiGridModelSlice.setOwner:  use replaceOwner to change/replace the current owner" );

        }

    }

    /* package private */
    @SuppressWarnings("unused")
    FlexiGridPanelModel replaceOwner( final @NotNull FlexiGridPanelModel<FlexiGridModelSlice> newOwner ) {

        FlexiGridPanelModel oldOwner = _owner;
        _owner = newOwner;

        return oldOwner;

    }

    /* package private */
    @SuppressWarnings("unused")
    FlexiGridPanelModel clearOwner() {

        FlexiGridPanelModel oldOwner = _owner;
        _owner = null;

        _currentIndex = -1;

        return oldOwner;

    }

    /* package private */ void setCurrentIndex( final int currentIndex ) {

        _currentIndex = currentIndex;

    }

    public boolean hasOwner() {

        return _owner != null;

    }

    @NotNull
    Optional<FlexiGridPanelModel> getOptOwner() {

        return Optional.ofNullable( _owner );

    }

    @NotNull
    public String getName() {

        return _name;

    }

    @NotNull
    public Optional<Integer> getOptCurrentIndex() {

        if ( _currentIndex < 0 ) {

            return Optional.empty();

        } else {

            return Optional.of( _currentIndex );

        }

    }

    public int compareTo( final @NotNull FlexiGridModelSlice rhs ) {

        return Integer.compare( _id, rhs._id );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof FlexiGridModelSlice && compareTo( (FlexiGridModelSlice)rhs ) == 0;

    }

    public int hashCode() {

        return Integer.hashCode( _id );

    }

    @NotNull
    public String toString() {

        Optional<Integer> optCurrentIndex = getOptCurrentIndex();
        return "FlexiGridModelSlice( " +
               ( isVisible() ? "visible" : "invisible" ) + ", " +
               ( hasOwner() ? ( "within " + _owner.getName() ) : "***ORPHAN***" ) +
               ", currentIndex=" + ( optCurrentIndex.isPresent() ? optCurrentIndex.get() : "unassigned" ) +
               ", markerTag=" + _markerTag + ( _markerTagCrystallized ? " (crystallized)" : " (not crystallized)" ) +
               ", orientation=" + getOrientation() +
               " )";

    }

}
