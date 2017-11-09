/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.exceptions;

/**
 * Thrown if a syntax error is encountered somewhere.
 */

public class SyntaxErrorException extends Exception {

    public SyntaxErrorException( final String msg ) {
        super( msg );

    }

    public SyntaxErrorException( final String msg, final Throwable e ) {
        super( msg, e );

    }

}

