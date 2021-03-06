package com.obtuse.db.exceptions;

/**
 * Thrown if we are unable to connect to the database server.
 * <p/>
 * Copyright © 2012 Obtuse Systems Corporation.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class ObtuseJDBCgetConnectionFailedException extends Exception {

    public ObtuseJDBCgetConnectionFailedException() {
        super();
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public ObtuseJDBCgetConnectionFailedException( String why ) {
        super(why);
    }

    public ObtuseJDBCgetConnectionFailedException( String why, Throwable e ) {
        super(why,e);
    }
}
