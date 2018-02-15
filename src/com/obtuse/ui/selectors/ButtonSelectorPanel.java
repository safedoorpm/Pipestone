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

//    private final boolean _isZeroASelection;

    private final ButtonGroup _buttonGroup;
    private final JPanel _buttonPanel;
//    private final Function<E, C> _componentGetter;
    @SuppressWarnings("FieldCanBeLocal") private final BorderLayout _borderLayout;

//    private final Box _comboBoxSelectorPanel = new Box( BoxLayout.X_AXIS );

    private final JPanel _postSelectionPanel;
//    private final JLabel _selectorLabel;
//    private final BoxLayout _boxLayout;
//    private final Function<String, C> _comboBoxComponentGetter;
//    private final Function<String, C> _buttonComponentGetter;
//    private final SortedMap<String,C> _selectionCache;

//    /**
//     @param name
//     @param selectorLabel
//     @param model
//     @param firstSelection
//     @param componentGetter
//     */
//    public ButtonSelectorPanel(
//            final @NotNull String name,
//            final @NotNull String selectorLabel,
//            final @NotNull DefaultComboBoxModel<E> model,
//            @Nullable final C firstSelection,
//            final @NotNull Function<E,C> componentGetter
//    ) {
//        super();
//
//        setLayout( new BorderLayout() );
//
//        _comboBoxComponentGetter = componentGetter;
//        _buttonComponentGetter = null;
//
//        setName( name );
//
//        _comboBoxModel = model;
//
//        _boxLayout = new BoxLayout( _comboBoxSelectorPanel, BoxLayout.X_AXIS );
////        _comboBoxSelectorPanel.setLayout( _boxLayout );
//
//        _selectorLabel = new JLabel( selectorLabel );
//        _comboBoxSelectorPanel.add( _selectorLabel );
//
//        _selectorComboBox = new JComboBox<>( _comboBoxModel );
//        _selectorComboBox.addActionListener(
//                new MyActionListener() {
//                    @Override
//                    protected void myActionPerformed( final ActionEvent actionEvent ) {
//
//                        _postSelectionPanel.removeAll();
//                        C component = _comboBoxComponentGetter.apply( _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() ) );
//                        if ( component != null ) {
//
//                            _postSelectionPanel.add( component, BorderLayout.CENTER );
//
//                        }
//                        revalidate();
//
//                    }
//
//                }
//        );
//        _comboBoxSelectorPanel.add( _selectorComboBox );
//
//        add( _comboBoxSelectorPanel, BorderLayout.NORTH );
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
//        _buttonGroup = null;
//        _buttonPanel = null;
//
//    }

    public ButtonSelectorPanel(
            final @NotNull String name,
//            final @NotNull String selectorLabel,
            final @NotNull ButtonGroup buttonGroup,
            final @NotNull JPanel buttonPanel,
            @Nullable final JComponent firstSelection,
            final @NotNull Function<AbstractButton,C> componentGetter,
            final boolean cacheSelections
    ) {
        super( firstSelection != null, cacheSelections, componentGetter );

//        _selectionCache = cacheSelections ? new TreeMap<>() : null;

        setLayout( new BorderLayout() );

//        _buttonComponentGetter = componentGetter;

        setName( name );

//        _boxLayout = null;
//        _comboBoxSelectorPanel.setLayout( _boxLayout );

//        _comboBoxSelectorPanel.add( _selectorLabel );

        _buttonPanel = buttonPanel;
        _buttonGroup = buttonGroup;

        Enumeration<AbstractButton> enumeration = _buttonGroup.getElements();
        while ( enumeration.hasMoreElements() ) {

            AbstractButton ab = enumeration.nextElement();

            ab.addActionListener(
                    new MyActionListener() {
                        @Override
                        protected void myActionPerformed( final ActionEvent actionEvent ) {

                            _postSelectionPanel.removeAll();

//                            String key = ab.getText();
                            Optional<C> optComponent = getSelectedComponent( ab );

                            optComponent.ifPresent( c -> _postSelectionPanel.add( c, BorderLayout.CENTER ) );

                            revalidate();
//
//                            _postSelectionPanel.removeAll();
//
//                            String key = ab.getText();
//
//                            C component = null;
//                            if ( _selectionCache != null ) {
//
//                                component = _selectionCache.get( key );
//
//                            }
//
//                            if ( component == null ) {
//
//                                component = _comboBoxComponentGetter.apply( key );
//
//                                if ( component == null ) {
//
//                                    throw new HowDidWeGetHereError(
//                                            "ComboBoxSelectorPanel.actionListener:  cache does not contain " +
//                                            ObtuseUtil.enquoteToJavaString( String.valueOf( key ) ) + " component getter yielded null" );
//
//                                }
//
//                            }
//
//
//                            if ( _selectionCache != null && !_selectionCache.containsKey( key ) ) {
//
//                                _selectionCache.put( key, component );
//
//                            }
//
//                            _postSelectionPanel.add( component, BorderLayout.CENTER );
//
//                            revalidate();
//
//
//                            C component = _buttonComponentGetter.apply( ab.getText() );
//                            if ( component != null ) {
//
//                                _postSelectionPanel.add( component, BorderLayout.CENTER );
//
//                            }
//                            revalidate();

                        }

                    }
            );

        }

//        _buttonGroup.addActionListener(
//                new MyActionListener() {
//                    @Override
//                    protected void myActionPerformed( final ActionEvent actionEvent ) {
//
//                        _postSelectionPanel.removeAll();
//                        C component = _componentGetter.apply( _selectorComboBox.getItemAt( _selectorComboBox.getSelectedIndex() ) );
//                        if ( component != null ) {
//
//                            _postSelectionPanel.add( component, BorderLayout.CENTER );
//
//                        }
//                        revalidate();
//
//                    }
//
//                }
//        );
//        _comboBoxSelectorPanel.add( _selectorComboBox );

        add( _buttonPanel, BorderLayout.NORTH );

        _borderLayout = new BorderLayout();
        _postSelectionPanel = new JPanel( _borderLayout );
        add( _postSelectionPanel, BorderLayout.CENTER );

        if ( isZeroASelection() ) {

            _postSelectionPanel.setVisible( true );
            _postSelectionPanel.add( firstSelection );

        }

//        add( _postSelectionPanel );

//        _selectorLabel = null;
//        _comboBoxComponentGetter = null;


    }

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

//    public static void main( String[] args ) {
//
//        BasicProgramConfigInfo.init( "Obtuse", "ComboBoxSelectorPanel", "testing", null );
//
////        String[] comboChoices = new String[] { "unselected", "choice 1", "choice 2" };
//
//        JFrame jf = new JFrame( "Testing SelectorForm" );
//        Function<String, JPanel> function = new Function<String,JPanel>() {
//
//            @Override
//            public JPanel apply( final String s ) {
//
//                JPanel jp = new JPanel();
//                jp.setBorder( BorderFactory.createLineBorder( Color.RED ) );
//                jp.add( new JLabel( s ) );
//                return jp;
//
//            }
//
//        };
//        JPanel sf = createSelectorForm( function, false );
//        jf.setContentPane( sf );
//
//        jf.pack();
//        jf.setVisible( true );
//
//    }

}
