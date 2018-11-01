/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.exceptions;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

/**
 * Thrown if something truly unexpected happens.
 */

public class HowDidWeGetHereError
        extends RuntimeException {

    static {

        setStackTracePrintStream( System.err );

    }

    /**
     The current oops catcher.
     */

    private static OopsCatcher s_oopsCatcher = null;

    /**
     The (optional) {@link PrintStream} that a stack traceback should be printed on if an instance of this class is created.
     <p/>Note that the stack traceback gets printed before this instance gets thrown which is a bit unconventional
     but you'll (hopefully) get used to it.
     */

    private static PrintStream s_stackTracePrintStream = null;

    /**
     Describe how an oops catcher is informed that a {@link HowDidWeGetHereError} instance has been created and is, presumably, about to be thrown.
     */

    public interface OopsCatcher {

        /**
         Notify someone about a {@link HowDidWeGetHereError} instance which is, presumably, about to be thrown.

         @param e the {@link HowDidWeGetHereError} instance which has just been created and is, presumably, about to be thrown.
         Note that a breakpoint in your oops catcher generally (always?) puts you right where you want to be when
         a {@link HowDidWeGetHereError} is about to be thrown.
         */

        void oops( HowDidWeGetHereError e );

    }

    @SuppressWarnings("UnusedDeclaration")
    public HowDidWeGetHereError() {
        super();

        notifyOopsCatcher();

    }

    public HowDidWeGetHereError( final String msg ) {
        super( msg );

        notifyOopsCatcher();

    }

    public HowDidWeGetHereError( final String msg, final Throwable e ) {
        super(msg, e);

        notifyOopsCatcher();

    }

    /**
     * Call the oops catcher if one is defined.
     */

    private void notifyOopsCatcher() {

        if ( s_stackTracePrintStream != null ) {

            printStackTrace( getStackTracePrintStream() );

        }

        if ( HowDidWeGetHereError.s_oopsCatcher != null ) {

            HowDidWeGetHereError.s_oopsCatcher.oops( this );

        }

    }

    /**
     Specify the optional {@link PrintStream} that a stack traceback is
     automagically printed on if an instance of this class gets created.
     */

    public static void setStackTracePrintStream( final PrintStream stackTracePrintStream ) {

        s_stackTracePrintStream = stackTracePrintStream;

    }

    /**
     Get the optional {@link PrintStream} that a stack traceback is
     automagically printed on if an instance of this class gets created.
     @return the optional {@link PrintStream}.
     */

    @Nullable
    public static PrintStream getStackTracePrintStream() {

        return s_stackTracePrintStream;

    }

    /**
     * Specify the oops catcher which is to be called whenever a {@link HowDidWeGetHereError} is created and, presumably, is about to be thrown.
     * @param oopsCatcher the oops catcher which is to catch the oops.
     * @return the previously configured oops catcher (a weak but adequate way to handle multiple oops catchers if everyone cooperates).
     */

    @SuppressWarnings("UnusedDeclaration")
    public static OopsCatcher setOopsCatcher( @Nullable final OopsCatcher oopsCatcher ) {

        OopsCatcher oldOopsCatcher = HowDidWeGetHereError.s_oopsCatcher;

        HowDidWeGetHereError.s_oopsCatcher = oopsCatcher;

        return oldOopsCatcher;

    }

}
