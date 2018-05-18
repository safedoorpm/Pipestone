/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 * An integer {@link ArgParser} argument.
 */

public abstract class ArgInt extends Arg {

    protected ArgInt( final String keyword ) {
        super( keyword );

    }

    public final void process( @NotNull final String keyword, @NotNull final String arg ) {

        try {

            process( keyword, Integer.decode( arg ).intValue() );

        } catch ( NumberFormatException e ) {

            throw new IllegalArgumentException( "invalid \"" + keyword + "\" argument (" + arg + ") - must be an integer", e );

        }

    }

    public abstract void process( String keyword, int arg );

    public String toString() {

        return "ArgInt( " + getKeyword() + " )";

    }

}
