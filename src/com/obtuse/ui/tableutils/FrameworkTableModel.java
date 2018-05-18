/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import com.obtuse.util.Trace;
import com.obtuse.util.things.ThingNameFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellEditor;
import java.util.*;

/**
 Manage a table of datum types.
 */

public class FrameworkTableModel<D extends CheckBoxRowWrapper.RowData> extends CheckBoxStyleSelectionTableModel<D> {

    public static class TCName implements Comparable<TCName> {

        private static final SimpleUniqueIntegerIdGenerator s_cNameIdGenerator =
                new SimpleUniqueIntegerIdGenerator( TCName.class.getCanonicalName() + " - id generator" );

        private final int _id;

        private final String _name;

        public TCName( final @NotNull String name ) {

            super();

            _name = name;
            _id = TCName.s_cNameIdGenerator.getUniqueId();

        }

        public int getId() {

            return _id;

        }

        @NotNull
        public String getName() {

            return _name;

        }

        public String toString() {

            return _name;

        }

        public int compareTo( final @NotNull TCName rhs ) {

            return _name.compareTo( rhs.getName() );

        }

        public int hashCode() {

            return _name.hashCode();

        }

        public boolean equals( final Object rhs ) {

            return rhs instanceof TCName && getName().equals( ( (TCName)rhs ).getName() );

        }

    }

    public abstract static class CManager<D extends CheckBoxRowWrapper.RowData> {

        private final Class _defaultCellClass;
        private final TCName _columnName;
        private final ThingNameFactory _thingNameFactory;

        private TableCellEditor _customTableCellEditor;

        public CManager( final @NotNull TCName columnName, final @NotNull ThingNameFactory thingNameFactory ) {
            super();

            _columnName = columnName;
            _defaultCellClass = thingNameFactory.getDefaultClass();
            _thingNameFactory = thingNameFactory;

        }

        public CManager( final @NotNull TCName columnName, final @NotNull Class defaultCellClass ) {
            super();

            _defaultCellClass = defaultCellClass;
            _columnName = columnName;
            _thingNameFactory = null;
        }

        public boolean isCellEditable( @SuppressWarnings("UnusedParameters") final int row, @SuppressWarnings("UnusedParameters") final int col ) {

            return true;

        }

        @NotNull
        public ThingNameFactory getThingNameFactory() {

            if ( _thingNameFactory == null ) {

                throw new IllegalArgumentException( "FrameworkTableModel.getThingNameFactory:  we don't have a thing name factory to give" );

            }

            return _thingNameFactory;

        }

        public boolean isSelectionColumn() {

            return false;

        }

        @SuppressWarnings("unused")
        public final Class getCellClass( final int row, final int col ) {

            return _defaultCellClass;

        }

        public abstract Object getValue( int row, int col, @NotNull CheckBoxRowWrapper<D> rowData );

        public abstract void setValue( int row, int col, @NotNull CheckBoxRowWrapper<D> rowData, Object newValue );

        public final TCName getColumnName() {

            return _columnName;

        }

        @SuppressWarnings("unused")
        public boolean isActive() {

            return true;

        }

        public String toString() {

            return getClass().getName() + "(" + getColumnName() + ")";

        }

        public TableCellEditor getCustomTableCellEditor( final int xRow, final int xCol ) {

            return _customTableCellEditor;

        }

        @SuppressWarnings("unused")
        public void setCustomTableCellEditor( final TableCellEditor customTableCellEditor ) {

            _customTableCellEditor = customTableCellEditor;

        }

    }

    private final boolean _readOnly;

    private boolean _traceMode = false;

    private int _selectionColumnNumber = -1;

    private boolean _definitionFrozen;

    private final Vector<CheckBoxRowWrapper<D>> _data = new Vector<>();

    private final SortedMap<Integer, CManager<D>> _columnsByNumber = new TreeMap<>();
    private final SortedMap<TCName, Integer> _fieldNameIndex = new TreeMap<>();
    private final SortedMap<TCName, Boolean> _readOnlyByColumnNameOverride = new TreeMap<>();

    @SuppressWarnings("unused")
    public FrameworkTableModel(
            final @NotNull D[] rowWrappers,
            final boolean singleSelectionMode,
            final boolean readOnly
    ) {

        this( /*table,*/ Arrays.asList( rowWrappers ), singleSelectionMode, readOnly );
    }

    public FrameworkTableModel(
            final @NotNull Collection<D> data,
            final boolean singleSelectionMode,
            final boolean readOnly
    ) {

        super( singleSelectionMode );

        _readOnly = readOnly;

        Collection<CheckBoxRowWrapper<D>> wrappers = new LinkedList<>();
        for ( D rowData : data ) {

            wrappers.add( new CheckBoxRowWrapper<>( this, rowData ) );

        }

        addRowWrappers( wrappers );

    }

