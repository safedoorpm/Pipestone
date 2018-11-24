package com.obtuse.ui.vsp.play;

import com.obtuse.ui.vsp.AbstractVirtualScrollablePanelModel;
import com.obtuse.ui.vsp.VirtualScrollableElementModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 Created by danny on 2018/11/21.
 */
public class PlayElementPanelModel extends AbstractVirtualScrollablePanelModel<PlayElementData> {

    private final ArrayList<VirtualScrollableElementModel<PlayElementData>> _elementDataModels;

    public PlayElementPanelModel( @NotNull final ArrayList<VirtualScrollableElementModel<PlayElementData>> elementDataModels ) {
        super( PlayElementView::new );

        _elementDataModels = elementDataModels;

    }

    @Override
    public @NotNull CurrentGoals<PlayElementData> getActualCurrentGoals() {

        List<VirtualScrollableElementModel<PlayElementData>> rval = new ArrayList<>();
        for ( VirtualScrollableElementModel<PlayElementData> em : _elementDataModels ) {

            if ( em.isVisible() ) {

                rval.add( em );

            }

        }

//                _nVisibleElements = rval.size();

//                VirtualScrollableElementModel<E> nullEm = null;

        return new CurrentGoals<>( 0, rval );

    }

}
