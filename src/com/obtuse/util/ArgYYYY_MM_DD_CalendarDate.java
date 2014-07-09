package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

import com.obtuse.util.exceptions.ParsingException;

/**
 * A date {@link ArgParser} argument.
 * <p/>The only supported format is "yyyy-MM-dd".
 * See {@link com.obtuse.util.DateUtils#parseYYYY_MM_DD} for more information.
 */

@SuppressWarnings("ClassNamingConvention")
public abstract class ArgYYYY_MM_DD_CalendarDate extends Arg {

    protected ArgYYYY_MM_DD_CalendarDate( String keyword ) {
        super( keyword );

    }

    public final void process( String keyword, String arg ) {

        try {

            process( keyword, new CalendarDate( arg ) );

        } catch ( ParsingException e ) {

            throw new IllegalArgumentException( "invalid \"" + keyword + "\" argument (" + arg + ") - must be a date in the format YYYY-MM-DD", e );

        }

    }

    public abstract void process( String keyword, CalendarDate arg );

    public String toString() {

        return "ArgLong( " + getKeyword() + " )";

    }

}
