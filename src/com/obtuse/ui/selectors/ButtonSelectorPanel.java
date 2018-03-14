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
import java.util.function.Function;

/*
 */

/**
 Manage a selector of some sort (currently a JComboBox or a button group) that decides what goes into a panel.
 @param <C> The type of {@link Container} that will appear in the post-selection panel.
 */

public class ButtonSelectorPanel<C extends Container> extends SelectorPanel<AbstractButton,C> {

    private final ButtonGroup _buttonGroup;
    private final JPanel _buttonPanel;

    public ButtonSelectorPanel(
            final @NotNull String name,
            final @NotNull ButtonGroup buttonGroup,
            final @NotNull JPanel buttonPanel,
            @Nullable final JComponent firstSelection,
            final @NotNull Function<AbstractButton,C> componentGetter,
            final boolean cacheSelections
    ) {
        super( name + "(BSP #" + UniqueEntity.getDefaultIdGenerator().getUniqueId() + ")", firstSelection != null, cacheSelections, componentGetter );

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

                            Optional<C> optComponent = getSelectedComponent( ab );

                            if ( optComponent.isPresent() ) {

                                setPostSelectionPanelContents( "ButtonSelectorPanel.actionListener", optComponent.get() );

                                ButtonSelectorPanel.this.invalidate();
                                optComponent.get().invalidate();

                            }

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

    public String toString() {

        return "ButtonSelectorForm<" + getName() + ">()";

    }

}
