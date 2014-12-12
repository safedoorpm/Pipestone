/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.LinkedList;

@SuppressWarnings( { "FieldCanBeLocal" } )
public abstract class WindowWithMenus extends TrackedWindow {

    private final JMenuBar _menuBar;

    private final JMenu _fileMenu;

    private final JMenuItem _preferencesMenuItem;

    private JCheckBoxMenuItem _showLogsMenuItem;

    private static PreferencesHandler s_preferencesHandler;

    private static final Collection<WindowWithMenus> s_allWindowsWithLogsMenuItem = new LinkedList<WindowWithMenus>();
    private static boolean s_showLogsMode;

    @SuppressWarnings({ "ClassWithoutToString", "SameParameterValue" })
    protected WindowWithMenus( String windowPrefsName, boolean includeLogsMenuItem ) {
        super( windowPrefsName );

        _menuBar = new JMenuBar();

        _fileMenu = new JMenu( "File" );

        _preferencesMenuItem = new JMenuItem( "Preferences" );
        OSLevelCustomizations osLevelCustomizations = OSLevelCustomizations.getCustomizer();

//                new AboutWindowHandler() {
//
//                    public void makeVisible() {
//
//                        Logger.logMsg( "about window launch request ignored" );
//
//                    }
//
//                }
//        );

        if ( osLevelCustomizations == null || OSLevelCustomizations.onWindows() ) {

            _fileMenu.add( _preferencesMenuItem );
            _preferencesMenuItem.setAccelerator(
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    )
            );

//        } else {
//
//            osLevelCustomizations.setPreferencesHandler( this );

        }

        _preferencesMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {
                        handlePreferences();
                    }
                }
        );

        if ( includeLogsMenuItem ) {

            _showLogsMenuItem = new JCheckBoxMenuItem( "Show Log Messages" );

            _showLogsMenuItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent actionEvent ) {

                            if ( _showLogsMenuItem.getState() ) {

                                LogsWindow.launch();

                            } else {

                                LogsWindow.getInstance().setVisible( false );

                            }

                            WindowWithMenus.setAllShowLogsModeInMenu( _showLogsMenuItem.getState() );

                        }
                    }
            );

            _showLogsMenuItem.setState( WindowWithMenus.s_showLogsMode );

            _fileMenu.add( _showLogsMenuItem );
            _showLogsMenuItem.setAccelerator(
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    )
            );

            WindowWithMenus.s_allWindowsWithLogsMenuItem.add( this );

        }

        if ( !OSLevelCustomizations.onMacOsX() ) {

            JMenuItem exitItem = new JCheckBoxMenuItem( "Exit" );

            exitItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent actionEvent ) {

                            System.exit( 0 );

                        }
                    }
            );

            _fileMenu.add( exitItem );
        }

        _menuBar.add( _fileMenu );

        _menuBar.add( defineEditMenu() );

//        // Replace the coffee cup icon in Windows XP JFrames
//
////        Trace.event( "inserting logo into JFrames" );
//
//        ImageIcon icon = new ImageIcon( LoaLogo.LOA_LOGO_16x16 );
//
//        setIconImage( icon.getImage() );

//        Trace.event( "setting the menu" );

        setJMenuBar( _menuBar );

//        Trace.event( "constructed WindowWithMenus" );

    }

    private void handlePreferences() {

        if ( s_preferencesHandler != null ) {

            s_preferencesHandler.handlePreferences();

        }

    }

    /**
     * Set the preferences handler.
     * @param preferencesHandler the preferences handler (null if you don't want one).
     */

    @SuppressWarnings("UnusedDeclaration")
    public static void setPreferencesHandler( PreferencesHandler preferencesHandler ) {

        s_preferencesHandler = preferencesHandler;

        // This is the Mac way to set the preferences handler.
        // The windows way was handled as best we could handle it in our constructor above.
        //
        // There is no straightforward way to hide/show the preferences menu item if a separate menu item
        // exists in each and every instance of this class.  The not-straightforward way seems to require
        // the use of weak references.  I don't think that making the preferences menu items appear/disappear
        // is important enough to get involved with weak references.

        OSLevelCustomizations customizer = OSLevelCustomizations.getCustomizer();

        if ( customizer != null ) {

            customizer.setPreferencesHandler( preferencesHandler );

        }

    }

    public static void setAllShowLogsModeInMenu( boolean value ) {

        WindowWithMenus.s_showLogsMode = value;
        for ( WindowWithMenus window : WindowWithMenus.s_allWindowsWithLogsMenuItem ) {

            window.setShowLogsModeInMenu( value );

        }

    }

    public void setShowLogsModeInMenu( boolean value ) {

        if ( _showLogsMenuItem != null ) {

            _showLogsMenuItem.setState( value );

        }

    }

    /**
     * Create an Edit menu that has Cut, Copy and Paste items which are always disabled to ensure that the menu exists and 'looks nice' if
     * this window does not actually need an Edit menu.
     * <p/>
     * Override this method in your derived class if you want an Edit menu that actually accomplishes something.
     * @return this window's Edit menu.
     */

    protected JMenu defineEditMenu() {

        JMenu skeletalEditMenu = new JMenu( "Edit" );

        JMenuItem selectAllMenuItem = new JMenuItem( "Select All" );
        LogsWindow.setMenuEnabled( "WWM:dEM", selectAllMenuItem, false );
        selectAllMenuItem.setAccelerator(

                KeyStroke.getKeyStroke(
                        KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )

        );

        JMenuItem cutMenuItem = new JMenuItem( "Cut" );
        LogsWindow.setMenuEnabled( "WWM:dEM", cutMenuItem, false );
        cutMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );


        JMenuItem copyMenuItem = new JMenuItem( "Copy" );
        LogsWindow.setMenuEnabled( "WWM:dEM", copyMenuItem, false );
        copyMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        JMenuItem pasteMenuItem = new JMenuItem( "Paste" );
        LogsWindow.setMenuEnabled( "WWM:dEM", pasteMenuItem, false );
        pasteMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );


        skeletalEditMenu.add( cutMenuItem );
        skeletalEditMenu.add( copyMenuItem );
        skeletalEditMenu.add( pasteMenuItem );
        skeletalEditMenu.add( selectAllMenuItem );

        return skeletalEditMenu;

    }

}
