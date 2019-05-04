/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridDivider;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridModelSlice;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Take the FlexiGridLayoutManager out for a test drive.
 */

public class FlexiGridTesting {

    public static final int NROWS = 5;

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "BiSheng", "FlexiGridLayoutManager" );

        launchSimpleTestFrame( false, true );

        launchModelTestFrame( false, true );

    }

    @SuppressWarnings("SameParameterValue")
    private static void launchSimpleTestFrame( final boolean msgTraceMode, final boolean useLayoutTracer ) {

        FlexiGridContainer fgc = new FlexiGridContainer1( "test1", msgTraceMode, useLayoutTracer );
        fgc.setBorder( BorderFactory.createLineBorder( Color.BLUE ) );

        for ( int row = 0; row < NROWS; row += 1 ) {

            if ( row == 0 ) {

                JPanel box = new JPanel();
                box.setMinimumSize( new Dimension( 50, 50 ) );
                box.setPreferredSize( new Dimension( 100, 100 ) );
                box.setMaximumSize( new Dimension( 200, 200 ) );
                box.setBackground( Color.RED );

                fgc.add(
                        box,
                        new FlexiGridBasicConstraint(
                                "row " + row + " label",
                                row,
                                0
                        ) // .setMargins( 2, 2, 2, 2 )
                          .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.CENTER )
                          .setVerticalJustification( FlexiGridBasicConstraint.VJustification.CENTER )
                );

            } else {

                fgc.add(
                        new JLabel( "row " + row + ObtuseUtil.replicate( "!", row ) ),
                        new FlexiGridBasicConstraint(
                                "row " + row + " label",
                                row,
                                0
                        ) // .setMargins( 2, 2, 2, 2 )
                          .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.LEFT )
                          .setVerticalJustification( FlexiGridBasicConstraint.VJustification.TOP )
                );

            }

            fgc.add(
                    new JButton( NounsList.pickNoun() ),
                    new FlexiGridBasicConstraint(
                            "row " + row + " label",
                            row,
                            1
                    ) // .setMargins( 2, 2, 2, 2 )
                    .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.CENTER )
            );

            fgc.add(
                    new JLabel( NounsList.pickNoun() ),
                    new FlexiGridBasicConstraint(
                            "row " + row + " label",
                            row,
                            2
                    ) // .setMargins( 2, 2, 2, 2 )
                    .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.RIGHT )
                    .setVerticalJustification( FlexiGridBasicConstraint.VJustification.BOTTOM )
            );

            if ( row == 0 ) {

                JPanel box = new JPanel();
                box.setMinimumSize( new Dimension( 50, 50 ) );
                box.setPreferredSize( new Dimension( 400, 200 ) );
                box.setMaximumSize( new Dimension( 500, 500 ) );
                box.setBackground( Color.BLUE );

                fgc.add(
                        box,
                        new FlexiGridBasicConstraint(
                                "row " + row + " label",
                                row,
                                3
                        ) // .setMargins( 2, 2, 2, 2 )
                          .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.RIGHT )
                          .setVerticalJustification( FlexiGridBasicConstraint.VJustification.TOP )
                );

                box.setVisible( false );

            } else if ( row == 1 ) {

                JPanel box = new JPanel();
                box.setLayout( new BoxLayout( box, BoxLayout.X_AXIS ) );
                box.add( new JButton( "first" ) );
                box.add( new JCheckBox( "second" ) );
                box.add( new JLabel( "third" ) );
                fgc.add(
                        box,
                        new FlexiGridBasicConstraint(
                                "row " + row + " label",
                                row,
                                3
                        ) // .setMargins( 2, 2, 2, 2 )
                          .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.RIGHT )
                          .setVerticalJustification( FlexiGridBasicConstraint.VJustification.TOP )
                );

            } else if ( row == 2 ) {

                boolean diagonally = ObtuseUtil.never();
                FlexiGridContainer f2 = new FlexiGridContainer1( "f2", msgTraceMode, useLayoutTracer );
                f2.setBorder( BorderFactory.createLineBorder( Color.BLUE ) );
                for ( int cc = 0; cc < 20; cc += 2 ) {

                    Component thing = cc == 0 ? new JButton( "Button" ) : new JLabel( VerbsList.pickVerb() );

                    if ( thing instanceof JButton && "Button".equals( ((JButton)thing).getText() ) ) {

                        ((JButton)thing).addActionListener(
                                new MyActionListener() {
                                    @Override
                                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                                        Logger.logMsg( "Button clicked" );

                                    }

                                }
                        );
                    }

                    FlexiGridBasicConstraint wordConstraints = new FlexiGridBasicConstraint(
                            "f2." + cc,
                            diagonally ? cc : 0,
                            cc
                    ).setVerticalJustification( FlexiGridBasicConstraint.VJustification.values()[cc % 3] );
                    f2.add(
                            thing,
                            wordConstraints
                    );

                    FlexiGridDivider divider = new FlexiGridDivider(
                            cc == 6 ? FlexiGridDivider.DividerStyle.DOUBLE_LINE : FlexiGridDivider.DividerStyle.SINGLE_LINE,
                            FlexiGridPanelModel.Orientation.COLUMN,
                            cc + 1,
                            cc == 6
                    );

                    divider.setForeground( cc == 6 ? Color.RED : Color.BLUE );

                    FlexiGridBasicConstraint dividerConstraint = divider.generateConstraint( diagonally ? cc : 0 /*cc == 0 ? -1 : cc*/ );
                    f2.add(
                            divider,
                            dividerConstraint
                    );

                }

                fgc.add(
                        f2.getAsContainer(),
                        new FlexiGridBasicConstraint(
                                "f2",
                                row,
                                3
                        )
                );

            }

        }

        JPanel box = new JPanel();
        box.setMinimumSize( new Dimension( 50, 50 ) );
        box.setPreferredSize( new Dimension( 400, 200 ) );
        box.setMaximumSize( new Dimension( 500, 500 ) );
        box.setBackground( Color.GREEN );

        fgc.add(
                box,
                new FlexiGridBasicConstraint(
                        "row " + ( NROWS + 1 ) + " label",
                        NROWS + 1,
                        3
                ) // .setMargins( 2, 2, 2, 2 )
                  .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.RIGHT )
                  .setVerticalJustification( FlexiGridBasicConstraint.VJustification.TOP )
        );

        box.setVisible( true );

        JFrame jf = new JFrame( "FlexiGrid testing" );
        jf.setContentPane( ( fgc ).getAsContainer() );
        jf.pack();
        jf.setVisible( true );

    }

    @SuppressWarnings("ClassHasNoToStringMethod")
    private static class MyButtonActionListener extends MyActionListener {

        private final FlexiGridPanelModel<FlexiGridModelSlice> _model;
        private final FlexiGridModelSlice _slice;
        private final JButton _button;
        private final boolean _markerMode;

        public MyButtonActionListener(
                final @NotNull FlexiGridPanelModel<FlexiGridModelSlice> model,
                final @NotNull FlexiGridModelSlice slice,
                final @NotNull JButton button,
                final boolean markerMode
        ) {
            super();

            _model = model;
            _slice = slice;
            _button = button;
            _markerMode = markerMode;

        }

        @Override
        protected void myActionPerformed( final ActionEvent actionEvent ) {

            if ( _slice == null ) {

                throw new IllegalArgumentException( "FlexiGridTesting.MyButtonActionListener.myActionPerformed:  slice not set" );

            }

            if ( _button == null ) {

                throw new IllegalArgumentException( "FlexiGridTesting.MyButtonActionListener.myActionPerformed:  button not set" );

            }

            FlexiGridModelSlice theButtonSlice = makeButtonSlice(
                    "Add here",
                    FlexiGridPanelModel.Orientation.ROW,
                    _model
            );

            Optional<Integer> optCurrentIndex = _slice.getOptCurrentIndex();
            if ( optCurrentIndex.isPresent() ) {

                Logger.logMsg( "markerMode=" + _markerMode + ", button " + ObtuseUtil.enquoteToJavaString( _button.getText() ) + " is at position " + optCurrentIndex.get() );

                Logger.logMsg( "modifiers=" + actionEvent.getModifiers() + ", ctrl_mask=" + ActionEvent.CTRL_MASK + ", index=" + optCurrentIndex.get() );
                if ( _markerMode ) {

                    FlexiGridModelSlice markerSlice = ( actionEvent.getModifiers() & ActionEvent.CTRL_MASK ) == 0 ? _alpha : _beta;
                    _model.addAtMarker( theButtonSlice, markerSlice == _alpha, markerSlice );

                } else {

                    int offset = ( actionEvent.getModifiers() & ActionEvent.CTRL_MASK ) == 0 ? 0 : 1;
                    int index = optCurrentIndex.get()
                                               .intValue() + offset;
                    _model.add( theButtonSlice, index );

                }

            } else {

                throw new IllegalArgumentException( "myActionListener:  we don't have a current index" );

            }

        }

    }

    private static FlexiGridModelSlice _alpha;
    private static FlexiGridModelSlice _beta;

    @SuppressWarnings("SameParameterValue")
    private static void launchModelTestFrame( final boolean msgTraceMode, final boolean useLayoutTracer ) {

        FlexiGridPanelModel<FlexiGridModelSlice> model = new FlexiGridPanelModel<>( "model test", FlexiGridPanelModel.Orientation.ROW, msgTraceMode, useLayoutTracer );

        _alpha = makeMarkedLabelSlice(
                FlexiGridPanelModel.Orientation.ROW,
                model,
                "alpha"
        );
        model.add( _alpha );

        FlexiGridModelSlice one = makeButtonSlice(
                "Add Here",
                FlexiGridPanelModel.Orientation.ROW,
                model
        );

        model.add( one );

        _beta = makeMarkedLabelSlice(
                FlexiGridPanelModel.Orientation.ROW,
                model,
                "beta"
        );
        model.add( _beta );

        for ( FlexiGridModelSlice slice : model.getSlicesInOrder() ) {

            Logger.logMsg( "FGT slice:  " + slice );

        }

        for ( Component c : model.getFlexiGridContainer().getAsContainer().getComponents() ) {

            Logger.logMsg( "FGT component is " + ( c.isVisible() ? "visible" : "invisible" ) + " - " + c );

        }

        JButton flipInvisibilityButton = new JButton( "make something invisible" );
        flipInvisibilityButton.addActionListener(
                new MyActionListener() {
                    FlexiGridModelSlice _invisibleSlice = null;

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        if ( _invisibleSlice == null ) {

                            List<FlexiGridModelSlice> slices = model.getSlicesInOrder();
                            int ix = RandomCentral.nextInt( slices.size() );
                            _invisibleSlice = slices.get( ix );
                            Logger.logMsg( "### making slice " + ix + " invisible - " + _invisibleSlice );
                            _invisibleSlice.setVisible( false );

                            flipInvisibilityButton.setText( "make something visible" );

                        } else {

                            Logger.logMsg( "### making slice visible - " + _invisibleSlice );
                            _invisibleSlice.setVisible( true );
                            _invisibleSlice = null;

                            flipInvisibilityButton.setText( "make something invisible" );

                        }

                    }

                }
        );

        JButton redoLayoutButton = new JButton( "Redo Layout" );
        redoLayoutButton.addActionListener(
                new MyActionListener() {
                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        Logger.logMsg( "forcing Redo Layout" );
                        model.getFlexiGridContainer().revalidate();

                    }

                }
        );

        JFrame jf = new JFrame( "FlexiGridPanelModel testing" );
        JPanel jp = new JPanel();
        jp.setLayout( new BorderLayout() );
        jp.add( flipInvisibilityButton, BorderLayout.NORTH );
        jp.add( model.getFlexiGridContainer().getAsContainer(), BorderLayout.CENTER );
        jp.add( redoLayoutButton, BorderLayout.SOUTH );

        jf.getRootPane().setOpaque( true );
        jf.setContentPane( jp );
        jf.setMinimumSize( new Dimension( 300, 400 ) );
        jf.pack();
        jf.setVisible( true );

    }

    private static FlexiGridModelSlice makeButtonSlice(
            final @NotNull String name,
            @SuppressWarnings("SameParameterValue") final @NotNull FlexiGridPanelModel.Orientation orientation,
            final @NotNull FlexiGridPanelModel<FlexiGridModelSlice> model
    ) {

        JButton button = new JButton();
        FlexiGridModelSlice rval = makeSimpleSlice(
                orientation,
                model,
                new Component[]{ button }
        );

        MyButtonActionListener myActionListener = new MyButtonActionListener( model, rval, button, true );
        button.addActionListener( myActionListener );

        button.setText( name + " #" + rval.getId() );

        return rval;

    }

    private static FlexiGridModelSlice makeMarkedLabelSlice(
            @SuppressWarnings("SameParameterValue") final @NotNull FlexiGridPanelModel.Orientation orientation,
            final @NotNull FlexiGridPanelModel model,
            final @NotNull String marker
    ) {

        JLabel label = new JLabel();
        FlexiGridModelSlice rval = makeSimpleSlice(
                orientation,
                model,
                new Component[]{ label }
        );

        label.setText( marker );
        rval.setMarkerTag( marker );

        return rval;

    }

    private static FlexiGridModelSlice makeSimpleSlice(
            final @NotNull FlexiGridPanelModel.Orientation orientation,
            final FlexiGridPanelModel model,
            final @NotNull Component[] components
    ) {

        SortedMap<Integer,FlexiGridItemInfo> dataMap = new TreeMap<>();
        int ix = 0;
        boolean rowOrientation = orientation.isRowOrientation();
        String namePrefix = NounsList.pickNoun();
        for ( Component component : components ) {

            int row = rowOrientation ? -1 : ix;
            int col = rowOrientation ? ix : -1;
            FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                    new FlexiGridBasicConstraint( namePrefix + " @ " + ix, row, col )
            );

            dataMap.put(
                    ix,
                    new FlexiGridItemInfo( row, col, component, constraintsTable )
            );

        }

        FlexiGridModelSlice newSlice = new FlexiGridModelSlice(
                "control row",
                orientation,
                dataMap
        );

        return newSlice;

    }

}
