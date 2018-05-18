/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.ui.layout.flexigrid1.FlexiGridContainer1;
import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import javax.swing.*;
import java.awt.*;

/**
 Take the FlexiGridModel thing out for a test drive.
 */

@SuppressWarnings("ClassHasNoToStringMethod")
public class FlexiGridModelTesting extends JFrame {

    private FlexiGridPanelModel<FlexiGridModelSlice> _model;
    @SuppressWarnings("FieldCanBeLocal") private FlexiGridContainer1 _container;
    private int _nextIx;
    private FlexiGridModelSlice _target = null;

    public FlexiGridModelTesting() {
        super( "FlexiGridModelTesting" );

        _model = new FlexiGridPanelModel<>( "fg1", FlexiGridPanelModel.Orientation.ROW, false, true );
        _container = _model.getFlexiGridLayoutManager()
                           .getTarget();

        JPanel xx = new JPanel();
        xx.setLayout( new BorderLayout() );
        JButton addOneButton = new JButton( "Add one" );
        addOneButton.addActionListener(
                e -> addRow( _nextIx++ )
        );
        xx.add( addOneButton, BorderLayout.NORTH );
        JButton flipVisibilityButton = new JButton( "Flip one" );
        flipVisibilityButton.addActionListener(
                e -> {

                    if ( _target.isVisible() ) {

                        Logger.logMsg( "making invisible" );

                        _target.setVisible( false );

                    } else {

                        Logger.logMsg( "making visible" );

                        _target.setVisible( true );

                    }

                }
        );
        xx.add( flipVisibilityButton, BorderLayout.SOUTH );
        xx.add( _container, BorderLayout.CENTER );
        setContentPane( xx );

        setMinimumSize( new Dimension( 30,30 ) );

        for ( int ix = 0; ix < 4; ix += 1 ) {

            addRow( ix );

            _nextIx = ix + 1;

        }

    }

    public void addRow( final int ix ) {

        FlexiGridModelSlice slice = new FlexiGridModelSlice( "s" + ix, FlexiGridPanelModel.Orientation.ROW );
        FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                new FlexiGridBasicConstraint( "s" + ix + " @ " + 1, -1, 1 )
        );
        slice.setComponent(
                0,
                new FlexiGridItemInfo(
                        "s" + ix + "c1",
                        -1,
                        0,
                        new JLabel( "s" + ix + "c1" ),
                        constraintsTable
                )
        );

        if ( _target == null && ix == 2 ) {

            _target = slice;

        }

        _model.add( slice );

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "Pipestone", "FlexiGridPanelModel - testing", null );

        FlexiGridModelTesting fg1 = new FlexiGridModelTesting();
        fg1.pack();
        fg1.setVisible( true );

    }

}
