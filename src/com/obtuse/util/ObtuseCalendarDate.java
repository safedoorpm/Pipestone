/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.exceptions.ParsingException;
import org.jetbrains.annotations.NotNull;

import javax.management.timer.Timer;
import java.util.Calendar;
import java.util.Date;

/**
 * Represent a calendar date.
 * <p/>
 * This class represents dates as actual calendar dates.
 * This avoids the alternative of using a Java Date object
 * with the time-within-the-date part of the object's value
 * set to something which is always a compromise of some sort.
 * <p/>
 * Instances of this class are immutable.
 */

@SuppressWarnings("UnusedDeclaration")
public class ObtuseCalendarDate implements Comparable<ObtuseCalendarDate> {

    private final String _dateString;
    private final long _dateStartTimeMs;
    private final long _dateEndTimeMs;
    private final long _midnightUtcMs;

    /**
     Create an instance from a date string of the format <code>YYYY-MM-DD</code>.
     @param dateString a date in the format <code>YYYY-MM-DD</code>.
     @throws ParsingException if the date string cannot be parsed.
     */

    public ObtuseCalendarDate( String dateString )
            throws ParsingException {
        super();

        _dateString = dateString;

        if ( dateString.length() != "2012-10-05".length() ) {

            throw new ParsingException(
                    "date \"" + dateString + "\" is wrong length (must be _exactly_ " + "2012-10-05".length() + " characters)",
                    0,
                    0,
                    ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

        _midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( dateString, 0 ).getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime( DateUtils.parseYYYY_MM_DD( dateString, 0 ) );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        _dateStartTimeMs = cal.getTimeInMillis();
        cal.add( Calendar.DAY_OF_YEAR, 1 );
        _dateEndTimeMs = cal.getTimeInMillis() - 1;

    }

    /**
     Create an instance from a {@link Date} instance.
     <p/>Equivalent to
     <blockquote><code>new ObtuseCalendarDate( DateUtils.formatYYYY_MM_DD( date ) )</code></blockquote>
     @param date the input date.
     */

    public ObtuseCalendarDate( Date date ) {
        super();

        _dateString = DateUtils.formatYYYY_MM_DD( date );
        try {

            _midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( _dateString, 0 ).getTime();

        } catch ( ParsingException e ) {

            throw new HowDidWeGetHereError( "unable to parse date \"" + _dateString + "\" which we formatted", e );

        }

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        _dateStartTimeMs = cal.getTimeInMillis();
        cal.add( Calendar.DAY_OF_YEAR, 1 );
        _dateEndTimeMs = cal.getTimeInMillis() - 1;

    }

    /**
     Construct an exact clone of another {@link ObtuseCalendarDate} instance.
     @param date the instance to be cloned.
     */

    public ObtuseCalendarDate( ObtuseCalendarDate date ) {
	super();

	_dateString = date.getDateString();
	_dateStartTimeMs = date.getDateStartTimeMs();
	_dateEndTimeMs = date.getDateEndTimeMs();
	_midnightUtcMs = date._midnightUtcMs;

    }

    /**
     Construct an instance without forcing the user to bother with catching the parsing exception.
     We'll turn it into an {@link IllegalArgumentException} if something goes wrong.
     */

    public static ObtuseCalendarDate parse( String date ) {

	try {

	    return new ObtuseCalendarDate( date );

	} catch ( ParsingException e ) {

	    throw new IllegalArgumentException( "unable to parse \"" + date + "\"", e );

	}

    }

    /**
     Compute the number of days between two dates.
     <p/>The distance between two dates includes the endpoints.
     In other words, if <code>today</code> contains today's date and <code>tomorrow</code> contains tomorrow's date then
     <blockquote><code>ObtuseCalendarDate.computeDurationDays( abc, abc )</code></blockquote> returns <code>2</code>.
     Also, increasing the distance between any pair of {@link ObtuseCalendarDate} values <code>abc</code> and <code>xyz</code>
     by <code>1</code> increases the return value of
     <blockquote><code>ObtuseCalendarDate.computeDurationDays( abc, abc )</code></blockquote> by <code>1</code>.
     <p/>Strange things start to happen if the two dates are more than about 292,271,023 years apart (roughly the maximum date
     value that can be represented as a 64-bit count of milliseconds).
     @param from the starting date.
     @param to the ending date.
     @return the distance between the two dates including the endpoint dates.
     @throws IllegalArgumentException if <code>from</code> is greater than <code>to</code>.
     */

    public static int computeDurationDays( ObtuseCalendarDate from, ObtuseCalendarDate to ) {

        if ( from.compareTo( to ) > 0 ) {

            throw new IllegalArgumentException( "probable bug:  from (" + from + ") > to (" + to + ")" );

        }

        long durationMs = to._midnightUtcMs - from._midnightUtcMs;
        if ( durationMs % Timer.ONE_DAY != 0L ) {

            throw new HowDidWeGetHereError( "days are not 24 hours long" );

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        int durationDays = 1 + (int)( durationMs / Timer.ONE_DAY );

//        Logger.logMsg( "duration between " + this + " and " + rhs + " is " + computeDurationDays + " days" );

        return durationDays;

    }

    /**
     Get a new calendar date which is some number of days prior to or after a specified calendar date.
     <p/>This method is conceptually equivalent to adding <code>delta * 1000L * 24L * 60L * 60L</code>
     @param date the specified calendar date.
     @param delta the number of days to add to the specified calendar date. Negative <code>delta</code> values work as one would expect.
     For example, a <code>delta</code> of <code>-1</code> yields the date immediately before <code>date</code>.
     @return the resulting calendar date.
     */

    public static ObtuseCalendarDate addDays( ObtuseCalendarDate date, int delta ) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( date._dateStartTimeMs );
        cal.add( Calendar.DAY_OF_YEAR, delta );

        return new ObtuseCalendarDate( cal.getTime() );

    }

    /**
     Get this instance's date as a string in the format <code>"YYYY-MM-DD"</code>.
     @return this instance's date as a string in the format <code>"YYYY-MM-DD"</code>.
     */

    public String getDateString() {

        return _dateString;

    }

    /**
     Get the time in milliseconds since the Java epoch when this instance's date starts in the local timezone.
     <p/>If <code>date</code> is any valid {@link ObtuseCalendarDate} instance then
     <blockquote><code>new Date( date.getDateStartTimeMs() - 1 ) )</code></blockquote>
     yields a {@link Date} instance whose time is the very last millisecond (in the local timezone) of the day before <code>date</code>.
     In other words, if <code>date</code> is <code>2009-03-03</code> then
     <blockquote><code>System.out.println( "" + new Date( end.getDateStartTimeMs() - 1 );</code></blockquote>
     prints out
     <blockquote><code>Mon Mar 02 23:59:59 MDT 2009</code></blockquote>
     if the program is running in the MDT timezone.
     <p/>If the same {@link Date} instance were to be printed using an appropriate
     date format which also formats the instance's milliseconds, the output would be
     <blockquote><code>Mon Mar 02 23:59:59.999 MDT 2009</code></blockquote> if the program is running in the MDT timezone.
     @return the time in milliseconds since the Java epoch when this instance's date starts.
     */

    public long getDateStartTimeMs() {

        return _dateStartTimeMs;

    }

    /**
     Get the time in milliseconds since the Java epoch when this instance's date ends in the local timezone.
     This is the value which is one millisecond before the start of the next date in the same local timezone.
     <p/>If <code>date</code> is any valid {@link ObtuseCalendarDate} instance then
     <blockquote><code>new Date( date.getDateEndTimeMs() + 1 ) )</code></blockquote>
     yields a {@link Date} instance whose time is the very first millisecond (in the local timezone) of the day after <code>date</code>.
     In other words, if <code>date</code> is <code>2009-03-03</code> then
     <blockquote><code>System.out.println( "" + new Date( end.getDateEndTimeMs() + 1 );</code></blockquote>
     prints out
     <blockquote><code>Wed Mar 04 00:00:00 MDT 2009</code></blockquote>
     if the program is running in the MDT timezone.
     <p/>If the same {@link Date} instance were to be printed using the appropriate
     date format which also formats the instance's milliseconds, the output would be
     <blockquote><code>Wed Mar 04 00:00:00.000 MDT 2009</code></blockquote> if the program is running in the MDT timezone.
     @return the time in milliseconds since the Java epoch when this instance's date starts.
     */

    public long getDateEndTimeMs() {

        return _dateEndTimeMs;

    }

    /**
     Compare two instances for equality.
     <p/>
     There are a few cases of interest:
     <ol>
     <li>if <code>rhs</code> is an {@link ObtuseCalendarDate} instance representing the same calendar date as this instance
     then this method returns <code>true</code>.</li>
     <li>if <code>rhs</code> is a {@link Date} instance representing any time in the local timezone on the same calendar date as this instance
     then this method returns <code>true</code>.</li>
     <li>in all other cases, this method returns <code>false</code>.</li>
     </ol>
     @param rhs any valid value including <code>null</code>.
     @return <code>true</code> if <code>rhs</code> is an {@link ObtuseCalendarDate} instance representing the same calendar date as this instance represents.
     */

    public boolean equals( Object rhs ) {

        //noinspection ChainOfInstanceofChecks
        if ( rhs instanceof ObtuseCalendarDate ) {

            return _dateString.equals( ((ObtuseCalendarDate) rhs).getDateString() );

        } else if ( rhs instanceof Date ) {

            Date rhsDate = (Date)rhs;
            return _dateStartTimeMs <= rhsDate.getTime() && rhsDate.getTime() <= _dateEndTimeMs;

        } else {

            return false;

        }

    }

    /**
     Return a hash code derived from this instance's calendar date value.
     <p/>This method is exactly equivalent to
     <blockquote><code>{@link #getDateString()}.hashCode()</code></blockquote>
     @return a hash code derived from this instance's calendar date value.
     */

    public int hashCode() {

        return _dateString.hashCode();

    }

    /**
     Compare this instance's date string to this or some other instance's date string.
     <p/>
     This method is exactly equivalent to
     <blockquote><code>{@link #getDateString()}.compareTo( rhs.{@link #getDateString()} )</code></blockquote>
     See the {@link String#compareTo} method's documentation for more information.
     @param rhs the instance that this instance is to be compared to.
     @return the value 0 if the <code>rhs</code> instance's calendar date is the same as this instance's calendar date;
     a value less than 0 if this instance's calendar date is prior to the <code>rhs</code> instance's calendar date;
     a value greater than 0 if this instance's calendar date is after the <code>rhs</code> instance's calendar date.
     */

    public int compareTo( @NotNull ObtuseCalendarDate rhs ) {

        return _dateString.compareTo( rhs._dateString );

    }

    /**
     Determine if the time (in the local timezone) described by a specified {@link Date} instance is on the same calendar date as this instance's calendar date.
     <p/>
     This method is exactly equivalent to
     <blockquote><code>{@link #getDateStartTimeMs()} <= rhs.getTime() && rhs.getTime() <= {@link #getDateEndTimeMs()}</code></blockquote>
     @param rhs the {@link Date} instance of interest.
     @return true if the <code>rhs</code> {@link Date} instance specifies a time in the local timezone which is on the same calendar date as this instance's calendar date.
     */

    public boolean containsDate( Date rhs ) {

        return _dateStartTimeMs <= rhs.getTime() && rhs.getTime() <= _dateEndTimeMs;

    }

    /**
     Return this instance's calendar date string in YYYY-MM-DD format.
     <p/>This method is exactly equivalent to <code>{@link #getDateString()}</code>
     @return this instance's calendar date string.
     */

    public String toString() {

        return _dateString;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "ObtuseCalendarDate", null );

        try {

            ObtuseCalendarDate start = new ObtuseCalendarDate( "2009-02-28" );
            for ( int i = 0; i < 20; i += 1 ) {

                ObtuseCalendarDate end = ObtuseCalendarDate.addDays( start, i );

                Logger.logMsg(
			"from " + start + " to " + end + " is " + ObtuseCalendarDate.computeDurationDays( start, end ) +
			" days (" + end + " is one millisecond before " + new Date( end.getDateEndTimeMs() + 1 ) +
			", last millisecond of yesterday is " + new Date( end.getDateStartTimeMs() - 1 ) +
			")"
		);

            }

        } catch ( ParsingException e ) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        } catch ( HowDidWeGetHereError e ) {

            e.printStackTrace();

        }

	Logger.logMsg( "the future ends at " + new Date( Long.MAX_VALUE ) + " or " + Long.MAX_VALUE + " ms" );
	Logger.logMsg( "Long.MAX_VALUE equals about " + Long.MAX_VALUE / (1000L * 86400 * 365 + 1000L * 86400 / 4 ) );

    }

}
