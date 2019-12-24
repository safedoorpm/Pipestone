/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.*;

/**
 Measure how long things take.
 */

@SuppressWarnings("UnusedDeclaration")
public class Measure implements Closeable {

    public enum SortedBy {

        ADJUSTED_TOTAL_COST { @NotNull public String readableName() { return "Adjusted Cost"; } },
        MEAN { @NotNull public String readableName() { return "Mean"; } },
        COUNT { @NotNull public String readableName() { return "Count"; } },
        RATE { @NotNull public String readableName() { return "Rate"; } },
        TOTAL { @NotNull public String readableName() { return "Total Cost"; } },
        ALPHABETICAL { @NotNull public String readableName() { return "Alphabetical"; } };

        @NotNull public abstract String readableName();

    }

    private static final String OUTER_DONE_STATS = "<outerDoneStats>";

    private static final String INNER_DONE_STATS = "<innerDoneStats>";

    private static boolean s_globallyEnabled = false;

    private static boolean s_onlyEventThreadWork = true;

    private final boolean _onEventThread;

    private final String _categoryName;

    private final long _startTimeMillis;

    private final boolean _initialized;

    private boolean _finished = false;

    private long _finishedDelta = 0L;

    private final Stack<StackLevelInfo> _ourStack;

    private static final Long LOCK = 0L;

    private static SortedMap<String, Stats> s_stats = new TreeMap<>();

    @SuppressWarnings("ConstantConditions")
    private static int s_maxCategoryNameLength = Math.max( OUTER_DONE_STATS.length(), INNER_DONE_STATS.length() );

    private static long s_measuringSinceMillis = System.currentTimeMillis();

    private static SortedMap<Long, Stack<StackLevelInfo>> s_threadStacks = new TreeMap<>();

    private static SortedMap<String, Stats> s_crossThreadStats = new TreeMap<>();

    private static SortedMap<String, Stats> s_stackErrorStats = new TreeMap<>();

    private static SortedMap<String, Stats> s_stackData;
    private static StackLevelStats s_stackStats = new StackLevelStats( "root", null );

    static {

        s_stats.put( OUTER_DONE_STATS, new Stats() );

    }

    public static class StackLevelInfo {

        private final String _levelName;
        private final String _stackTraceAboveUs;
        private final String _fullStackTrace;
        private final List<String> _levelNames;

        public StackLevelInfo( final String levelName, final Stack<StackLevelInfo> stack ) {

            super();

            _levelName = levelName;
            _levelNames = new LinkedList<>();

            StringBuilder sb = new StringBuilder().append( "/" );
            String arrow = "";
            for ( StackLevelInfo level : stack ) {

                sb.append( arrow ).append( level.getLevelName() );
                arrow = "->";
                _levelNames.add( level.getLevelName() );

            }
            _stackTraceAboveUs = sb.toString();

            sb.append( arrow ).append( levelName );

            _fullStackTrace = sb.toString();
            _levelNames.add( levelName );

        }

        public String getLevelName() {

            return _levelName;

        }

        public String getStackTraceAboveUs() {

            return _stackTraceAboveUs;

        }

        public String getStackTrace() {

            return _fullStackTrace;

        }

        public List<String> getLevelNames() {

            return Collections.unmodifiableList( _levelNames );

        }

        public String toString() {

            return "SLI:  " + _fullStackTrace;

        }

    }

    public static class StackLevelStats {

        private final SortedMap<String, StackLevelStats> _ourChildren;
        private final Stats _ourStats;
        private final StackLevelStats _parent;
        private final String _levelName;

        public StackLevelStats( final String levelName, final StackLevelStats parent ) {

            super();

            _levelName = levelName;
            _ourChildren = new TreeMap<>();
            _ourStats = new Stats();
            _parent = parent;

        }

        public StackLevelStats getParent() {

            return _parent;

        }

        public boolean isRoot() {

            return _parent == null;

        }

        public void datum( final List<String> relativePath, final long delta ) {

            if ( relativePath.isEmpty() ) {

                _ourStats.datum( delta / 1e3 );

            } else {

                List<String> relativePathCopy = new LinkedList<>( relativePath );
                String nextChildName = relativePathCopy.remove( /*relativePathCopy.size() - 1*/ 0 );

                StackLevelStats childNode = _ourChildren.computeIfAbsent( nextChildName, n -> new StackLevelStats( n, this ) );

                childNode.datum( relativePathCopy, delta );

            }

        }

        public String getLevelName() {

            return _levelName;

        }

        public Collection<StackLevelStats> getChildren() {

            return Collections.unmodifiableCollection( _ourChildren.values() );

        }

        public Stats getStats() {

            return new Stats( _ourStats );

        }

