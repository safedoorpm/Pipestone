package com.obtuse.ui.gpe;

import com.obtuse.util.SimpleUniqueLongIdGenerator;
import com.obtuse.util.UniqueWithId;
import org.jetbrains.annotations.NotNull;

/**
 A model describing a row in a generic panel.
 */

@SuppressWarnings("rawtypes")
public abstract class GenericPanelRowModel<
        SLICE extends GenericPanelSlice
        > implements UniqueWithId, Comparable<GenericPanelRowModel> {

    private static SimpleUniqueLongIdGenerator s_idGenerator = new SimpleUniqueLongIdGenerator( "GPEM" );

    private final GenericPanelManager<SLICE> _pm;

    private final long _id;

    private final String _name;

    private SLICE _ourSlice = null;

    public GenericPanelRowModel(
            GenericPanelManager<SLICE> pm,
            @NotNull final String name
    ) {
        super();

        _id = s_idGenerator.getUniqueId();
        _name = name;
        _pm = pm;

    }

    public GenericPanelManager<SLICE> getGenericPanelManager() {

        return _pm;

    }

    public SLICE getRowSlice(
            final @NotNull GenericPanelSliceFactory<SLICE> sliceFactory
    ) {

        if ( _ourSlice == null ) {

            _ourSlice = sliceFactory.createSlice( this ); // new TestSlice( getName() );

        }

        return _ourSlice;

    }

    @NotNull
    public String getName() {

        return _name;

    }

    public long getId() {

        return _id;

    }

    public int compareTo( @NotNull final GenericPanelRowModel rhs ) {

        return Long.compare( _id, rhs._id );

    }

}
