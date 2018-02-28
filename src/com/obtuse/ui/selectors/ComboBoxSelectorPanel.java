/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.UniqueEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 */

/**
 Manage a {@link JComboBox}{@code <E>} that decides what goes into an associated panel.
 @param <E> the class of things in the {@code JComboBox<E>}'s model.
 @param <C> the class of things, derived from {@link Container}, which appear in the associated panel.
 */

public class ComboBoxSelectorPanel<E,C extends Container> extends SelectorPanel<E,C> {

    private final DefaultComboBoxModel<E> _comboBoxModel;
    private final JComboBox<E> _selectorComboBox;

    @SuppressWarnings("FieldCanBeLocal") private final Box _comboBoxSelectorPanel = new Box( BoxLayout.X_AXIS );

    @SuppressWarnings("FieldCanBeLocal") private final JLabel _selectorLabel;

    /**
     @param name the name of this selector panel (becomes the name of the JPanel that we are derived from; intended to be used for debugging purposes).
     @param selectorLabel the label in front of the {@link JComboBox}{@code <E>}.
     @param model the {@link JComboBox}'s {@link DefaultComboBoxModel}{@code <E>}.
     @param firstSelection the {@link Container} that is the initial selection.
     If {@code null} then the first item in the specified {@code model} is assumed to have no {@code Container} associated with it.
     @param componentGetter a {@link Function}{@code <E,C>} to retrieve the {@code <C>} associated with a specified {@code <E>}.
     @param cacheSelections {@code true} if {@code componentGetter.apply(E selection)} should be called not more than once for each
     distinct {@code selection} (this requires that this instance cache the values return by each call to {@code componentGetter.apply(E selection)}
     with a distinct {@code selection});
     {@code false} if {@code componentGetter.apply(E selection)} should be called for each selection made by the human.
     <p>Note that a selection by the human that selects what was already selected is always ignored.</p>
     */

    public ComboBoxSelectorPanel(
            final @NotNull String name,
            final @NotNull String selectorLabel,
            final @NotNull DefaultComboBoxModel<E> model,
            @Nullable final C firstSelection,
            final @NotNull Function<E,C> componentGetter,
            final boolean cacheSelections
    ) {
        super( "comboBox selector panel " + UniqueEntity.getDefaultIdGenerator().getUniqueId(), firstSelection != null, cacheSelections, componentGetter );

        setName( name );

        _comboBoxModel = model;

        _selectorLabel = new JLabel( selectorLabel + "  " );
        _comboBoxSelectorPanel.add( _selectorLabel );

        _selectorComboBox = new JComboBox<>( _comboBoxModel );
        _selectorComboBox.addActionListener(
                new MyActionListener() {
                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        clearPostSelectionPanelContents();

                        E key = _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() );

                        Optional<C> optComponent = getSelectedComponent( key );
                        optComponent.ifPresent(
                                new Consumer<C>() {
                                    @Override
                                    public void accept( final C c ) {

                                        setPostSelectionPanelContents( "ComboBoxSelectorPanel.actionListener", c );

                                    }

                                }
                        );

                        revalidate();

                    }

                }
        );

        _comboBoxSelectorPanel.add( _selectorComboBox );

        add( _comboBoxSelectorPanel, BorderLayout.NORTH );

        if ( isZeroASelection() ) {

            cacheSelection( _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() ), firstSelection );

            setPostSelectionPanelContents( "ComboBoxSelectorPanel.isZeroASelection", firstSelection );

        }

    }

    @SuppressWarnings("unused")
    public DefaultComboBoxModel<E> getComboBoxModel() {

        return _comboBoxModel;
    }

    public String toString() {

        return "ComboBoxSelectorForm<" + getName() + ">()";

    }

}
