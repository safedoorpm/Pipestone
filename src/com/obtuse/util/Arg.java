/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * An argument managed by {@link ArgParser}.
 */

public abstract class Arg {

    private final String _keyword;

    protected Arg( String keyword ) {
        super();

        _keyword = keyword;
    }

    public abstract void process( String keyword, String arg );

    public String getKeyword() {

        return _keyword;

    }

}
