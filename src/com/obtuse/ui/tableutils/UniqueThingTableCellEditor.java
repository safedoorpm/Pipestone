/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.tableutils;

import com.obtuse.ui.MessageLabelInterface;
import com.obtuse.ui.ObtuseMessageLabel;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.things.ThingInfo;
import com.obtuse.util.things.ThingName;
import com.obtuse.util.things.ThingNameFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 An editor designed to ensure that someone doesn't create a duplicate name within a column in a table.
 Put another way, this editor ensures that a table column which is supposed to contain unique string values actuall does contain unique string values.
 */

public class UniqueThingTableCellEditor<T extends ThingInfo> extends CustomizableCellEditor {

    private final FrameworkTableModel<T> _tm;

    private final int _row;

    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) private final int _col;

    private final MessageLabelInterface _jMessage;

    private final ThingNameFactory _nameFactory;

    public UniqueThingTableCellEditor(
            final @NotNull FrameworkTableModel<T> tm,
            final int row,
            final int col,
            final @NotNull ThingNameFactory nameFactory,
            @Nullable final MessageLabelInterface jMessage
    ) {

        super();

        _tm = tm;

        _row = row;
        _col = col;

        _jMessage = jMessage;

        _nameFactory = nameFactory;

    }

    @Override
    public void validate( final String newNameString ) {

        try {

            innerValidate( newNameString );

            Logger.logMsg( "validation of " + ObtuseUtil.enquoteToJavaString( newNameString ) + " worked" );

            if ( _jMessage != null ) {

                _jMessage.clear();

            }

        } catch ( Exception e ) {

            if ( _jMessage != null ) {

                _jMessage.setMessage( e );

            }

            throw e;

        }

    }

    public void innerValidate( final String newNameString ) {

        Logger.logMsg( "Verifying " + ObtuseUtil.enquoteToJavaString( newNameString ) );

        Optional<ObtuseMessageLabel.AugmentedMessage> optMessage = _nameFactory.validateNameSyntax( newNameString );
        if ( optMessage.isPresent() ) {

            throw new ObtuseMessageLabel.AugmentedIllegalArgumentException( optMessage.get() );

        }

        optMessage = Optional.empty();
        ThingName newName = _nameFactory.makeThingName( newNameString );

        for ( int i = 0; i < _tm.getRowCount(); i++ ) {

            if ( i != _row ) {

                ThingName existingName = (ThingName)_tm.getValueAt( i, 1 );
                Logger.logMsg( "verifying row " + i + " containing " + existingName.enquote() );
                if ( existingName.actuallyExists() ) {

                    Logger.logMsg( "project " + existingName.enquote() + " actually exists" );

                }

                if ( existingName.equals( newName ) ) {

                    optMessage = Optional.of( new ObtuseMessageLabel.AugmentedMessage( "there is already a project named " + existingName.enquote() ) );

                    break;

                }

            } else {

                Logger.logMsg( "ignoring row " + i );

            }

        }

        Logger.logMsg( "verification " + ( optMessage.isPresent() ? "failed" : "succeeded" ) );

        if ( optMessage.isPresent() ) {

            throw new ObtuseMessageLabel.AugmentedIllegalArgumentException( optMessage.get() );

        }

    }

    public String toString() {

        return "UniqueThingTableCellEditor()";

    }

}
