/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Function;

/*
 */

/**
 Manage a selector of some sort (currently a JComboBox or a button group) that decides what goes into a panel.
 @param <C> The type of {@link Container} that will appear in the post-selection panel.
 */

public class ButtonSelectorPanel<CHOICE,C extends JPanel> extends SelectorPanel<CHOICE,C> {

    private final ButtonGroup _buttonGroup;
    private final JPanel _buttonPanel;

    public ButtonSelectorPanel(
//            final @NotNull String name,
            final WrappedButtonSelectorPanel.IController<CHOICE,C> iController,
            final @NotNull ButtonGroup buttonGroup,
            final @NotNull JPanel buttonPanel,
            final @NotNull SortedMap<CHOICE, JRadioButton> buttonMap,
            final @Nullable JComponent firstSelection,
            final @NotNull Function<CHOICE,C> componentGetter,
            final boolean cacheSelections
    ) {
        super(
                iController.getPanelName() +
                "(BSP #" + UniqueEntity.getDefaultIdGenerator().getUniqueId() + ")",
                firstSelection != null,
                cacheSelections,
                componentGetter
        );

        setName( getOurName() );

        _buttonPanel = buttonPanel;
        if ( _buttonPanel.getName() == null ) {

            _buttonPanel.setName( "_buttonPanel" );

        }

        _buttonGroup = buttonGroup;

        Enumeration<AbstractButton> enumeration = _buttonGroup.getElements();
        while ( enumeration.hasMoreElements() ) {

            AbstractButton ab = enumeration.nextElement();

            ab.addActionListener(
                    new MyActionListener() {
                        @Override
                        protected void myActionPerformed( final ActionEvent actionEvent ) {

                            clearPostSelectionPanelContents();
                            @SuppressWarnings("unchecked") CHOICE choice =
                                    (CHOICE)ButtonSelectorPanel.lookupChoice( ab, buttonMap );
//                            iController.reportChoice( choice );
                            Optional<C> optComponent = getSelectedComponent( choice );

                            if ( optComponent.isPresent() ) {

                                setPostSelectionPanelContents( "ButtonSelectorPanel.actionListener", optComponent.get() );

                                ButtonSelectorPanel.this.invalidate();
                                optComponent.get().invalidate();

                            }

                            fireSelectionChangedListeners(
                                    "ButtonSelectorPanel",
                                    "selection changed to " + ObtuseUtil.enquoteJavaObject( choice ),
                                    choice
                            );

                        }

                    }
            );

        }

        add( _buttonPanel, BorderLayout.NORTH );

        if ( isZeroASelection() ) {

            setPostSelectionPanelContents( "ButtonSelectorPanel.isZeroASelection", firstSelection );

        }

        ObtuseUtil.doNothing();

    }

    @SuppressWarnings("unused")
    public ButtonGroup getButtonGroup() {

        return _buttonGroup;

    }

    @SuppressWarnings("unused")
    public JPanel getButtonPanel() {

        return _buttonPanel;

    }

    public static Object lookupChoice( final AbstractButton ab, final @NotNull SortedMap<?, JRadioButton> buttonMap ) {

        Object correctChoice = null;
        for ( Object choice : buttonMap.keySet() ) {

            JRadioButton button = buttonMap.get( choice );
            if ( button.equals( ab ) ) {

                correctChoice = choice;

                break;

            }

        }

        if ( correctChoice == null ) {

            throw new IllegalArgumentException( "WrappedButtonSelectorPanel.apply:  cannot find button " + ab + " in the button map" );

        }

        return correctChoice;

    }

    public String toString() {

        return "ButtonSelectorForm<" + getName() + ">()";

    }

}
