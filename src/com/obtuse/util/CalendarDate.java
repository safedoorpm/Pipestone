package com.obtuse.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.exceptions.ParsingException;

import java.util.Date;

/**
 Equivalent to the old com.obtuse.util.CalendarDate class but uses the new com.obtuse.util.ObtuseCalendarDate class to do the work.
 <p/>Any software that uses the old CalendarDate class should switch to using the new ObtuseCalendarDate class (all the constructors
 and methods have the same signature).
 */

@SuppressWarnings("deprecation")
@Deprecated
public class CalendarDate extends ObtuseCalendarDate {

    public CalendarDate( final String dateString )
	    throws ParsingException {
	super( dateString );
    }

    public CalendarDate( final Date date ) {
	super( date );

    }

    public CalendarDate( final ObtuseCalendarDate obtuseCalendarDate ) {
	super( obtuseCalendarDate );
    }

    @SuppressWarnings("unused")
    public static int computeDurationDays( final CalendarDate from, final CalendarDate to ) {

	return ObtuseCalendarDate.computeDurationDays( from, to );

    }

    public static CalendarDate addDays( final CalendarDate date, final int days ) {

	CalendarDate newDate = new CalendarDate( ObtuseCalendarDate.addDays( date, days ) );

	return newDate;

    }

    public static void main( final String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Shared", "ObtuseCalendarDate", null );

	try {

	    CalendarDate start = new CalendarDate( "2009-02-28" );
	    for ( int i = 0; i < 20; i += 1 ) {

		CalendarDate end = CalendarDate.addDays( start, i );

		Logger.logMsg( "from " + start + " to " + end + " is " + ObtuseCalendarDate.computeDurationDays( start, end ) + " days" );

	    }

	} catch ( ParsingException | HowDidWeGetHereError e ) {

	    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

	}

    }

}
