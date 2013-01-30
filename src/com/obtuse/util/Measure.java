package com.obtuse.util;

import java.io.PrintStream;
import java.util.*;

/**
 * Measure how long things take.
 * <p/>
 * Copyright © 2008 Obtuse Systems Corporation.
 */

@SuppressWarnings("UnusedDeclaration")
public class Measure {

    private static boolean s_globallyEnabled = false;

    private final String _categoryName;

    private final long _startTimeMillis;

    private final boolean _initialized;

    private final Stack<StackLevelInfo> _ourStack;

    private static final Long LOCK = 0L;

    private static SortedMap<String,Stats> s_stats = new TreeMap<String, Stats>();

    private static int s_maxCategoryNameLength = 0;

    private static long s_measuringSinceMillis = System.currentTimeMillis();

    private static SortedMap<Long,Stack<StackLevelInfo>> s_threadStacks = new TreeMap<Long, Stack<StackLevelInfo>>();

    private static SortedMap<String,Stats> s_crossThreadStats = new TreeMap<String, Stats>();

    private static SortedMap<String,Stats> s_stackErrorStats = new TreeMap<String, Stats>();

    private static SortedMap<String,Stats> s_stackData;

    private static StackLevelStats s_stackStats = new StackLevelStats( "root", null );
    private static final String OUTER_DONE_STATS = "<outerDoneStats>";

//    private static Stats s_outerDoneStats = new Stats();
//    private static Stats s_innerDoneStats = new Stats();

    public static class StackLevelInfo {

        private final String _levelName;
        private final String _stackTraceAboveUs;
        private final String _fullStackTrace;
        private final List<String> _levelNames;

