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
import java.util.GregorianCalendar;

/**
 * Represent a calendar date.
 * <p/>
 * This class represents dates as actual calendar dates.
 * This avoids the alternative of using a Java Date object
 * with the time-within-the-date part of the object's value
 * set to something which is always a compromise of some sort.
 * <p/>
 * A few words to the wise are in order:
 * <ol>
 *     <li>The earliest date that this class supports is March 1, 4 AD. This is the date that the Julian calendar rules came into effect in Rome.
 *     This class supports all dates through to December 31, 9999 because I'm an optimist at heart.</li>
 *     <li>The Julian calendar considered March 25 to be New Year's Day (first day of the new year). To avoid confusion and to allow us to simply
 *     use the underlying Java date management classes, this class treats January 1 as New Year's Day regardless of whether the date is in the Julian or the Gregorian era.</li>
 *     <li>Be careful with dates during the year in which your locale changed from the Julian to the Gregorian calendar system as there will be dates during that year which seem 'normal'
 *     but which will result in parsing errors when used with this class. For example, parsing dates in the range <tt>1582-10-05</tt> through <tt>1582-10-14</tt> will fail in many countries
 *     because those dates did not occur in those countries. This was when many switched from the Julian system to the Gregorian system and dropped the appropriate number of days as part of the switch.</li>
 * </ol>
 * <p/>
 * Instances of this class are immutable.
 */

@SuppressWarnings("UnusedDeclaration")
public class ObtuseCalendarDate implements Comparable<ObtuseCalendarDate> {

    /**
     The earliest date that this class supports.
     <p/>The Julian calendar rules came into effect on March 1, 4 AD. Hence that is the earliest date that we support.
     */

    public static final String EARLIEST_SUPPORTED_DATE_STRING = "0004-03-01";

    /**
     The latest date that this class supports.
     */

    public static final String LATEST_SUPPORTED_DATE_STRING = "9999-12-31";

    private static ObtuseCalendarDate s_earliestSupportedDate;
    private static ObtuseCalendarDate s_latestSupportedDate;

    private final String _dateString;
    private final long _dateStartTimeMs;
    private final long _dateEndTimeMs;
    private final long _midnightUtcMs;
    private final int _year;
    private final int _month;
    private final int _dayOfMonth;

    public enum MonthName {
        JANUARY, FEBRUARY, MARCH,
	APRIL, MAY, JUNE,
	JULY, AUGUST, SEPTEMBER,
	OCTOBER, NOVEMBER, DECEMBER;

//        JANUARY { public String getLongName() { return "January"; } },
//	FEBRUARY { public String getLongName() { return "February"; } },
//	MARCH { public String getLongName() { return "xxx"; } },
//	APRIL { public String getLongName() { return "xxx"; } },
//	MAY { public String getLongName() { return "xxx"; } },
//	JUNE { public String getLongName() { return "xxx"; } },
//	JULY { public String getLongName() { return "xxx"; } },
//	AUGUST { public String getLongName() { return "xxx"; } },
//	SEPTEMBER { public String getLongName() { return "xxx"; } },
//	OCTOBER { public String getLongName() { return "xxx"; } },
//	NOVEMBER { public String getLongName() { return "xxx"; } },
//	DECEMBER { public String getLongName() { return "xxx"; } };

	public String getLongName() { return upcaseName( name() ); }

	private String upcaseName( String s ) {

	    return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 ).toLowerCase();

	}

	public String getShortName() { return getLongName().substring( 0, 3 ); }

    }