        public String toString() {

            if ( _parent == null ) {

                return "SLS:  /" + _levelName;

            } else {

                return _parent.toString() + "->" + _levelName;

            }

        }

        public void showStats( final PrintStream where, final boolean showTitle ) {

            if ( showTitle ) {

                where.println(
                        ObtuseUtil.lpad( "count", 10 )
                        + "   " +
                        ObtuseUtil.lpad( "mean", 14 )
                        + "   " +
                        ObtuseUtil.lpad( "stdev", 14 )
                        + "   " +
                        ObtuseUtil.lpad( "total", 16 )
                        + "   " +
                        "stack trace"
                );

            }

            where.println(
                    ObtuseUtil.lpad( _ourStats.n(), 10 )
                    + " : " +
                    String.format( "%14.9f", _ourStats.n() == 0 ? 0 : _ourStats.mean() )
                    + " : " +
                    String.format( "%14.9f", _ourStats.n() == 0 ? 0 : _ourStats.populationStdev() )
                    + " : " +
                    String.format( "%16.9f", _ourStats.sum() )
                    + "   " + this
            );

            where.println(
                    ObtuseUtil.lpad( "", 10 )
                    + " * " +
                    String.format( "%14.9f", _ourStats.n() == 0 ? 0 : Measure.adjustMean( "", _ourStats.mean() ) )
                    + " * " +
                    ObtuseUtil.lpad( "", 14 )
                    + " * " +
                    String.format( "%16.9f", Measure.adjustSum( "", _ourStats.sum(), _ourStats.n() ) )
                    + "   " + this
            );

            TreeSorter<Double, String> sorted = new TreeSorter<>( Comparator.reverseOrder() );

            for ( String categoryName : _ourChildren.keySet() ) {

                Stats stats = _ourChildren.get( categoryName ).getStats();

                double value = stats.sum();

                sorted.add( value, categoryName );

            }

            for ( String categoryName : sorted.getAllValues() ) {

                _ourChildren.get( categoryName ).showStats( where, false );

            }

        }

    }

    public Measure( final @NotNull String categoryName ) {

        super();

        _onEventThread = SwingUtilities.isEventDispatchThread();

        _categoryName = categoryName;
        _startTimeMillis = System.currentTimeMillis();

        if ( !Measure.s_globallyEnabled ) {

            _ourStack = null;
            _initialized = false;

            return;

        }

        if ( s_onlyEventThreadWork && !_onEventThread ) {

            _ourStack = null;
            _initialized = false;

            return;

        }

        synchronized ( Measure.LOCK ) {

            long threadId = Thread.currentThread().getId();
            Stack<StackLevelInfo> ourStack = Measure.s_threadStacks.computeIfAbsent( threadId, k -> new Stack<>() );

            _ourStack = ourStack;

            ourStack.push( new StackLevelInfo( categoryName, ourStack ) );

        }

        _initialized = true;

    }

    @NotNull
    public String getCategoryName() {

        return _categoryName;

    }

    @Override
    public void close() {

        done();

    }

    /**
     Record the completion of this event.
     @return the duration of this event in milliseconds.
     */

    public long done() {

        return done( _categoryName, null );

    }

    /**
     Record the completion of an event (possibly overriding the event's category name in the flat measurement bucketing).
     <p>This is primarily intended to facilitate the separate measurement of success cases and failure cases of events.
     Overriding the category name only affects which flat measurement category the time for this event lands in.
     The nested call stack accounting uses the event's category name as specified when this instance was created (because
     the stack accounting scheme cannot handle changing event names).
     </p>
     @param categoryName the category under which this event should be 'filed'.
     @param categoryNameAugmentation an optional name 'appendage' that changes which
     bucket this instance contributes to. Effectively, this allows the category name
     of an instance to be changed after the instance is created. This can be useful
     if something happens during the lifetime of the instance that makes what the instance
     is measuring sufficiently atypical to warrant being handled separately.
     For example, an instance measuring something that has failed unexpectedly might
     end with a call to
     @return the duration of the event in milliseconds.
     */

