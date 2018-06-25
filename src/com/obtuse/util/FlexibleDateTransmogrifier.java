/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Take a date string and turn it into a canonical form.
 <p/>The input date string must be in one of the following formats (see notes below for more information):
 <ul>
 <li>{@code yyyy} (yields itself with any whitespace removed)</li>
 <li>{@code yyyy-m[m]} (yields yyyy-mm ; {@code [x]} means that {@code x} is optional; lower case letters in the pattern each represent one digit)</li>
 <li>{@code yyyy.m[m]} (yields yyyy-mm)</li>
 <li>{@code yyyy/m[m]} (yields yyyy-mm)</li>
 <li>{@code yyyy-m[m]-d[d]} (yields yyyy-mm)</li>
 <li>{@code yyyy-m[m]} (yields yyyy-mm)</li>
 <li>{@code yyyy.m[m].d[d]} (yields yyyy-mm-dd)</li>
 <li>{@code yyyy.m[m]} (yields yyyy-mm)</li>
 <li>{@code yyyy/m[m]/d[d]} (yields yyyy-mm-dd)</li>
 <li>{@code yyyy/m[m]} (yields yyyy-mm)</li>
 <li>{@code d[d] MMM~ yyyy} (yields yyyy-mm-dd); {@code MMM~} means either the first three letters of a month name or an entire month name)</li>
 <li>{@code MMM~ yyyy} (yields yyyy-mm)</li>
 <li>{@code yyyy MMM~ d[d]} (yields yyyy-mm-dd)</li>
 <li>{@code yyyy MMM~} (yields yyyy-mm)</li>
 <li>{@code MMM~ d[d], yyyy} (yields yyyy-mm-dd)</li>
 <li>{@code MMM~, yyyy} (yields yyyy-mm)</li>
 </ul>
 <b>Notes:</b>
 Each input string is parsed in turn using each the formats shown above in the order listed.
 The first parse that completes without encountering an error is assumed to be correct and the value understood by that pattern is returned in the appropriate canonical format.
 <p/>
 <b>Note to anyone modifying this class' implementation:
 while the order in which the above formats are tried doesn't matter in the current implementation of this class, it could matter in the future so be careful.</b>
 <p/>
 Arbitrary whitespace is allowed before and after the date as well as before and after any punctuation (-./,} or spaces.
 In other words, {@code "1957-10-4"} is equivalent to {@code " 1957 - 10 - 4 "}.
 Note that {@code "19 57-10-4"} is invalid as spaces are not allowed within numbers.
 <p/>The resulting string will be in one of the following canonical formats:
 <ul>
 <li>{@code yyyy}</li>
 <li>{@code yyyy-mm}</li>
 <li>{@code yyyy-mm-dd}</li>
 </ul>
 Dates in these formats are supported by {@link com.obtuse.util.ObtuseApproximateCalendarDate} which, to be bluntly honest, is why this class returns dates in these formats.
 <p/>
 Very little in the way of sanity checking is done as this really is a transmogrifier and not a validator.
 For example, if you give it {@code "31 February 2001"} then you're going to get back {@code "2001-02-31"} even though February of 2001 only had 28 days.
 <p/>If you give it something totally bogus like a date string where the day of the month is greater than 31 then you'll get an error return.
 */

public class FlexibleDateTransmogrifier {

    private static class TestData {

        private final String[] input;
        private final String output;

        private TestData( final String input, final String output ) {
            this( new String[] { input }, output );

        }

        private TestData( final @NotNull String@NotNull[] inputs, final String output ) {
            super();

            this.input = new String[inputs.length];
            System.arraycopy( inputs, 0, this.input, 0, inputs.length );
            this.output = output;

        }

        @SuppressWarnings("unused")
        public TestData( final String input ) {
            this( new String[] { input } );

        }

        public TestData( final @NotNull String@NotNull[] inputs ) {

            this.input = new String[inputs.length];
            System.arraycopy( inputs, 0, this.input, 0, inputs.length );
            this.output = null;

        }

        public String toString() {

            return "TestData( \"" + Arrays.toString( input ) + "\" -> \"" + output + "\" )";

        }

    }

