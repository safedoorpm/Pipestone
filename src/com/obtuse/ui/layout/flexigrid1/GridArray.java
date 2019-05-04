/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 Provide a two-dimensional array.
 */

public class GridArray<T extends GridArray.ItemInfo> {

    public static class ItemInfo<E> {

        private final String _name;
        private final int _row;
        private final int _col;
        private final Component _component;
        private final E _info;

        public ItemInfo( final @NotNull String name, final int row, final int col, final @NotNull Component component, final @Nullable E info ) {
            super();

            _name = name;
            _row = row;
            _col = col;
            _component = component;
            _info = info;

        }

        public ItemInfo( final int row, final int col, final @NotNull Component component, final @Nullable E info ) {
            this( component.getClass().getSimpleName() + "[" + row + "," + col + "]", row, col, component, info );

        }

        public ItemInfo( final int row, final int col, final @NotNull Component component ) {
            this( row, col, component, null );

        }

        public String name() {

            return _name;

        }

        public int row() {

            return _row;
        }

        public int column() {

            return _col;
        }

        public Component component() {

            return _component;
        }

        public E getInfo() {

            return _info;
        }

        public String toString() {

            return "ItemInfo( " + row() + ", " + column() + ", " +
                   ( component() == null ? "null" :
                             ( component().getName() + "=" + component().getClass().getCanonicalName() )
                   ) +
                   ( component() instanceof JLabel ? ( " " + ( (JLabel)component()).getText() ) : "" ) +
                   " )";

        }

    }

    public final SortedMap<Integer,SortedMap<Integer,T>> _rowMajorGrid = new TreeMap<>();
    public final SortedMap<Integer,SortedMap<Integer,T>> _columnMajorGrid = new TreeMap<>();

    public GridArray( final @NotNull String name ) {
        super();

    }

    private boolean checkExistence( final SortedMap<Integer, SortedMap<Integer, T>> map, int i1, int i2 ) {

        SortedMap<Integer, T> slice = map.get( i1 );
        return slice != null && slice.containsKey( i2 );

    }

    private void validateGridCell( String who, int row, int col ) {

        if ( row < -1 ) {

            throw new IllegalArgumentException( who + ":  row (" + row + ") must not be negative" );

        }

        if ( col < -1 ) {

            throw new IllegalArgumentException( who + ":  col (" + col + ") must not be negative" );

        }

        boolean rMajorPresent = checkExistence( _rowMajorGrid, row, col );
        boolean cMajorPresent = checkExistence( _columnMajorGrid, col, row );

        if ( rMajorPresent != cMajorPresent ) {

            IllegalArgumentException e = new IllegalArgumentException(
                    who + ":  [" + row + "," + col + "]  gridRow status (" +
                    rMajorPresent +
                    ") does not agree with gridColumn status status (" +
                    cMajorPresent +
                    ")"
            );

            Logger.logMsg( "throwing exception:  " + e );

            throw e;

        }

    }

