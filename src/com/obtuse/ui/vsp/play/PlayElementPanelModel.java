package com.obtuse.ui.vsp.play;

import com.obtuse.ui.vsp.AbstractVirtualScrollablePanelModel;
import com.obtuse.ui.vsp.VirtualScrollableElementModel;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
    public @NotNull CurrentGoals<PlayElementData> getActualCurrentGoals(
            final int firstVisibleElementIx,
            @NotNull final Dimension viewportSize
    ) {

        List<VirtualScrollableElementModel<PlayElementData>> rval = new ArrayList<>();
        for ( VirtualScrollableElementModel<PlayElementData> em : _elementDataModels ) {

            if ( em.isVisible() ) {

                rval.add( em );

            }

        }

        return new CurrentGoals<>( firstVisibleElementIx, 0, rval.size(), rval );

    }

    @Override
    public boolean checkForUpdates() {

        ObtuseUtil.doNothing();

        return false;

    }

    @Override
    public void startNewElementViewAllocationRound() {

        ObtuseUtil.doNothing();

    }

}
