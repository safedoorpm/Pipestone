package com.obtuse.util.pepys.data;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import java.util.HashSet;
import java.util.Set;

/**
 Something to maintain a strong reference to {@link PepysEventListener} instances.
 <p/>As long as you maintain a strong reference to a {@link PepysAnchor} object, the listeners that it holds will also be strongly referenced.
 */

public class PepysAnchor<T extends PepysEventListener> {

    private final Set<T> _listeners = new HashSet<>();

    public synchronized T anchor( final T listener ) {

        _listeners.add( listener );

        return listener;

    }

    public synchronized T castOff( final T listener ) {

        if ( _listeners.contains( listener ) ) {

            _listeners.remove( listener );

        }

        return null;

    }

    public String toString() {

        return "PepysAnchor()";

    }

}