    private static TestData[] s_testData = {
            new TestData(
                    new String[] {
                            "32 may 1972",
                            "",
                            "932", "12321",
                            "1972-", "0932-", "932-",
                            "1972.", "0932.", "932.",
                            "1972/", "0932/", "932/",
                            "2 ma 1972",
                            "2 marc 1972",
                            "1972 may 32",
                            "1972-05-32",
                            "1972.05.32",
                            "1972/05/32",
                            "1972.05-25",
                            "1972-05/25",
                            "1972/05-25",
                    }
            ),
            new TestData( "1972", "1972" ),
            new TestData( "0932", "0932" ),
            new TestData(
                    new String[] {
                            "1972-02-30", "1972.02.30", "1972/02/30",
                            "1972 - 02 - 30", "1972 . 02 . 30", "1972 / 02 / 30"
                    },
                    "1972-02-30"
            ),
            new TestData(
                    new String[] {
                            "1972-10-8", "1972.10.8", "1972/10/8",
                            "1972 - 10 - 8", "1972 . 10 . 8", "1972 / 10 / 8"
                    },
                    "1972-02-08"
            ),
            new TestData(
                    new String[] {
                            "1972-01", "1972.01", "1972/01",
                            "1972-1", "1972.1", "1972/1",
                            "1972 - 01", "1972 . 01", "1972 / 01",
                            "1972 - 1", "1972 . 1", "1972 / 1",
                            "1972-01-00", "1972.01.00", "1972/01/00",
                            "1972-01-0", "1972.01.0", "1972/01/0",
                            "1972-1-00", "1972.1.00", "1972/1/00",
                            "1972-1-0", "1972.1.0", "1972/1/0"
                    },
                    "1972-01"
            ),
            new TestData(
                    new String[] {
                            "2 march 1972", "2 mar 1972",
                    },
                    "1972-03-02"
            ),
            new TestData(
                    new String[] {
                            "21 march 1972", "21 mar 1972",
                    },
                    "1972-03-21"
            ),
            new TestData(
                    new String[]{
                            "1972 march 2", "1972 mar 2",
                    },
                    "1972-03-02"
            ),
            new TestData(
                    new String[]{
                            "1972 march 29", "1972 mar 29",
                    },
                    "1972-03-29"
            ),
            new TestData( "10 janUary 1972", "1972-01-10" ),
            new TestData( "10 February 1972", "1972-02-10" ),
            new TestData( "10 mArch 1972", "1972-03-10" ),
            new TestData( "10 apRil 1972", "1972-04-10" ),
            new TestData( "10 MAY 1972", "1972-05-10" ),
            new TestData( "10 juNE 1972", "1972-06-10" ),
            new TestData( "10 july 1972", "1972-07-10" ),
            new TestData( "10 augUst 1972", "1972-08-10" ),
            new TestData( "10 septeMber 1972", "1972-09-10" ),
            new TestData( "10 octobeR 1972", "1972-10-10" ),
            new TestData( "10 novemBer 1972", "1972-11-10" ),
            new TestData( "10 deCember 1972", "1972-12-10" ),
            new TestData( "janUary 1972", "1972-01" ),
            new TestData( "February 1972", "1972-02" ),
            new TestData( "mArch 1972", "1972-03" ),
            new TestData( "apRil 1972", "1972-04" ),
            new TestData( "MAY 1972", "1972-05" ),
            new TestData( "juNE 1972", "1972-06" ),
            new TestData( "july 1972", "1972-07" ),
            new TestData( "augUst 1972", "1972-08" ),
            new TestData( "septeMber 1972", "1972-09" ),
            new TestData( "octobeR 1972", "1972-10" ),
            new TestData( "novemBer 1972", "1972-11" ),
            new TestData( "deCember 1972", "1972-12" ),
            new TestData( "janUary 11, 1972", "1972-01-11" ),
            new TestData( "February 12, 1972", "1972-02-12" ),
            new TestData( "mArch 13, 1972", "1972-03-13" ),
            new TestData( "apRil 14, 1972", "1972-04-14" ),
            new TestData( "MAY 15, 1972", "1972-05-15" ),
            new TestData( "juNE 16, 1972", "1972-06-16" ),
            new TestData( "july 17, 1972", "1972-07-17" ),
            new TestData( "augUst 18, 1972", "1972-08-18" ),
            new TestData( "septeMber 19, 1972", "1972-09-19" ),
            new TestData( "octobeR 20, 1972", "1972-10-20" ),
            new TestData( "novemBer 21, 1972", "1972-11-21" ),
            new TestData( "deCember 22, 1972", "1972-12-22" ),
            new TestData( "janUary 11 , 1972", "1972-01-11" ),
            new TestData( "February 12 , 1972", "1972-02-12" ),
            new TestData( "mArch 13 , 1972", "1972-03-13" ),
            new TestData( "apRil 14 , 1972", "1972-04-14" ),
            new TestData( "MAY 15 , 1972", "1972-05-15" ),
            new TestData( "juNE 16 , 1972", "1972-06-16" ),
            new TestData( "july 17 , 1972", "1972-07-17" ),
            new TestData( "augUst 18 , 1972", "1972-08-18" ),
            new TestData( "septeMber 19 , 1972", "1972-09-19" ),
            new TestData( "octobeR 20 , 1972", "1972-10-20" ),
            new TestData( "novemBer 21 , 1972", "1972-11-21" ),
            new TestData( "deCember 22 , 1972", "1972-12-22" ),
    };

