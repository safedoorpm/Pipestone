/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 * A long {@link ArgParser} argument.
 */

@SuppressWarnings("UnusedDeclaration")
public abstract class ArgLong extends Arg {

    protected ArgLong( final @NotNull String keyword ) {
        super( keyword );

    }

    public final void process( final @NotNull String keyword, final @NotNull String arg ) {

        try {

            process( keyword, Long.parseLong( arg ) );

        } catch ( NumberFormatException e ) {

            throw new IllegalArgumentException( "invalid \"" + keyword + "\" argument (" + arg + ") - must be a long", e );

        }

    }

    public abstract void process( String keyword, long arg );

    public String toString() {

        return "ArgLong( " + getKeyword() + " )";

    }

}
