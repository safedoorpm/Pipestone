/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.db.exceptions;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class ObtuseJDBCDriverLoadFailedException extends Exception {

    public ObtuseJDBCDriverLoadFailedException() {
        super();
    }

    public ObtuseJDBCDriverLoadFailedException( final String why ) {
        super(why);
    }

    public ObtuseJDBCDriverLoadFailedException( final String why, final Throwable e ) {
        super(why,e);
    }
}