    public static class PatternInfo {

        private final Pattern _pattern;
        private final int _yyIx;
        private final int _mmIx;
        private final int _ddIx;
        private final boolean _numericMonth;

        public PatternInfo( final Pattern pattern, final int yyIx, final int mmIx, final int ddIx, final boolean numericMonth ) {

            if ( ( yyIx == mmIx && yyIx > 0 ) || ( mmIx == ddIx && mmIx > 0 ) || ( yyIx == ddIx && yyIx > 0 ) ) {

                throw new HowDidWeGetHereError( "FlexibleDateTransmogrifier #1:  yyIx=" + yyIx + ", mmIx=" + mmIx + ", ddIx=" + ddIx + " for pattern <<<" + pattern + ">>>" );

            }

            if ( mmIx < 0 && ddIx > 0 ) {

                throw new HowDidWeGetHereError( "FlexibleDateTransmogrifier #2:  yyIx=" + yyIx + ", mmIx=" + mmIx + ", ddIx=" + ddIx + " for pattern <<<" + pattern + ">>>" );

            }

            if ( yyIx < 0 ) {

                throw new HowDidWeGetHereError( "FlexibleDateTransmogrifier #3:  yyIx=" + yyIx + ", mmIx=" + mmIx + ", ddIx=" + ddIx + " for pattern <<<" + pattern + ">>>" );

            }

            _pattern = pattern;
            _yyIx = yyIx;
            _mmIx = mmIx;
            _ddIx = ddIx;
            _numericMonth = numericMonth;

        }

        public Pattern getPattern() {

            return _pattern;

        }

        public int getYyIx() {

            return _yyIx;

        }

        public int getMmIx() {

            return _mmIx;
        }

        public int getDdIx() {

            return _ddIx;

        }

        public boolean isNumericMonth() {

            return _numericMonth;

        }

        public String toString() {

            return "PatternInfo( p=<<<" + _pattern + ">>>, yyIx=" + _yyIx + ", mmIx=" + _mmIx + ", ddIx=" + _ddIx + ", nm=" + _numericMonth + " )";

        }

    }

