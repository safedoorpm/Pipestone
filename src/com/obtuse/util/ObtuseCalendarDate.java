/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.exceptions.ParsingException;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

import javax.management.timer.Timer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Represent a calendar date.
 <p/>
 This class represents dates as actual calendar dates.
 This avoids the alternative of using a Java Date object
 with the time-within-the-date part of the object's value
 set to something which is always a compromise of some sort.
 <p/>
 A few words to the wise are in order:
 <ol>
 <li>The earliest date that this class supports is March 1, 4 AD. This is the date that the Julian calendar rules came into effect in Rome.
 This class supports all dates through to December 31, 9999 because I'm an optimist at heart.</li>
 <li>The Julian calendar considered March 25 to be New Year's Day (first day of the new year). To avoid confusion and to allow us to simply
 use the underlying Java date management classes, this class treats January 1 as New Year's Day regardless of whether the date is in the Julian or the Gregorian era.</li>
 <li>Be careful with dates during the year in which your locale changed from the Julian to the Gregorian calendar system as there will be dates during that year which seem 'normal'
 but which will result in parsing errors when used with this class. For example, parsing dates in the range <tt>1582-10-05</tt> through <tt>1582-10-14</tt> will fail in many countries
 because those dates did not occur in those countries. This was when many switched from the Julian system to the Gregorian system and dropped the appropriate number of days as part of the switch.</li>
 </ol>
 <p/>
 Instances of this class are immutable.
 */

