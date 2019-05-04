/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

/**
 Encapsulate a sanitized tuple of minimum and maximum values (sometimes written as {@code (minimum, maximum)}.
 <p/>The sanitization process applies the following constraints in the order specified:
 <ol>
 <li>{@code minimum} and {@code maximum} are each forced to {@code 0} if they happen to be negative.</li>
 <li>if {@code minimum} is greater than {@code maximum} then {@code minimum} is forced to be equal to {@code maximum}.</li>
 </ol>
 Examples - each of these {@code (minimum,maximum)} yield the specified triplet:
 <blockquote>
 {@code ( 1, 3 )} is accepted as-is.
 <br>{@code ( -1, 3 ) } yields {@code ( 0, 3 ) } by rule #1 above.
 <br>{@code ( 3, 1 ) } yields {@code ( 1, 1 ) } by rule #2 above.
 <br>{@code ( 3, -3 ) } first yields {@code ( 3, 0 ) } by rule #1 above which in turn yields {@code ( 0, 0 } by rule #2
 above.
 </blockquote>
 */

public class ConstraintTuple {

    /**
     The constraint's minimum value.
     */

    public final int minimum;

    /**
     The constraint's maximum value.
     */

    public final int maximum;

    /**
     Create a sanitized {@code ( minimum, maximum ) } tuple.

     @param minimum the proposed minimum value.
     @param maximum the proposed maximum value.
     */

    public ConstraintTuple( final int minimum, final int maximum ) {

        super();

        // Apply rule #1 - make sure that minimum and maximum are non-negative.

        int min = Math.max( minimum, 0 );
        int max = Math.max( maximum, 0 );

        // Apply rule #2 - if the adjusted minimum is greater than the adjusted maximum then force minimum to adjusted
        // maximum.

        if ( min > max ) {

            min = max;

        }

        // Save the results.

        this.minimum = min;
        this.maximum = max;

    }

    /**
     Create a sanitized {@code ( minimum, maximum ) } tuple.

     @param value the proposed minimum and maximum value.
     <p/>Note that <blockquote>{@code new ConstraintTuple( x )}</blockquote> for any {@code int} value {@code x} is
     exactly equivalent to
     <blockquote>{@code new ConstraintTuple( x, x )}</blockquote>
     */

    public ConstraintTuple( final int value ) {

        this( value, value );
    }

    /**
     Get the constraint's minimum value.

     @return {@code this.minimum}
     */

    public int getMinimum() {

        return this.minimum;

    }

    /**
     Get the constraint's maximum value.

     @return {@code this.maximum}
     */

    public int getMaximum() {

        return this.maximum;

    }

    /**
     Compare this instance to the specified object.

     @param rhs the specified object.
     @return {@code true} if the specified object is a {@link ConstraintTuple} instance with
     the same {@code minimum} and {@code maximum} values as this instance; {@code false} otherwise.
     */

    public boolean equals( final Object rhs ) {

        return rhs instanceof ConstraintTuple &&
               minimum == ( (ConstraintTuple)rhs ).minimum &&
               maximum == ( (ConstraintTuple)rhs ).maximum;

    }

    /**
     Compute a hashcode for this instance.

     @return the result of calling {@code Integer.hashCode( minimum ^ maximum )}
     */

    public int hashCode() {

        return Integer.hashCode( minimum ^ maximum );

    }

    /**
     A shortcut for determining if an instance has a specified minimum and maximum.

     @param min the specified minimum.
     @param max the specified maximum.
     @return {@code true} if {@code this.minimum == min} and {@code this.maximum == max}; {@code false} otherwise.
     <p/>EXACTLY, equivalent to
     <blockquote>{@code this.equals( new ConstraintTuple( min, max ) )}</blockquote>
     */

    public boolean equals( final int min, final int max ) {

        return this.equals( new ConstraintTuple( min, max ) );

    }

    /**
     Return a string representation of this doublet.

     @return a String of the form
     <blockquote>{@code ( min=}<em>{@code minimum}</em>, max=<em>{@code maximum}</em> )</blockquote>
     For example,
     <blockquote>{@code new ConstraintTuple( 5, 10 ).toString()}</blockquote>
     would yield the String
     <blockquote>{@code "( min=5, max=10 )"}</blockquote>
     */

    public String toString() {

        return "( min=" + minimum + ", max=" + maximum + " )";

    }

    private static void doit( final int min, final int max, final int eMin, final int eMax ) {

        ConstraintTuple ct = new ConstraintTuple( min, max );
        boolean correct = ct.minimum == eMin && ct.maximum == eMax;

        Logger.logMsg(
                "( " + min + ", " + max + " ) yields " + new ConstraintTuple( min, max ) +
                ( correct ? " correct" : " wrong (should be ( " + eMin + ", " + eMax + " )" )
        );

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "testing" );

        doit( 1, 3, 1, 3 );
        doit( -1, 3, 0, 3 );
        doit( 3, 1, 1, 1 );
        doit( 3, -3, 0, 0 );
        doit( 2, 3, 2, 3 );
        doit( 2, -3, 0, 0 );

    }

}
