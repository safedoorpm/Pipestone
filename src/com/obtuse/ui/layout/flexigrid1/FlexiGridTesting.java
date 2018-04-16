/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridModelSlice;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Take the FlexiGridLayoutManager out for a test drive.
 */

public class FlexiGridTesting {

    public static final int NROWS = 5;

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "BiSheng", "FlexiGridLayoutManager", null );

        launchSimpleTestFrame();

        launchModelTestFrame();

    }

    private static void launchSimpleTestFrame() {

        FlexiGridContainer fgc = new FlexiGridContainer1( "test1" );

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

                FlexiGridContainer f2 = new FlexiGridContainer1( "f2" );
                f2.setPreferredSize( new Dimension( 500, 45 ) );
                for ( int cc = 0; cc < 10; cc += 1 ) {

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
                    f2.add(
                            thing,
                            new FlexiGridBasicConstraint(
                                    "f2." + cc,
                                    0,
                                    cc
                            ).setVerticalJustification( FlexiGridBasicConstraint.VJustification.values()[cc % 3] )
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

//        Logger.logMsg( "done - FlexiGrid test frame should be visible" );

    }

//    private static class XActionListener extends MyActionListener {
//
//        private final String _word;
//        private final FlexiGridPanelModel _model;
//        private FlexiGridModelSlice _slice;
//
//        private XActionListener( final @NotNull String word, final @NotNull FlexiGridPanelModel model ) {
//            super();
//
//            _word = word;
//            _model = model;
//
//        }
//
//        protected void myActionPerformed( final ActionEvent actionEvent ) {
//
//            int offset = ( actionEvent.getModifiers() & ActionEvent.CTRL_MASK ) == 0 ? 0 : 1;
//
//            FlexiGridModelSlice _slice = makeButtonSlice(
//                    "Add here",
//                    FlexiGridPanelModel.Orientation.ROWS,
//                    _model,
//                    this
//            );
//            Optional<Integer> optCurrentIndex = _slice.getOptCurrentIndex();
//            if ( optCurrentIndex.isPresent() ) {
//
//                Logger.logMsg( "modifiers=" + actionEvent.getModifiers() + ", ctrl_mask=" + ActionEvent.CTRL_MASK + ", index=" + optCurrentIndex.get() );
//                int index = optCurrentIndex.get() + offset;
//                _model.add( theButtonSlice, index );
//
//            } else {
//
//                throw new IllegalArgumentException( "myActionListener:  we don't have a current index" );
//
//            }
//
//        }
//
//    }

    private static class MyButtonActionListener extends MyActionListener {

        private final FlexiGridPanelModel _model;
        private final FlexiGridModelSlice _slice;
        private final JButton _button;
        private final boolean _markerMode;

        public MyButtonActionListener(
                final @NotNull FlexiGridPanelModel model,
                final @NotNull FlexiGridModelSlice slice,
                final @NotNull JButton button,
                final boolean markerMode
        ) {
            super();

            _model = model;
            _slice = slice;
            _button = button;
            _markerMode = markerMode;

//            _slice = slice;

        }

//        public void setSlice( FlexiGridModelSlice slice ) {
//
//            _slice = slice;
//
//        }
//
//        public void setButton( JButton button ) {
//
//            _button = button;
//
//        }

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
                    FlexiGridPanelModel.Orientation.ROWS,
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
                    int index = optCurrentIndex.get() + offset;
                    _model.add( theButtonSlice, index );

                }

            } else {

                throw new IllegalArgumentException( "myActionListener:  we don't have a current index" );

            }

        }

    }

    private static FlexiGridModelSlice _alpha;
    private static FlexiGridModelSlice _beta;

    private static void launchModelTestFrame() {

        FlexiGridPanelModel model = new FlexiGridPanelModel( "model test", FlexiGridPanelModel.Orientation.ROWS );

        _alpha = makeMarkedLabelSlice(
                FlexiGridPanelModel.Orientation.ROWS,
                model,
                "alpha"
        );
        model.add( _alpha );

        FlexiGridModelSlice one = makeButtonSlice(
                "Add Here",
                FlexiGridPanelModel.Orientation.ROWS,
                model
        );

        model.add( one );

        _beta = makeMarkedLabelSlice(
                FlexiGridPanelModel.Orientation.ROWS,
                model,
                "beta"
        );
        model.add( _beta );

        JFrame jf = new JFrame( "FlexiGridPanelModel testing" );
        jf.setContentPane( model.getFlexiGridContainer().getAsContainer() );
        jf.setMinimumSize( new Dimension( 300, 400 ) );
        jf.pack();
        jf.setVisible( true );

//        Logger.logMsg( "done - FlexiGrid test frame should be visible" );

    }

//    private static final HashMap<JButton,FlexiGridModelSlice> _buttonToSliceMap = new HashMap<>();

    private static FlexiGridModelSlice makeButtonSlice(
            final @NotNull String name,
            final @NotNull FlexiGridPanelModel.Orientation orientation,
            final @NotNull FlexiGridPanelModel model
    ) {

        JButton button = new JButton();
//        button.addActionListener( myActionListener );

        FlexiGridModelSlice rval = makeSimpleSlice(
                orientation,
                model,
                new Component[]{ button }
        );

        MyButtonActionListener myActionListener = new MyButtonActionListener( model, rval, button, true );
        button.addActionListener( myActionListener );

        button.setText( name + " #" + rval.getId() );

//        _buttonToSliceMap.put( button, rval );

        return rval;

    }

    private static FlexiGridModelSlice makeMarkedLabelSlice(
            final @NotNull FlexiGridPanelModel.Orientation orientation,
            final @NotNull FlexiGridPanelModel model,
            final @NotNull String marker
    ) {

        JLabel label = new JLabel();
//        label.addActionListener( myActionListener );

        FlexiGridModelSlice rval = makeSimpleSlice(
                orientation,
                model,
                new Component[]{ label }
        );

//        MyButtonActionListener myActionListener = new MyButtonActionListener( model, rval, label );
//        label.addActionListener( myActionListener );

        label.setText( marker );
        rval.setMarkerTag( marker );

//        _buttonToSliceMap.put( label, rval );

        return rval;

    }

    private static FlexiGridModelSlice makeSimpleSlice(
            FlexiGridPanelModel.Orientation orientation,
            final FlexiGridPanelModel model,
            Component[] components
    ) {

        SortedMap<Integer,FlexiGridItemInfo> dataMap = new TreeMap<>();
        int ix = 0;
        boolean rowOrientation = orientation == FlexiGridPanelModel.Orientation.ROWS;
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
                model,
                dataMap
        );

        return newSlice;

    }

}
