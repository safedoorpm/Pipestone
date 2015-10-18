/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.ParsingException;

/**
 * A date {@link ArgParser} argument.
 * <p/>The only supported format is "yyyy-MM-dd".
 * See {@link com.obtuse.util.DateUtils#parseYYYY_MM_DD} for more information.
 */

@SuppressWarnings({ "ClassNamingConvention", "UnusedDeclaration" })
public abstract class ArgYYYY_MM_DD_CalendarDate extends Arg {

    protected ArgYYYY_MM_DD_CalendarDate( String keyword ) {
        super( keyword );

    }

    public final void process( String keyword, String arg ) {

        try {

            process( keyword, new ObtuseCalendarDate( arg ) );

        } catch ( ParsingException e ) {

            throw new IllegalArgumentException( "invalid \"" + keyword + "\" argument (" + arg + ") - must be a date in the format YYYY-MM-DD", e );

        }

    }

    public abstract void process( String keyword, ObtuseCalendarDate arg );

    public String toString() {

        return "ArgLong( " + getKeyword() + " )";

    }

}
