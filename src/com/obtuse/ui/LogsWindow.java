/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class LogsWindow extends WindowWithMenus {

    private JPanel _contentPane;

    @SuppressWarnings( { "UnusedDeclaration" } )
    private JScrollPane _messageWindowScrollPane;

    private JList _messageWindowList;

    private JButton _closeButton;

    private final DefaultListModel _messagesListModel = new DefaultListModel();

    private static final Long WINDOW_LOCK = 0L;

    @SuppressWarnings( { "FieldAccessedSynchronizedAndUnsynchronized" } )
    private static LogsWindow s_logsWindow = null;

    private static DateFormat s_dateFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private static boolean s_useHTML = false;

    public static final int MAX_LINES_IN_MESSAGE_WINDOW = 1000;

//    public static final int LOGO_SIZE = 80;

    @SuppressWarnings("FieldCanBeLocal")
    private JMenu _editMenu;
    private JMenuItem _copyMenuItem;

    @SuppressWarnings("CanBeFinal")
    private Clipboard _systemClipboard;

//    private static final String WINDOW_NAME = "LogsWindow";

//    private static ImageIcon _loaLogo = BasicIconLogoHandler.fetchIconImage("loa_logo.png", LOGO_SIZE);

    public LogsWindow( String windowPrefsName ) {
        super( windowPrefsName, true );

        _systemClipboard = getToolkit().getSystemClipboard();

        setContentPane( _contentPane );

        // Handle the close button.

        //noinspection ClassWithoutToString
        _closeButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {

                        WindowWithMenus.setAllShowLogsModeInMenu( false );
                        setVisible( false );

                    }
                }
        );

        setTitle( BasicProgramConfigInfo.getApplicationName() + " Log Messages" );

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

        //noinspection RefusedBequest,ClassWithoutToString
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent e ) {

                        WindowWithMenus.setAllShowLogsModeInMenu( false );
                    }
                }
        );

        _messageWindowList.setModel( _messagesListModel );
        _messageWindowList.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        _messageWindowList.addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged( ListSelectionEvent listSelectionEvent ) {

                        if ( !listSelectionEvent.getValueIsAdjusting() ) {

                            try {

                                JList list = (JList)listSelectionEvent.getSource();
                                int[] selectedIndices = list.getSelectedIndices();

                                if ( selectedIndices.length > 0 ) {

                                    setMenuEnabled( "LW:LW", _copyMenuItem, true );

                                } else {

                                    setMenuEnabled( "LW:LW", _copyMenuItem, false );

                                }

                            } catch ( ClassCastException e ) {

                                Logger.logErr(
                                        "unexpected object type in log message window's selection listener (" +
                                        listSelectionEvent.getSource().getClass() + ") - selection ignored"
                                );

                            }

                        }

                    }

                }
        );

        WindowWithMenus.setAllShowLogsModeInMenu( false );
        setVisible( false );

        pack();

        restoreWindowGeometry( getWidth(), getHeight() );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public static void addMessage( String msg ) {

        LogsWindow.addMessage( new Date(), msg );

    }

    public static void addMessage( Date when, final String msg ) {

        final String timeStampedMessage = LogsWindow.s_dateFormatter.format( when ) + ":  " + msg;
        if ( SwingUtilities.isEventDispatchThread() ) {

            LogsWindow.getInstance().insertMessageAtEnd( timeStampedMessage );

        } else {

        //noinspection ClassWithoutToString
            SwingUtilities.invokeLater(

                    new Runnable() {

                        public void run() {
                            try {

                                LogsWindow.getInstance().insertMessageAtEnd( timeStampedMessage );

                            } catch ( RuntimeException e ) {

                                Logger.logErr( "unable to insert message \"" + msg + "\" into log messages", e );

                            }

                        }

                    }

            );

        }

    }

    /**
     * Utility method to trace the setting of a menu item's enabled state.
     * <p/>This method lives in this class instead of say the Trace class because adding this method to the
     * Trace class would result in any program that uses the Trace class implicitly sucking in a huge chunk of Swing.
     * Since this class already uses Swing, putting the method here does no real harm.
     * @param context where this is being done.
     * @param menuItem the menu item in question.
     * @param value its new state.
     */

    public static void setMenuEnabled( String context, JMenuItem menuItem, boolean value ) {

        String text = menuItem.getText();
        if ( text == null ) {

            text = "<unknown>";

        }

        Trace.event( ( context == null ? "" : context + ":  " ) + "menu item \"" + text + "\" set to " + ( value ? "enabled" : "not enabled" ) );

        menuItem.setEnabled( value );

    }

