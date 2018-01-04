/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import java.awt.event.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A popup menu of words with which the word list can be filtered by typing, while the menu is visible,
 * prefixes of words to keep in the menu.
 */

public class AcceleratedPopupMenu1 extends JPopupMenu {

    @SuppressWarnings("FieldCanBeLocal")
    private JFrame _frame = new JFrame( "my frame" );
    private JPanel _panel = new JPanel();
    private StringBuilder _accelerator;
    private JMenuItem _firstMenuItem;
    private JMenuItem _secondMenuItem;
    private List<JMenuItem> _wordMenuItems;

    public AcceleratedPopupMenu1( @NotNull final String@NotNull [] words ) {
        super();

        _frame.setContentPane( _panel );

        _firstMenuItem = new JMenuItem( "first menu item" );
        add( _firstMenuItem );

        _secondMenuItem = new JMenuItem( "second menu item" );
        add( _secondMenuItem );

        _wordMenuItems = new LinkedList<>();

        for ( String word : words ) {

            JMenuItem item = new JMenuItem( word );
            item.addActionListener(
                    new MyActionListener() {

                        public void myActionPerformed( final ActionEvent actionEvent ) {

                            JMenuItem menuItem = (JMenuItem) actionEvent.getSource();

                            Logger.logMsg( "item selected:  \"" + menuItem.getText() + "\"" );

                        }

                    }
            );
            add( item );

            _wordMenuItems.add( item );

        }

        _panel.addMouseListener(
                new MouseAdapter() {

                    public void mousePressed( final MouseEvent e ) {

                        _accelerator = new StringBuilder();
                        setVisibility();
                        show(
                                _panel,
                                e.getX(),
                                e.getY()
                        );

                    }

                }
        );

        addMenuKeyListener(
                new MenuKeyListener() {

                    public void menuKeyTyped( final MenuKeyEvent keyEvent ) {

                        char keyChar = keyEvent.getKeyChar();
                        Logger.logMsg( "key typed \"" + keyChar + "\" + " + ObtuseUtil.hexvalue( ( "" + keyChar ).getBytes() ) );
                        if ( keyChar == '\b' || keyChar == 0x7f ) {

                            if ( _accelerator.length() > 0 ) {

                                _accelerator.deleteCharAt( _accelerator.length() - 1 );

                            }

                        } else if ( keyChar >= ' ' && keyChar <= '~' ) {

                            _accelerator.append( keyChar );

                        }

                        setVisibility();

                    }

                    public void menuKeyPressed( final MenuKeyEvent keyEvent ) {

                        Logger.logMsg( "key pressed \"" + keyEvent.getKeyChar() + "\"" );

                    }

                    public void menuKeyReleased( final MenuKeyEvent keyEvent ) {

                        Logger.logMsg( "key released \"" + keyEvent.getKeyChar() + "\"" );

                    }

                }
        );

        _frame.setVisible( true );

    }

    private void setVisibility() {

        int visibleCount = 0;
        String prefix = _accelerator.toString();

        Logger.logMsg( "accelerator = \"" + prefix + "\" (" + prefix.length() + " chars)" );

        for ( JMenuItem menuItem : _wordMenuItems ) {

            String word = menuItem.getText();
            if ( word.startsWith( prefix ) ) {

                menuItem.setVisible( true );
                visibleCount += 1;

            } else {

                menuItem.setVisible( false );

            }

        }

        _firstMenuItem.setText( visibleCount == 0 ? "No choices left" : "Type word prefix to accelerate" );
        _secondMenuItem.setText( "word prefix is \"" + _accelerator.toString() + "\"" );

        if ( visibleCount == 0 ) {

            _firstMenuItem.setVisible( true );

        } else {

            _firstMenuItem.setVisible( true );

        }

        WindowWithMenus.setMenuEnabled( "APM1:sV", _firstMenuItem, false );
        WindowWithMenus.setMenuEnabled( "APM1:sV", _secondMenuItem, false );

        pack();

    }

    public AcceleratedPopupMenu1( final Collection<String> words ) {
        this( words.toArray( new String[words.size()] ) );
    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Test", null );

        List<String> words = new LinkedList<>();
        for ( int i = 0; i < 100; i += 1 ) {

            words.add( VerbsList.pickVerb() );

        }


        @SuppressWarnings("UnusedDeclaration")
        AcceleratedPopupMenu1 apm = new AcceleratedPopupMenu1( words );

    }

}