//    public static final int JANUARY = 0;
//    public static final int FEBRUARY = 1;
//    public static final int MARCH = 2;
//    public static final int APRIL = 3;
//    public static final int MAY = 4;
//    public static final int JUNE = 5;
//    public static final int JULY = 6;
//    public static final int AUGUST = 7;
//    public static final int SEPTEMBER = 8;
//    public static final int OCTOBER = 9;
//    public static final int NOVEMBER = 10;
//    public static final int DECEMBER = 11;

    /**
     Create an instance from a date string of the format <code>YYYY-MM-DD</code>.
     <p/>This method supports dates in the range 0001-01-01 through 9999-12-31 although you need to know a lot about historical dating to properly deal with dates much before about 1000AD.
     You also need to know about the distinction between Julian dates and Gregorian dates in order to properly deal with Julian dates (this method interprets dates as Julian or Gregorian
     depending on their year, month and day as well as the locale that the method is operating within (still more stuff that you need to know - sorry but dates are just plain hard if you go back very far).
     @param dateString a date in the format <code>YYYY-MM-DD</code> (note that the year portion really must be exactly 4 digits long, the month portion really must be exactly 2 digits long,
                       and the day of month portion really must be exactly 2 digits long; this also means that dates prior to 1000AD must have enough 0's prepended to the year to make it 4 digits long).
     @throws ParsingException if the date string cannot be parsed.
     */

    public ObtuseCalendarDate( String dateString )
            throws ParsingException {
        super();

        if ( dateString.length() != "2012-10-05".length() ) {

            throw new ParsingException(
                    "date \"" + dateString + "\" is wrong length (must be _exactly_ " + "2012-10-05".length() + " characters)",
                    0,
                    0,
                    ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

        _dateString = dateString;

//        if ( dateString.length() != "2012-10-05".length() && dateString.length() != "999-01-01".length() ) {
//
//            throw new ParsingException(
//                    "date \"" + dateString + "\" is wrong length (must be _exactly_ " + "999-01-01".length() + " or " + "2012-10-05".length() + " characters)",
//                    0,
//                    0,
//                    ParsingException.ErrorType.DATE_FORMAT_ERROR
//            );
//
//        }
//
//        if ( dateString.length() == "999-01-01".length() ) {
//
//	    _dateString = "0" + dateString;
//
//	} else {
//
//	    _dateString = dateString;
//
//	}

	_midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( dateString, 0, false ).getTime();

	// Make sure nobody tries to slip a date that is to early for us to support properly.
	// Don't check if we are parsing the earliest date that we support or we'll spin off into infinite recursion land.

	if ( !EARLIEST_SUPPORTED_DATE_STRING.equals( dateString ) ) {

	    if ( _midnightUtcMs < getEarliestSupportedDate()._midnightUtcMs ) {

		throw new ParsingException(
			dateString + " is an unsupported date (earliest supported date is " + EARLIEST_SUPPORTED_DATE_STRING + ")", 0, 0, ParsingException.ErrorType.DATE_FORMAT_ERROR
		);

	    }

	}

	Calendar cal = Calendar.getInstance();
        cal.setTime( DateUtils.parseYYYY_MM_DD( dateString, 0 ) );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );

	int calYear = cal.get( Calendar.YEAR );
	int calMonth = cal.get( Calendar.MONTH );
	int calDayOfMonth = cal.get( Calendar.DAY_OF_MONTH ) - 1;

	_year = calYear;
	_month = calMonth;
	_dayOfMonth = calDayOfMonth;

	_dateStartTimeMs = cal.getTimeInMillis();
	cal.add( Calendar.DAY_OF_YEAR, 1 );
	_dateEndTimeMs = cal.getTimeInMillis() - 1;

//	_year = Integer.parseInt( _dateString.substring( 0, 4 ) );
//	_month = Integer.parseInt( _dateString.substring( 5, 7 ) ) - 1;
//	_dayOfMonth = Integer.parseInt( _dateString.substring( 8 ) ) - 1;
//
//	if ( _year != calYear ) {
//
//	    throw new HowDidWeGetHereError( "date \"" + dateString + "\" has Calendar object year of " + calYear + " but parsed year of " + _year );
//
//	}
//
//	if ( _month != calMonth ) {
//
//	    throw new HowDidWeGetHereError( "date \"" + dateString + "\" has zero-origin Calendar object month of " + calMonth + " but parsed zero-origin month of " + _month );
//
//	}
//
//	if ( _dayOfMonth != calDayOfMonth ) {
//
//	    throw new HowDidWeGetHereError( "date \"" + dateString + "\" has zero-origin Calendar object day of month of " + calDayOfMonth + " but parsed zero-origin day of month of " + _dayOfMonth );
//
//	}

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

	_year = cal.get( Calendar.YEAR );
	_month = cal.get( Calendar.MONTH );
	_dayOfMonth = cal.get( Calendar.DAY_OF_MONTH ) - 1;

	_dateStartTimeMs = cal.getTimeInMillis();
        cal.add( Calendar.DAY_OF_YEAR, 1 );
        _dateEndTimeMs = cal.getTimeInMillis() - 1;

//	_year = Integer.parseInt( _dateString.substring( 0, 4 ) );
//	_month = Integer.parseInt( _dateString.substring( 5, 7 ) ) - 1;
//	_dayOfMonth = Integer.parseInt( _dateString.substring( 8 ) );

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

	_year = date.getYear(); // Integer.parseInt( _dateString.substring( 0, 4 ) );
	_month = date.getMonthOfYear(); // Integer.parseInt( _dateString.substring( 5, 7 ) ) - 1;
	_dayOfMonth = date.getDayOfMonth(); // Integer.parseInt( _dateString.substring( 8 ) );

	String formattedYYYYMMDD = ObtuseUtil.lpad( _year, 4, '0' ) + '-' +
				   ObtuseUtil.lpad( _month + 1, 2, '0' ) + '-' +
				   ObtuseUtil.lpad( _dayOfMonth + 1, 2, '0' );
	if ( !formattedYYYYMMDD.equals( _dateString ) ) {

	    throw new HowDidWeGetHereError( "cloning of OCD date \"" + date.getDateString() + "\" screwed up (ended up with \"" + formattedYYYYMMDD + "\")" );

	}

    }

    /**
     Get the year part of the date.
     @return the year part of the date.
     */

    public int getYear() {

        return _year;

    }

    /**
     Get the zero-origin month (0=January, 1=February, etc).
     @return the month part of the date (0=January, 1=February, etc).
     */

    public int getMonthOfYear() {

        return _month;

    }

    /**
     Get the zero-origin day of the month (0=first of month, 1=second of month, etc).
     @return the day part of the date.
     */

    public int getDayOfMonth() {

        return _dayOfMonth;

    }

    /**
     Return the number of days in the date's month (properly handles leap years including century years).
     */

    public static int getDaysInMonth( int year, int month ) {

	Calendar cal = Calendar.getInstance();

	cal.setLenient( false );

	cal.set( Calendar.YEAR, year );
	cal.set( Calendar.MONTH, month );
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	cal.add( Calendar.MONTH, 1 );
	cal.add( Calendar.DAY_OF_YEAR, -1 );

	int rval = cal.get( Calendar.DAY_OF_MONTH );

	return rval;

//	if ( month == FEBRUARY ) {
//
//	    Calendar cal = Calendar.getInstance();
//
//	    cal.setLenient( false );
//
//	    cal.set( Calendar.YEAR, year );
//	    cal.set( Calendar.MONTH, Calendar.MARCH );
//	    cal.set( Calendar.DAY_OF_MONTH, 1 );
//	    cal.add( Calendar.DAY_OF_YEAR, 1 );
//
//	    int rval = cal.get( Calendar.DAY_OF_MONTH );
//
//	    return rval;
//
//	}

//        %%% get Calendar object for March 1st, subtract one day and use that date's day of month
//        if ( year <= 0 ) {
//
//            throw new IllegalArgumentException( "ObtuseCalendarDate.getDaysInMonth( int year, int month ):  year must be non-negative" );
//
//	}
//
//        switch ( month ) {
//
//	    // Thirty days hath September, April, June and November.
//
//	    case SEPTEMBER:
//	    case APRIL:
//	    case JUNE:
//	    case NOVEMBER:
//
//		return 30;
//
//	    // All the rest have thirty one,
//
//	    case JANUARY:
//	    case MARCH:
//	    case MAY:
//	    case JULY:
//	    case AUGUST:
//	    case OCTOBER:
//	    case DECEMBER:
//
//		return 31;
//
//	    // 'cept that pesky February . . .
//
//	    case FEBRUARY:
//
//		if ( year % 4 == 0 ) {
//
//		    if ( year % 100 == 0 && year % 400 != 0 ) {
//
//			return 28;
//
//		    } else {
//
//			return 29;
//
//		    }
//
//		} else {
//
//		    return 28;
//
//		}
//
//	    default:
//
//	        throw new IllegalArgumentException( "ObtuseCalendarDate.getDaysInMonth( int year, int month ):  bogus month " + month + " - month must be between 1 and 12 inclusive" );
//
//	}

    }

    /**
     Construct an instance without forcing the user to bother with catching the parsing exception.
     We'll turn it into an {@link IllegalArgumentException} if something goes wrong.
     @throws IllegalArgumentException
     */

    public static ObtuseCalendarDate parseCalendarDate( String date ) throws IllegalArgumentException {

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

    public static ObtuseCalendarDate getEarliestSupportedDate() {

        if ( s_earliestSupportedDate == null ) {

	    try {

		s_earliestSupportedDate = new ObtuseCalendarDate( EARLIEST_SUPPORTED_DATE_STRING );

	    } catch ( ParsingException e ) {

		throw new HowDidWeGetHereError( "ObtuseCalendarDate.getEarliestSupportedDate: unable to parse earliest supported date \"" + EARLIEST_SUPPORTED_DATE_STRING + "\"" );

	    }

	}

	return s_earliestSupportedDate;

    }

    public static ObtuseCalendarDate getLatestSupportedDate() {

        if ( s_latestSupportedDate == null ) {

	    try {

		s_latestSupportedDate = new ObtuseCalendarDate( LATEST_SUPPORTED_DATE_STRING );

	    } catch ( ParsingException e ) {

		throw new HowDidWeGetHereError( "ObtuseCalendarDate.getLatestSupportedDate: unable to parse latest supported date \"" + LATEST_SUPPORTED_DATE_STRING + "\"" );

	    }

	}

	return s_latestSupportedDate;

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

            String[] testDateStrings = { "1993-04-15", "0814-01-28", "0999-12-31", "1000-01-01", EARLIEST_SUPPORTED_DATE_STRING, LATEST_SUPPORTED_DATE_STRING };
            for ( String dateString : testDateStrings ) {

                Logger.logMsg( "checking \"" + dateString + "\"" );

                ObtuseCalendarDate testDate = parseCalendarDate( dateString );
		if ( !dateString.equals( testDate.toString() ) ) {

		    Logger.logErr( "date string \"" + dateString + "\" incorrectly parsed as \"" + testDate + "\" (1)" );

		}

		ImmutableDate iDate = DateUtils.parseYYYY_MM_DD( dateString, 0 );
		ObtuseCalendarDate testDate2 = new ObtuseCalendarDate( iDate );

		if ( !dateString.equals( testDate2.toString() ) ) {

		    Logger.logErr( "date string \"" + dateString + "\" incorrectly parsed as \"" + testDate2 + "\" (2)" );

		}

		ObtuseCalendarDate testDate3 = new ObtuseCalendarDate( testDate );

		if ( !dateString.equals( testDate3.toString() ) ) {

		    Logger.logErr( "date string \"" + dateString + "\" incorrectly parsed as \"" + testDate2 + "\" (3)" );

		}

	    }

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

            for ( int year : new int[] { 1899, 1900, 1901, 1999, 2000, 2001 } ) {

                Logger.logMsg( "year:  " + year );

//                Logger.logMsg( "February in " + year + " has " + getDaysInMonth( year, MonthName.FEBRUARY.ordinal() ) );

		for ( int month = 0; month < 12; month += 1 ) {

		    MonthName monthName = MonthName.values()[ month ];
		    Logger.logMsg( monthName.getShortName() + " has " + getDaysInMonth( year, month ) + " days" );

		}

	    }

	    ObtuseCalendarDate earliestSupportedDate = getEarliestSupportedDate();
	    ObtuseCalendarDate latestSupportedDate = getLatestSupportedDate();
	    Logger.logMsg( "earliest supported date is " + earliestSupportedDate + ", latest supported date is " + latestSupportedDate );
	    Logger.logMsg(
		    "from " + earliestSupportedDate + " to " + latestSupportedDate + " is " +
		    ObtuseUtil.readable( ObtuseCalendarDate.computeDurationDays( earliestSupportedDate, latestSupportedDate ) ) +
		    " days"
	    );
	    Logger.logMsg(
		    " (" + earliestSupportedDate + " is one millisecond before " + new Date( earliestSupportedDate.getDateEndTimeMs() + 1 ) +
		    ", last millisecond of the day before is " + new Date( earliestSupportedDate.getDateStartTimeMs() - 1 ) +
		    ")"
	    );
	    Logger.logMsg(
		    " (" + latestSupportedDate + " is one millisecond before " + new Date( latestSupportedDate.getDateEndTimeMs() + 1 ) +
		    ", last millisecond of the day before is " + new Date( latestSupportedDate.getDateStartTimeMs() - 1 ) +
		    ")"
	    );

//	    for ( int month = 0; month < 12; month += 1 ) {
//
//	    }

	    GregorianCalendar gregorianCalendar = new GregorianCalendar();
	    Logger.logMsg( "Julian to Gregorian switch occurred on " + gregorianCalendar.getGregorianChange() );
	    gregorianCalendar.setTimeInMillis( ObtuseCalendarDate.parseCalendarDate( "1582-10-01" ).getDateStartTimeMs() );
	    for ( int i = 0; i < 30; i += 1 ) {

		Logger.logMsg( "i=" + i + ", date is " + gregorianCalendar.getTime() );
		gregorianCalendar.add( Calendar.DAY_OF_YEAR, 1 );

	    }

	    Logger.logMsg( "this statement should print out March 1, 4 AD:  " + new ObtuseCalendarDate( "0004-03-01" ) );
	    Logger.logMsg( "this statement should throw an exception:  " + new ObtuseCalendarDate( "0004-02-01" ) );

	} catch ( HowDidWeGetHereError | IllegalArgumentException | ParsingException e ) {

	    e.printStackTrace();

	}

	GregorianCalendar gregorianCalendar = new GregorianCalendar();
	Logger.logMsg( "Julian to Gregorian switch occurred on " + gregorianCalendar.getGregorianChange() );
	gregorianCalendar.setTime( gregorianCalendar.getGregorianChange() );
	gregorianCalendar.add( Calendar.DAY_OF_YEAR, -5 );

	for ( int d = 1; d <= 31; d += 1 ) {

	    String dString = "1582-10-" + ObtuseUtil.lpad( d, 2, '0' );
	    Logger.logMsg( "parsing \"" + dString + "\"" );

	    try {

		ObtuseCalendarDate ocd = new ObtuseCalendarDate( dString );

	    } catch ( Throwable e ) {

	        Logger.logErr( "parse of \"" + dString + "\" failed:  " + e );

		ObtuseUtil.doNothing();

	    }

	}

//	Logger.logMsg( "pre-Java 8 date support ends at " + new Date( Long.MAX_VALUE ) + " or epoch+" + ObtuseUtil.readable( Long.MAX_VALUE ) + " ms" );
//	Logger.logMsg( "Long.MAX_VALUE equals about " + Long.MAX_VALUE / (1000L * 86400 * 365 + 1000L * 86400 / 4 ) );

    }

}
