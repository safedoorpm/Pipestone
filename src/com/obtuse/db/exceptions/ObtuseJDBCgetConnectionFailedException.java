/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.db.exceptions;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class ObtuseJDBCgetConnectionFailedException extends Exception {

    public ObtuseJDBCgetConnectionFailedException() {
        super();
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public ObtuseJDBCgetConnectionFailedException( final String why ) {
        super(why);
    }

    public ObtuseJDBCgetConnectionFailedException( final String why, final Throwable e ) {
        super(why,e);
    }
}