    public static Pattern YYYY = Pattern.compile( "(\\d{4})" );
    public static Pattern YYYY_MM_DD_DASHES = Pattern.compile( "(\\d{4}) *- *(\\d\\d?) *- *(\\d\\d?)" );
    public static Pattern YYYY_MM_DD_PERIODS = Pattern.compile( "(\\d{4}) *\\. *(\\d\\d?) *\\. *(\\d\\d?)" );
    public static Pattern YYYY_MM_DD_SLASHES = Pattern.compile( "(\\d{4}) */ *(\\d\\d?) */ *(\\d\\d?)" );
    public static Pattern YYYY_MM_00_DASHES = Pattern.compile( "(\\d{4}) *- *(\\d\\d?) *- *00?" );
    public static Pattern YYYY_MM_00_PERIODS = Pattern.compile( "(\\d{4}) *\\. *(\\d\\d?) *\\. *00?" );
    public static Pattern YYYY_MM_00_SLASHES = Pattern.compile( "(\\d{4}) */ *(\\d\\d?) */ *00?" );
    public static Pattern YYYY_00_00_DASHES = Pattern.compile( "(\\d{4}) *- *00? *- *00?" );
    public static Pattern YYYY_00_00_PERIODS = Pattern.compile( "(\\d{4}) *\\. *00? *\\. *00?" );
    public static Pattern YYYY_00_00_SLASHES = Pattern.compile( "(\\d{4}) */ *00? */ *00?" );
    public static Pattern YYYY_MM_DASHES = Pattern.compile( "(\\d{4}) *- *(\\d\\d?)" );
    public static Pattern YYYY_MM_PERIODS = Pattern.compile( "(\\d{4}) *\\. *(\\d\\d?)" );
    public static Pattern YYYY_MM_SLASHES = Pattern.compile( "(\\d{4}) */ *(\\d\\d?)" );
    public static Pattern DD_MMM_YYYY = Pattern.compile( "(\\d\\d?) +([A-Za-z]{3,}) +(\\d{4})" );
    public static Pattern DD_MMM_YYYY_N = Pattern.compile( "(\\d\\d?) +(\\d\\d?) +(\\d{4})" );
    public static Pattern YYYY_MMM_DD = Pattern.compile( "(\\d{4}) +([A-Za-z]{3,}) +(\\d\\d?)" );
    public static Pattern YYYY_MMM_DD_N = Pattern.compile( "(\\d{4}) +(\\d\\d?) +(\\d\\d?)" );
    public static Pattern MMM_YYYY = Pattern.compile( "([A-Za-z]{3,}) +(\\d{4})" );
    public static Pattern MMM_DD_YYYY1 = Pattern.compile( "([A-Za-z]{3,}) +(\\d\\d?) *, *(\\d{4})" );
    public static Pattern MMM_DD_YYYY2 = Pattern.compile( "([A-Za-z]{3,}) +(\\d\\d?) */ *(\\d{4})" );
    public static Pattern MMM_DD_YYYY3 = Pattern.compile( "([A-Za-z]{3,}) +(\\d\\d?) +(\\d{4})" );

    private static PatternInfo[] s_patternInfos = {
            new PatternInfo( YYYY, 1, -1, -1, true ),
            new PatternInfo( YYYY_MM_DD_DASHES, 1, 2, 3, true ),
            new PatternInfo( YYYY_MM_DD_PERIODS, 1, 2, 3, true ),
            new PatternInfo( YYYY_MM_DD_SLASHES, 1, 2, 3, true ),
            new PatternInfo( YYYY_MM_00_DASHES, 1, 2, -1, true ),
            new PatternInfo( YYYY_MM_00_PERIODS, 1, 2, -1, true ),
            new PatternInfo( YYYY_MM_00_SLASHES, 1, 2, -1, true ),
            new PatternInfo( YYYY_00_00_DASHES, 1, -1, -1, true ),
            new PatternInfo( YYYY_00_00_PERIODS, 1, -1, -1, true ),
            new PatternInfo( YYYY_00_00_SLASHES, 1, -1, -1, true ),
            new PatternInfo( YYYY_MM_DASHES, 1, 2, -1, true ),
            new PatternInfo( YYYY_MM_PERIODS, 1, 2, -1, true ),
            new PatternInfo( YYYY_MM_SLASHES, 1, 2, -1, true ),
            new PatternInfo( DD_MMM_YYYY, 3, 2, 1, false ),
            new PatternInfo( DD_MMM_YYYY_N, 3, 2, 1, true ),
            new PatternInfo( YYYY_MMM_DD, 1, 2, 3, false ),
            new PatternInfo( YYYY_MMM_DD_N, 1, 2, 3, true ),
            new PatternInfo( MMM_DD_YYYY1, 3, 1, 2, false ),
            new PatternInfo( MMM_DD_YYYY2, 3, 1, 2, false ),
            new PatternInfo( MMM_DD_YYYY3, 3, 1, 2, false ),
            new PatternInfo( MMM_YYYY, 2, 1, -1, false ),
    };

    private static boolean s_traceOneCall = false;

    private static boolean s_traceAllCalls = false;

    private FlexibleDateTransmogrifier() {
        super();
    }

    /**
     Transmogrify a fairly wide range of date strings into a canonical date string.
     @param dateString the input date string. See {@link FlexibleDateTransmogrifier} JavaDocs for much more information.
     @return an {@code Optional<String>} instance containing the transmogrified string if the attempt worked or an empty {@code Optional<String>} instance if the attempt failed.
     See {@link FlexibleDateTransmogrifier} JavaDocs for more information.
     */