    public boolean isTraceMode() {

        return _traceMode;

    }

    private void maybeLog( final @NotNull String msg ) {

        if ( isTraceMode() ) {

            Logger.logMsg( msg );

        } else {

            Trace.event( msg );

        }

    }

    @SuppressWarnings("unused")
    public void setTraceMode( final boolean traceMode ) {

        _traceMode = traceMode;

    }

    @Override
    public int getColumnNumber( final TCName tcName ) {

        Integer columnNumber = _fieldNameIndex.get( tcName );
        if ( columnNumber == null ) {

            throw new IllegalArgumentException( "getColumnNumber( \"" + tcName + "\" ): no such column - " + this );

        }

        return columnNumber.intValue();

    }

    public CManager<D> getColumnManager( final int col ) {

        return _columnsByNumber.get( col );

    }

    @Override
    public boolean hasNamedColumn( final TCName tcName ) {

        return _fieldNameIndex.containsKey( tcName );

    }

    /**
     Add a column to the table without specifying a readonly override.
     <p/>See {@link #addColumn(CManager, Boolean)} for more info about the readonly override concept.

     @param cManager the column manager for the new column.
     */

    protected void addColumn( final CManager<D> cManager ) {

        addColumn( cManager, null );

    }

    /**
     Add a column to the table with an optionally specified readonly override.

     @param cManager         the column manager for the new column.
     @param readOnlyOverride {@code null} if no override,
     {@code true} if this column is to be treated as readonly regardless of what the column manager might believe,
     or {@code false} if this column is to be treated as not readonly regardless of what the column manager might believe.
     */
    protected void addColumn( final CManager<D> cManager, final Boolean readOnlyOverride ) {

        if ( isFrozen() ) {

            throw new IllegalArgumentException( "FrameworkTableModel.addColumn:  cannot add more columns once model definition is frozen" );

        }

        final int nextColumnNumber = _columnsByNumber.size();
        TCName columnName = cManager.getColumnName();
        if ( _fieldNameIndex.containsKey( columnName ) ) {

            throw new IllegalArgumentException( "field name \"" +
                                                columnName + "\" appears more than once in table" );

        }

        if ( cManager.isSelectionColumn() ) {

            if ( _selectionColumnNumber >= 0 ) {

                throw new IllegalArgumentException( "columns " +
                                                    _selectionColumnNumber +
                                                    " and " +
                                                    nextColumnNumber +
                                                    " both claim to be the selection column" );

            }

            _selectionColumnNumber = nextColumnNumber;

        }

        _fieldNameIndex.put( columnName, nextColumnNumber );

        _columnsByNumber.put( nextColumnNumber, cManager );

        _readOnlyByColumnNameOverride.put( columnName, readOnlyOverride );

    }

    public void freeze() {

        if ( isFrozen() ) {

            throw new IllegalArgumentException( "FrameworkTableModel.freeze: table is already frozen" );

        }

        _definitionFrozen = true;

    }

    public boolean isFrozen() {

        return _definitionFrozen;

    }

    @SuppressWarnings("unused")
    public void rowDataChanged( @Nullable final CheckBoxRowWrapper<D> changedRowWrapper ) {

        if ( changedRowWrapper == null ) {

            fireTableDataChanged();

            return;

        }

        int row = 0;
        for ( CheckBoxRowWrapper wrapper : _data ) {

            if ( wrapper.equals( changedRowWrapper ) ) {

                fireTableRowsUpdated( row, row );

                return;

            }

            row += 1;

        }

    }

    /**
     Determine how many columns are in each of the table's rows.
     @return the number of columns in each of the table's rows.
     */

    @Override
    public int getColumnCount() {

        return _fieldNameIndex.size();

    }

    /**
     Determine how many rows are in the table.
     @return the number of rows of data in the table.
     */

    @Override
    public int getRowCount() {

        return _data.size();

    }

    /**
     Determine if the table empty.
     @return {@code true} if the table is empty (has no rows); {@code false} otherwise.
     */

    public boolean isEmpty() {

        return getRowCount() == 0;

    }

    /**
     Get a particular row wrapper.

     @param row the number of the row who's wrapper is to be gotten (clumsy English deliberate).
     @return the requested row wrapper.
     */

    @NotNull // 2017-11-18 danny - I'm about 99.7% sure that it is impossible to ever return a non-null value from this method.
    public CheckBoxRowWrapper<D> getRowWrapper( final int row ) {

        return _data.get( row );

    }

