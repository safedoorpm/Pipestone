package com.obtuse.ui.entitySorter;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public class MyButtonEntity implements SortableEntity {

    private final SortedPanelModel<String, MyButtonEntity> _myModel;

    private final String _word;

    public MyButtonEntity( final SortedPanelModel<String, MyButtonEntity> myModel, final String word ) {

	super();

	_myModel = myModel;
	_word = word;

    }

    public String getWord() {

        return _word;

    }

    @NotNull
    public SortableEntityView createEntityView( final String key ) {

	return new MyButtonView( this, _myModel, _word, key );

    }

    public String toString() {

	return "MyButtonEntity( label = \"" + _word + "\" )";

    }

    @Override
    public <K extends Comparable<K>, E extends SortableEntity> SortableEntityView<K, E> createEntityView( final SortedPanelModel<K, E> panelModel, final K key ) {

	MyButtonView view = new MyButtonView( this, _myModel, _word, (String) key );
	@SuppressWarnings("unchecked") SortableEntityView<K, E> rval = (SortableEntityView<K, E>) view;
	return rval;

    }

}