//    public static void setMenuEnabled( JMenuItem menuItem, boolean value ) {
//
//        setMenuEnabled( null, menuItem, value );
//
//    }

    /**
     * Utility method to trace the setting of a button's enabled state.
     * <p/>This method lives in this class instead of say the Trace class because adding this method to the
     * Trace class would result in any program that uses the Trace class implicitly sucking in a huge chunk of Swing.
     * Since this class already uses Swing, putting the method here does no real harm.
     * @param button the menu item in question.
     * @param value its new state.
     */

    public static void setButtonEnabled( JButton button, boolean value ) {

        String text = button.getText();
        if ( text == null ) {

            text = "<unknown>";

        }

        Trace.event( "button \"" + text + "\" set to " + ( value ? "enabled" : "not enabled" ) );

        button.setEnabled( value );

    }

    /**
     * Utility method to trace the setting of a label's enabled state.
     * <p/>This method lives in this class instead of say the Trace class because adding this method to the
     * Trace class would result in any program that uses the Trace class implicitly sucking in a huge chunk of Swing.
     * Since this class already uses Swing, putting the method here does no real harm.
     * @param label the menu item in question.
     * @param value its new state.
     */

    public static void setLabelEnabled( JLabel label, boolean value ) {

        String text = label.getText();
        if ( text == null ) {

            text = "<unknown>";

        }

        Trace.event( "label \"" + text + "\" set to " + ( value ? "enabled" : "not enabled" ) );

        label.setEnabled( value );

    }

    private void insertMessageAtEnd( String timeStampedMessage ) {

        int listSize = _messagesListModel.getSize();
        if ( listSize >= LogsWindow.MAX_LINES_IN_MESSAGE_WINDOW ) {

            _messagesListModel.remove( 0 );
            listSize -= 1;

        }

        int lastVisibleIx = _messageWindowList.getLastVisibleIndex();

        if ( LogsWindow.s_useHTML ) {

            _messagesListModel.addElement( "<html><tt>" + ObtuseUtil.htmlEscape( timeStampedMessage ) + "</tt></html>" );

        } else {

            _messagesListModel.addElement( timeStampedMessage );

        }

        if ( lastVisibleIx + 1 == listSize ) {

            _messageWindowList.ensureIndexIsVisible( lastVisibleIx + 1 );

        }

        WindowWithMenus.setAllShowLogsModeInMenu( true );
        setVisible( true );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public void setUseHTML( boolean useHTML ) {

        //noinspection AssignmentToStaticFieldFromInstanceMethod
        LogsWindow.s_useHTML = useHTML;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public boolean useHTML() {

        return LogsWindow.s_useHTML;

    }

    public static LogsWindow getInstance() {

        synchronized ( LogsWindow.WINDOW_LOCK ) {

            if ( LogsWindow.s_logsWindow == null ) {

                if ( BasicProgramConfigInfo.getApplicationName() == null ) {

                    OkPopupMessageWindow.fatal(
                            "Application has not registered its name using BasicProgramConfigInfo.",
                            "Unable to continue.",
                            "I Will Submit A Bug Report"
                    );

                }

                if ( BasicProgramConfigInfo.getPreferences() == null ) {

                    OkPopupMessageWindow.fatal(
                            "Application has not registered its preferences object using BasicProgramConfigInfo.",
                            "Unable to continue.",
                            "I Will Submit A Bug Report"
                    );

                }

                LogsWindow.s_logsWindow = new LogsWindow( "LogsWindow" );

            }

        }

        return LogsWindow.s_logsWindow;

    }

    public static void launch() {

        WindowWithMenus.setAllShowLogsModeInMenu( true );
        LogsWindow.getInstance().setVisible( true );

    }

    protected JMenu defineEditMenu() {

        _editMenu = new JMenu( "Edit" );

        JMenuItem selectAllMenuItem = new JMenuItem( "Select All" );
        setMenuEnabled( "LW:dEM", selectAllMenuItem, true );
        selectAllMenuItem.addActionListener(

                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        _messageWindowList.getSelectionModel().setSelectionInterval(
                                0,
                                _messagesListModel.size() - 1
                        );

                    }

                }

        );

        selectAllMenuItem.setAccelerator(

                KeyStroke.getKeyStroke(
                        KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )

        );

        JMenuItem cutMenuItem = new JMenuItem( "Cut" );

        cutMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        setMenuEnabled( "LW:dEM", cutMenuItem, false );

        _copyMenuItem = new JMenuItem( "Copy" );

        setMenuEnabled( "LW:dEM", _copyMenuItem, false );

        _copyMenuItem.addActionListener(

                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        StringWriter lines = new StringWriter();
                        PrintWriter writer = new PrintWriter( lines );
                        int[] selectedIndices = _messageWindowList.getSelectedIndices();
			List selectedValues = _messageWindowList.getSelectedValuesList();

//                        for ( int ix = 0; ix < selectedIndices.length; ix += 1 ) {
			int ix = 0;
			for ( Object sv : selectedValues ) {

			    writer.print( sv );
			    if ( ix < selectedIndices.length - 1 ) {

				writer.println();

			    }

                            ix += 1;

                        }

                        writer.flush();
                        StringSelection selection = new StringSelection( lines.getBuffer().toString() );
                        _systemClipboard.setContents( selection, selection );

                    }

                }

        );

        _copyMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        JMenuItem pasteMenuItem = new JMenuItem( "Paste" );

        pasteMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        setMenuEnabled( "LW:dEM", pasteMenuItem, false );

        _editMenu.add( cutMenuItem );   // never enabled (yet?)
        _editMenu.add( _copyMenuItem );
        _editMenu.add( pasteMenuItem ); // never enabled (yet?)
        _editMenu.add( selectAllMenuItem );

        return _editMenu;

    }

}