    /**
     Empty the table.
     */

    public void clear() {

        _data.clear();
        fireTableDataChanged();

    }

    @Override

    public Object getValueAt( final int row, final int col ) {

        if ( !isFrozen() ) {

            throw new IllegalArgumentException( "FTM.getValueAt():  attempt to get a value at (" +
                                                row +
                                                "," +
                                                col +
                                                ") when table structure is not frozen" );

        }

        CheckBoxRowWrapper<D> rowData = _data.get( row );

        if ( col == _selectionColumnNumber ) {

            return rowData.isSelected();

        } else {

            CManager<D> cManager = _columnsByNumber.get( col );
            return cManager.getValue( row, col, rowData );

        }

    }

    public List<CheckBoxRowWrapper<D>> getWrappedRowsInRowOrder() {

        return Collections.unmodifiableList( _data );

    }

    public String getColumnName( final int col ) {

        TCName tcName = getTCName( col );
        return tcName.getName();

    }

    public TCName getTCName( final int col ) {

        CManager<D> cManager = _columnsByNumber.get( col );
        return cManager.getColumnName();

    }

    public Class getColumnClass( final int col ) {

        CManager<D> cManager = _columnsByNumber.get( col );
        return cManager.getCellClass( -1, col );

    }

    public boolean hasSelectionColumn() {

        return _selectionColumnNumber >= 0;

    }

//    /**
//     If this table has a selection column, set it for a specified row.
//     <p>This method silently ignores requests to set the selection column for tables that don't have selection columns.</p>
//     @param row the row of interest.
//     @param isSelected the new selection value for the row of interest.
//     */
//
//    public void setSelectionColumn( final int row, final boolean isSelected ) {
//
//        if ( _selectionColumnNumber >= 0 ) {
//
//            setValueAt( isSelected, row, _selectionColumnNumber );
//
//        }
//
//    }

    public void setValueAt( final Object value, final int row, final int col ) {

        maybeLog( "setting row " + row + " col " + col + " to " + value );

        if ( !isFrozen() ) {

            throw new IllegalArgumentException(
                    "FTM.setValueAt():  attempt to set a value at " +
                    "(" + row + "," + col + ")" +
                    " when table structure is not frozen"
            );

        }

        CheckBoxRowWrapper<D> rowData = _data.get( row );

        try {

            maybeLog(
                    "setting cell at (" +
                    row +
                    "," +
                    col +
                    ") to " +
                    (
                            value == null ?
                                    null :
                                    (
                                            value.getClass()
                                                 .getSimpleName() + " instance = (" + value + ")"
                                    )
                    )
            );

            if ( col == _selectionColumnNumber ) {

                rowData.setSelected( value != null && ( (Boolean)value ).booleanValue() );

            } else {

                CManager<D> cManager = _columnsByNumber.get( col );
                cManager.setValue( row, col, rowData, value );

            }

            maybeLog( "FrameworkTableModel.setValueAt:  firing table cell updated for (" + row + "," + col + ")" );
            fireTableCellUpdated( row, col );
            maybeLog( "FrameworkTableModel.setValueAt:  firing our selection changed for " + row );
            fireOurSelectionChanged( row );

        } catch ( Throwable e ) {

            e.printStackTrace();

            throw new HowDidWeGetHereError( "error setValueAt( " + value + ", " + row + ", " + col + ", rowData=" + rowData + " )", e );

        }

    }

    public Collection<CheckBoxRowWrapper<D>> getWrappers() {

        return Collections.unmodifiableCollection( _data );

    }

    public void setSelectedAtRow( final int row, final boolean selected ) {

        maybeLog( "FrameworkTableModel.setSelectedAtRow:  setting selection for row " + row + " to " + selected );

        CheckBoxRowWrapper<D> rowData = _data.get( row );
        rowData.setSelected( selected );
        if ( _selectionColumnNumber >= 0 ) {

            setValueAt( selected, row, _selectionColumnNumber );

        }

    }

    public boolean getSelectedAtRow( final int row ) {

        CheckBoxRowWrapper<D> rowData = _data.get( row );
        return rowData.isSelected();

    }

    /**
     Determine if a particular cell is editable.
     This is determined by considering the following in the order specified:
     <ul>
     <li>the cell is in the selection column which makes it readonly by definition.</li>
     <li>if the table itself is readonly then all cells are readonly.</li>
     <li>if the cell's column was added using {@link #addColumn(CManager, Boolean)} with a non-null second parameter (readOnlyOverride)
     then the cell is editable if and only if the second parameter was {@code false}.</li>
     <li>the column manager's {@link CManager#isCellEditable(int, int)} method is called to determine if the cell is editable.</li>
     </ul>

     @param row which row is of interest.
     @param col which column is of interest.
     @return true if editable; false otherwise.
     */

