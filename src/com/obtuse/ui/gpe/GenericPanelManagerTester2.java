package com.obtuse.ui.gpe;

import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.NounsList;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Created by danny on 2019/07/11.
 */
public class GenericPanelManagerTester2 {

    /**
     The model that describes something that appears in two different panels.
     */

    public static class ActualModel {

    }

    /**
     The model for each row of the first panel.
     */

    public static class TestModel1 extends GenericPanelRowModel<TestSlice1> {

        private final TestManager1 _gpm;

        private String _name;

        public TestModel1( final TestManager1 gpm ) {
            super( gpm, "TestModel" );

            _gpm = gpm;

            _name = NounsList.pickNoun();

        }

        @SuppressWarnings("unused")
        public TestManager1 getPanelManager() {

            return _gpm;

        }

        @NotNull
        public String getName() {

            return _name;

        }

        public String toString() {

            return "TestModel1( " + ObtuseUtil.enquoteToJavaString( _name ) + " )";

        }

    }

    /**
     The model for each row of the first panel.
     */

    public static class TestModel2 extends GenericPanelRowModel<TestSlice2> {

        private final TestManager2 _gpm;

        private String _name;

        public TestModel2( final TestManager2 gpm ) {
            super( gpm, "TestModel" );

            _gpm = gpm;

            _name = NounsList.pickNoun();

        }

        @SuppressWarnings("unused")
        public TestManager2 getPanelManager() {

            return _gpm;

        }

        @NotNull
        public String getName() {

            return _name;

        }

        public String toString() {

            return "TestModel1( " + ObtuseUtil.enquoteToJavaString( _name ) + " )";

        }

    }

    /**
     The panel manager for the first panel.
     */

    public static class TestManager1 extends GenericPanelManager<TestSlice1> {

        public TestManager1(
                @NotNull final GenericPanelManagerTester2.TestSliceFactory1 sliceFactory
        ) {
            super( sliceFactory );

        }

        @Override
        public String toString() {

            return "GPMT.TestManager";

        }

    }

    /**
     The panel manager for the first panel.
     */

    public static class TestManager2 extends GenericPanelManager<TestSlice2> {

        public TestManager2(
                @NotNull final GenericPanelManagerTester2.TestSliceFactory2 sliceFactory
        ) {
            super( sliceFactory );

        }

        @Override
        public String toString() {

            return "GPMT.TestManager";

        }

    }

    /**
     The slice for each row of the first panel.
     */

    public static class TestSlice1 extends GenericPanelSlice {

        public TestSlice1( final GenericPanelRowModel<TestSlice1> model ) {
            super( model );

            JLabel jLabel = new JLabel( "Chumbly 4:" );
            FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                    new FlexiGridBasicConstraint( "label=" + model.getName(), -1, 0 )
                            .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.LEFT )
            );
            setComponent(
                    0,
                    new FlexiGridItemInfo(
                            "label=" + model.getName(),
                            -1,
                            0,
                            jLabel,
                            constraintsTable
                    )
            );

            JTextField jTextField = new JTextField( model.getName() );
            constraintsTable = new FlexiGridConstraintsTable(
                    new FlexiGridBasicConstraint( "label=" + model.getName(), -1, 1 )
                            .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.FILL )
            );
            setComponent(
                    1,
                    new FlexiGridItemInfo(
                            "label=" + model.getName(),
                            -1,
                            1,
                            jTextField,
                            constraintsTable
                    )
            );

            ObtuseUtil.doNothing();

        }

        @NotNull
        @Override
        public String toString() {

            return "TestSlice(GPM)";

        }

    }

    /**
     The slice for each row of the panel.
     */

    public static class TestSlice2 extends GenericPanelSlice {

        public TestSlice2( final GenericPanelRowModel<TestSlice2> model ) {
            super( model );

            JLabel jLabel = new JLabel( "Chumbly 5:" );
            FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                    new FlexiGridBasicConstraint( "label=" + model.getName(), -1, 0 )
                            .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.LEFT )
            );
            setComponent(
                    0,
                    new FlexiGridItemInfo(
                            "label=" + model.getName(),
                            -1,
                            0,
                            jLabel,
                            constraintsTable
                    )
            );

            JTextField jTextField = new JTextField( model.getName() );
            constraintsTable = new FlexiGridConstraintsTable(
                    new FlexiGridBasicConstraint( "label=" + model.getName(), -1, 1 )
                            .setHorizontalJustification( FlexiGridBasicConstraint.HJustification.FILL )
            );
            setComponent(
                    1,
                    new FlexiGridItemInfo(
                            "label=" + model.getName(),
                            -1,
                            1,
                            jTextField,
                            constraintsTable
                    )
            );

            ObtuseUtil.doNothing();

        }

        @NotNull
        @Override
        public String toString() {

            return "TestSlice(GPM)";

        }

    }

    public static class TestSliceFactory1 implements GenericPanelSliceFactory<TestSlice1> {

        @NotNull
        @Override
        public TestSlice1 createSlice( final GenericPanelRowModel<TestSlice1> model ) {

            return new TestSlice1( model );

        }

    }

    public static class TestSliceFactory2 implements GenericPanelSliceFactory<TestSlice2> {

        @NotNull
        @Override
        public TestSlice2 createSlice( final GenericPanelRowModel<TestSlice2> model ) {

            return new TestSlice2( model );

        }

    }

    @SuppressWarnings("FieldCanBeLocal") private static TestManager1 s_gpm1; // GenericPanelManager<TestModel, TestSlice> s_gpm;
    @SuppressWarnings("FieldCanBeLocal") private static TestManager2 s_gpm2; // GenericPanelManager<TestModel, TestSlice> s_gpm;

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "GenericPanelManager", "testing" );

        JFrame jf = new JFrame( "Generic Panel Manager Tester" );
        jf.setMinimumSize( new Dimension( 400, 40 ) );

        s_gpm1 = new TestManager1(
                new TestSliceFactory1()
        );

        for ( int row = 0; row < 5; row += 1 ) {

            TestModel1 testModel = new TestModel1( s_gpm1 );
            s_gpm1.addRow( testModel );

            GenericPanelRowModel<TestSlice1> extractedModel = s_gpm1.getModel( row );

        }

        jf.setContentPane(
                s_gpm1.getJPanel()
        );

        jf.pack();

        ObtuseUtil.doNothing();

        jf.setVisible( true );

    }

}
