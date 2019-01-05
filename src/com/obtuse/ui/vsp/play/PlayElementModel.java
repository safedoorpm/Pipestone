package com.obtuse.ui.vsp.play;

import com.obtuse.ui.vsp.VirtualScrollableElementModel;
import com.obtuse.ui.vsp.VirtualScrollablePanelModel;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/19.
 */

public class PlayElementModel implements VirtualScrollableElementModel<PlayElementData> {

    private final PlayElementData _element;
    public static SimpleUniqueLongIdGenerator
            s_idGenerator = new SimpleUniqueLongIdGenerator( "PlayElementModel id generator" );
    private UniqueId _UniqueId = UniqueId.getJvmLocalUniqueId();
    private VirtualScrollablePanelModel<PlayElementData> _panelModel;

    public PlayElementModel( @NotNull final PlayElementData element ) {

        _element = element;

    }

    public PlayElementData getElementData() {

        return _element;

    }

    @Override
    public UniqueId getUniqueId() {

        return _UniqueId;

    }

    public void setVisible( boolean visible ) {

        _element.setVisible( visible );

    }

    @Override
    public boolean isVisible() {

        return _element.isVisible();

    }

    @NotNull
    public VirtualScrollablePanelModel<PlayElementData> getMandatoryPanelModel() {

        return _panelModel;

    }

    public String toString() {

        return "PlayElementModel( " + _element + " )";

    }

}
