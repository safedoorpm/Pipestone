/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

/**
 Manage a sanitized triplet of minimum, preferred, and maximum values.
 <p/>The sanitization process applies the following constraints in the order specified:
 <ol>
 <li>{@code minimum}, {@code preferred}, and {@code maximum} are each forced to {@code 0} if they happen to be negative.</li>
 <li>if {@code minimum} is greater than {@code maximum} then both are forced to be equal to {@code preferred}.</li>
 <li>if {@code preferred} is less than {@code minimum} then force it to {@code minimum}.</li>
 <li>if {@code preferred} is greater than {@code maximum} then force it to {@code maximum}.</li>
 </ol>
 Examples - each of these {@code (minimum,preferred,maximum)} yield the specified triplet:
 <blockquote>
 {@code ( 1, 2, 3 )} is accepted as-is.
 <br>{@code ( -1, 2, 3 ) } yields {@code ( 0, 2, 3 ) } by rule #1 above.
 <br>{@code ( 3, x, 1 ) } where {@code x} is any non-negative integer yields {@code ( x, x, x ) } by rule #2 above.
 <br>{@code ( 2, 1, 3 ) } yields {@code ( 2, 2, 3 ) } by rule #3 above.
 <br>{@code ( 2, 4, 3 ) } yields {@code ( 2, 3, 3 ) } by rule #4 above.
 <br>{@code ( 2, 1, -3 ) } is forced to {@code ( 2, 1, 0 ) } by rule #1 above and then rule #2 yields {@code ( 1, 1, 1 ) }.
 </blockquote>
 */

public class ConstraintTriplet {

    private final int minimum;

    private final int preferred;

    private final int maximum;

    /**
     Create a sanitized {@code ( minimum, preferred, maximum ) } triplet.
     @param minimum the proposed minimum value.
     @param preferred the proposed preferred value.
     @param maximum the proposed maximum value.
     */

    public ConstraintTriplet( int minimum, int preferred, int maximum ) {

	super();

	// Apply rule #1 - make sure that minimum, preferred and maximum are all non-negative.

	int min = Math.max( minimum, 0 );
	int pref = Math.max( preferred, 0 );
	int max = Math.max( maximum, 0 );

	// Apply rule #2 - if the adjusted minimum is greater than the adjusted maximum then force everything to adjusted preferred.

	if ( min > max ) {

	    min = pref;
	    max = pref;

	}

	// Apply rule #3 - force preferred to be in the range [minimum,maximum].

	pref = Math.min( Math.max( min, pref ), max );

	// Save the results.

	this.minimum = min;
	this.preferred = pref;
	this.maximum = max;

    }

    public ConstraintTriplet( int size ) {
	this( size, size, size );
    }

    public int getMinimum() {

	return this.minimum;

    }

    public int getPreferred() {

	return this.preferred;

    }

    public int getMaximum() {

	return this.maximum;

    }

    public String toString() {

	return "( min=" + minimum + ", pref=" + preferred + ", max=" + maximum + " )";

    }

    private static void doit( int min, int pref, int max ) {

	Logger.logMsg( "( " + min + ", " + pref + ", " + max + " ) yields " + new ConstraintTriplet( min, pref, max ) );

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Burke", "testing", null );

	doit( 1, 2, 3 );
	doit( -1, 2, 3 );
	doit( 3, 5, 1 );
	doit( 2, 1, 3 );
	doit( 2, 4, 3 );
	doit( 2, 1, -3 );

    }

}
