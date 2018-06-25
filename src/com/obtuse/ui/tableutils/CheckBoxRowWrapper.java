/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

/**
 Manage a row of {@link JTable} data.
 This class includes support for marking rows as selected (or not) and as readonly (or not).
 */

public class CheckBoxRowWrapper<D extends CheckBoxRowWrapper.RowData> {

    private static final SimpleUniqueIntegerIdGenerator s_snGenerator =
            new SimpleUniqueIntegerIdGenerator( "CheckBoxRowWrapper - sn generator" );

    private final int _serialNumber = s_snGenerator.getUniqueId();
    private boolean _traceMode = false;

    public interface RowData {

        String getName();

    }

    private final FrameworkTableModel<D> _tableModel;

    private final D _rowData;

    private boolean _readOnly;

    private boolean _selected;

    public CheckBoxRowWrapper( final FrameworkTableModel<D> tableModel, final @NotNull D rowData ) {

        super();

        if ( isTraceMode() ) {

            Logger.logMsg( "======================================================" );
            Logger.logMsg( "====================================================== row with serial number " + _serialNumber + " created" );
            Logger.logMsg( "======================================================" );

        }

        _tableModel = tableModel;

        _rowData = rowData;

    }

    @SuppressWarnings("unused")
    public boolean isTraceMode() {

        return _traceMode;

    }

    @SuppressWarnings("unused")
    public void setTraceMode( final boolean traceMode ) {

        _traceMode = traceMode;

    }

    @SuppressWarnings("unused")
    public boolean isReadOnly() {

        return _readOnly;

    }

    @SuppressWarnings("unused")
    public void setReadOnly( final boolean readOnly ) {

        _readOnly = readOnly;

    }

    @SuppressWarnings("unused")
    public boolean isSelected() {

        return _selected;

    }

    @SuppressWarnings("unused")
    public void setSelected( final boolean selected ) {

        if ( _tableModel.hasSelectionColumn() ) {

            Optional<Integer> optRowIx = _tableModel.findTypeDefinitionRow( getRowData() );
            if ( optRowIx.isPresent() ) {

                int rowIx = optRowIx.get().intValue();

                if ( selected != _selected ) {

                    _selected = selected;

                    if ( isTraceMode() ) {

                        Logger.logMsg( "......................................................" );
                        Logger.logMsg(
                                "...................................................... row " +
                                rowIx + " is " + ( _selected ? "" : "NOT " ) + "selected (rowSn=" + _serialNumber + ")"
                        );
                        Logger.logMsg( "......................................................" );

                    }

                    _tableModel.setSelectedAtRow( rowIx, selected );
                    _tableModel.fireOurSelectionChanged( rowIx );

                } else {

                    if ( isTraceMode() ) {

                        Logger.logMsg(
                                "...................................................... row " +
                                rowIx + " is already " + ( _selected ? "" : "NOT " ) + "selected (rowSn=" + _serialNumber + ")"
                        );

                    }

                }

            } else {

                throw new HowDidWeGetHereError( "CheckBoxRowWrapper.setSelected:  row does not exist - " + this );

            }

        } else {

            throw new IllegalArgumentException( "CheckBoxRowWrapper.setSelected:  table has no selected column" );

        }

    }

    @NotNull
    public D getRowData() {

        return _rowData;

    }

    public String toString() {

        return "CheckBoxRowWrapper( " + _rowData + " )";
    }

}
