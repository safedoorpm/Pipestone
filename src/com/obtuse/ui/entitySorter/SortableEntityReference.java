package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 Something which can be sorted and managed by a {@link SortedPanelModel}.
 */

public class SortableEntityReference<K extends Comparable<K>,E extends SortableEntity> {

    private K _key;

    private final E _value;

    public SortableEntityReference( K key, E value ) {
        super();

	_key = key;

	_value = value;

    }

    public K getActiveKey() {

        return _key;

    }

    @NotNull
    public SortableEntityView<K,E> createEntityView() {

	SortableEntityView<K, E> entityView = _value.createEntityView( _key );
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
