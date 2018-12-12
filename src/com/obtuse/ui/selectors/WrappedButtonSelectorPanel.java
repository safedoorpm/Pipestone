/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.util.Ix;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 A helper class that wraps either a {@link ButtonSelectorPanel}{@code <C>} or a JPanel.
 */

@SuppressWarnings("unused")
public class WrappedButtonSelectorPanel<CHOICE extends Comparable, PANEL extends JPanel>
        extends WrappedSelectorPanel<CHOICE> {

    public interface IController<ICCHOICE,ICPANEL extends JPanel> {

        @NotNull
        String getPanelName();

        /**
         Given the selection of a CHOICE by the human, get the panel that we're supposed to show them.
         @param choice the choice.
         @return the panel (could be null if nothing is to be shown).
         */

        @Nullable
        ICPANEL getPanel( @NotNull ICCHOICE choice );

        /**
         Get the choices in the order that the buttons should be presented to the human.
         @return the choices in selection order.
         */

        @NotNull
        List<ICCHOICE> getChoicesInSelectionOrder();

        ICCHOICE getInitialChoice();

        void reportChoice( @NotNull ICCHOICE choice );

    }

    private final InnerWrappedButtonSelectorPanel _buttonPanel;

    public class InnerWrappedButtonSelectorPanel extends ButtonSelectorPanel<CHOICE,PANEL> {

        private final IController<CHOICE,PANEL> _iController;

        private final SortedMap<CHOICE, JRadioButton> _buttonMap;

        private InnerWrappedButtonSelectorPanel(
                final @NotNull ButtonGroup buttonGroup,
                final @NotNull JPanel buttonPanel,
                final @NotNull SortedMap<CHOICE, JRadioButton> buttonMap,
                final IController<CHOICE,PANEL> iController
        ) {

            super(
                    iController,
                    buttonGroup,
                    buttonPanel,
                    buttonMap,
                    null,
                    choice -> {

                        PANEL panel = iController.getPanel( choice );

                        return panel;

                    },
                    false

            );

            _iController = iController;
            _buttonMap = buttonMap;

        }

        public IController getChoice() {

            return _iController;

        }

        public CHOICE getSelected() {

            for ( CHOICE choice : _buttonMap.keySet() ) {

                if ( _buttonMap.get( choice ).isSelected() ) {

                    return choice;

                }

            }

            return null;

        }

        public void setSelected( final @NotNull CHOICE choice ) {

            JRadioButton targetButton = _buttonMap.get( choice );
            if ( targetButton == null ) {

                throw new IllegalArgumentException(
                        "WrappedButtonSelectorPanel.setSelected:  no button labeled " +
                        ObtuseUtil.enquoteJavaObject( choice )
                );

            }

            Logger.logMsg(
                    "clicking on button labeled " + ObtuseUtil.enquoteJavaObject( choice ) +
                    " == " + ObtuseUtil.enquoteJavaObject( targetButton.getText() ) + ":  " +
                    targetButton
            );

            targetButton.doClick();

        }

        public SortedMap<CHOICE, JRadioButton> getButtonMap() {

            return _buttonMap;

        }

        public String toString() {

            return "InnerWrappedButtonSelectorPanel( " + ObtuseUtil.enquoteToJavaString( getName() ) + " )";

        }

        public void configureDataAndRepositoryComboBoxes() {

            Logger.logMsg(
                    "InnerWrappedButtonSelectorPanel.configureDataAndRepositoryComboBoxes:  unimplemented"
            );

        }

        /**
         Switch to a new button.

         @param oldChoice the old button's choice.
         @param newChoice the new button's choice
         (guaranteed by our caller to be different than {@code oldChoice}).
         @return {@code true} if this represents an actual change in which button is selected;
         {@code false} otherwise.
         */

        @SuppressWarnings("UnusedReturnValue")
        public boolean notifyCurrentChildChange(
                final @Nullable CHOICE oldChoice,
                final @NotNull CHOICE newChoice
        ) {

            JRadioButton newButton = getButtonMap().get( newChoice );
            if ( newButton == null ) {

                throw new IllegalArgumentException(
                        "InnerWrappedButtonSelectorPanel.notifyCurrentChildChange:  " +
                        "attempt to switch to non-existent button" +
                        " " + newChoice
                );

            }

            if ( newButton.isSelected() ) {

                // Depending on how we handle button selections, this might or might not be a bug.
                // Let's just log it for now (and maybe forever).

                Logger.logMsg(
                        "InnerWrappedButtonSelectorPanel.notifyCurrentChildChange:  asked to change from " +
                        oldChoice + " to " + newChoice + " when that's already our currently selected button"
                );

                ObtuseUtil.doNothing();

                return false;

            } else {

                newButton.setSelected( true );

                ObtuseUtil.doNothing();

                return true;

            }

        }

    }

    @Override
    public boolean notifyCurrentChildChange(
            final @Nullable CHOICE oldChoice,
            final @NotNull CHOICE newChoice
    ) {

        return _buttonPanel.notifyCurrentChildChange( oldChoice, newChoice );

    }

    public WrappedButtonSelectorPanel(
            final @NotNull IController<CHOICE,PANEL> iController
    ) {
        super();

        JPanel buttonPanel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();

        List<CHOICE> allChoices = iController.getChoicesInSelectionOrder();
        CHOICE initialChoice = iController.getInitialChoice();

        // Pick the number of columns (3 or 4) that fills up the most of the last row.

        int nDCN = allChoices.size();
        int v3 = nDCN % 3 == 0 ? 3 : nDCN % 3;
        int v4 = nDCN % 4 == 0 ? 4 : nDCN % 4;
        int winner = v4 >= v3 ? 4 : 3;
        Logger.logMsg(
                "nDCN=" + nDCN + ", v4=" + v4 + ", v3=" + v3 + ", " +
                "winner=" + ( v4 >= v3 ? "v4" : "v3" ) + "=" + winner
        );

        buttonPanel.setLayout( new GridLayout( 0, winner ) );

        // Setup the data category radio dials.
        // Easy to turn this into a combo box if, as seems likely, that turns out to be a better approach.

        // First step is to get a list that has the 'unspecified' choice
        // which is followed by all the other data category choices in
        // name-sorted order.

        // The approach is to get a sorted set of all of the data category names into a list and to then
        // move the 'unspecified' data category's name to the front of the list.

        // We now create and configure the data category radio dial buttons and put them into
        // the data categories panel in the GUI in the order that they came out of the previous step.

        SortedMap<CHOICE, JRadioButton> buttonMap = new TreeMap<>();
        boolean choiceFound = false;
        CHOICE firstButton = null;
        for ( Ix<CHOICE> choice : Ix.arrayList( allChoices ) ) {

            JRadioButton jrb = new JRadioButton( "" + choice.item );
            jrb.setName( "" + choice.item );
            buttonMap.put( choice.item, jrb );
            if ( firstButton == null ) {

                firstButton = choice.item;

            }

            jrb.setSelected( false );
            choiceFound = choiceFound || choice.item.equals( initialChoice );

            buttonGroup.add( jrb );
            buttonPanel.add( jrb );

        }

        if ( firstButton == null ) {

            throw new IllegalArgumentException( "WrappedButtonSelectorPanel:  no choices provided" );

        }

        if ( !choiceFound ) {

            initialChoice = buttonMap.firstKey();

        }

        _buttonPanel = new InnerWrappedButtonSelectorPanel(
                buttonGroup,
                buttonPanel,
                buttonMap,
                iController
        );

        _buttonPanel.setSelected( initialChoice );

        setLayout( new BorderLayout() );
        add( _buttonPanel, BorderLayout.CENTER );

    }

    public boolean setSubsidiaryPanelBorder( Border border ){

        if ( _buttonPanel != null ) {

            _buttonPanel.setSubsidiaryPanelBorder( border );

            return true;

        }

        return false;
    }

    public String toString() {

        return "WrappedButtonSelectorPanel( " + _buttonPanel + " )";

    }

}