/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 * An argument managed by {@link ArgParser}.
 */

public abstract class Arg {

    private final String _keyword;

    protected Arg( final String keyword ) {
        super();

        _keyword = keyword;
    }

    public abstract void process( final @NotNull String keyword, final @NotNull String arg );

    public String getKeyword() {

        return _keyword;

    }

}
