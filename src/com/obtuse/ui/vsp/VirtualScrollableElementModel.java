package com.obtuse.ui.vsp;

import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/19.
 */

public interface VirtualScrollableElementModel<E extends VirtualScrollableElement> {

    UniqueId getUniqueId();

    boolean isVisible();

    @SuppressWarnings("unused")
    @NotNull
    VirtualScrollablePanelModel<E> getMandatoryPanelModel();

}
