/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.ui.MyActionListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
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
//    private final boolean _isZeroASelection;

//    @SuppressWarnings("FieldCanBeLocal") private final BorderLayout _borderLayout;

    @SuppressWarnings("FieldCanBeLocal") private final Box _comboBoxSelectorPanel = new Box( BoxLayout.X_AXIS );

    private final JPanel _postSelectionPanel;
    @SuppressWarnings("FieldCanBeLocal") private final JLabel _selectorLabel;
//    @SuppressWarnings("FieldCanBeLocal") private final BoxLayout _boxLayout;
//    private final Function<E, C> _comboBoxComponentGetter;

//    private final SortedMap<E,C> _selectionCache;

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
        super( firstSelection != null, cacheSelections, componentGetter );

        setLayout( new BorderLayout() );

//        _comboBoxComponentGetter = componentGetter;

        setName( name );

        _comboBoxModel = model;

        _selectorLabel = new JLabel( selectorLabel );
        _comboBoxSelectorPanel.add( _selectorLabel );

        _selectorComboBox = new JComboBox<>( _comboBoxModel );
        _selectorComboBox.addActionListener(
                new MyActionListener() {
                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        _postSelectionPanel.removeAll();

                        E key = _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() );

                        Optional<C> optComponent = getSelectedComponent( key );
                        optComponent.ifPresent( c -> _postSelectionPanel.add( c, BorderLayout.CENTER ) );

                        revalidate();

                    }

                }
        );
        _comboBoxSelectorPanel.add( _selectorComboBox );

        add( _comboBoxSelectorPanel, BorderLayout.NORTH );

//        _borderLayout = new BorderLayout();
        _postSelectionPanel = new JPanel( new BorderLayout() );
        add( _postSelectionPanel, BorderLayout.CENTER );
        if ( isZeroASelection() ) {

            cacheSelection( _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() ), firstSelection );

//            if ( _selectionCache != null ) {
//
//                _selectionCache.put( _comboBoxModel.getElementAt( 0 ), firstSelection );
//
//            }

            _postSelectionPanel.setVisible( true );
            _postSelectionPanel.add( firstSelection );

        }

        add( _postSelectionPanel );

    }

    @SuppressWarnings("unused")
    public DefaultComboBoxModel<E> getComboBoxModel() {

        return _comboBoxModel;
    }

//    public ComboBoxSelectorPanel(
//            final @NotNull String name,
////            final @NotNull String selectorLabel,
//            final @NotNull ButtonGroup buttonGroup,
//            final @NotNull JPanel buttonPanel,
//            @Nullable final JComponent firstSelection,
//            final @NotNull Function<String,C> componentGetter
//    ) {
//        super();
//
//        setLayout( new BorderLayout() );
//
//        _buttonComponentGetter = componentGetter;
//
//        setName( name );
//
//        _comboBoxModel = null;
//        _selectorComboBox = null;
//
//        _boxLayout = null;
////        _comboBoxSelectorPanel.setLayout( _boxLayout );
//
////        _comboBoxSelectorPanel.add( _selectorLabel );
//
//        _buttonPanel = buttonPanel;
//        _buttonGroup = buttonGroup;
//
//        Enumeration<AbstractButton> enumeration = _buttonGroup.getElements();
//        while ( enumeration.hasMoreElements() ) {
//
//            AbstractButton ab = enumeration.nextElement();
//
//            ab.addActionListener(
//                    new MyActionListener() {
//                        @Override
//                        protected void myActionPerformed( final ActionEvent actionEvent ) {
//
//                            _postSelectionPanel.removeAll();
//                            C component = _buttonComponentGetter.apply( ab.getText() );
//                            if ( component != null ) {
//
//                                _postSelectionPanel.add( component, BorderLayout.CENTER );
//
//                            }
//                            revalidate();
//
//                        }
//
//                    }
//            );
//
//        }
//
////        _buttonGroup.addActionListener(
////                new MyActionListener() {
////                    @Override
////                    protected void myActionPerformed( final ActionEvent actionEvent ) {
////
////                        _postSelectionPanel.removeAll();
////                        C component = _componentGetter.apply( _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() ) );
////                        if ( component != null ) {
////
////                            _postSelectionPanel.add( component, BorderLayout.CENTER );
////
////                        }
////                        revalidate();
////
////                    }
////
////                }
////        );
////        _comboBoxSelectorPanel.add( _selectorComboBox );
//
//        add( _buttonPanel, BorderLayout.NORTH );
//
//        _borderLayout = new BorderLayout();
//        _postSelectionPanel = new JPanel( _borderLayout );
//        _isZeroASelection = firstSelection != null;
//        if ( _isZeroASelection ) {
//
//            _postSelectionPanel.setVisible( true );
//            _postSelectionPanel.add( firstSelection );
//
//        }
//
//        add( _postSelectionPanel );
//
//        _selectorLabel = null;
//        _comboBoxComponentGetter = null;
//
//
//    }