    public long done( @NotNull final String categoryName, @Nullable final String categoryNameAugmentation ) {

        if ( !_initialized ) {

            return 0;

        }

        if ( _finished ) {

            return _finishedDelta;

        }

        long now = System.currentTimeMillis();
        long delta = now - _startTimeMillis;

        if ( categoryNameAugmentation != null ) {

            ObtuseUtil.doNothing();

        }

        synchronized ( Measure.LOCK ) {

            long innerNow = System.currentTimeMillis();

            recordData( categoryName, categoryNameAugmentation, Measure.s_stats, delta );

            long threadId = Thread.currentThread().getId();
            Stack<StackLevelInfo> ourStack = Measure.s_threadStacks.get( threadId );
            if ( ourStack == null || ourStack != _ourStack ) {

                recordData( _categoryName, null, Measure.s_crossThreadStats, delta );

            } else if ( ourStack.isEmpty() || !ourStack.peek().getLevelName().equals( _categoryName ) ) {

                ourStack.clear();
                recordData( _categoryName, null, Measure.s_stackErrorStats, delta );

            } else {

                StackLevelInfo ourLevel = ourStack.pop();
                String stackAboveUs = ourLevel.getStackTraceAboveUs();
                Measure.s_stackStats.datum( ourLevel.getLevelNames(), delta );

            }

            Measure.recordData( Measure.s_stats, Measure.INNER_DONE_STATS, System.currentTimeMillis() - innerNow );

        }

        Measure.recordData( Measure.s_stats, Measure.OUTER_DONE_STATS, System.currentTimeMillis() - now );

        _finished = true;
        _finishedDelta = delta;

        return delta;

    }

    public long getStartTimeMillis() {

        return _startTimeMillis;

    }

    public long deltaMillis() {

        return System.currentTimeMillis() - _startTimeMillis;

    }

    public boolean isInitialized() {

        return _initialized;

    }

    public boolean isFinished() {

        return _finished;

    }

    public long getFinishedDelta() {

        return _finished ? 0 : _finishedDelta;

    }

    @Contract(pure = true)
    public static boolean isGloballyEnabled() {

        return Measure.s_globallyEnabled;

    }

    public static void setGloballyEnabled( final boolean globallyEnabled ) {

        Measure.s_globallyEnabled = globallyEnabled;

    }

    public static double adjustMean( final String categoryName, final double mean ) {

        if ( !Measure.s_globallyEnabled || categoryName.startsWith( "<" ) ) {

            return mean;

        } else {

            return mean - Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean();

        }

    }

    public static double adjustSum( final String categoryName, final double sum, final int n ) {

        if ( !Measure.s_globallyEnabled || categoryName.startsWith( "<" ) ) {

            return sum;

        } else {

            return sum - n * Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean();

        }

    }

    private void recordData(
            @NotNull final String categoryName,
            @Nullable final String categoryNameAugmentation,
            @NotNull final SortedMap<String, Stats> map, final long delta
    ) {

        String overrideCategoryName;

        if ( categoryNameAugmentation == null ) {

            overrideCategoryName = categoryName;

        } else {

            overrideCategoryName = categoryName + ":" + categoryNameAugmentation;

        }

        Measure.recordData( map, overrideCategoryName, delta );

        Measure.s_maxCategoryNameLength = Math.max( overrideCategoryName.length(), Measure.s_maxCategoryNameLength );

    }

    private static void recordData( final @NotNull SortedMap<String, Stats> map, final String name, final long delta ) {

        Stats stats = map.computeIfAbsent( name, k -> new Stats() );

        stats.datum( (double)delta / 1e3 );

    }

    public static void showStats() {

        showStats( SortedBy.ADJUSTED_TOTAL_COST );

    }

    public static void showStats( @NotNull final SortedBy sortedBy ) {

        Measure.showStats( sortedBy, System.out, true );

    }

