/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 Measure how long things take.
 */

@SuppressWarnings("UnusedDeclaration")
public class Measure implements Closeable {

    private static final String OUTER_DONE_STATS = "<outerDoneStats>";

    private static final String INNER_DONE_STATS = "<innerDoneStats>";

    private static boolean s_globallyEnabled = false;

    private final String _categoryName;

    private final long _startTimeMillis;

    private final boolean _initialized;

    private boolean _finished = false;

    private long _finishedDelta = 0L;

    private final Stack<StackLevelInfo> _ourStack;

    private static final Long LOCK = 0L;

    private static SortedMap<String, Stats> s_stats = new TreeMap<>();

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
                    ObtuseUtil.lpad( (long)_ourStats.n(), 10 )
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

        _categoryName = categoryName;
        _startTimeMillis = System.currentTimeMillis();

        if ( !Measure.s_globallyEnabled ) {

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

        return done( _categoryName );

    }

    /**
     Record the completion of an event (possibly overriding the event's category name in the flat measurement bucketing).
     <p>This is primarily intended to facilitate the separate measurement of success cases and failure cases of events.
     Overriding the category name only affects which flat measurement category the time for this event lands in.
     The nested call stack accounting uses the event's category name as specified when this instance was created (because
     the stack accounting scheme cannot handle changing event names).
     </p>
     @param categoryName the category under which this event should be 'filed'.
     @return the duration of the event in milliseconds.
     */

    public long done( final @NotNull String categoryName ) {

        if ( !_initialized ) {

            return 0;

        }

        if ( _finished ) {

            return _finishedDelta;

        }

        long now = System.currentTimeMillis();
        long delta = now - _startTimeMillis;

        synchronized ( Measure.LOCK ) {

            long innerNow = System.currentTimeMillis();

            recordData( categoryName, Measure.s_stats, delta );

            long threadId = Thread.currentThread().getId();
            Stack<StackLevelInfo> ourStack = Measure.s_threadStacks.get( threadId );
            if ( ourStack == null || ourStack != _ourStack ) {

                recordData( _categoryName, Measure.s_crossThreadStats, delta );

            } else if ( ourStack.isEmpty() || !ourStack.peek().getLevelName().equals( _categoryName ) ) {

                ourStack.clear();
                recordData( _categoryName, Measure.s_stackErrorStats, delta );

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

    public boolean isInitialized() {

        return _initialized;

    }

    public boolean isFinished() {

        return _finished;

    }

    public long getFinishedDelta() {

        return _finished ? 0 : _finishedDelta;

    }

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

    private void recordData( final String overrideCategoryName, final SortedMap<String, Stats> map, final long delta ) {

        Measure.recordData( map, overrideCategoryName, delta );

        Measure.s_maxCategoryNameLength = Math.max( overrideCategoryName.length(), Measure.s_maxCategoryNameLength );

    }

    private static void recordData( final SortedMap<String, Stats> map, final String name, final long delta ) {

        Stats stats = map.computeIfAbsent( name, k -> new Stats() );

        stats.datum( (double)delta / 1e3 );

    }

    public static void showStats() {

        Measure.showStats( System.out, true );

    }

    public static void showStats( final PrintStream where ) {

        Measure.showStats( where, true );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void showStats( final PrintStream where, final boolean showTitle ) {

        if ( !Measure.s_globallyEnabled ) {

            return;

        }

        synchronized ( Measure.LOCK ) {

            Measure.s_stackStats.showStats( where, showTitle );

            TreeSorter<Double, String> sorted = new TreeSorter<>( Comparator.reverseOrder() );

            for ( String categoryName : Measure.s_stats.keySet() ) {

                Stats stats = Measure.s_stats.get( categoryName );

                double value = Measure.adjustSum( categoryName, stats.sum(), stats.n() );

                sorted.add( value, categoryName );

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

            for ( String categoryName : sorted.getAllValues() ) {

                Stats stats = Measure.s_stats.get( categoryName );

                where.println(
                        ObtuseUtil.rpad( categoryName, Measure.s_maxCategoryNameLength + 2 )
                        + " : " +
                        ObtuseUtil.lpad( (long)stats.n(), 10 )
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

    /**
     A (relatively) easy way to measure a block of code.
     @param categoryName the name of the category being measured.
     @param runnable the code to be measured.
     @return the delta for this invocation.
     @deprecated Switch to the at least arguably cleaner and definitely more flexible
     <blockquote>
     <code>try ( Measure m = new Measure( __category_name__ ) ) {
     <blockquote>... the code to be measured ...</blockquote>
     }</code>
     </blockquote>
     */

    @Deprecated
    public static long measure( final String categoryName, final Runnable runnable ) {

        Measure measure = new Measure( categoryName );
        runnable.run();
        return measure.done();

    }

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
