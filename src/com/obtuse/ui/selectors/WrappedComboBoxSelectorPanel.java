/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Vector;
import java.util.function.Function;

/**
 A wrapped {@link ComboBoxSelectorPanel} that provides various wrapped panels to deal with combo-box situations.
 <p>This class can be used as a slightly simpler to use version of {@link ComboBoxSelectorPanel} or it can be used to
 handle the degenerate cases where there are zero or one alternatives for the human to choose from.</p>
 */

public class WrappedComboBoxSelectorPanel<CHOICE, PANEL extends JPanel> extends JPanel {

    public enum DegenerateCase {
        NO_ENTITIES,
        ONE_ENTITY
    }

    public interface CController<CCCHOICE,CCPANEL> {

        CCPANEL getPanel( @NotNull CCCHOICE choice );

        Vector<CCCHOICE> getActualRepositories();

        boolean handleZeroAndOneCaseSpecially();

        Vector<CCCHOICE> getUnspecifiedAndActualRepositories();

        String getPanelName();

        String getDegenerateCaseMessage( DegenerateCase degenerateCase );

        String getPickListPrompt();

    }

    private final CController<CHOICE,PANEL> _cController;
    private final boolean _degenerateCase;
    private final InnerWrappedComboBoxSelectorPanel _comboPanel;
    private final DefaultComboBoxModel<CHOICE> _comboBoxModel;
    private final Container _panel;

//    public InnerWrappedComboBoxSelectorPanel<ICHOICE,IPANEL> create(
//            final ReferenceCard referenceCard,
//            final @NotNull DataCategoryName dataCategoryName,
//            final ConnectorComboBoxModel connectorComboBoxModel,
//            final @NotNull DataCategoryStateNode dataCategoryStateNode
//    ) {
//
//        InnerWrappedComboBoxSelectorPanel<ICHOICE, JPanel>
//                rval = new InnerWrappedComboBoxSelectorPanel<IKEY, ICHOICE, DefaultComboBoxModel<CHOICE>, JPanel>(
//                referenceCard,
//                dataCategoryName,
//                connectorComboBoxModel,
//                dataCategoryStateNode
//        );
//
//        return rval;
//
//    }

    public WrappedComboBoxSelectorPanel(
            final @NotNull CController<CHOICE,PANEL> cController
    ) {
        super();

        _cController = cController;

        Vector<CHOICE> actualRepositories = _cController.getActualRepositories();

        setLayout( new BorderLayout() );
        if ( actualRepositories.size() <= 1 && _cController.handleZeroAndOneCaseSpecially() ) {

            _degenerateCase = true;

            _comboPanel = null;
            _comboBoxModel = null;

            if ( actualRepositories.isEmpty() ) {

                _panel = new JLabel( _cController.getDegenerateCaseMessage( DegenerateCase.NO_ENTITIES ) ); // "Nothing to choose from in " + _cController.getPanelName() );

            } else {

                _panel = _cController.getPanel( actualRepositories.firstElement() );

            }

        } else {

            _degenerateCase = false;

            _comboBoxModel = new DefaultComboBoxModel<CHOICE>( cController.getUnspecifiedAndActualRepositories() );

            _comboPanel = new InnerWrappedComboBoxSelectorPanel( _comboBoxModel );

            _panel = _comboPanel;

        }

        add( _panel, BorderLayout.CENTER );

//        LinearLayoutUtil.describeFullyGuiEntity( "WCBSP(container)", this );

    }

    public WrappedComboBoxSelectorPanel( final @NotNull Container container ) {

        if ( container instanceof JLabel ) {

            Logger.logMsg( "WCBSP:  container is JLabel( " + ObtuseUtil.enquoteToJavaString( ((JLabel)container).getText() ) + " )" );

        }

        _degenerateCase = true;
        _panel = container;
        _cController = null;
        _comboBoxModel = null;
        _comboPanel = null;

        setLayout( new BorderLayout() );
        add( container, BorderLayout.CENTER );
//        LinearLayoutUtil.describeFullyGuiEntity( "WCBSP(container)", this );

    }

    public boolean isDegenerateCase() {

        return _degenerateCase;

    }

    public boolean setSubsidiaryPanelBorder( Border border ){

        if ( _comboPanel != null ) {

            _comboPanel.setSubsidiaryPanelBorder( border );

            return true;

        } else if ( _panel != null ) {

            if ( _panel instanceof JComponent ) {

                ( (JComponent)_panel ).setBorder( border );

                return true;

            }

        }

        return false;
    }