    public static void showStats( @NotNull SortedBy sortedBy, final PrintStream where ) {

        Measure.showStats( sortedBy, where, true );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void showStats( @NotNull final SortedBy sortedBy, final PrintStream where, final boolean showTitle ) {

        if ( !Measure.s_globallyEnabled ) {

            Logger.logMsg( "```````````````````````````" );
            Logger.logMsg( "```````````````````````````" );
            Logger.logMsg( "```````````````````````````" );
            Logger.logMsg( "``````````````````````````` Measure facility is currently globally disabled" );
            Logger.logMsg( "```````````````````````````" );
            Logger.logMsg( "```````````````````````````" );
            Logger.logMsg( "```````````````````````````" );

            return;

        }

        synchronized ( Measure.LOCK ) {

            Measure.s_stackStats.showStats( where, showTitle );

            TreeSorter<Double,String> sorter = new TreeSorter<>( Comparator.reverseOrder() );
//                if ( sortedBy == SortedBy.ALPHABETICAL ) {
//
//                    sorter = null;
//
//                } else {
//
//                    sorter = new TreeSorter<>( Comparator.reverseOrder() );
//
//                }

            double ix = 0.0;
            for ( String categoryName : Measure.s_stats.keySet() ) {

                Stats stats = Measure.s_stats.get( categoryName );

                switch ( sortedBy ) {

                    case ADJUSTED_TOTAL_COST:
                        double adjustedTotalCost = Measure.adjustSum( categoryName, stats.sum(), stats.n() );
                        sorter.add( adjustedTotalCost, categoryName );
                        break;

                    case COUNT:
                        sorter.add( (double)stats.n(), categoryName );
                        break;

                    case MEAN:
                        sorter.add( stats.mean(), categoryName );
                        break;

                    case RATE:
                        double rate = stats.mean() == 0 ? Double.POSITIVE_INFINITY : 1 / stats.mean();
                        if ( rate > 0 && rate < 1e9 ) {

                            sorter.add(
                                    rate,
                                    categoryName
                            );

                        }

                        break;

                    case TOTAL:
                        sorter.add( stats.sum(), categoryName );
                        break;

                    case ALPHABETICAL:
                        sorter.add( ix, categoryName );
                        ix += 1;

                }

            }

            if ( showTitle ) {

                where.println(
                        ObtuseUtil.rpad( "category", Measure.s_maxCategoryNameLength + 2 )
                        + "   " +
                        ObtuseUtil.lpad( "count", 10 )
                        + "   " +
                        ObtuseUtil.lpad( "mean", 14 )
                        + "   " +
                        ObtuseUtil.lpad( "stdev", 14 )
                        + "   " +
                        ObtuseUtil.lpad( "total", 16 )
                        + "   " +
                        ObtuseUtil.lpad( "events/sec", 14 )
                );

            }

            for ( String categoryName : sorter.getAllValues() ) {

                Stats stats = Measure.s_stats.get( categoryName );

                where.println(
                        ObtuseUtil.rpad( categoryName, Measure.s_maxCategoryNameLength + 2 )
                        + " : " +
                        ObtuseUtil.lpad( stats.n(), 10 )
                        + " : " +
                        String.format( "%14.9f", stats.mean() )
                        + " : " +
                        String.format( "%14.9f", stats.populationStdev() )
                        + " : " +
                        String.format( "%16.9f", stats.sum() )
                        + " : " +
                        String.format( "%14.3f/s", (stats.mean() == 0 ? Double.POSITIVE_INFINITY : 1 / stats.mean() ) )
                );

                where.println(
                        ObtuseUtil.rpad( "", Measure.s_maxCategoryNameLength + 2 )
                        + " * " +
                        ObtuseUtil.lpad( "", 10 )
                        + " * " +
                        String.format( "%14.9f", Measure.adjustMean( categoryName, stats.mean() ) )
                        + " * " +
                        ObtuseUtil.lpad( "", 14 )
                        + " * " +
                        String.format( "%16.9f", Measure.adjustSum( categoryName, stats.sum(), stats.n() ) )
                        + " * " +
                        String.format( "%14.3f/s", ( Measure.adjustMean( categoryName, stats.mean() ) == 0 ? Double.POSITIVE_INFINITY : 1 / Measure.adjustMean( categoryName, stats.mean() ) ) )
                );

            }

            where.println();

            String adjustmentAsString = String.format( "%14.9f", Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean() );
            where.println( "* means adjusted by " + adjustmentAsString + " and sums adjusted by n * " + adjustmentAsString );

            where.println( "Measuring for " +
                           Math.round( ( System.currentTimeMillis() - Measure.s_measuringSinceMillis ) / 1e3 ) +
                           " seconds" );

        }

    }

    public static void restart() {

        Measure.s_stats.clear();
        Measure.s_maxCategoryNameLength = 0;
        Measure.s_measuringSinceMillis = System.currentTimeMillis();

    }

//    /**
//     A (relatively) easy way to measure a block of code.
//     @param categoryName the name of the category being measured.
//     @param runnable the code to be measured.
//     @return the delta for this invocation.
//     <p>While there may still be situations in which this {@link Runnable}-based approach makes sense,
//     it is probably best to switch to the at least arguably cleaner and definitely more flexible
//     <blockquote>
//     <code>try ( Measure ignored = new Measure( "category name" ) ) {
//     <blockquote>... the code to be measured ...</blockquote>
//     }</code>
//     </blockquote></p>
//     <p>P.S. the above try block based approach is exactly how this method is implemented.</p>
//
//     */
//
//    @Deprecated
//    public static long measure( final String categoryName, @NotNull final Runnable runnable ) {
//
//        try ( Measure m = new Measure( categoryName ) ) {
//
//            runnable.run();
//
//            return m.deltaMillis();
//
//        }
//
//    }

    public String toString() {

        if ( _initialized ) {

            if ( _finished ) {

                return "Measure( finished delta=" + DateUtils.formatDuration( _finishedDelta ) + " )";

            } else {

                return "Measure( active delta=" +
                       DateUtils.formatDuration( System.currentTimeMillis() - _startTimeMillis ) +
                       " )";

            }

        } else {

            return "Measure( <<uninitialized>> )";

        }

    }

}
