/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.db.exceptions;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class ObtuseJDBCDriverLoadFailedException extends Exception {

    public ObtuseJDBCDriverLoadFailedException() {
        super();
    }

    public ObtuseJDBCDriverLoadFailedException( String why ) {
        super(why);
    }

    public ObtuseJDBCDriverLoadFailedException( String why, Throwable e ) {
        super(why,e);
    }
}
