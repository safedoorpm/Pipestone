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

/**
 Created by danny on 2019/07/11.
 */
public class GenericPanelManagerTester {

    /**
     The model for each row of the panel.
     */

    public static class TestModel extends GenericPanelRowModel<TestSlice> {

        private final TestManager _gpm;

        private String _name;

        public TestModel( final TestManager gpm ) {
            super( gpm, "TestModel" );

            _gpm = gpm;

            _name = NounsList.pickNoun();

        }

        @SuppressWarnings("unused")
        public TestManager getPanelManager() {

            return _gpm;

        }

        @NotNull
        public String getName() {

            return _name;

        }

        public String toString() {

            return "TestModel( " + ObtuseUtil.enquoteToJavaString( _name ) + " )";

        }

    }

    /**
     The panel manager.
     */

    public static class TestManager extends GenericPanelManager<TestSlice> {

        public TestManager(
                @NotNull final GenericPanelManagerTester.TestSliceFactory sliceFactory
        ) {
            super( sliceFactory );

        }

        @Override
        public String toString() {

            return "GPMT.TestManager";

        }

    }

    /**
     The slice for each row of the panel.
     */

    public static class TestSlice extends GenericPanelSlice {

        public TestSlice( final GenericPanelRowModel<TestSlice> model ) {
            super( model );

            JLabel jLabel = new JLabel( "Chumbly 2:" );
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

    public static class TestSliceFactory implements GenericPanelSliceFactory<TestSlice> {

        @NotNull
        @Override
        public TestSlice createSlice( final GenericPanelRowModel<TestSlice> model ) {

            return new TestSlice( model );

        }

    }

    @SuppressWarnings("FieldCanBeLocal") private static TestManager s_gpm; // GenericPanelManager<TestModel, TestSlice> s_gpm;

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "GenericPanelManager", "testing" );

        JFrame jf = new JFrame( "Generic Panel Manager Tester" );
        jf.setMinimumSize( new Dimension( 400, 40 ) );

        s_gpm = new TestManager(
                new TestSliceFactory()
        );

        for ( int row = 0; row < 5; row += 1 ) {

            TestModel testModel = new TestModel( s_gpm );
            s_gpm.addRow( testModel );

            GenericPanelRowModel<TestSlice> extractedModel = s_gpm.getModel( row );

        }

        jf.setContentPane(
                s_gpm.getJPanel()
        );

        jf.pack();

        ObtuseUtil.doNothing();

        jf.setVisible( true );

    }

}
