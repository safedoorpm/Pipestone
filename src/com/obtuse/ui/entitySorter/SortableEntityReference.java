package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Something which can be sorted and managed by a {@link SortedPanelModel}.
 */

public class SortableEntityReference<K extends Comparable<K>,E extends SortableEntity> {

    private final K _key;

    private final E _value;

    private final SortedPanelModel<K,E> _panelModel;

    public SortableEntityReference( @NotNull final SortedPanelModel<K,E> panelModel, @NotNull final K key, @NotNull final E value ) {
        super();

	_panelModel = panelModel;

	_key = key;

	_value = value;

    }

    public K getActiveKey() {

        return _key;

    }

    public SortedPanelModel<K, E> getPanelModel() {

        return _panelModel;

    }

    @NotNull
    public SortableEntityView<K,E> createEntityView() {

	SortableEntityView<K, E> entityView = _value.createEntityView( _panelModel, _key );
	if ( entityView == null ) {

	    throw new IllegalArgumentException( "null entity view returned for value {" + _value + "} with key \"" + _key + "\"" );

	}

	return entityView;

    }

    @NotNull
    public E getValue() {

        return _value;

    }

    public String toString() {

        return "SER( key=\"" + _key + "\", value = {" + _value + "}";
    }

}