//    public ComboBoxSelectorPanel(
//            final @NotNull String name,
//            final @NotNull ButtonGroup buttonGroup,
//            final @NotNull JPanel buttonPanel,
//            final boolean isZeroASelection,
//            final @NotNull Function<E,JComponent> componentGetter
//    ) {
//        super();
//
//        setName( name );
//
//        _buttonGroup = buttonGroup;
//        _buttonPanel = buttonPanel;
//
//        _comboBoxModel = null;
//        _selectorComboBox = null;
//
//        _borderLayout = new BorderLayout();
//
//        _isZeroASelection = isZeroASelection;
//        _postSelectionPanel = new JPanel();
//        _postSelectionPanel.setVisible( _isZeroASelection );
//
//        _componentGetter = componentGetter;
//
//    }

    public String toString() {

        return "ComboBoxSelectorForm<" + getName() + ">()";

    }

//    public static void main( final String[] args ) {
//
//        BasicProgramConfigInfo.init( "Obtuse", "ComboBoxSelectorPanel", "testing", null );
//
////        String[] comboChoices = new String[] { "unselected", "choice 1", "choice 2" };
//
//        JFrame jf = new JFrame( "Testing SelectorForm" );
//        Function<String, JPanel> function = s -> {
//
//            JPanel jp = new JPanel();
//            jp.setBorder( BorderFactory.createLineBorder( Color.RED ) );
//            jp.add( new JLabel( s ) );
//            return jp;
//
//        };
//
//        Vector<String> choices = new Vector<>();
//        Collections.addAll( choices, "alpha", "beta", "gamma" );
//        SelectorPanel<String,JPanel> sf = SelectorPanel.createSelectorForm( function, choices, true, false );
//        jf.setContentPane( sf );
//
//        jf.pack();
//        jf.setVisible( true );
//
//    }

//    @NotNull
//    private static ComboBoxSelectorPanel<String, JPanel> createSelectorForm( final Function<String, JPanel> function, final boolean doComboBox ) {
//
//        Vector<String> comboChoices = new Vector<>();
//        comboChoices.add( "unselected" );
//        comboChoices.add( "choice 1" );
//        comboChoices.add( "choice 2" );
//
//        ComboBoxSelectorPanel<String, JPanel> sf;
//        if ( doComboBox ) {
//
//            Vector<String> strings = new Vector<>( comboChoices );
//            sf =
//                    new ComboBoxSelectorPanel<String, JPanel>(
//                            "combo box selector",
//                            "Pick something",
//                            new DefaultComboBoxModel<String>( strings ),
//                            null,
//                            function
////                new Function<String, JComponent>() {
////                    @Override
////                    public JComponent apply( final String s ) {
////
////                        return null;
////                    }
////                }
//
////                new DefaultComboBoxModel<String>( comboChoices ),
////                function,
////                false
//                    );
//
//        } else {
//
//            JPanel buttonPanel = new JPanel();
//            ButtonGroup buttonGroup = new ButtonGroup();
//            for ( String label : new String[]{ "hello", "there", "world" } ) {
//
//                JRadioButton button = new JRadioButton( label );
//                buttonGroup.add( button );
//                buttonPanel.add( button );
//
//            }
//
//            sf =
//                    new ButtonSelectorPanel<String, JPanel>(
//                            "button group selector",
//                            buttonGroup,
//                            buttonPanel,
//                            new JLabel( "Pick something" ),
//                            function
//                    );
//
//        }
//
//        return sf;
//
//    }

}
