package com.obtuse.ui.vsp;

import com.obtuse.util.UniqueID;

/**
 Mark something as being displayable in a {@link VirtualScrollablePanel}.
 */

public interface VirtualScrollableElement {

    /**
     Get a displayable thing's unique id.
     <p/>Each and every distinct 'thing' (i.e. {@link VirtualScrollableElement} instance) that might
     appear within a given {@link VirtualScrollablePanel} must have a different id.
     <p/>One easy way to get such an unique id is to use {@link UniqueID#getJvmLocalUniqueID()}.
     */

    UniqueID getUniqueID();

}
