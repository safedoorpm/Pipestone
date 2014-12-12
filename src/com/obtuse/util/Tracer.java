/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

@SuppressWarnings( { "UnusedDeclaration" } )
public interface Tracer {

    void event( String description );

    void event( String description, Throwable e );

    String emitTrace( String description );

    String emitTrace( String description, Throwable e );

    String emitTrace( Throwable e );

}