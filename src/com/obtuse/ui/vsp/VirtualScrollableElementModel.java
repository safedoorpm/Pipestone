package com.obtuse.ui.vsp;

import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/19.
 */

public interface VirtualScrollableElementModel<E extends VirtualScrollableElement> {

    UniqueID getUniqueID();

    boolean isVisible();

    @NotNull
    VirtualScrollablePanelModel<E> getMandatoryPanelModel();

}
