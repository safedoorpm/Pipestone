/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.OSLevelCustomizations;
import com.obtuse.util.PreferencesHandler;
import com.obtuse.util.Trace;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;

@SuppressWarnings( { "FieldCanBeLocal" } )
public class WindowWithMenus extends TrackedWindow {

    public static class BasicEditMenu extends JMenu {

        private JMenuItem _cutMenuItem;
        private JMenuItem _copyMenuItem;
        private JMenuItem _pasteMenuItem;
        private JMenuItem _selectAllMenuItem;

        public BasicEditMenu( final @NotNull String menuName ) {
            super( menuName );

        }

        public JMenuItem getCutMenuItem() {

            return _cutMenuItem;

        }

        public void setCutMenuItem( final JMenuItem cutMenuItem ) {

            if ( _cutMenuItem != null ) {

                remove( _cutMenuItem );

            }

            _cutMenuItem = cutMenuItem;
            add( cutMenuItem );

        }

        public JMenuItem getCopyMenuItem() {

            return _copyMenuItem;

        }

        public void setCopyMenuItem( final JMenuItem copyMenuItem ) {

            if ( _copyMenuItem != null ) {

                remove( _copyMenuItem );

            }

            _copyMenuItem = copyMenuItem;
            add( copyMenuItem );

        }

        public JMenuItem getPasteMenuItem() {

            return _pasteMenuItem;

        }

        public void setPasteMenuItem( final JMenuItem pasteMenuItem ) {

            if ( _pasteMenuItem != null ) {

                remove( _pasteMenuItem );

            }

            _pasteMenuItem = pasteMenuItem;
            add( pasteMenuItem );

        }

        public JMenuItem getSelectAllMenuItem() {

            return _selectAllMenuItem;

        }

        public void setSelectAllMenuItem( final JMenuItem selectAllMenuItem ) {

            if ( _selectAllMenuItem != null ) {

                remove( _selectAllMenuItem );

            }

            _selectAllMenuItem = selectAllMenuItem;
            add( selectAllMenuItem );

        }

        public String toString() {

            return "BasicEditMenu()";

        }

    }

    private final JMenuBar _menuBar;

    private final JMenu _fileMenu;

    private final BasicEditMenu _editMenu;

    private final JMenuItem _preferencesMenuItem;

    private JCheckBoxMenuItem _showLogsMenuItem;

    private static PreferencesHandler s_preferencesHandler;

    private static final Collection<WindowWithMenus> s_allWindowsWithLogsMenuItem = new LinkedList<>();
    private static boolean s_showLogsMode;

    @SuppressWarnings({ "ClassWithoutToString", "SameParameterValue" })
    public WindowWithMenus( final String windowPrefsName, final boolean includeLogsMenuItem ) {
        super( windowPrefsName );

        _menuBar = new JMenuBar();

        _fileMenu = new JMenu( "File" );

        _preferencesMenuItem = new JMenuItem( "Preferences" );
        OSLevelCustomizations osLevelCustomizations = OSLevelCustomizations.getCustomizer();

        if ( osLevelCustomizations == null || OSLevelCustomizations.onWindows() ) {

            _fileMenu.add( _preferencesMenuItem );
            _preferencesMenuItem.setAccelerator(
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    )
            );

        }

        _preferencesMenuItem.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        handlePreferences();

                    }

                }
        );

        if ( includeLogsMenuItem ) {

            _showLogsMenuItem = new JCheckBoxMenuItem( "Show Log Messages" );

            _showLogsMenuItem.addActionListener(
                    new MyActionListener() {

                        public void myActionPerformed( final ActionEvent actionEvent ) {

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
                    new MyActionListener() {

                        public void myActionPerformed( final ActionEvent actionEvent ) {

                            System.exit( 0 );

                        }

                    }
            );

            _fileMenu.add( exitItem );
        }

        _menuBar.add( _fileMenu );

        _editMenu = defineEditMenu();

        _menuBar.add( _editMenu );

        setJMenuBar( _menuBar );

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
    public static void setPreferencesHandler( final PreferencesHandler preferencesHandler ) {

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

    /**
     Utility method to trace the setting of a menu item's enabled state.
     <p/>This method lives in this class instead of say the Trace class because adding this method to the
     Trace class would result in any program that uses the Trace class implicitly sucking in a huge chunk of Swing.
     Since this class already uses Swing, putting the method here does no real harm.

     @param context  where this is being done.
     @param menuItem the menu item in question.
     @param value    its new state.
     */

    public static void setMenuEnabled( final String context, final JMenuItem menuItem, final boolean value ) {

        String text = menuItem.getText();
        if ( text == null ) {

            text = "<unknown>";

        }

        Trace.event( ( context == null ? "" : context + ":  " ) + "menu item \"" + text + "\" set to " + ( value ? "enabled" : "not enabled" ) );

        menuItem.setEnabled( value );

    }

    public static void setAllShowLogsModeInMenu( final boolean value ) {

        WindowWithMenus.s_showLogsMode = value;
        for ( WindowWithMenus window : WindowWithMenus.s_allWindowsWithLogsMenuItem ) {

            window.setShowLogsModeInMenu( value );

        }

    }

    public void setShowLogsModeInMenu( final boolean value ) {

        if ( _showLogsMenuItem != null ) {

            _showLogsMenuItem.setState( value );

        }

    }

    @NotNull
    protected BasicEditMenu getEditMenu() {

        return _editMenu;

    }

    /**
     * Create an Edit menu that has Cut, Copy, Paste and Select All items which are always disabled to ensure that the menu exists and 'looks nice' if
     * this window does not actually need an Edit menu.
     * <p/>
     * Override this method in your derived class if you want an Edit menu that actually accomplishes something.
     * @return a {@link BasicEditMenu} defining a simple Edit menu.
     */

    protected BasicEditMenu defineEditMenu() {

        BasicEditMenu skeletalEditMenu = new BasicEditMenu( "Edit" );

        skeletalEditMenu.setCutMenuItem( createMenuItem( "Cut", KeyEvent.VK_X ) );

        skeletalEditMenu.setCopyMenuItem( createMenuItem( "Copy", KeyEvent.VK_C ) );

        skeletalEditMenu.setPasteMenuItem( createMenuItem( "Paste", KeyEvent.VK_V ) );

        skeletalEditMenu.setSelectAllMenuItem( createMenuItem( "Select All", KeyEvent.VK_A ) );

        return skeletalEditMenu;

    }

    @NotNull
    private static JMenuItem createMenuItem( final String operationName, final int keyEvent ) {

        JMenuItem menuItem = new JMenuItem( operationName );
        setMenuEnabled( "WWM:dEM", menuItem, false );
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        return menuItem;

    }

    public String toString() {

        return "WindowWithMenus()";

    }

}
