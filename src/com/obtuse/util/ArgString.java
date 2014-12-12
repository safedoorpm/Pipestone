/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * A string {@link com.obtuse.util.ArgParser} argument.
 */

public abstract class ArgString extends Arg {

    protected ArgString( String keyword ) {
        super( keyword );
    }

    public String toString() {

        return "ArgString( " + getKeyword() + " )";

    }

}