    @NotNull
    public static Optional<String> transmogrify( final String dateString ) {

        if ( ObtuseApproximateCalendarDate.FORMATTED_UNKNOWN_APPROXIMATE_DATE.equalsIgnoreCase( dateString.trim() ) ) {

            return Optional.of( ObtuseApproximateCalendarDate.FORMATTED_UNKNOWN_APPROXIMATE_DATE );

        }

        try {

            String trimmedDateString = dateString.trim();
            Optional<String> rval;
            for ( int i = 0; i < s_patternInfos.length; i++ ) {
                PatternInfo pi = s_patternInfos[i];
                if ( s_traceOneCall | s_traceAllCalls ) {

                    Logger.logMsg( "trying " + pi + " for \"" + dateString + "\"" );
                    ObtuseUtil.doNothing();

                }

                rval = doit( i, pi.getPattern(), pi.isNumericMonth(), trimmedDateString, pi.getYyIx(), pi.getMmIx(), pi.getDdIx() );
                if ( rval.isPresent() ) {

                    return rval;

                }

            }

            return Optional.empty();

        } finally {

            s_traceOneCall = false;

        }

    }

    public static void traceOneCall() {

        s_traceOneCall = true;

    }

    public static void setTraceAllCalls( final boolean traceAllCalls ) {

        s_traceAllCalls = traceAllCalls;

    }

    /**
     Transmogrify a date using a specified pattern (not for the faint of heart).
     @param ix used for debug output
     @param p the pattern to be used to take apart the date string
     @param numericMonth are numeric months required or prohibited (no support WITHIN this method for making them optional).
     @param dateString the date string to be parsed
     @param yyIx which group in a successful matcher (1-origin) contains the year (if the match works).
     @param mmIx which group in a successful matcher (1-origin) contains the month (if the match works).
     @param ddIx which group in a successful matcher (1-origin) contains the month (if the match works).
     @return an Optional containingthe date in yyyy-mm-dd format if the parse worked; empty otherwise.
     */

    public static Optional<String> doit( final int ix, final Pattern p, final boolean numericMonth, final String dateString, final int yyIx, final int mmIx, final int ddIx ) {

        StringBuilder sb = new StringBuilder();

        Matcher m = p.matcher( dateString );
        if ( m.matches() ) {

            try {

                sb.append( "ix=" )
                  .append( ix )
                  .append( ", yyIx=" )
                  .append( yyIx )
                  .append( ", mmIx=" )
                  .append( mmIx )
                  .append( ", ddIx=" )
                  .append( ddIx )
                  .append( ", dateString=" )
                  .append( ObtuseUtil.enquoteToJavaString( dateString ) )
                  .append( ", p=<<<" )
                  .append( p )
                  .append( ">>>" );

                if ( mmIx <= 0 && ddIx > 0 ) {

                    return Optional.empty();

                }

                if ( s_traceOneCall || s_traceAllCalls ) {

                    for ( int i = 0; i <= m.groupCount(); i += 1 ) {

                        Logger.logMsg( "group(" + i + ")=>>>" + m.group(i) + "<<<" );

                    }

                }

                String yearString = m.group( yyIx );
                sb.append( ", ys=\"" ).append( ObtuseUtil.enquoteToJavaString( yearString ) ).append( "\"" );
                int year = parseYear( yearString );
                sb.append( ", year=" ).append( year );
                if ( year < 0 ) {

                    return Optional.empty();

                }
                if ( mmIx <= 0 ) {

                    return Optional.of( yearString );

                }

                String monthString = m.group( mmIx );
                sb.append( ", ms=\"" ).append( ObtuseUtil.enquoteToJavaString( monthString ) ).append( "\"" );
                sb.append( ", nm=" ).append( numericMonth );
                int month = numericMonth ? parseNumericMonth( monthString ) : parseCharacterMonth( monthString );
                sb.append( ", m=" ).append( month );
                if ( month <= 0 ) {

                    return Optional.empty();

                }
                if ( ddIx <= 0 ) {

                    return Optional.of( yearString + "-" + ObtuseUtil.lpad( month, 2, '0' ) );

                }

                String dayString = m.group( ddIx );
                sb.append( ", ds=\"" ).append( ObtuseUtil.enquoteToJavaString( dayString ) ).append( "\"" );
                int day = parseDay( dayString );
                sb.append( ", d=" ).append( month );

                if ( day < 0 ) {

                    return Optional.empty();

                }

                return Optional.of( "" + year + "-" + ObtuseUtil.lpad( month, 2, '0' ) + "-" + ObtuseUtil.lpad( day, 2, '0' ) );

            } catch ( IndexOutOfBoundsException e ) {

                Logger.logErr( "FlexibleDateTransmogrifier:  logic error in transmogrifier (yyIx=" + yyIx + ",mmIx=" + mmIx + ",ddIx=" + ddIx + "), sb:  " + sb, e );

                return Optional.empty();

            }

        } else {

            if ( s_traceOneCall | s_traceAllCalls ) {

                Logger.logMsg( "pattern match of " + p + " on \"" + dateString + "\" failed, sb:  " + sb );

            }

            return Optional.empty();

        }

    }

