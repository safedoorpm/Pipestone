package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.ui.layout.flexigrid1.FlexiGridCache1;
import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private int _currentIndex = -1;
    private FlexiGridPanelModel _owner;
    private final SortedMap<Integer,FlexiGridItemInfo> _dataMap;
    private boolean _markerTagCrystallized;
    private String _markerTag;

    public FlexiGridModelSlice(
            final @NotNull String name,
            final @NotNull FlexiGridPanelModel owner,
            SortedMap<Integer,FlexiGridItemInfo> dataMap
    ) {
        super();

        _name = name;

        _owner = owner;

        _dataMap = Collections.unmodifiableSortedMap( new TreeMap<>( dataMap ) );

    }

    /**
     Set this instance's marker tag.
     <p>A marker tag can be used to indicate where in a {@link FlexiGridPanelModel} and new slice should be placed.
     An instance's marker tag is crystallized (becomes impossible to change) once it has been set or queried.</p>
     @param markerTag this isntance's marker tag.
     @return this instance.
     @throws IllegalArgumentException if this instance's marker tag is already crystallized.
     */

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

        return _dataMap;

    }

    /* package private */ void setOwner( final @NotNull FlexiGridPanelModel owner ) {

        if ( _owner == null ) {

            _owner = owner;
            _currentIndex = -1;

        } else {

            throw new IllegalArgumentException( "FlexiGridModelSlice.setOwner:  use replaceOwner to change/replace the current owner" );

        }

    }

    /* package private */ FlexiGridPanelModel replaceOwner( final @NotNull FlexiGridPanelModel newOwner ) {

        FlexiGridPanelModel oldOwner = _owner;
        _owner = newOwner;

        return oldOwner;

    }

    /* package private */ FlexiGridPanelModel clearOwner() {

        FlexiGridPanelModel oldOwner = _owner;
        _owner = null;

        _currentIndex = -1;

        return oldOwner;

    }

    /* package private */ void setCurrentIndex( final int currentIndex ) {

        _currentIndex = currentIndex;

    }

    public boolean isOrphan() {

        return _owner == null;

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

        return _name.compareTo( rhs._name );

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof FlexiGridModelSlice && compareTo( (FlexiGridModelSlice)rhs ) == 0;

    }

    public int hashCode() {

        return _name.hashCode();

    }

    @NotNull
    public String toString() {

        Optional<Integer> optCurrentIndex = getOptCurrentIndex();
        return "FlexiGridModelSlice( " +
               ( isOrphan() ? "***ORPHAN***" : ( "within " + _owner.getName() ) ) +
               ", currentIndex=" + ( optCurrentIndex.isPresent() ? optCurrentIndex.get() : "unassigned" ) +
               ", markerTag=" + _markerTag + ( _markerTagCrystallized ? " (crystallized)" : " (not crystallized)" ) +
               ", orientation=" + ( _owner == null ? "unknown" : _owner.getOrientation() ) +
               " )";

    }

}
