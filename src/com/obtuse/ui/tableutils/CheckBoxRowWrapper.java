/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import javax.swing.*;

/**
 Manage a row of {@link JTable} data.
 This class includes support for marking rows as selected (or not) and as readonly (or not).
 */

public class CheckBoxRowWrapper<D extends CheckBoxRowWrapper.RowData> {

    public interface RowData {

        String getName();

    }

    private final FrameworkTableModel<D> _tableModel;

    private final D _rowData;

    private boolean _readOnly;

    private boolean _selected;

    public CheckBoxRowWrapper( final FrameworkTableModel<D> tableModel, final D rowData ) {

        super();

        _tableModel = tableModel;

        _rowData = rowData;

    }

    public boolean isReadOnly() {

        return _readOnly;

    }

    public void setReadOnly( final boolean readOnly ) {

        _readOnly = readOnly;

    }

    public boolean isSelected() {

        return _selected;

    }

    public void setSelected( final boolean selected ) {

        _selected = selected;

    }

    public D getRowData() {

        return _rowData;

    }

    public String toString() {

        return "CheckBoxRowWrapper( " + _rowData + " )";
    }

}