        public StackLevelInfo( String levelName, Stack<StackLevelInfo> stack ) {
            super();

            _levelName = levelName;
            _levelNames = new LinkedList<String>();

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

        private final SortedMap<String,StackLevelStats> _ourChildren;
        private final Stats _ourStats;
        private final StackLevelStats _parent;
        private final String _levelName;

        public StackLevelStats( String levelName, StackLevelStats parent ) {
            super();

            _levelName = levelName;
            _ourChildren = new TreeMap<String, StackLevelStats>();
            _ourStats = new Stats();
            _parent = parent;

        }

        public StackLevelStats getParent() {

            return _parent;

        }

        public boolean isRoot() {

            return _parent == null;

        }

        public void datum( List<String> relativePath, long delta ) {

            if ( relativePath.isEmpty() ) {

                _ourStats.datum( delta / 1e3 );

            } else {

                List<String> relativePathCopy = new LinkedList<String>( relativePath );
                String nextChildName = relativePathCopy.remove( /*relativePathCopy.size() - 1*/ 0 );

                StackLevelStats childNode = _ourChildren.get( nextChildName );

                if ( childNode == null ) {

                    childNode = new StackLevelStats( nextChildName, this );
                    _ourChildren.put( nextChildName, childNode );

                }

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

        public void showStats( PrintStream where, boolean showTitle ) {

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
                    ObtuseUtil.lpad( (long) _ourStats.n(), 10 )
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

            TreeSorter<Double, String> sorted = new TreeSorter<Double, String>(
                    new Comparator<Double>() {

                        public int compare( Double lhs, Double rhs ) {

                            return rhs.compareTo( lhs );
                        }
                    }
            );

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

    public Measure( String categoryName ) {
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
            Stack<StackLevelInfo> ourStack = Measure.s_threadStacks.get( threadId );
            if ( ourStack == null ) {

                ourStack = new Stack<StackLevelInfo>();
                Measure.s_threadStacks.put( threadId, ourStack );

            }

            _ourStack = ourStack;

            ourStack.push( new StackLevelInfo( categoryName, ourStack ) );

        }

        _initialized = true;

    }

    public void done() {

        if ( !_initialized ) {

            return;

        }

        long now = System.currentTimeMillis();
        long delta = now - _startTimeMillis;

        synchronized ( Measure.LOCK ) {

            long innerNow = System.currentTimeMillis();

            recordData( Measure.s_stats, delta );
//            Stats stats = Measure.s_stats.get( _categoryName );
//            if ( stats == null ) {
//
//                stats = new Stats();
//
//                Measure.s_stats.put( _categoryName, stats );
//
//                if ( _categoryName.length() > Measure.s_maxCategoryNameLength ) {
//
//                    Measure.s_maxCategoryNameLength = _categoryName.length();
//
//                }
//
//            }
//
//            //noinspection MagicNumber
//            stats.datum( (double)delta / 1e3 );

            long threadId = Thread.currentThread().getId();
            Stack<StackLevelInfo> ourStack = Measure.s_threadStacks.get( threadId );
            if ( ourStack == null || ourStack != _ourStack ) {

                recordData( Measure.s_crossThreadStats, delta );

            } else if ( ourStack.isEmpty() || !ourStack.peek().getLevelName().equals( _categoryName ) ) {

                ourStack.clear();
                recordData( Measure.s_stackErrorStats, delta );

            } else {

                StackLevelInfo ourLevel = ourStack.pop();
                String stackAboveUs = ourLevel.getStackTraceAboveUs();
                Measure.s_stackStats.datum( ourLevel.getLevelNames(), delta );
//                Measure.recordData( Measure.s_stackData, ourLevel.getLevelName(), delta );

            }

            Measure.recordData( Measure.s_stats, "<innerDoneStats>", System.currentTimeMillis() - innerNow );
//            Measure.s_innerDoneStats.datum( ( System.currentTimeMillis() - innerNow ) / 1e3 );

        }

        Measure.recordData( Measure.s_stats, Measure.OUTER_DONE_STATS, System.currentTimeMillis() - now );
//        Measure.s_outerDoneStats.datum( ( System.currentTimeMillis() - now ) / 1e3 );

    }

    public boolean isInitialized() {

        return _initialized;

    }

    public static boolean isGloballyEnabled() {

        return Measure.s_globallyEnabled;

    }

    public static void setGloballyEnabled( boolean globallyEnabled ) {

        Measure.s_globallyEnabled = globallyEnabled;

    }


    public static double adjustMean( String categoryName, double mean ) {

        if ( !Measure.s_globallyEnabled || categoryName.startsWith( "<" ) ) {

            return mean;

        } else {

            return mean - Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean();

        }

    }

    public static double adjustSum( String categoryName, double sum, int n ) {

        if ( !Measure.s_globallyEnabled || categoryName.startsWith( "<" ) ) {

            return sum;

        } else {

            return sum - n * Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean();

        }

    }

    private void recordData( SortedMap<String,Stats> map, long delta ) {

        Measure.recordData( map, _categoryName, delta );

        if ( _categoryName.length() > Measure.s_maxCategoryNameLength ) {

            Measure.s_maxCategoryNameLength = _categoryName.length();

        }

    }

    private static void recordData( SortedMap<String,Stats> map, String name, long delta ) {

        Stats stats = map.get( name );
        if ( stats == null ) {

            stats = new Stats();

            map.put( name, stats );

        }

        stats.datum( (double)delta / 1e3 );

    }

    public static void showStats() {

        Measure.showStats( System.out, true );

    }

    public static void showStats( PrintStream where ) {

        Measure.showStats( where, true );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void showStats( PrintStream where, boolean showTitle ) {

        if ( !Measure.s_globallyEnabled ) {

            return;

        }

        Measure.s_stackStats.showStats( where, showTitle );

        TreeSorter<Double,String> sorted = new TreeSorter<Double, String>(
                new Comparator<Double>() {
                    public int compare( Double lhs, Double rhs ) {
                        return rhs.compareTo( lhs );
                    }
                }
        );

//        SortedMap<Double,String> sorted = new TreeMap<Double, String>(
//                new Comparator<Double>() {
//                    public int compare( Double lhs, Double rhs ) {
//                        return rhs.compareTo( lhs );
//                    }
//                }
//        );

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
            );

        }

        for ( String categoryName : sorted.getAllValues() ) {

            Stats stats = Measure.s_stats.get( categoryName );

            where.println(
                    ObtuseUtil.rpad( categoryName, Measure.s_maxCategoryNameLength + 2 )
                            + " : " +
                            ObtuseUtil.lpad( (long) stats.n(), 10 )
                            + " : " +
                            String.format( "%14.9f", stats.mean() )
                            + " : " +
                            String.format( "%14.9f", stats.populationStdev() )
                            + " : " +
                            String.format( "%16.9f", stats.sum() )
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
            );


        }

        where.println();

        String adjustmentAsString = String.format( "%14.9f", Measure.s_stats.get( Measure.OUTER_DONE_STATS ).mean() );
        where.println( "* means adjusted by " + adjustmentAsString + " and sums adjusted by n * " + adjustmentAsString );

        where.println( "Measuring for " + Math.round( ( System.currentTimeMillis() - Measure.s_measuringSinceMillis ) / 1e3 ) + " seconds" );
//        where.println( "Outer done stats are " + Measure.s_outerDoneStats );
//        where.println( "Inner done stats are " + Measure.s_innerDoneStats );

    }

    public static void restart() {

        Measure.s_stats.clear();
        Measure.s_maxCategoryNameLength = 0;
        Measure.s_measuringSinceMillis = System.currentTimeMillis();

    }

    public static void measure( String categoryName, Runnable runnable ) {

        Measure measure = new Measure( categoryName );
        runnable.run();
        measure.done();

    }

}
