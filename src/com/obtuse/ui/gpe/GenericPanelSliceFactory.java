package com.obtuse.ui.gpe;

import org.jetbrains.annotations.NotNull;

/**
 A slice factory.
 */

public interface GenericPanelSliceFactory<SLICE extends GenericPanelSlice> {

    @NotNull SLICE createSlice( final GenericPanelRowModel<SLICE> model );

}
