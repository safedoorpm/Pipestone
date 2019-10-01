package com.obtuse.ui.gpe;

import com.obtuse.util.SimpleUniqueLongIdGenerator;
import com.obtuse.util.UniqueWithId;
import org.jetbrains.annotations.NotNull;

/**
 A model describing a row in a generic panel.
 */

public abstract class GenericPanelRowModel<
        SLICE extends GenericPanelSlice
//        ,
//        MANAGER extends GenericPanelManager<? /* GenericPanelRowModel<SLICE,?> */, SLICE>
//        ,
//        MANAGER extends GenericPanelManager<GenericPanelRowModel<SLICE,MANAGER>,SLICE>
        > implements UniqueWithId, Comparable<GenericPanelRowModel> {

    private static SimpleUniqueLongIdGenerator s_idGenerator = new SimpleUniqueLongIdGenerator( "GPEM" );

//    private final GenericPanelManager<GenericPanelRowModel<SLICE>,SLICE> _gpm;
    private final GenericPanelManager<SLICE> /*GenericPanelManager<GenericPanelRowModel<SLICE, MANAGER>, SLICE>*/ _pm;

    private final long _id;

    private final String _name;

    private SLICE _ourSlice = null;

    public GenericPanelRowModel(
//            GenericPanelManager<GenericPanelRowModel<SLICE>, SLICE> gpm,
//            final GenericPanelManager<GenericPanelRowModel<SLICE,MANAGER>, SLICE> pm,
            GenericPanelManager<SLICE> pm,
//            GenericPanelManager<GenericPanelRowModel<SLICE>, SLICE> gpm,
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
//    @SuppressWarnings("unused")
//    @NotNull
//    public GenericPanelManager<GenericPanelRowModel<SLICE>,SLICE> /*GenericPanelManager<? extends GenericPanelRowModel<SLICE>, SLICE>*/ getGenericPanelManager() {
//
//        return _gpm;
//
//    }

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

//    public abstract SLICE getRowSlice(
////            final GenericPanelManager modelsliceGenericPanelManager,
//            final GenericPanelSliceFactory<SLICE> sliceFactory
//    );

// THIS ONE IS THE LATEST ONE
//    public abstract SLICE getRowSlice(
////            @NotNull final GenericPanelRowModel<SLICE> model,
//            @NotNull final GenericPanelSliceFactory<SLICE> factory
//    );

//    public abstract <SLICE extends GenericPanelSlice> SLICE getRowSlice(
//            final @NotNull GenericPanelRowModel<SLICE> model,
//            final @NotNull GenericPanelSliceFactory<
//                    GenericPanelRowModel<SLICE>,
//                    SLICE
//                    > factory
//    );
}
