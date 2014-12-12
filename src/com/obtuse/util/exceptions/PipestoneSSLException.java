/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.exceptions;

import com.obtuse.util.Trace;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class PipestoneSSLException extends Exception {

    public PipestoneSSLException() {
        super();
        Trace.event( this );
    }

    public PipestoneSSLException( String why ) {
        super( why );
        Trace.event( this );
    }

    public PipestoneSSLException( String why, Throwable e ) {
        super( why, e );
        Trace.event( this );
    }

}
