/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * A clock event.
 */

public abstract class SimpleEvent {

    private final String _description;

    public SimpleEvent( final String description ) {
        super();

        _description = description;

    }

    public abstract void run( FormattedImmutableDate when );

    public String toString() {

        return "ClockEvent( \"" + _description + "\" )";

    }

    @SuppressWarnings("UnusedDeclaration")
    public String getDescription() {

        return _description;

    }

}
