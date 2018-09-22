/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;

/**
 A wrapped {@link ComboBoxSelectorPanel} that provides various wrapped panels to deal with combo-box situations.
 <p>This class can be used as a slightly simpler to use version of {@link ComboBoxSelectorPanel} or it can be used to
 handle the degenerate cases where there are zero or one alternatives for the human to choose from.</p>
 */

public class WrappedComboBoxSelectorPanel<CHOICE, PANEL extends JPanel>
        extends WrappedSelectorPanel<CHOICE>
        implements ObtuseListenerProxy<CHOICE> {

    public enum DegenerateCase {
        NO_ENTITIES,
        ONE_ENTITY
    }

    public interface CController<CCCHOICE,CCPANEL> {

        CCPANEL getPanel( @NotNull CCCHOICE choice, final boolean degeneratePanel );

        @NotNull
        Vector<CCCHOICE> getActualChoices();

        boolean handleZeroAndOneCaseSpecially();

        @NotNull
        Vector<CCCHOICE> getUnspecifiedAndActualChoices();

        String getPanelName();

        @Nullable
        String getDegenerateCaseMessage( DegenerateCase degenerateCase );

        String getPickListPrompt();

//        void reportChoice( CCCHOICE choice, int w );

        @Nullable
        CCCHOICE getInitialChoice();

        boolean isUnspecifiedChoice( final @NotNull CCCHOICE choice );

    }

    private final CController<CHOICE,PANEL> _cController;
    private final boolean _degenerateCase;
    private final InnerWrappedComboBoxSelectorPanel _comboPanel;
    private final DefaultComboBoxModel<CHOICE> _comboBoxModel;
    private final Container _panel;

    public WrappedComboBoxSelectorPanel(
            final @NotNull CController<CHOICE,PANEL> cController
    ) {
        super();

        _cController = cController;

        Vector<CHOICE> actualChoices = _cController.getActualChoices();

        setLayout( new BorderLayout() );
        if ( actualChoices.size() <= 1 && _cController.handleZeroAndOneCaseSpecially() ) {

            _degenerateCase = true;

            _comboPanel = null;
            _comboBoxModel = null;

            if ( actualChoices.isEmpty() ) {

                String msg = _cController.getDegenerateCaseMessage( DegenerateCase.NO_ENTITIES );
                if ( msg == null ) {

                    _panel = new BorderLayoutPanel();

                } else {

                    _panel = new JLabel( _cController.getDegenerateCaseMessage( DegenerateCase.NO_ENTITIES ) ); // "Nothing to choose from in " + _cController.getPanelName() );

                }

            } else {

                Box comboBoxSelectorPanel = new Box( BoxLayout.X_AXIS );

                comboBoxSelectorPanel.add( new JLabel( cController.getPickListPrompt() + "  " ) );

                JComboBox<CHOICE> comboBox = new JComboBox<>( new DefaultComboBoxModel<>( actualChoices ) );

                comboBoxSelectorPanel.add( comboBox );

                add( comboBoxSelectorPanel, BorderLayout.NORTH );

                CHOICE initialChoice = _cController.getInitialChoice();
                _panel = _cController.getPanel(
                        initialChoice == null ? actualChoices.firstElement() : initialChoice,
                        true
                );

            }

        } else {

            _degenerateCase = false;

            _comboBoxModel = new DefaultComboBoxModel<>( cController.getUnspecifiedAndActualChoices() );

            _comboPanel = new InnerWrappedComboBoxSelectorPanel( _comboBoxModel );

            _panel = _comboPanel;

        }

        add( _panel, BorderLayout.CENTER );

    }

    public WrappedComboBoxSelectorPanel( final @NotNull Container container ) {
        super();

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

    }

    public boolean isDegenerateCase() {

        return _degenerateCase;

    }

    public boolean setSubsidiaryPanelBorder( final Border border ){

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

    public class InnerWrappedComboBoxSelectorPanel extends ComboBoxSelectorPanel<CHOICE, PANEL> {

        private InnerWrappedComboBoxSelectorPanel(
                final @NotNull DefaultComboBoxModel<CHOICE> comboBoxModel
        ) {

            super(
//                    _cController.getPanelName(),
//                    _cController.getPickListPrompt(),
                    _cController,
                    comboBoxModel,
                    null,
                    choice -> {

                        PANEL panel = _cController.getPanel( choice, false );

                        return panel;

                    },
                    false

            );

            CHOICE initialChoice = _cController.getInitialChoice();
            getComboBoxModel().setSelectedItem( initialChoice == null ? getComboBoxModel().getElementAt( 0 ) : initialChoice );

        }

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

            }

            _comboBoxModel.setSelectedItem( newChoice );

            Logger.logMsg( "InnerWrappedComboBoxSelectorPanel.notifyCurrentChildChange:  from " + oldChoice + " to " + newChoice );

            return true;

        }

        public String toString() {

            return "InnerWrappedComboBoxSelectorPanel( " + ObtuseUtil.enquoteToJavaString( getName() ) + " )";

        }

    }

    public CHOICE getCurrentChoice() {

        @SuppressWarnings("unchecked") CHOICE rval = (CHOICE)_comboBoxModel.getSelectedItem();

        return rval;

    }

    public boolean notifyCurrentChildChange(
            final @Nullable CHOICE oldChoice,
            final @NotNull CHOICE newChoice
    ) {

        return _comboPanel.notifyCurrentChildChange( oldChoice, newChoice );

    }

//    @Override
//    public void fireListeners( final String who, final CHOICE item ) {
//
//        if ( _comboPanel != null ) {
//
//            _comboPanel.fireSelectionChangedListeners( who, item );
//
//        }
//
//    }

    @Override
    public @NotNull List<SimpleObtuseListenerManager.ListenerInfo> getAllListeners() {

        return _comboPanel == null ? new Vector<>() : _comboPanel.getAllListeners();
    }

    @Override
    public @NotNull Optional<SimpleObtuseListenerManager.ListenerInfo> findListenerByName( @NotNull final String name ) {

        return _comboPanel == null ? Optional.empty() : _comboPanel.findListenerByName( name );
    }

    @Override
    public boolean removeByName( @NotNull final String name ) {

        return _comboPanel != null && _comboPanel.removeByName( name );
    }

    public void addSelectionChangedListener(
            @NotNull final String name,
            @NotNull final SelectorPanel.SelectionChangedListener actionListener
    ) {

        if ( _comboPanel != null ) {

            _comboPanel.addSelectionChangedListener( name, actionListener );

        }

    }

//    public void addVetSelectionListener(
//            @NotNull final String name,
//            @NotNull final SelectorPanel.VetSelectionListener actionListener
//    ) {
//
//        if ( _comboPanel != null ) {
//
//            _comboPanel.addVetSelectionListener( name, actionListener );
//
//        }
//
//    }

    public String toString() {

        String toString = "WrappedComboBoxSelectorPanel( panel=" +
                   _panel +
                   ", comboBoxPanel=" +
                   ( _panel == _comboPanel && _panel != null ? "<same as panel>" : _comboPanel ) +
                   " )";
        return toString;

    }

}
