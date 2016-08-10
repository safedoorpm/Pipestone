package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public interface SortableEntity {

    <K extends Comparable<K>, E extends SortableEntity> SortableEntityView<K,E> createEntityView( SortedPanelModel<K, E> panelModel, K key );

}
