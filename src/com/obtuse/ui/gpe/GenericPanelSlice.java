package com.obtuse.ui.gpe;

import com.obtuse.ui.layout.flexigrid1.model.FlexiGridModelSlice;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;

/**
 A slice in the underlying {@link FlexiGridPanelModel}.
 */

public abstract class GenericPanelSlice extends FlexiGridModelSlice {

    private final GenericPanelRowModel<? extends GenericPanelSlice> _model;

    public GenericPanelSlice( final GenericPanelRowModel<? extends GenericPanelSlice> model ) {
        super( model.getName(), FlexiGridPanelModel.Orientation.ROW );

        _model = model;

    }

    public GenericPanelRowModel<? extends GenericPanelSlice> getModel() {

        return _model;

    }

}
