package com.obtuse.util.exceptions;

import com.obtuse.util.Trace;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings( { "ClassWithoutToString" } )
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