@SuppressWarnings("UnusedDeclaration")
public class ObtuseCalendarDate extends GowingAbstractPackableEntity implements GowingBackReferenceable, Comparable<ObtuseCalendarDate> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ObtuseCalendarDate.class );

    private static final int VERSION = 1;

    private static final EntityName DATE_STRING = new EntityName( "_ds" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            String dateString = bundle.MandatoryStringValue( DATE_STRING );

            try {

                return new ObtuseCalendarDate( dateString );

            } catch ( com.obtuse.util.exceptions.ParsingException e ) {

                throw new GowingUnpackingException( e + " recovering date string", unPacker.curLoc(), e );

            }

        }

    };

    /**
     The earliest date that this class supports.
     <p/>The Julian calendar rules came into effect on March 1, 4 AD. Hence that is the earliest date that we support.
     */

    public static final String EARLIEST_SUPPORTED_DATE_STRING = "0004-03-01";

    /**
     The latest date that this class supports.
     */

    public static final String LATEST_SUPPORTED_DATE_STRING = "9999-12-31";

    public static final Pattern OACD_DATE_PATTERN = Pattern.compile( "(\\d\\d\\d\\d)\\s*-\\s*(\\d\\d?)\\s*-\\s*(\\d\\d?)" );

    private static ObtuseCalendarDate s_earliestSupportedDate;
    private static ObtuseCalendarDate s_latestSupportedDate;

    private final String _dateString;
    private final long _dateStartTimeMs;
    private final long _dateEndTimeMs;
    private final long _midnightUtcMs;
    private final int _year;
    private final int _month;
    private final int _dayOfMonth;

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ObtuseCalendarDate.ENTITY_TYPE_NAME,
                ObtuseCalendarDate.VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( DATE_STRING, _dateString, true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        Logger.logMsg( "finishing OCD " + getInstanceId() );

        return true;

    }

    public enum MonthName {
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER;

        public String getLongName() {

            return upcaseName( name() );
        }

        private String upcaseName( final String s ) {

            return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 ).toLowerCase();

        }

        public String getShortName() {

            return getLongName().substring( 0, 3 );
        }

    }

    /**
     Create an instance from a date string of the format <code>YYYY-MM-DD</code>.
     <p/>This method supports dates in the range 0001-01-01 through 9999-12-31 although you need to know a lot about historical dating to properly deal with dates much before about 1000AD.
     You also need to know about the distinction between Julian dates and Gregorian dates in order to properly deal with Julian dates (this method interprets dates as Julian or Gregorian
     depending on their year, month and day as well as the locale that the method is operating within (still more stuff that you need to know - sorry but dates are just plain hard if you go back very far).

     @param xdateString a date in the format <code>YYYY-MM-DD</code> (note that the year portion really must be exactly 4 digits long, the month portion really must be exactly 2 digits long,
     and the day of month portion really must be exactly 2 digits long; this also means that dates prior to 1000AD must have enough 0's prepended to the year to make it 4 digits long).
     @throws ParsingException if the date string cannot be parsed.
     */

    public ObtuseCalendarDate( final String xdateString )
            throws ParsingException {

        super( new GowingNameMarkerThing() );

        Matcher dateMatcher = OACD_DATE_PATTERN.matcher( xdateString );
        if ( !dateMatcher.matches() ) {

            throw new ParsingException(
                    "invalid date \"" + xdateString + "\" (must be \"YYYY-MM-DD\")",
                    0,
                    0,
                    ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

        _dateString = ObtuseUtil.lpad( dateMatcher.group(1), 4, '0' ) + '-' +
                      ObtuseUtil.lpad( dateMatcher.group(2), 2, '0' ) + '-' +
                      ObtuseUtil.lpad( dateMatcher.group(3), 2, '0' );

        _midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( _dateString, 0, false ).getTime();

        // Make sure nobody tries to slip a date that is to early for us to support properly.
        // Don't check if we are parsing the earliest date that we support or we'll spin off into infinite recursion land.

        if ( !EARLIEST_SUPPORTED_DATE_STRING.equals( _dateString ) ) {

            if ( _midnightUtcMs < getEarliestSupportedDate()._midnightUtcMs ) {

                throw new ParsingException(
                        _dateString + " is an unsupported date (earliest supported date is " + EARLIEST_SUPPORTED_DATE_STRING + ")",
                        0,
                        0,
                        ParsingException.ErrorType.DATE_FORMAT_ERROR
                );

            }

        }

        Calendar cal = Calendar.getInstance();
        cal.setTime( DateUtils.parseYYYY_MM_DD( _dateString, 0 ) );
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

    }

    /**
     Get today's date (local time zone).
     */

    public static ObtuseCalendarDate todayLocal() {

        Calendar cal = Calendar.getInstance();
//        System.out.println( "current local time is " + cal.get( Calendar.HOUR ) + ":" + cal.get( Calendar.MINUTE ) + ":" + cal.get( Calendar.SECOND ) );

        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );

        String yyyymmdd = ObtuseUtil.lpad( year, 4, '0' ) + '-' +
                          ObtuseUtil.lpad( month, 2, '0' ) + '-' +
                          ObtuseUtil.lpad( dayOfMonth, 2, '0' );

        return parseCalendarDate( yyyymmdd );

    }

    /**
     Get today's date (UTC).
     */

    public static ObtuseCalendarDate todayUTC() {

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
//        System.out.println( "current UTC time is " + cal.get( Calendar.HOUR ) + ":" + cal.get( Calendar.MINUTE ) + ":" + cal.get( Calendar.SECOND ) );

        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );

        String yyyymmdd = ObtuseUtil.lpad( year, 4, '0' ) + '-' +
                          ObtuseUtil.lpad( month, 2, '0' ) + '-' +
                          ObtuseUtil.lpad( dayOfMonth, 2, '0' );

        return parseCalendarDate( yyyymmdd );

    }

    /**
     Today's date (UTC) via a method name that some folks might prefer.
     <p>This method returns exactly the same result that {@link #todayUTC()} returns.
     It is provided as a convenience for folks who prefer to use the term "UCT".</p>
     <p>As far as I can tell, UTC is the 'official' acronym.</p>
     */

    public static ObtuseCalendarDate todayUCT() {

        return todayUTC();

    }

    /**
     Today's date (UTC) via a method name that some folks might prefer.
     <p>This method returns exactly the same result that {@link #todayUTC()} returns.
     It is provided as a convenience for folks who prefer to use the term "GMT".</p>
     <p>As far as I can tell, UTC is the 'official' acronym.</p>
     */

    public static ObtuseCalendarDate todayGMT() {

        return todayUTC();

    }

    /**
     Create an instance from a {@link Date} instance.
     <p/>Equivalent to
     <blockquote><code>new ObtuseCalendarDate( DateUtils.formatYYYY_MM_DD( date ) )</code></blockquote>

     @param date the input date.
     */

    public ObtuseCalendarDate( final Date date ) {

        super( new GowingNameMarkerThing() );

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

    }

    /**
     Construct an exact clone of another {@link ObtuseCalendarDate} instance.

     @param date the instance to be cloned.
     */

    public ObtuseCalendarDate( final ObtuseCalendarDate date ) {

        super( new GowingNameMarkerThing() );

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

            throw new HowDidWeGetHereError( "cloning of OCD date \"" +
                                            date.getDateString() +
                                            "\" screwed up (ended up with \"" +
                                            formattedYYYYMMDD +
                                            "\")" );

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

    public static int getDaysInMonth( final int year, final int month ) {

        Calendar cal = Calendar.getInstance();

        cal.setLenient( false );

        cal.set( Calendar.YEAR, year );
        cal.set( Calendar.MONTH, month );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        cal.add( Calendar.MONTH, 1 );
        cal.add( Calendar.DAY_OF_YEAR, -1 );

        @SuppressWarnings("UnnecessaryLocalVariable")
        int rval = cal.get( Calendar.DAY_OF_MONTH );

        return rval;

    }

    /**
     Construct an instance without forcing the user to bother with catching the parsing exception.
     We'll turn it into an {@link IllegalArgumentException} if something goes wrong.

     @throws IllegalArgumentException if the parse fails.
     The {@link IllegalArgumentException} will contain the underlying {@link ParsingException} as its cause
     (accessible via {@link IllegalArgumentException#getCause()}).
     */

    public static ObtuseCalendarDate parseCalendarDate( final String date ) throws IllegalArgumentException {

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
     @param to   the ending date.
     @return the distance between the two dates including the endpoint dates.
     @throws IllegalArgumentException if <code>from</code> is greater than <code>to</code>.
     */

    public static int computeDurationDays( final ObtuseCalendarDate from, final ObtuseCalendarDate to ) {

        if ( from.compareTo( to ) > 0 ) {

            throw new IllegalArgumentException( "probable bug:  from (" + from + ") > to (" + to + ")" );

        }

        long durationMs = to._midnightUtcMs - from._midnightUtcMs;
        if ( durationMs % Timer.ONE_DAY != 0L ) {

            throw new HowDidWeGetHereError( "days are not 24 hours long" );

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        int durationDays = 1 + (int)( durationMs / Timer.ONE_DAY );

        return durationDays;

    }

    /**
     Get a new calendar date which is some number of days prior to or after a specified calendar date.
     <p/>This method is conceptually equivalent to adding <code>delta * 1000L * 24L * 60L * 60L</code>

     @param date  the specified calendar date.
     @param delta the number of days to add to the specified calendar date. Negative <code>delta</code> values work as one would expect.
     For example, a <code>delta</code> of <code>-1</code> yields the date immediately before <code>date</code>.
     @return the resulting calendar date.
     */

    public static ObtuseCalendarDate addDays( final ObtuseCalendarDate date, final int delta ) {

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

                throw new HowDidWeGetHereError( "ObtuseCalendarDate.getEarliestSupportedDate: unable to parse earliest supported date \"" +
                                                EARLIEST_SUPPORTED_DATE_STRING +
                                                "\"" );

            }

        }

        return s_earliestSupportedDate;

    }

    public static ObtuseCalendarDate getLatestSupportedDate() {

        if ( s_latestSupportedDate == null ) {

            try {

                s_latestSupportedDate = new ObtuseCalendarDate( LATEST_SUPPORTED_DATE_STRING );

            } catch ( ParsingException e ) {

                throw new HowDidWeGetHereError( "ObtuseCalendarDate.getLatestSupportedDate: unable to parse latest supported date \"" +
                                                LATEST_SUPPORTED_DATE_STRING +
                                                "\"" );

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

    public boolean equals( final Object rhs ) {

        //noinspection ChainOfInstanceofChecks
        if ( rhs instanceof ObtuseCalendarDate ) {

            return _dateString.equals( ( (ObtuseCalendarDate)rhs ).getDateString() );

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

    public int compareTo( final @NotNull ObtuseCalendarDate rhs ) {

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

    public boolean containsDate( final Date rhs ) {

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

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "ObtuseCalendarDate", null );

        try {

            Logger.logMsg( "today (local timezone) is " + todayLocal() );
            Logger.logMsg( "today (UTC) is " + todayUTC() + " and " + todayUCT() + " and " + todayGMT() );

            String[] testDateStrings =
                    { "1993-04-15", "0814-01-28", "0999-12-31", "1000-01-01", EARLIEST_SUPPORTED_DATE_STRING, LATEST_SUPPORTED_DATE_STRING };
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

            for ( int year : new int[]{ 1899, 1900, 1901, 1999, 2000, 2001 } ) {

                Logger.logMsg( "year:  " + year );

                for ( int month = 0; month < 12; month += 1 ) {

                    MonthName monthName = MonthName.values()[month];
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

    }

}
