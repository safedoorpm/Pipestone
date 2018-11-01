/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.exceptions;

import com.obtuse.util.Range;
import com.obtuse.util.Ranges;

/**
 * Thrown by {@link Ranges} if a {@link Range} passed to add() ultimately proves to be unacceptable.
 */

@SuppressWarnings("UnusedDeclaration")
public class RejectRangeException extends Exception {

    public RejectRangeException() {
        super();

    }

    public RejectRangeException( final String why ) {
        super( why );

    }

    public RejectRangeException( final String why, final Throwable e ) {
        super( why, e );

    }

    @SuppressWarnings("EmptyMethod")
    public String toString() {

        return super.toString();

    }

}