    private static int parseYear( final String yearString ) {

        try {

            int year = Integer.parseInt( yearString );
            if ( yearString.length() == 4 ) {

                return year;

            } else {

                return -1;

            }

        } catch ( NumberFormatException e ) {

            return -2;

        }

    }

    private static final String[] s_monthNames = {
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
    };

    private static int parseCharacterMonth( final String monthString ) {

        if ( monthString.length() < 3 ) {

            return -1;

        }

        for ( int month = 0; month < 12; month += 1 ) {

            // If the provided month is exactly three characters long then check it against the first three characters of the known-valid name.

            if ( monthString.length() == 3 && s_monthNames[month].substring( 0, 3 ).equalsIgnoreCase( monthString ) ) {

                return month + 1;

            }

            // Also check the provided month name against the entire known-valid month name.

            if ( monthString.length() == s_monthNames[month].length() && s_monthNames[month].substring( 0, monthString.length() ).equalsIgnoreCase( monthString ) ) {

                return month + 1;

            }

        }

        return -2;

    }

    private static int parseNumericMonth( final String monthString ) {

        try {

            int month = Integer.parseInt( monthString );
            if ( month >= 1 && month <= 12 ) {

                return month;

            } else {

                return -1;

            }

        } catch ( NumberFormatException e ) {

            return -2;

        }

    }

    private static int parseDay( final String dayString ) {

        try {

            int day = Integer.parseInt( dayString );
            if ( day >= 1 && day <= 31 ) {

                return day;

            } else {

                return -1;

            }

        } catch ( NumberFormatException e ) {

            return -2;

        }

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "testing", null );

        boolean failed = false;
        long startTime = 0;
        long endTime;
        int maxTrials = 100000;
        int callCount = 0;

        for ( int trial = 0; trial <= maxTrials; trial += 1 ) {

            if ( trial == 1 ) {

                startTime = System.currentTimeMillis();

            }

            for ( TestData td : s_testData ) {

                for ( String input : td.input ) {

                    Optional<String> result = transmogrify( input );
                    callCount += 1;

                    if ( result.isPresent() ) {

                        if ( td.output == null ) {

                            failed = true;
                            if ( trial == 0 ) {

                                Logger.logMsg( "FlexibleDateTransmogrifier:  FAILED  input \"" + input + "\" transmogrified to \"" + result.get() + "\", should have failed" );
                                Optional<String> result2 = transmogrify( input );
                                Logger.logMsg( "second attempt:  " + result2 );
                                ObtuseUtil.doNothing();

                            }
                            break;

                        } else {

                            if ( trial == 0 ) {

                                Logger.logMsg( "FlexibleDateTransmogrifier:  CORRECT input \"" + input + "\" correctly transmogrified to \"" + result.get() + "\"" );

                            }

                        }

                    } else {

                        if ( td.output == null ) {

                            if ( trial == 0 ) {

                                Logger.logMsg( "FlexibleDateTransmogrifier:  CORRECT input \"" + input + "\" rejected" );

                            }

                        } else {

                            failed = true;
                            if ( trial == 0 ) {

                                Logger.logMsg( "FlexibleDateTransmogrifier:  FAILED  input \"" + input + "\" rejected, should have been transmogrified to \"" + td.output + "\"" );
                                traceOneCall();
                                Optional<String> result2 = transmogrify( input );
                                Logger.logMsg( "second attempt:  " + result2 );
                                ObtuseUtil.doNothing();

                            }
                            break;

                        }

                    }

                }

                if ( failed ) {

                    break;

                }

            }

            if ( failed ) {

                break;

            }

        }

        if ( !failed ) {

            endTime = System.currentTimeMillis();
            Logger.logMsg( "FlexibleDateTransmogrifier:  " + ObtuseUtil.readable( callCount ) + " took " + DateUtils.formatDuration( endTime - startTime ) + ", or " + ( callCount / (double)( endTime - startTime ) ) + " per ms" );

        }

    }

}
