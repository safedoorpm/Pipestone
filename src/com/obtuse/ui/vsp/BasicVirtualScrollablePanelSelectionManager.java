package com.obtuse.ui.vsp;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

import com.obtuse.util.Logger;
import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 Created by danny on 2020/01/04.
 */

public class BasicVirtualScrollablePanelSelectionManager<E extends VirtualScrollableElement>
        implements VirtualScrollablePanel.VirtualScrollablePanelSelectionManager {

    private final SortedSet<UniqueId> _selectedUniqueIds = new TreeSet<>();
    private final VirtualScrollablePanelModel<E> _panelModel;

    private UniqueId _lastSelection = null;

    public BasicVirtualScrollablePanelSelectionManager( final VirtualScrollablePanelModel<E> panelModel ) {
        super();

        _panelModel = panelModel;

    }

    @Override
    public boolean isSelected( final UniqueId uniqueId ) {

        return _selectedUniqueIds.contains( uniqueId );

    }

    @Override
    public void setSelected( final UniqueId uniqueId, final boolean selected ) {

        if ( selected ) {

            _selectedUniqueIds.add( uniqueId );
            _lastSelection = uniqueId;

            showStatus( "setSelected", uniqueId, true );

        } else {

            _selectedUniqueIds.remove( uniqueId );
            _lastSelection = null;

            showStatus( "setSelected", uniqueId, false );

        }

    }

    public void showStatus( @NotNull final String who, @Nullable UniqueId uniqueId, boolean nowSelected ) {

        Logger.logMsg(
                who + ":  " +
                (
                        uniqueId == null
                                ?
                                "nothing just selected"
                                :
                                ( uniqueId + " just " + ( nowSelected ? "selected" : "unselected" ) )
                )
        );

        for ( UniqueId tmpUniqueId : _selectedUniqueIds ) {

            Logger.logMsg( "    " + tmpUniqueId );

        }

    }

    @Override
    public void clearAllSelected() {

        _selectedUniqueIds.clear();

    }

    @Override
    public boolean extendSelection( final UniqueId uniqueId ) {

        return false;

    }

}
