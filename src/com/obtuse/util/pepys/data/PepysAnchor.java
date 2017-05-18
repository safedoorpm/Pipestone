package com.obtuse.util.pepys.data;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 Something to maintain a strong reference to {@link PepysEventListener} instances.
 <p/>As long as you maintain a strong reference to a {@link PepysAnchor} objects, the listeners that it holds will also be strongly referenced.
 */

public class PepysAnchor<T extends PepysEventListener> {

    private final Set<T> _listeners = new HashSet<>();
    private final Set<PepysSource> _sources = new HashSet<>();

    public synchronized T anchor( T listener ) {

	_listeners.add( listener );

	return listener;

    }

//    public synchronized PepysSource anchor( PepysSource source ) {
//
//	_sources.add( source );
//
//	return source;
//
//    }

    public synchronized T castOff( T listener ) {

	if ( _listeners.contains( listener ) ) {

	    _listeners.remove( listener );

	}

	return null;

    }

//    public synchronized void castOff( PepysSource source ) {
//
//	if ( _sources.contains( source ) ) {
//
//	    _sources.remove( source );
//
//	}
//
//    }

}
