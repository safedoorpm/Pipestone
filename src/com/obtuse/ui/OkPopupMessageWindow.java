/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Popup a window with a message and a single button (which disposes of the window).
 *
 * @noinspection ClassWithoutToString, UnusedDeclaration, ClassHasNoToStringMethod
 */

public class OkPopupMessageWindow extends JDialog {

    @SuppressWarnings({ "InstanceVariableNamingConvention" })

    private JPanel _contentPane;

    private JButton _okButton;

    private JLabel _firstMessageField;

    private JLabel _secondMessageField;

    protected OkPopupMessageWindow(
            final String firstMessage,
            @Nullable final String secondMessage,
            final String buttonLabel
    ) {
        super();

        setContentPane( _contentPane );
        setModal( true );

        getRootPane().setDefaultButton( _okButton );

        _okButton.setText( buttonLabel );
        _okButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onOK();

                    }

                }
        );

        _firstMessageField.setText( "<html>" + firstMessage );
        if ( secondMessage == null || secondMessage.trim().isEmpty() ) {

            _secondMessageField.setVisible( false );

        } else {

            _secondMessageField.setText( "<html>" + secondMessage );

        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        //noinspection RefusedBequest
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( final WindowEvent e ) {

                        onOK();

                    }

                }
        );

        // call onCancel() on ESCAPE
        _contentPane.registerKeyboardAction(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onOK();

                    }

                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        pack();
        setResizable( false );

    }

    @SuppressWarnings({ "SameParameterValue" })
    protected OkPopupMessageWindow( final String firstMessage, final String buttonLabel ) {
        this( firstMessage, null, buttonLabel );

    }

    private void onOK() {

        dispose();

        ok();

    }

    /**
     Called as the last thing an instance of this class does after someone clicks the ok button.
     In particular, this instance will have already invoked ({@link java.awt.Window#dispose()} on itself).
     This method is provided so that users of this class can deal with
     clicks of the ok button themselves. The default implementation of this method does nothing.
     */

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    protected void ok() {

        ObtuseUtil.doNothing();

    }

    /**
     * Wait for the human to click the button or otherwise dispose of the window.
     */

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    public void go() {

        pack();
        setVisible( true );
        dispose();

    }

    /**
     * Abort the popup.
     * <p/>
     * This method may be called from any thread.  It causes the popup to vanish without its button
     * being clicked (i.e. the popup's {@link #ok} method is never called.  Instead, the popup's
     * {@link #aborted} method is called.  Depending on what events happen to be queued at the moment that
     * this method is called, it is possible for the popup's {@link #aborted} and {@link #ok} methods to
     * both be called (it should not be possible for the {@link #ok} method to be called after the {@link #aborted}
     * method is called but one never knows).
     *
     * @param why the reason that the popup was aborted.  This value is passed to the popup's {@link
     *            #aborted} method.
     */

    public void abortPopup( final String why ) {

        SwingUtilities.invokeLater(
                () -> {

                    setVisible( false );
                    aborted( why );

                }
        );

    }

    /**
     * This method is invoked if the popup's {@link #abortPopup} method is called.
     * The default implementation does nothing.
     * @param why the string passed to the popup's {@link #abortPopup} method.
     */

    @SuppressWarnings({ "EmptyMethod", "NoopMethodInAbstractClass" })
    public void aborted( final String why ) {

        // Do nothing by default.

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( final String line1,
                             @Nullable final String line2,
                             final String button,
                             @Nullable final Runnable runnable
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    //noinspection ClassWithoutToString
                    OkPopupMessageWindow ok = new OkPopupMessageWindow(
                            line1,
                            line2,
                            button
                    ) {

                        protected void ok() {

                            if ( runnable != null ) {

                                runnable.run();

                            }

                        }

                    };

                    ok.go();

                }
        );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( final String line1, final String button ) {

        OkPopupMessageWindow.doit( line1, null, button, null );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( final String line1, final String line2, final String button ) {

        OkPopupMessageWindow.doit( line1, line2, button, null );

    }

    public static void fatal( final String line1 ) {

        OkPopupMessageWindow.fatal( line1, null, "Sorry" );

    }

    public static void fatal( final String line1, final String line2 ) {

        OkPopupMessageWindow.fatal( line1, line2, "Sorry" );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void fatal(
            final String line1,
            @Nullable final String line2,
            @Nullable final String buttonText
    ) {

        Runnable runnable = () -> {

            //noinspection ClassWithoutToString
            OkPopupMessageWindow ok = new OkPopupMessageWindow(
                    line1,
                    line2 == null
                            ?
                            "Please click the button to terminate."
                            :
                            line2,
                    buttonText == null ? "Sorry" : buttonText
            ) {

                protected void ok() {

                    System.exit( 1 );

                }

            };

            ok.go();

            // Try to keep the window in the foreground.
            //noinspection InfiniteLoopStatement
            while ( true ) {

                ObtuseUtil.safeSleepMillis( javax.management.timer.Timer.ONE_SECOND );
                ok.setVisible( true );

            }

        };

        if ( SwingUtilities.isEventDispatchThread() ) {

            runnable.run();

        } else {

            SwingUtilities.invokeLater(

                    runnable

            );

        }

        // We aren't allowed to return from this method!

        //noinspection InfiniteLoopStatement
        while ( true ) {

            ObtuseUtil.safeSleepMillis( javax.management.timer.Timer.ONE_MINUTE );

        }

    }

    public static void testIt() {

        OkPopupMessageWindow dialog = new OkPopupMessageWindow(
                "123456789.123456789.123456789.123456789.12345<br>123456789.123456789.123456789.123456789.12345",
                "123456789.123456789.123456789.123456789.12345",
                "OK"
        );

        Logger.logMsg( "size is " + dialog.getSize() );
        dialog.go();

        dialog = new OkPopupMessageWindow(
                "Looks like a nice day today", "Although I suppose it could rain", "Sigh"
        );
        dialog.go();

        dialog = new OkPopupMessageWindow( "How are you today?", "Fine Thanks" );
        dialog.go();

        System.exit( 0 );

    }

}
