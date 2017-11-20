/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 %%% Something clever goes here.
 */

public class TableUtils {

    public static void setColWidth( final TableColumnModel tcm, final int columnIx, final int minWidth, final int maxWidth ) {

        if ( columnIx < 0 ) {

            return;

        }

        TableColumn tc = tcm.getColumn( columnIx );

        tc.setMinWidth( minWidth );
        tc.setMaxWidth( maxWidth < minWidth ? Integer.MAX_VALUE : maxWidth );
        tc.setPreferredWidth( minWidth );
        tc.setWidth( minWidth );
        tc.setResizable( minWidth != maxWidth );

    }

}
