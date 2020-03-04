/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.RejectRangeException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 Keep track of ranges of values.
 <p/>
 Maintains a series of sorted ranges.  Ranges are combined when gaps between them are filled in.
 */

@SuppressWarnings("UnusedDeclaration")
public class Ranges<T extends Comparable<T>> implements Iterable<Range<T>>, Serializable {

    /**
     Compare two T's according to how they are defined to compare.
     <p/>
     This class is a named class (as opposed to an anonymous class) so that we can specify that it implements Serializable.
     */

    private class RangeComparator implements Comparator<T>, Serializable {

        public int compare( final T lhs, final T rhs ) {

            return lhs.compareTo( rhs );

        }

    }

    @SuppressWarnings({ "ClassWithoutToString" })
    private final SortedMap<T, Range<T>> _ranges = new TreeMap<>(
            new RangeComparator()
    );

    @SuppressWarnings({ "ClassWithoutToString" })
    private final RangeFactory<T> _rangeFactory;

    public Ranges( final Range<T> range, final RangeFactory<T> rangeFactory )
            throws RejectRangeException {

        super();

        _rangeFactory = rangeFactory;
        add( range );

    }

    public Ranges( final RangeFactory<T> rangeFactory ) {

        super();

        _rangeFactory = rangeFactory;

    }

    public int size() {

        return _ranges.size();

    }

    public boolean isEmpty() {

        return _ranges.isEmpty();

    }

    public Collection<Range<T>> getRanges() {

        return Collections.unmodifiableCollection( _ranges.values() );

    }

    public Ranges<T> getOverlappingDateRanges( final Range<T> range, final RangeFactory<T> rangeFactory )
            throws RejectRangeException {

        Ranges<T> rval = new Ranges<>( rangeFactory );
        for ( T key : _ranges.keySet() ) {

            Range<T> r = _ranges.get( key );

            if ( r.overlaps( range ) ) {

                rval.add( r );

            } else if ( r.getLongEndValue() < range.getLongStartValue() ) {

                break;

            }

        }

        return rval;

    }

    public boolean hasOverlappedRanges( final Range<T> range )
            throws RejectRangeException {

        Measure m = new Measure( "head map" );
        try {

            SortedMap<T, Range<T>> headMap = _ranges.headMap( range.getStartValue() );
            if ( !headMap.isEmpty() ) {

                T key = headMap.lastKey();
                Range<T> r = _ranges.get( key );

                if ( r.overlaps( range ) ) {

                    return true;

                }

            }

        } finally {

            m.done();

        }

        m = new Measure( "tail map" );
        try {

            // IntelliJ complains that the values returned by keySet() are not necessarily T instances.
            // It is wrong. The _ranges map uses T instances as keys which means that a tail map of a
            // _ranges map uses T instances as keys which means that this call to keySet can only return T instances.

            for ( T key : _ranges.tailMap( range.getStartValue() )
                                 .keySet() ) {

                Range<T> r = _ranges.get( key );

                if ( r.overlaps( range ) ) {

                    return true;

                }

                // If the current range is after the range we are looking for then there is no chance of an overlap of ranges.

                if ( range.getLongEndValue() < r.getLongStartValue() ) {

                    break;

                }

            }

        } finally {

            m.done();

        }

        return false;

    }

    /**
     Add a new range to this set of ranges, merging ranges as appropriate.

     @param newRange the to-be-added range.
     @return this set of ranges (to allow chained adds).
     @throws com.obtuse.util.exceptions.RejectRangeException thrown if this instance's DateRangeFactory throws this exception.
     */

    public Ranges<T> add( final Range<T> newRange )
            throws RejectRangeException {

        myAdd( newRange );

        //noinspection ReturnOfThis
        return this;

    }

    public void dump() {

        for ( Range<T> r : getRanges() ) {

            r.dump();

        }

    }

    public void dump( final String why ) {

        Logger.logMsg( "dumping range - " + why );
        dump();

    }

    @NotNull
    public Iterator<Range<T>> iterator() {

        return _ranges.values()
                      .iterator();

    }