    public class InnerWrappedComboBoxSelectorPanel // <ICHOICE, IPANEL extends JPanel>
            extends ComboBoxSelectorPanel<CHOICE, PANEL> { //ComboBoxSelectorPanel<String, JPanel>> {

//        private final DataCategoryName _dataCategoryName;

//    private final JPanel _buttonPanel;
//    private ButtonGroup _buttonGroup;

        private InnerWrappedComboBoxSelectorPanel(
//                final @NotNull DataCategoryName dataCategoryName,
                final @NotNull DefaultComboBoxModel<CHOICE> comboBoxModel //,
//                final @NotNull DataCategoryStateNode dataCategoryStateNode,
//                final @NotNull CController<CHOICE,PANEL> cController
        ) {

            super(
                    _cController.getPanelName() , // "DataRepositoryConnector selection panel",
                    _cController.getPickListPrompt(), // "Pick a repository",
                    comboBoxModel,
                    null,
                    new Function<CHOICE, PANEL>() {
                        @Override
                        public PANEL apply( final CHOICE choice ) {

                            PANEL panel = _cController.getPanel( choice );

                            return panel;

                        }

                    },
                    false

            );

//            _dataCategoryName = dataCategoryName;

        }

//        public ReferenceCard getReferenceCard() {
//
//            return _referenceCard;
//
//        }

//        public DataCategoryName getDataCategoryName() {
//
//            return _dataCategoryName;
//
//        }

        @SuppressWarnings("UnusedReturnValue")
        public boolean notifyCurrentChildChange(
                final @Nullable CHOICE oldChoice,
                final @NotNull CHOICE newChoice
        ) {

            int newChoiceIndex = _comboBoxModel.getIndexOf( newChoice );
            if ( newChoiceIndex < 0 ) {

                throw new IllegalArgumentException( "InnerWrappedComboBoxSelectorPanel.notifyCurrentChildChange:  attempt to switch to non-existent choice" +
                                                    " " +
                                                    newChoice );

            }

            int selectedIx = _comboBoxModel.getIndexOf( _comboBoxModel.getSelectedItem() );
            if ( selectedIx == newChoiceIndex ) {

                // Depending on how we handle button selections, this might or might not be a bug.
                // Let's just log it for now (and maybe forever).

                Logger.logMsg(
                        "InnerWrappedComboBoxSelectorPanel.notifyCurrentChildChange:  " +
                        "asked to change from " + oldChoice + "@" + _comboBoxModel.getIndexOf( oldChoice ) +
                        " to " + newChoice + "@" + newChoiceIndex + " when that's already our currently selected button"
                );

                ObtuseUtil.doNothing();

                return false;

            } else {

                _comboBoxModel.setSelectedItem( newChoice );

                Logger.logMsg( "InnerWrappedComboBoxSelectorPanel.notifyCurrentChildChange:  from " + oldChoice + " to " + newChoice );

                return true;

            }

//
//            Logger.logMsg( "InnerWrappedComboBoxSelectorPanel.notifyCurrentChildChange:  from " +
//                           oldCurrentChildInstanceId +
//                           " to " +
//                           newCurrentChildInstanceId );
//
//            ObtuseUtil.doNothing();
//
//            return true;

//        JRadioButton newButton = getButtonMap().get( newCurrentChildInstanceId.getStringName() );
//        if ( newButton == null ) {
//
//            throw new IllegalArgumentException( "DataCategorySelectionPanel.notifyCurrentChildChange:  attempt to switch to non-existent button " + newCurrentChildInstanceId );
//
//        }
//
//        if ( newButton.isSelected() ) {
//
//            // Depending on how we handle button selections, this might or might not be a bug.
//            // Let's just log it for now (and maybe forever).
//
//            Logger.logMsg(
//                    "DataCategorySelectionPanel.notifyCurrentChildChange:  asked to change from " + oldCurrentChildInstanceId +
//                    "to " + newCurrentChildInstanceId + " when that's already our currently selected button"
//            );
//
//            ObtuseUtil.doNothing();
//
//            return false;
//
//        } else {
//
//            newButton.setSelected( true );
//
//            ObtuseUtil.doNothing();
//
//            return true;
//
//        }

        }

        public String toString() {

            return "InnerWrappedComboBoxSelectorPanel( " + ObtuseUtil.enquoteToJavaString( getName() ) + " )";

        }

    }

    public boolean notifyCurrentChildChange(
            final @Nullable CHOICE oldChoice,
            final @NotNull CHOICE newChoice
    ) {

        return _comboPanel.notifyCurrentChildChange( oldChoice, newChoice );

    }

    public String toString() {

        return "WrappedComboBoxSelectorPanel( " + _comboPanel + " )";

    }

}