    public void validate( String why ) {

        for ( int row : _rowMajorGrid.keySet() ) {

            SortedMap<Integer, T> rowElements = _rowMajorGrid.get( row );
            for ( int col : rowElements.keySet() ) {

                if ( !isSomethingAtR( row, col ) ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "):  expected [" + row + "," + col + "] not found via rows on row-major" );

                }

                if ( !isSomethingAtC( row, col ) ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "):  expected [" + row + "," + col + "] not found via rows on column-major" );

                }

                ItemInfo ii1 = _rowMajorGrid.get( row ).get( col );
                ItemInfo ii2 = _columnMajorGrid.get( col ).get( row );

                if ( ii1 != ii2 ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "): R - ii1=" + ii1 + " but ii2=" + ii2 );

                }

            }

        }

        for ( int col : _columnMajorGrid.keySet() ) {

            SortedMap<Integer, T> columnElements = _columnMajorGrid.get( col );
            for ( int row : columnElements.keySet() ) {

                if ( !isSomethingAtR( row, col ) ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "):  expected [" + row + "," + col + "] not found via cols on row-major" );

                }

                if ( !isSomethingAtC( row, col ) ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "):  expected [" + row + "," + col + "] not found via cols on column-major" );

                }

                ItemInfo ii1 = _rowMajorGrid.get( row ).get( col );
                ItemInfo ii2 = _columnMajorGrid.get( col ).get( row );

                if ( ii1 != ii2 ) {

                    throw new HowDidWeGetHereError( "GridArray.validate(" + why + "): C - ii1=" + ii1 + " but ii2=" + ii2 );

                }

            }

        }

    }

    public int size() {

        int size = 0;

        for ( SortedMap<Integer,T> rowElements : _rowMajorGrid.values() ) {

            size += rowElements.size();

        }

        return size;

    }

    public boolean isEmpty() {

        return size() == 0;

    }

    /**
     Get the dimensions of this array.
     @return a {@link Dimension} instance describing the dimensions of this array.
     {@link Dimension#height} will contain the number of rows and
     {@link Dimension#width} will contain the number of columns.
     <p>Note that an array with 5 rows will have indices {@code 0}, {@code 1}, {@code 2}, {@code 3}, and {@code 4}.
     Put another way, an array with n columns where n is greater than {@code 0} has a column {@code 0} and does NOT have a column {@code n}.
     */

    public Dimension getDimension() {

        // Are we empty?

        if ( _rowMajorGrid.isEmpty() != _columnMajorGrid.isEmpty() ) {

            throw new HowDidWeGetHereError(
                    "GridArray.getDimension:  _rowMajorGrid is " + ( _rowMajorGrid.isEmpty() ? "" : "not " ) +
                    "empty but _columnMajorGrid is " + ( _columnMajorGrid.isEmpty() ? "" : "not " ) +
                    "empty"
            );

        }

        if ( _rowMajorGrid.isEmpty() ) {

            return new Dimension( 0, 0 );

        }

        // Get our dimensions.
        // Note that we are computing the index of the last row PLUS 1 and the index of the last column PLUS 1.
        // This allows a dimension of 0x0 to indicate 'empty'.

        Dimension dim = new Dimension( _rowMajorGrid.lastKey() + 1, _columnMajorGrid.lastKey() + 1 );

        Logger.logMsg( "dimension of GridArray is " + ObtuseUtil.fDim( dim ) );

        return dim;
    }

    public ItemInfo put( T ii, boolean replaceOk ) {

        validateGridCell( "GridArray.put", ii.row(), ii.column() );

        SortedMap<Integer,T> gridRow = _rowMajorGrid.computeIfAbsent( ii.row(), k -> new TreeMap<>() );
        SortedMap<Integer,T> gridColumn = _columnMajorGrid.computeIfAbsent( ii.column(), k -> new TreeMap<>() );

        if ( !replaceOk && gridRow.containsKey( ii.column() ) ) {

            String msg = "GridArray.put:  there is already something at [" + ii.row() + "," + ii.column() + "] " + ii;

            throw new IllegalArgumentException( msg );

        }

        gridRow.put( ii.column(), ii );
        gridColumn.put( ii.row(), ii );

        return ii;

    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemInfo put( T ii ) {

        return put( ii, true );

    }

    public boolean isSomethingAt( int row, int col ) {

        return isSomethingAtR( row, col );

    }

    public boolean isSomethingAtR( int row, int col ) {

        validateGridCell( "GridArray.isSomethingAtR", row, col );

        boolean rMajorPresent = checkExistence( _rowMajorGrid, row, col );

        return rMajorPresent;

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSomethingAtC( int row, int col ) {

        validateGridCell( "GridArray.isSomethingAtC", row, col );

        boolean cMajorPresent = checkExistence( _columnMajorGrid, col, row );

        return cMajorPresent;

    }

    public Optional<ItemInfo> get( int row, int col ) {

        validateGridCell( "GridArray.get", row, col );

        SortedMap<Integer,T> gridRow = _rowMajorGrid.get( row );
        SortedMap<Integer,T> gridColumn = _columnMajorGrid.get( col );

        if ( gridRow == null || gridColumn == null ) {

            return Optional.empty();

        }

        if ( gridRow.containsKey( col ) != gridColumn.containsKey( row ) ) {

            throw new IllegalArgumentException(
                    "GridArray.get:  [" + row + "," + col + "]  gridRow status (" +
                    gridRow.containsKey( col ) +
                    " does not agree with gridColumn status status (" +
                    gridColumn.containsKey( row ) +
                    ")"
            );

        }

        return Optional.ofNullable( gridRow.get( col ) );

    }

    public SortedMap<Integer, T> getRow( int row ) {

        SortedMap<Integer, T> rval = _rowMajorGrid.get( row );

        return rval == null ? Collections.emptySortedMap() : rval;

    }

    public SortedMap<Integer, T> getColumn( int column ) {

        SortedMap<Integer, T> rval = _columnMajorGrid.get( column );

        return rval == null ? Collections.emptySortedMap() : rval;

    }

    public SortedSet<Integer> getRowSet() {

        Set<Integer> rval = _rowMajorGrid.keySet();
        if ( !( rval instanceof SortedSet ) ) {

            rval = new TreeSet<>( rval );

        }

        return Collections.unmodifiableSortedSet( (SortedSet<Integer>)rval );

    }

    public SortedSet<Integer> getColumnSet() {

        Set<Integer> rval = _columnMajorGrid.keySet();
        return new TreeSet<>( rval );

    }

    public Optional<ItemInfo> remove( int r, int c ) {

        ItemInfo victim = null;

        if ( isSomethingAt( r, c ) ) {

            Optional<ItemInfo> optV = get( r, c );

            if ( !optV.isPresent() ) {

                throw new HowDidWeGetHereError( "GridArray.remove:  isSomething( " + r + ", " + c + " ) said yes but there's nothing there" );

            }

            victim = optV.get();

            SortedMap<Integer, T> rowElements = _rowMajorGrid.get( r );
            if ( !rowElements.containsKey( c ) ) {

                dump( "GridArray.remove(" + r + "," + "c) not in row " + r );
                throw new HowDidWeGetHereError( "GridArray.remove:  expected element [" + r + "," + c + "] not found in row " + r );

            }

            SortedMap<Integer, T> columnElements = _columnMajorGrid.get( c );
            if ( !columnElements.containsKey( r ) ) {

                dump( "GridArray.remove(" + r + "," + "c) not in column " + c );
                throw new HowDidWeGetHereError( "GridArray.remove:  expected element [" + r + "," + c + "] not found in column " + c );

            }

            ItemInfo rowVictim = rowElements.remove( c );
            ItemInfo columnVictim = columnElements.remove( r );

            if ( victim != rowVictim || rowVictim != columnVictim ) {

                dump( "GridArray.remove(" + r + "," + "c) big mess v=" + victim + ", rv=" + rowVictim + ", cv=" + columnVictim );
                throw new HowDidWeGetHereError( "GridArray.remove:  remove failure - v=" + victim + ", rv=" + rowVictim + ", cv=" + columnVictim );

            }

            // Get rid of any row or column tables that are now empty.
            // We have to do this as the code that figures out the dimensions of the array depends on
            // there not being any empty row or column tables.

            if ( rowElements.isEmpty() ) {

                _rowMajorGrid.remove( r );

            }

            if ( columnElements.isEmpty() ) {

                _columnMajorGrid.remove( c );

            }

        }

        return Optional.ofNullable( victim );

    }

    public void clear() {

        _rowMajorGrid.clear();
        _columnMajorGrid.clear();

    }

    public void clearRow( int row ) {

        SortedMap<Integer, T> rowElements = _rowMajorGrid.get( row );
        if ( rowElements == null ) {

            return;

        }

        cleanHouse( "GridArray.clearRow", true, row, rowElements, _columnMajorGrid );

        _rowMajorGrid.remove( row );

    }

    public void clearColumn( int col ) {

        SortedMap<Integer, T> columnElements = _columnMajorGrid.get( col );
        if ( columnElements == null ) {

            return;

        }

        cleanHouse( "GridArray.clearRow", false, col, columnElements, _rowMajorGrid );

        _columnMajorGrid.remove( col );

    }

    private void cleanHouse(
            @SuppressWarnings("SameParameterValue") final @NotNull String who,
            final boolean outerIsRow,
            final int outer,
            final SortedMap<Integer, T> outerElements,
            final SortedMap<Integer, SortedMap<Integer, T>> grid
    ) {

        for ( int inner : outerElements.keySet() ) {

            if ( outerIsRow ) {

                validateGridCell( who, outer, inner );

            } else {

                validateGridCell( who, inner, outer );

            }

            SortedMap<Integer, T> innerElements = grid.get( inner );
            if ( outerIsRow ) {

                if ( !isSomethingAt( outer, inner ) ) {

                    throw new HowDidWeGetHereError( who + ":  grid is invalid - [" + outer + "," + inner + "] must but does not exist" );

                }

            } else {

                if ( !isSomethingAt( inner, outer ) ) {

                    throw new HowDidWeGetHereError( who + ":  grid is invalid - [" + outer + "," + inner + "] must but does not exist" );

                }

            }

            innerElements.remove( outer );

            if ( innerElements.isEmpty() ) {

                grid.remove( inner );

            }

        }

    }

    public void dump( String why ) {

        Logger.logMsg( "dumping " + this + " (" + why + "):" );

        Logger.logMsg( "    row major:" );
        for ( int row : getRowSet() ) {

            SortedMap<Integer, T> rowElements = getRow( row );
            for ( int col : rowElements.keySet() ) {

                Logger.logMsg( "        [" + row + "," + col + "] = " + rowElements.get( col ) );

            }

        }

        Logger.logMsg( "    column major:" );
        for ( int col : getColumnSet() ) {

            SortedMap<Integer, T> columnElements = getColumn( col );
            for ( int row : columnElements.keySet() ) {

                Logger.logMsg( "        [" + col + "," + row + "] = " + columnElements.get( row ) );

            }

        }

        Logger.logMsg( "done" );

    }

    public String toString() {

        int rs = _rowMajorGrid.size();
        int cs = _columnMajorGrid.size();
        int gridSize = size();
        return
                "GridArray( " + gridSize + " element" + ( gridSize == 1 ? "" : "s" ) + " across " +
                rs + " row" + ( rs == 1 ? "" : "s" ) + " and " +
                cs + " column" + ( cs == 1 ? "" : "s" ) +
                " )";

    }

    private static final int SIZE = 25;

    private static boolean s_verbose;

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "FlexiGrid", "testing" );

        GridArray<ItemInfo<?>> ga = new GridArray<>( "GridArray.main" );

        ga.put( new ItemInfo( 0, 1, new JLabel( "[0,1]") ) );
        ga.dump( "after adding [0,1]" );

        Optional<ItemInfo> cc = ga.get( 0, 1 );
        Logger.logMsg( "cc = " + cc );

        ga.put( new ItemInfo( 0, 2, new JLabel( "[0,2]") ) );
        ga.put( new ItemInfo( 1, 1, new JLabel( "[1,1]") ) );
        ga.put( new ItemInfo( 1, 2, new JLabel( "[1,2]") ) );

        ga.dump( "after adding [0,2], [1,1], and [1,2]" );
        ga.clearRow( 0 );
        ga.dump( "after clearing row 0" );
        ga.clearColumn( 2 );
        ga.dump( "after clearing column 2" );
        ga.clear();
        ga.dump( "after full clear" );

        Random rg = new Random( 123432154 );

        SortedMap<String,JLabel> cache = new TreeMap<>();

        Measure.setGloballyEnabled( true );
        for ( int trial = 0; trial < 1000000; trial += 1 ) {

            try ( Measure ignored = new Measure( "stress trial" ) ) {

                int r = rg.nextInt( SIZE );
                int c = rg.nextInt( SIZE );
                String label = "[" + r + "," + c + "]";
                JLabel jLabel = cache.computeIfAbsent( label, s -> new JLabel( label ) );

                if ( trial % 2 == 0 ) {

                    if ( s_verbose ) Logger.logMsg( "put( " + r + ", " + c + " )" );
                    ga.put( new ItemInfo( r, c, jLabel ) );

                } else {

                    if ( s_verbose ) Logger.logMsg( "remove( " +
                                                    r +
                                                    ", " +
                                                    c +
                                                    " ) == " +
                                                    ga.get( r, c )
                                                      .orElse( null ) );
                    Optional<ItemInfo> optV = ga.remove( r, c );
                    String expected = "[" + r + "," + c + "]";
                    if ( optV.isPresent() ) {

                        String found = (
                                (JLabel)optV.get()
                                            .component()
                        ).getText();
                        if ( !found.equals( expected ) ) {

                            throw new HowDidWeGetHereError( "big grid test:  expected " +
                                                            expected +
                                                            " but got " +
                                                            found );

                        }

                    }

                }

                if ( trial % 10000 == 0 ) {

                    Logger.logMsg( "done trial " + trial );

                }

            }

        }

        Measure.showStats();

    }

}