    @SuppressWarnings({ "UnusedReturnValue" })
    private Ranges<T> myAdd( final Range<T> newRange ) throws RejectRangeException {

        if ( _ranges.isEmpty() ) {

            _ranges.put( newRange.getStartValue(), newRange );
            //noinspection ReturnOfThis
            return this;

        }

        SortedMap<T, Range<T>> sortedByStartValue = new TreeMap<>();
        SortedMap<T, Range<T>> sortedByEndValue = new TreeMap<>();
        sortedByStartValue.put( newRange.getStartValue(), newRange );
        sortedByEndValue.put( newRange.getEndValue(), newRange );
        SortedMap<T, Range<T>> existingRecordsToReplace = new TreeMap<>();

        for ( T startValue : _ranges.keySet() ) {
            Range<T> r = _ranges.get( startValue );

            if ( r.overlaps( newRange ) ) {

                existingRecordsToReplace.put( r.getStartValue(), r );
                sortedByStartValue.put( r.getStartValue(), r );
                sortedByEndValue.put( r.getEndValue(), r );

            }

            // Does the current range touch the new range?

            if ( r.touches( newRange ) ) {

                existingRecordsToReplace.put( r.getStartValue(), r );
                sortedByStartValue.put( r.getStartValue(), r );
                sortedByEndValue.put( r.getEndValue(), r );

            } else if ( r.getStartValue()
                         .compareTo( newRange.getEndValue() ) > 0 ) {

                // The current range does not touch the new range and the current range is completely after the new range.
                // No more overlaps or touches are possible so we can bail out now.

                break;

            }

        }

        Range<T> tmpRange = _rangeFactory.createMergedRange( sortedByStartValue, sortedByEndValue );

        for ( T victimKey : existingRecordsToReplace.keySet() ) {

            _ranges.remove( victimKey );

        }

        _ranges.put( tmpRange.getStartValue(), tmpRange );

        //noinspection ReturnOfThis
        return this;

    }

    public String toString() {

        StringBuilder rvalBuilder = new StringBuilder();
        String comma = "";

        for ( Range<T> r : _ranges.values() ) {

            rvalBuilder.append( comma )
                       .append( r );
            comma = ", ";

        }

        return "Range( { " + rvalBuilder.toString() + " } )";

    }

    @SuppressWarnings({ "MagicNumber", "UseOfSystemOutOrSystemErr" })
    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Ranges", "testing" );
        @SuppressWarnings({ "ClassWithoutToString" })
        RangeFactory<Integer> rangeFactory = new RangeFactory<>() {

            private Range<Integer> createRange( final Range<Integer> before, final Range<Integer> after ) {

                return new Range<>(
                        before.getStartValue(),
                        after.getEndValue(),
                        before.getLongStartValue(),
                        after.getLongEndValue()
                );

            }

            public Range<Integer> createMergedRange(
                    final SortedMap<Integer, Range<Integer>> sortedByStartValue,
                    final SortedMap<Integer, Range<Integer>> sortedByEndValue
            ) {

                return createRange(
                        sortedByStartValue.get( sortedByStartValue.firstKey() ),
                        sortedByEndValue.get( sortedByEndValue.lastKey() )
                );

            }

        };

        Ranges<Integer> ranges = new Ranges<>( rangeFactory );

        Ranges.doit( ranges, 0, 0 );
        Ranges.doit( ranges, 10, 10 );
        Ranges.doit( ranges, 1, 9 );
        Ranges.doit( ranges, 20, 30 );
        Ranges.doit( ranges, 40, 50 );
        Ranges.doit( ranges, -5, 55 );

        Logger.logMsg( "starting again" );

        ranges = new Ranges<>( rangeFactory );

        Ranges.doit( ranges, 1, 10 );
        Ranges.doit( ranges, -10, -1 );
        Ranges.doit( ranges, -1, 1 );
        Ranges.doit( ranges, -20, -10 );
        Ranges.doit( ranges, 10, 20 );
        Ranges.doit( ranges, -30, -21 );
        Ranges.doit( ranges, 21, 30 );

        Logger.logMsg( "starting again" );
        ranges = new Ranges<>( rangeFactory );

        Ranges.doit( ranges, 1, 10 );
        Ranges.doit( ranges, 5, 15 );
        Ranges.doit( ranges, -5, 5 );

    }

    private static void doit( final Ranges<Integer> ranges, final int start, final int end ) {

        Ranges.doit( ranges, new Range<>( start, end, start, end ) );

    }

    @SuppressWarnings({ "UseOfSystemOutOrSystemErr", "CallToPrintStackTrace" })
    private static void doit( final Ranges<Integer> ranges, final Range<Integer> newRange ) {

        try {

            Logger.logMsg( "adding range " + newRange + " yielded " + ranges.add( newRange ) );

        } catch ( RejectRangeException e ) {

            e.printStackTrace();

        }

    }

}
