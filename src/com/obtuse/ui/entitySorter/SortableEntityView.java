package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import javax.swing.*;

/**
 A {@link javax.swing.JComponent} capable of living within a {@link SortedPanel}.
 <p/>
 Bad things will probably happen if a class implementing this method is not derived from the {@link javax.swing.JComponent} class.
 */

public interface SortableEntityView<K extends Comparable<K>,E> {

    public K getActiveKey();

    public void setActiveKey( K key );

    E getEntity();

}
