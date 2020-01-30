/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.ParsingException;
import org.jetbrains.annotations.NotNull;

import javax.management.timer.Timer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 Various methods for parsing and just generally managing dates.
 */

@SuppressWarnings({ "StaticMethodNamingConvention", "UnusedDeclaration" })
public class DateUtils {

    public static final TimeZone EASTERN_TIME = TimeZone.getTimeZone( "America/New_York" );
    public static final TimeZone CENTRAL_TIME = TimeZone.getTimeZone( "America/Chicago" );
    public static final TimeZone MOUNTAIN_TIME = TimeZone.getTimeZone( "America/Denver" );
    public static final TimeZone PACIFIC_TIME = TimeZone.getTimeZone( "America/Los_Angeles" );
    public static final TimeZone ARIZONA_TIME = TimeZone.getTimeZone( "America/Arizona" );
    public static final TimeZone UTC = TimeZone.getTimeZone( "UTC" );

    // IMPORTANT:  keep these date formats private as we fiddle with their embedded timezone in methods within this
    // class.

    private static final SimpleDateFormat HHMMSSS = new SimpleDateFormat( "hh:mm.SSS" );
    private static final SimpleDateFormat MMDDYYYY = new SimpleDateFormat( "MM/dd/yyyy" );
    private static final SimpleDateFormat MMDDYYYY_HHMM = new SimpleDateFormat( "MM/dd/yyyy hh:mmaa" );
    private static final SimpleDateFormat MM_DD_YYYY = new SimpleDateFormat( "MM/dd/yyyy" );
    private static final SimpleDateFormat MMMM_D_YYYY = new SimpleDateFormat( "MMMM d, YYYY" );
    private static final SimpleDateFormat MM_DD_YYYY_HH_MM = new SimpleDateFormat( "MM/dd/yyyy hh:mmaa" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_COLONS = new SimpleDateFormat( "yyyy:MM:dd HH:mm:ss" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SSS = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_ZZZ = new SimpleDateFormat( "yyyy-MM-dd HH:mm zzz" );
    private static final SimpleDateFormat HH_MM = new SimpleDateFormat( "hh:mmaa" );
    private static final SimpleDateFormat HH_MM_SS_12 = new SimpleDateFormat( "hh:mm:ssaa" );
    private static final SimpleDateFormat HH_MM_SS_24 = new SimpleDateFormat( "HH:mm:ss" );
    private static final SimpleDateFormat WWW_MMM_DD_HHMMSS_YYYY = new SimpleDateFormat( "EEE MMM dd HH:mm:ss yyyy" );
    private static final SimpleDateFormat MMM_DD_HHMMSS_YYYY = new SimpleDateFormat( "MMM dd HH:mm:ss yyyy" );
    private static final SimpleDateFormat WWWW_MMMM_D_YYYY = new SimpleDateFormat( "EEEE, MMMM d, yyyy" );
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat( "yyyy-MM-dd" );
    private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat( "yyyyMMdd" );
    private static final SimpleDateFormat YYMMDD = new SimpleDateFormat( "yyMMdd" );
    private static final SimpleDateFormat HH_MM_SS_12_EEE_MMM_DD = new SimpleDateFormat( "hh:mm:ss EEE MMM dd" );
    private static final SimpleDateFormat HH_MM_SS_24_EEE_MMM_DD = new SimpleDateFormat( "HH:mm:ss EEE MMM dd" );
    private static final SimpleDateFormat STANDARD = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
    private static final SimpleDateFormat STANDARD_MS = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
    private static final SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat( "d MMM, yyyy" );
    private static final SimpleDateFormat MARKER_FORMAT = new SimpleDateFormat( "yyyyMMdd_HHmmss_SSS'Z'" );
    private static final SimpleDateFormat MARKER2_FORMAT = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss.SSS'Z'" );

    /**
     Make it impossible to instantiate this class.
     */

    private DateUtils() {

        super();

    }

    /**
     Parse an MMDDYYYY_HHMM format date and time string in a specified timezone.

     @param timezone   the specified timezone.
     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseMMDDYYYY_HHMM( @NotNull final TimeZone timezone, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.MMDDYYYY_HHMM ) {

            DateUtils.MMDDYYYY_HHMM.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MMDDYYYY_HHMM, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an YYYY_MM_DD_HH_MM format date and time string.

     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_HH_MM( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM ) {

            DateUtils.YYYY_MM_DD_HH_MM.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYY_MM_DD_HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an YYYY_MM_DD_HH_MM_SS format date and time string.

     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_HH_MM_SS( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYY_MM_DD_HH_MM_SS, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an YYYY_MM_DD_HH_MM_SS_COLONS format date and time string.
     <p>This format seems to be used in EXIF data in certain common image file formats.</p>

     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_HH_MM_SS_COLONS( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an MMDDYYYY format date string.

     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseMMDDYYYY( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.MMDDYYYY ) {

            DateUtils.MMDDYYYY.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MMDDYYYY, token, lineNumber );
            return date;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatMMDDYYYY_HHMM( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MMDDYYYY_HHMM ) {

            DateUtils.MMDDYYYY_HHMM.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.MMDDYYYY_HHMM.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in MM/DD/YYYY format.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatMMDDYYYY( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MMDDYYYY ) {

            DateUtils.MMDDYYYY.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.MMDDYYYY.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in a 'standard' format which excludes milliseconds.
     <p/>The 'standard' format is
     <blockquote>{@code yyyy-MM-dd'T'HH:mm:ssZ}</blockquote>
     or
     <blockquote>{@code 2001-07-04T12:08:56-0700}</blockquote>
     */

    @NotNull
    public static String formatStandard( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.STANDARD ) {

            DateUtils.STANDARD.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.STANDARD.format( dateTime );
            return s;

        }
    }

    /**
     Format a date string in a 'standard' format which includes milliseconds.
     <p>The 'standard' format is</p>
     <blockquote>{@code yyyy-MM-dd'T'HH:mm:ss.SSSZ}</blockquote>
     For example, the date and time 2001-07-04 12:08:56.235 Canadian Mountain Standard time would be
     formatted as
     <blockquote>{@code 2001-07-04T12:08:56.235-0700}</blockquote>

     @param dateTime the date and time to be formatted.
     @return the specified date and time formatted using code which is equivalent to
     <blockquote>{@code new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ).format( dateTime );}</blockquote>
     */

    @NotNull
    public static String formatStandardMs( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.STANDARD_MS ) {

            DateUtils.STANDARD_MS.setTimeZone( TimeZone.getDefault() );

            String s = DateUtils.STANDARD_MS.format( dateTime );

            return s;

        }

    }

