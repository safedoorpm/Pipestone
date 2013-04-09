package com.obtuse.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown if something truly unexpected happens.
 * <p>
 * Copyright © 2008 Obtuse Systems Corporation
 */

public class HowDidWeGetHereError
        extends RuntimeException {

    /**
     * The current oops catcher.
     */

    private static OopsCatcher s_oopsCatcher = null;

    /**
     * Describe how an oops catcher is informed that a {@link HowDidWeGetHereError} instance has been created and is, presumably, about to be thrown.
     */

    public static interface OopsCatcher {

        /**
         * Notify someone about a {@link HowDidWeGetHereError} instance which is, presumably, about to be thrown.
         * @param e the {@link HowDidWeGetHereError} instance which has just been created and is, presumably, about to be thrown.
         */

        void oops( HowDidWeGetHereError e );

    }

    @SuppressWarnings("UnusedDeclaration")
    public HowDidWeGetHereError() {
        super();

        notifyOopsCatcher();

    }

    public HowDidWeGetHereError( String msg ) {
        super( msg );

        notifyOopsCatcher();

    }

    public HowDidWeGetHereError( String msg, Throwable e ) {
        super(msg, e);

        notifyOopsCatcher();

    }

    /**
     * Call the oops catcher if one is defined.
     */

    private void notifyOopsCatcher() {

        if ( HowDidWeGetHereError.s_oopsCatcher != null ) {

            HowDidWeGetHereError.s_oopsCatcher.oops( this );

        }

    }

    /**
     * Specify the opps catcher which is to be called whenever a {@link HowDidWeGetHereError} is created and, presumably, is about to be thrown.
     * @param oopsCatcher the oops catcher which is to catch the oops.
     * @return the previously configured oops catcher (a weak but adequate way to handle multiple oops catchers if everyone cooperates).
     */

    @SuppressWarnings("UnusedDeclaration")
    public static OopsCatcher setOopsCatcher( @Nullable OopsCatcher oopsCatcher ) {

        OopsCatcher oldOopsCatcher = HowDidWeGetHereError.s_oopsCatcher;

        HowDidWeGetHereError.s_oopsCatcher = oopsCatcher;

        return oldOopsCatcher;

    }

}