    @Override
    public boolean isCellEditable( final int row, final int col ) {

        Boolean readOnlyOverride = _readOnlyByColumnNameOverride.get( getTCName( col ) );

        String how;
        boolean rval;
        if ( col == _selectionColumnNumber ) {

            rval = true;
            how = "selection column is ALWAYS editable";

        } else if ( _readOnly ) {

            rval = false;
            how = "table is readonly";

        } else if ( readOnlyOverride != null ) {

            rval = !readOnlyOverride.booleanValue();
            how = getTCName( col ).getName() + "column's readonly-override set to " + readOnlyOverride;

        } else {

            CManager<D> cManager = _columnsByNumber.get( col );
            rval = cManager.isCellEditable( row, col );
            how = "column manager said so";

        }

        maybeLog( "cell(" + row + "," + col + ") is " + ( rval ? "" : "not " ) + "editable because " + how );

        return rval;

    }

    public void addRowWrapper( final CheckBoxRowWrapper<D> rowData ) {

        _data.add( rowData );

        fireTableDataChanged();

    }

    public void addRowWrappers( final Collection<CheckBoxRowWrapper<D>> rowsData ) {

        if ( rowsData.isEmpty() ) {

            return;

        }

        _data.addAll( rowsData );

        fireTableDataChanged();

    }

    @SuppressWarnings("unused")
    public void addRowDatums( final Collection<D> rows ) {

        Collection<CheckBoxRowWrapper<D>> wrappers = new LinkedList<>();
        for ( D row : rows ) {

            wrappers.add( new CheckBoxRowWrapper<>( this, row ) );

        }

        addRowWrappers( wrappers );

    }

    @SuppressWarnings("UnusedReturnValue")
    public CheckBoxRowWrapper<D> addRowDatum( final D rowData ) {

        CheckBoxRowWrapper<D> newRowWrapper = new CheckBoxRowWrapper<>( this, rowData );
        addRowWrapper( newRowWrapper );

        return newRowWrapper;

    }

    /**
     Remove a particular row.
     @param row the row to be removed.
     */

    public void removeRow( final int row ) {

        List<Integer> rows = new ArrayList<>();
        rows.add( row );

        removeRows( rows );

    }

    /**
     Remove a set of rows.
     Removes and (if necessary) de-selects a list of rows.
     @param rows a list of the rows to be deleted.
     The rows can be in any order and the same row can appear more than once
     (needless to say, each specified row is only actually deleted once).
     */

    public void removeRows( final Collection<Integer> rows ) {

        // This algorithm only works correctly if the rows are sorted in increasing order without duplicates.
        // See the bit at the end of this method where we actually delete the rows to see why (pay particular
        // attention to the {@code adjustment} variable).
        //
        // Consequently, while we are flexible about what is provided to us, we must work with a sorted list of row indices.

        SortedSet<Integer> sortedRows = new TreeSet<>( rows );

        maybeLog( "removing " + sortedRows );

        // Clear the selection on any of the rows which are being removed.

        for ( int requestedRow : sortedRows ) {

            CheckBoxRowWrapper<D> victimRow = _data.get( requestedRow );
            if ( victimRow.isSelected() ) {

                maybeLog( "deselecting selected row " + requestedRow + " (requested row " + requestedRow + ")" );

                setValueAt( false, requestedRow, _selectionColumnNumber );

            }

        }

        // Remove the requested rows while doing a bit of arithmetic to ensure that
        // the rows we remove are the ones that the user actually requested to be removed.

        int adjustment = 0;
        for ( int requestedRow : sortedRows ) {

            int actualRequestedRow = requestedRow - adjustment;

            maybeLog(
                    "removing actual row " + actualRequestedRow + " of " + _data.size() +
                    " (requested row " + requestedRow + ", adjustment is currently " + adjustment + ")"
            );

            actuallyRemoveRow( actualRequestedRow );
            adjustment += 1;

        }

        maybeLog( "done removing " + sortedRows );

    }

    private void actuallyRemoveRow( final int row ) {

        _data.removeElementAt( row );

        fireTableDataChanged();

    }

    @NotNull
    public Optional<Integer> findTypeDefinitionRow( final D actualData ) {

        int row = 0;
        for ( CheckBoxRowWrapper<D> wrapper : _data ) {

            if ( wrapper.getRowData().equals( actualData ) ) {

                return Optional.of( row );

            }

            row += 1;

        }

        return Optional.empty();

    }

    public String toString() {

        return "FrameworkTableModel( size = " + _data.size() + " )";

    }

}