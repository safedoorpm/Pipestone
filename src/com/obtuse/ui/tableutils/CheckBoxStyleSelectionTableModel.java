/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.MyActionListener;
import com.obtuse.util.Logger;
import com.obtuse.util.things.ThingInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 Manage table row selection using a checkbox on each row.
 */

public abstract class CheckBoxStyleSelectionTableModel<D extends ThingInfo> extends AbstractTableModel {

    private final boolean _singleSelectionMode;

    public CheckBoxStyleSelectionTableModel( final boolean singleSelectionMode ) {

        super();

        _singleSelectionMode = singleSelectionMode;

    }

    /**
     Notify a derived table model that one of the selection check boxes has probably changed.
     <p/>It is possible for this method to be called when nothing has changed.
     It is also possible that a single change to a single selection check box might result in multiple calls to this method.
     We report when the selection might have changed since that is less expensive and less complex
     than ensuring that we always reporting when a change has occurred while never reporting when a change has not occurred.
     In other words, we consider it better to over-report than to under-report.
     <p/>Those derived table models which truly care will need to implement the fairly simple code required to ignore calls to this method when nothing has actually changed.

     @param selectedRows which rows are currently selected.
     */

    public void selectionChanged( final @NotNull Collection<Integer> selectedRows ) {

        // can be overridden by implementations that care.

    }

    public void setColWidth( final TableColumnModel tcm, final FrameworkTableModel.TCName tcName, final int minWidth, final int maxWidth ) {

        if ( tcName == null ) {

            Logger.logMsg( "CBSSTM.setColWidth:  no column name - call ignored" );

            return;

        }

        if ( hasNamedColumn( tcName ) ) {

            TableUtils.setColWidth( tcm, getColumnNumber( tcName ), minWidth, maxWidth );

        } else {

            Logger.logMsg( "CBSSTM.setColWidth:  no column named \"" + tcName + "\" - call ignored" );

        }

    }

    public abstract int getColumnNumber( FrameworkTableModel.TCName tcName );

    public abstract boolean hasNamedColumn( FrameworkTableModel.TCName tcName );

    /**
     Get all the wrapped rows in row order.
     */

    protected abstract Collection<CheckBoxRowWrapper<D>> getWrappedRowsInRowOrder();

    /**
     Mark a row as selected.
     */

    protected abstract void setSelectedAtRow( int row, boolean selected );

    /**
     Determine if a particular row is selected.
     */

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected abstract boolean getSelectedAtRow( int row );

    /**
     Avoid infinite recursion (things can get a bit out of hand sometimes).
     */

    private boolean _recursing = false;

    /**
     Indicate that the selection has changed.

     @param changedRow the row whose selection state has changed (-1 indicates that all rows are to be deselected).
     */

    public final void fireOurSelectionChanged( final int changedRow ) {

        if ( _recursing ) {

            return;

        }

        if ( changedRow == -1 ) {

            try {

                _recursing = true;

                for ( int row = 0; row < getRowCount(); row += 1 ) {

                    //                        Logger.logMsg( "setting row " + row + " to " + ( row == changedRow ) );

                    setSelectedAtRow( row, false );

                }

            } finally {

                _recursing = false;

            }

        } else if ( _singleSelectionMode ) {

            if ( getOurSelectedRows().size() > 1 ) {

                if ( !getSelectedAtRow( changedRow ) ) {

                    throw new HowDidWeGetHereError( "more than one row selected but the changed row is not one of them" );

                }

                try {

                    _recursing = true;

                    for ( int row = 0; row < getRowCount(); row += 1 ) {

                        setSelectedAtRow( row, row == changedRow );

                    }

                } finally {

                    _recursing = false;

                }

            }

        }

        SortedSet<Integer> rval = getOurSelectedRows();

        selectionChanged( rval );

        fireTableDataChanged();

    }

    /**
     Determine the currently selected row's row number.

     @return the currently selected row's row number (-1 if zero rows or more than one row are/is selected).
     */

    public int getOurSelectedRow() {

        SortedSet<Integer> selectedRows = getOurSelectedRows();
        if ( selectedRows.size() == 1 ) {

            return selectedRows.first().intValue();

        } else {

            return -1;

        }

    }

    /**
     Figure out which of the rows are selected.

     @return a possibly empty list of the selected rows.
     */

    @NotNull
    public SortedSet<Integer> getOurSelectedRows() {

        TreeSet<Integer> rval = new TreeSet<>();
        int row = 0;
        for ( CheckBoxRowWrapper wrapper : getWrappedRowsInRowOrder() ) {

            if ( wrapper.isSelected() ) {

                rval.add( row );

            }

            row += 1;

        }

        return rval;

    }

    /**
     Handle the setting, flipping and unsetting of the boolean selection in the selection column of our tables.

     @param tableModel        the table model to manipulate.
     @param selectAllButton   the button that requests that all the rows be selected.
     @param flipAllButton     the button that requests that all the row selections be flipped.
     @param unselectAllButton the button that requests that all the rows be unselected.
     */

    public static void handleSelectors(
            final CheckBoxStyleSelectionTableModel tableModel,
            final JButton selectAllButton,
            final JButton flipAllButton,
            final JButton unselectAllButton
    ) {

        flipAllButton.addActionListener(
                new MyActionListener() {
                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        tableModel.flipAllRowSelections();

                    }

                }
        );

        unselectAllButton.addActionListener(
                new MyActionListener() {
                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        tableModel.clearAllRowSelections();

                    }

                }
        );

        selectAllButton.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        tableModel.selectAllRows();

                    }

                }
        );

    }

    public void clearAllRowSelections() {

        for ( int row = 0; row < getRowCount(); row += 1 ) {

            setSelectedAtRow( row, false );

        }

    }

    public void selectAllRows() {

        for ( int row = 0; row < getRowCount(); row += 1 ) {

            setSelectedAtRow( row, true );

        }

    }

    public void flipAllRowSelections() {

        for ( int row = 0; row < getRowCount(); row += 1 ) {

            setSelectedAtRow( row, !getSelectedAtRow( row ) );

        }

    }

    public String toString() {

        return "CheckBoxStyleSelectionTableModel( singleSelectionMode=" + _singleSelectionMode + " )";

    }

}