    /**
     Format a date string in a 'marker' format that could be used to create filenames and such.
     <p>The 'marker' format is</p>
     <blockquote>{@code yyyyMMdd_HHmmss_SSS'Z'}</blockquote>
     For example, the date and time 2001-07-04 12:08:56.235 UTC would be
     formatted as
     <blockquote>{@code 2001-07-04_12-08-56-235}</blockquote>

     @param dateTime the date and time to be formatted.
     @return the specified date and time formatted using code which is equivalent to
     <blockquote>{@code new SimpleDateFormat( "yyyyMMdd_HHmmssSSS'Z'" ).format( dateTime );}</blockquote>
     where the {@code SimpleDateFormat} is set to UTC.
     */

    @NotNull
    public static String formatMarkerUTC( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MARKER_FORMAT ) {

            DateUtils.MARKER_FORMAT.setTimeZone( UTC );

            String s = DateUtils.MARKER_FORMAT.format( dateTime );

            return s;

        }

    }

    /**
     Parse a date string in a 'marker' format ({@code yyyyMMdd_HHmmss_SSS'Z'}).
     <p>See {@link #formatMarkerUTC(Date)} for details.</p>
     @param token a string in the 'marker' format produced by {@link #formatMarkerUTC(Date)}.
     @return an {@link ImmutableDate} equivalent to the value in {@code token}.
     */

    @NotNull
    public static ImmutableDate parseMarkerUTC( @NotNull String token, int lineNumber ) throws ParsingException {

        synchronized (  ( DateUtils.MARKER_FORMAT ) ) {

            DateUtils.MARKER_FORMAT.setTimeZone( UTC );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MARKER_FORMAT, token, lineNumber );
            return date;

        }

    }

    /**
     Format a date string in a 'marker' format that could be used to create filenames and such.
     <p>The 'marker' format is</p>
     <blockquote>{@code yyyy-MM-dd_HH-mm-ss-SSS}</blockquote>
     For example, the date and time 2001-07-04 12:08:56.235 UTC would be
     formatted as
     <blockquote>{@code 2001-07-04_12-08-56-235}</blockquote>

     @param dateTime the date and time to be formatted.
     @return the specified date and time formatted using code which is equivalent to
     <blockquote>{@code new SimpleDateFormat( "yyyyMMdd_HHmmssSSS'Z'" ).format( dateTime );}</blockquote>
     where the {@code SimpleDateFormat} is set to UTC.
     */

    @NotNull
    public static String formatMarker2UTC( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MARKER2_FORMAT ) {

            DateUtils.MARKER2_FORMAT.setTimeZone( UTC );

            String s = DateUtils.MARKER2_FORMAT.format( dateTime );

            return s;

        }

    }

    /**
     Parse a date string in a 'marker' format.
     <p>See {@link #formatMarkerUTC(Date)} for details.</p>
     @param token a string in the 'marker' format produced by {@link #formatMarkerUTC(Date)}.
     @return an {@link ImmutableDate} equivalent to the value in {@code token}.
     */

    @NotNull
    public static ImmutableDate parseMarker2UTC( @NotNull String token, int lineNumber ) throws ParsingException {

        synchronized (  ( DateUtils.MARKER2_FORMAT ) ) {

            DateUtils.MARKER2_FORMAT.setTimeZone( UTC );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MARKER2_FORMAT, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an "WWW MMM DD YYYY HH:MM:SS" style date string.

     @param timezone   the date and time's timezone.
     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseLongDateTime( @NotNull final TimeZone timezone, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.WWW_MMM_DD_HHMMSS_YYYY ) {

            DateUtils.WWW_MMM_DD_HHMMSS_YYYY.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.WWW_MMM_DD_HHMMSS_YYYY, token, lineNumber );
            return date;

        }

    }

    @NotNull
    public static ImmutableDate dateParse( @NotNull final SimpleDateFormat format, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        try {

            return new ImmutableDate( format.parse( token ) );

        } catch ( ParseException e ) {

            throw new ParsingException(
                    e.getMessage(), lineNumber, e.getErrorOffset(), ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

    }

    /**
     Parse an HH_MM format time string in a specified timezone.

     @param timezone   the specified timezone.
     @param token      the time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid time string.
     */

    @NotNull
    public static ImmutableDate parseHHMM( @NotNull final TimeZone timezone, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.HH_MM ) {

            DateUtils.HH_MM.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an HH_MM_SS_12 format time string in a specified timezone.

     @param timezone   the specified timezone.
     @param token      the time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid time string.
     */

    @NotNull
    public static ImmutableDate parseHH_MM_SS( @NotNull final TimeZone timezone, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.HH_MM_SS_12 ) {

            DateUtils.HH_MM_SS_12.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an MMM_DD_HHMMSS_YYYY format date and time string in the default timezone.

     @param timezone   the specified timezone.
     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseMMM_DD_HHMMSS_YYYY(
            @NotNull TimeZone timezone,
            @NotNull final String token,
            final int lineNumber
    )
            throws ParsingException {

        synchronized ( DateUtils.MMM_DD_HHMMSS_YYYY ) {

            DateUtils.MMM_DD_HHMMSS_YYYY.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MMM_DD_HHMMSS_YYYY, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an MMDDYYYY_HHMM format date and time string in a specified timezone.

     @param timezone   the specified timezone.
     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseMM_DD_YYYY_HH_MM(
            @NotNull final TimeZone timezone,
            @NotNull final String token,
            final int lineNumber
    )
            throws ParsingException {

        synchronized ( DateUtils.MM_DD_YYYY_HH_MM ) {

            DateUtils.MM_DD_YYYY_HH_MM.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MM_DD_YYYY_HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     Parse an "MM/dd/yyyy" format date string.

     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseMM_DD_YYYY( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.MM_DD_YYYY ) {

            DateUtils.MM_DD_YYYY.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.MM_DD_YYYY, token, lineNumber );
            return date;

        }

    }

    /**
     Parse a "yyyy-MM-dd" format date string and return
     an {@link ImmutableDate} value that is midnight in the local timezone.

     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD ) {

            DateUtils.YYYY_MM_DD.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYY_MM_DD, token, lineNumber );

            return date;

        }

    }

    /**
     Parse a "yyyy-MM-dd" format date string and return an {@link ImmutableDate} value that is midnight UTC at the start of
     the specified date.

     @param token          the date string.
     @param lineNumber     where the date was found.
     @param lenientParsing should parsing be lenient or not (see {@link java.text.DateFormat#setLenient} for more info).
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_utc(
            @NotNull final String token,
            final int lineNumber,
            final boolean lenientParsing
    )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD ) {

            ImmutableDate date;
            boolean oldLenientParsing = DateUtils.YYYY_MM_DD.isLenient();
            try {

                DateUtils.YYYY_MM_DD.setLenient( lenientParsing );
                DateUtils.YYYY_MM_DD.setTimeZone( DateUtils.UTC );
                date = DateUtils.dateParse( DateUtils.YYYY_MM_DD, token, lineNumber );

            } finally {

                DateUtils.YYYY_MM_DD.setLenient( oldLenientParsing );

            }

            return date;

        }

    }

    /**
     Parse a "yyyy-MM-dd" format date string and return an {@link ImmutableDate} value
     that is midnight UTC at the start of the specified date.
     <p/>Equivalent to a call to {@code DateUtils.parseYYYY_MM_DD_utc( token, lineNumber, true )}.
     See {@link #parseYYYY_MM_DD_utc(String, int, boolean)} for more info.

     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_utc( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        return parseYYYY_MM_DD_utc( token, lineNumber, true );

    }

    /**
     Parse a "yyyyMMdd" format date string and return an {@link ImmutableDate} value
     that is midnight local time at the start of the specified date.

     @param token      the date string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseYYYYMMDD( @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYYMMDD ) {

            DateUtils.YYYYMMDD.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYYMMDD, token, lineNumber );
            return date;

        }

    }

    /**
     Parse a "yyyyMMdd" format date string and return an {@link ImmutableDate} value
     that is midnight in a specified {@link TimeZone} at the start of the specified date.

     @param token      the date string.
     @param timeZone   the specified {@link TimeZone}.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date string.
     */

    @NotNull
    public static ImmutableDate parseYYYYMMDD( @NotNull final String token, @NotNull final TimeZone timeZone, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYYMMDD ) {

            DateUtils.YYYYMMDD.setTimeZone( timeZone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYYMMDD, token, lineNumber );
            return date;

        }

    }

    /**
     Format a time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted time string.
     */

    @NotNull
    public static String formatHH_MM( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.HH_MM ) {

            DateUtils.HH_MM.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.HH_MM.format( dateTime );
            return s;

        }

    }

    /**
     Format a time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted time string.
     */

    @NotNull
    public static String formatHH_MM_SS_12( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.HH_MM_SS_12 ) {

            DateUtils.HH_MM_SS_12.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.HH_MM_SS_12.format( dateTime );
            return s;

        }

    }

    /**
     Format a time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted time string.
     */

    @NotNull
    public static String formatHH_MM_SS_24( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.HH_MM_SS_24 ) {

            DateUtils.HH_MM_SS_24.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.HH_MM_SS_24.format( dateTime );
            return s;

        }

    }

    /**
     Format a time string in 24-hour clock UTC.

     @param dateTime the date and time to be formatted.
     @return the formatted time string.
     */

    @NotNull
    public static String formatHH_MM_SS_24_UTC( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.HH_MM_SS_24 ) {

            DateUtils.HH_MM_SS_24.setTimeZone( DateUtils.UTC );
            String s = DateUtils.HH_MM_SS_24.format( dateTime );
            return s;

        }

    }

    /**
     Format a time string in 12-hour clock UTC with AM/PM indicator.

     @param dateTime the date and time to be formatted.
     @return the formatted time string.
     */

    @NotNull
    public static String formatHH_MM_SS_12_UTC( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.HH_MM_SS_12 ) {

            DateUtils.HH_MM_SS_12.setTimeZone( DateUtils.UTC );
            String s = DateUtils.HH_MM_SS_12.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatMM_DD_YYYY_HH_MM( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MM_DD_YYYY_HH_MM ) {

            DateUtils.MM_DD_YYYY_HH_MM.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.MM_DD_YYYY_HH_MM.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM( @NotNull final Date dateTime ) {

        return DateUtils.formatYYYY_MM_DD_HH_MM( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone.

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM ) {

            DateUtils.YYYY_MM_DD_HH_MM.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD_HH_MM.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS( @NotNull final Date dateTime ) {

        return DateUtils.formatYYYY_MM_DD_HH_MM_SS( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone.

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD_HH_MM_SS.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time with colons as separators.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS_COLONS( @NotNull final Date dateTime ) {

        return DateUtils.formatYYYY_MM_DD_HH_MM_SS_COLONS( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone with colons as separators.
     <p>This format seems to be used in EXIF data in certain common image file formats.</p>

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS_COLONS( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD_HH_MM_SS_COLONS.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatHH_MM_SS_12_EEE_MMM_DD( @NotNull final Date dateTime ) {

        return DateUtils.formatHH_MM_SS_12_EEE_MMM_DD( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone.

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatHH_MM_SS_12_EEE_MMM_DD( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.HH_MM_SS_12_EEE_MMM_DD ) {

            DateUtils.HH_MM_SS_12_EEE_MMM_DD.setTimeZone( timeZone );
            String s = DateUtils.HH_MM_SS_12_EEE_MMM_DD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatHH_MM_SS_24_EEE_MMM_DD( @NotNull final Date dateTime ) {

        return DateUtils.formatHH_MM_SS_24_EEE_MMM_DD( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone.

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatHH_MM_SS_24_EEE_MMM_DD( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.HH_MM_SS_24_EEE_MMM_DD ) {

            DateUtils.HH_MM_SS_24_EEE_MMM_DD.setTimeZone( timeZone );
            String s = DateUtils.HH_MM_SS_24_EEE_MMM_DD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date and time string in our local time.

     @param dateTime the date and time to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS_SSS( @NotNull final Date dateTime ) {

        return DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( dateTime, TimeZone.getDefault() );

    }

    /**
     Format a date and time string in a specified timezone.

     @param dateTime the date and time to be formatted.
     @param timeZone the timezone for which the date is to be formatted.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_SS_SSS( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS_SSS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS_SSS.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD_HH_MM_SS_SSS.format( dateTime );
            return s;

        }

    }

    /**
     Parse an YYYY_MM_DD_HH_MM_SS_SSS format date and time string in a specified timezone.

     @param timezone   the specified timezone.
     @param token      the date and time string.
     @param lineNumber where the date was found.
     @return the result in UTC.
     @throws ParsingException if the token does not contain a valid date and time string.
     */

    @NotNull
    public static ImmutableDate parseYYYY_MM_DD_HH_MM_SS_SSS( @NotNull final TimeZone timezone, @NotNull final String token, final int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_SS_SSS ) {

            DateUtils.YYYY_MM_DD_HH_MM_SS_SSS.setTimeZone( timezone );
            ImmutableDate date = DateUtils.dateParse( DateUtils.YYYY_MM_DD_HH_MM_SS_SSS, token, lineNumber );

            return date;

        }

    }

    /**
     Format a date and time string in a specified timezone with the timezone shown.

     @param dateTime the date and time to be formatted.
     @param timeZone the date and time's timezone.
     @return the formatted date and time string.
     */

    @NotNull
    public static String formatYYYY_MM_DD_HH_MM_ZZZ( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD_HH_MM_ZZZ ) {

            DateUtils.YYYY_MM_DD_HH_MM_ZZZ.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD_HH_MM_ZZZ.format( dateTime );

            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatMM_DD_YYYY( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MM_DD_YYYY ) {

            DateUtils.MM_DD_YYYY.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.MM_DD_YYYY.format( dateTime );

            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatYYYY_MM_DD( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.YYYY_MM_DD ) {

            DateUtils.YYYY_MM_DD.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.YYYY_MM_DD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in a specified timezone.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatYYYY_MM_DD( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYY_MM_DD ) {

            DateUtils.YYYY_MM_DD.setTimeZone( timeZone );
            String s = DateUtils.YYYY_MM_DD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatYYYYMMDD( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.YYYYMMDD ) {

            DateUtils.YYYYMMDD.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.YYYYMMDD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in a specified timezone.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatYYYYMMDD( @NotNull final Date dateTime, @NotNull final TimeZone timeZone ) {

        synchronized ( DateUtils.YYYYMMDD ) {

            DateUtils.YYYYMMDD.setTimeZone( timeZone );
            String s = DateUtils.YYYYMMDD.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string (e.g. {@code "Monday, July 1, 1867"}.
     */

    @NotNull
    public static String formatWWWW_MMMM_D_YYYY( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.WWWW_MMMM_D_YYYY ) {

            DateUtils.WWWW_MMMM_D_YYYY.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.WWWW_MMMM_D_YYYY.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string (e.g. {@code "July 1, 1867"}.
     */

    @NotNull
    public static String formatMMMM_D_YYYY( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.MMMM_D_YYYY ) {

            DateUtils.MMMM_D_YYYY.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.MMMM_D_YYYY.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string (e.g. {@code "1, July, 1867"}.
     */

    @NotNull
    public static String formatD_MMM_YYYY( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.DD_MMM_YYYY ) {

            DateUtils.DD_MMM_YYYY.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.DD_MMM_YYYY.format( dateTime );
            return s;

        }

    }

    /**
     Format a date string in our local time.

     @param dateTime the date to be formatted.
     @return the formatted date string.
     */

    @NotNull
    public static String formatYYMMDD( @NotNull final Date dateTime ) {

        synchronized ( DateUtils.YYMMDD ) {

            DateUtils.YYMMDD.setTimeZone( TimeZone.getDefault() );
            String s = DateUtils.YYMMDD.format( dateTime );
            return s;

        }

    }

    @NotNull
    public static String formatDuration( final long millis, final boolean shortForm ) {

        return DateUtils.formatDuration( millis, 3, shortForm );
    }

    @NotNull
    public static String formatDuration( final long millis ) {

        return DateUtils.formatDuration( millis, 3 );

    }

    @NotNull
    public static String formatDuration( final long millis, final int digits ) {

        return DateUtils.formatDuration( millis, digits, false );

    }

    @SuppressWarnings({ "UnnecessaryParentheses", "NestedConditionalExpression" })
    @NotNull
    public static String formatDuration( final long xmillis, final int digits, final boolean shortForm ) {

        long millis = xmillis;

        if ( millis == 0 ) {

            return "0" + ( shortForm ? "s" : " seconds" );

        }

        String rval = "";
        String spacing = "";
        if ( millis >= Timer.ONE_WEEK ) {

            long weeks = millis / Timer.ONE_WEEK;
            rval += spacing + weeks + ( shortForm ? "w" : ( " week" + ( weeks == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_WEEK;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_DAY ) {

            long days = millis / Timer.ONE_DAY;
            rval += spacing + days + ( shortForm ? "d" : ( " day" + ( days == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_DAY;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_HOUR ) {

            long hours = millis / Timer.ONE_HOUR;
            rval += spacing + hours + ( shortForm ? "h" : ( " hour" + ( hours == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_HOUR;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_MINUTE ) {

            long minutes = millis / Timer.ONE_MINUTE;
            rval += spacing + minutes + ( shortForm ? "m" : ( " minute" + ( minutes == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_MINUTE;
            spacing = " ";

        }

        if ( millis > 0L ) {

            long seconds = millis / Timer.ONE_SECOND;
            if ( shortForm && millis % Timer.ONE_SECOND == 0L ) {

                rval += spacing + seconds + "s";

            } else {

                rval += spacing + ObtuseUtil.lpad( millis / (double)Timer.ONE_SECOND, 0, digits ) +
                        ( shortForm ? "s " : ( " second" + ( millis == 1000L ? "" : "s" ) ) );

            }

            //noinspection UnusedAssignment
            millis %= Timer.ONE_SECOND;

        }

        return rval.trim();

    }

    /**
     Extract the day of the week from a {@link Date} instance.

     @param date the date instance from which the day of week is to be extracted.
     @return the day of the week from the specified {@link Date} instance.
     Sunday is represented by 1, Monday by 2, through to Saturday by 7.
     These correspond to the values returned by {@link java.util.Calendar#get(int)}.
     The constants {@link java.util.Calendar#SUNDAY}, {@link java.util.Calendar#MONDAY} ... may prove useful.
     */

    public static int extractDayOfWeek( @NotNull final Date date ) {

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );

        return cal.get( Calendar.DAY_OF_WEEK );

    }

    private static final String[] LONG_MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private static final String[] SHORT_MONTH_NAMES = {
            "Jan", "Feb", "Mar",
            "Apr", "May", "Jun",
            "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"
    };

    /**
     Return the long form of a month as returned by {@link Calendar#get}{@code ( Calendar.MONTH )} method.
     @param monthName the month (0=January, 1=February, 2=March, ..., 11=December).
     @return the long form of the specified month
     (January, February, March, April, May, June, July, August, September, October, November, December).
     @throws ArrayIndexOutOfBoundsException if the specified day of week is not in the range 1-7.
     */

    public static String longMonthName( final int monthName ) {

        return LONG_MONTH_NAMES[ monthName ];

    }

    /**
     Return the short (3-letter) form of a month as returned by {@link Calendar#get}{@code ( Calendar.MONTH )} method.
     @param monthName the day of the week (0=Jan, 1=Feb, 2=Mar, 11=Dec).
     @return the short form of the specified month (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec).
     @throws ArrayIndexOutOfBoundsException if the specified month is not in the range 0-11.
     */

    public static String shortMonthName( final int monthName ) {

        return SHORT_MONTH_NAMES[ monthName ];

    }

    private static final String[] LONG_DAY_OF_WEEK_NAMES = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    private static final String[] SHORT_DAY_OF_WEEK_NAMES = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    /**
     Return the long form of a day of week as returned by {@link Calendar#get}{@code ( Calendar.DAY_OF_WEEK )} method.
     @param dayOfWeek the one-origin day of the week (1==Sunday, 2==Monday, ..., 7==Saturday).
     @return the long form of the specified day of the week (Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, or Saturday).
     @throws ArrayIndexOutOfBoundsException if the specified day of week is not in the range 1-7.
     */

    public static String longDayOfWeek( final int dayOfWeek ) {

        return LONG_DAY_OF_WEEK_NAMES[dayOfWeek - 1 ];

    }

    /**
     Return the short (3-letter) form of a day of week as returned by {@link Calendar#get}{@code ( Calendar.DAY_OF_WEEK )} method.
     @param dayOfWeek the one-origin day of the week (1==Sun, 2==Mon, ..., 7==Sat).
     @return the short form of the specified day of the week (Sun, Mon, Tue, Wed, Thur, Fri, or Sat).
     @throws ArrayIndexOutOfBoundsException if the specified day of week is not in the range 1-7.
     */

    public static String shortDayOfWeek( final int dayOfWeek ) {

        return SHORT_DAY_OF_WEEK_NAMES[dayOfWeek - 1 ];

    }

    @NotNull
    public static Date addDays( @NotNull final Date date, final int delta ) {

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.add( Calendar.DAY_OF_MONTH, delta );

        return cal.getTime();

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Utils", "testing" );

        Date testDateTime = new Date();
        System.out.println( formatWWWW_MMMM_D_YYYY( testDateTime ) );
        System.out.println( formatMMMM_D_YYYY( testDateTime ) );
        System.out.println( formatMarkerUTC( testDateTime ) );
        System.out.println( formatWWWW_MMMM_D_YYYY( new Date( ObtuseCalendarDate.parseCalendarDate( "1867-07-01" )
                                                                                .getDateStartTimeMs() ) ) );
        System.out.println( formatWWWW_MMMM_D_YYYY( new Date( ObtuseCalendarDate.parseCalendarDate( "1900-01-01" )
                                                                                .getDateStartTimeMs() ) ) );

        for ( int i = 1; i <= 7; i += 1 ) {

            System.out.println( i + " = " + shortDayOfWeek( i ) + " == " + longDayOfWeek( i ) );

        }

        for ( int i = 0; i < 12; i += 1 ) {

            System.out.println( i + " = " + shortMonthName( i ) + " == " + longMonthName( i ) );

        }

        try {

            System.out.println( formatMarkerUTC( testDateTime ) + " should equal " + formatMarkerUTC( parseMarkerUTC( formatMarkerUTC( testDateTime ), 1 ) ) );
            System.out.println( formatMarker2UTC( testDateTime ) + " should equal " + formatMarker2UTC( parseMarker2UTC( formatMarker2UTC( testDateTime ), 1 ) ) );

        } catch ( ParsingException e ) {

            Logger.logErr( "com.obtuse.util.exceptions.ParsingException caught", e );

            ObtuseUtil.doNothing();

        }

    }

}
