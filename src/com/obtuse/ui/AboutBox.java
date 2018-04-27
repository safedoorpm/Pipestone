package com.obtuse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 A simple About box.
 */

public class AboutBox extends JDialog {

    private JPanel _contentPane;
    private JButton _buttonOK;
    private JLabel _copyrightLabel;

    // Sneak a copyright symbol past the Java compiler on Windows

    private static final char COPYRIGHT_SYMBOL = (char)169;

    @SuppressWarnings({ "SameParameterValue" })
    public AboutBox( JFrame owner ) {

        super( owner, true );

        setContentPane( _contentPane );

        setUndecorated( false );
        setLocation( new Point( 50, 50 ) );

        getRootPane().setDefaultButton( _buttonOK );

        _buttonOK.addActionListener(
                e -> onOK()
        );

        // We have to set the copyright notice field's text here since there appears to be
        // no other way to reliably get the copyright symbol into the notice.

        _copyrightLabel.setText(
                "<html><small>Copyright " + AboutBox.COPYRIGHT_SYMBOL + " 1867 Someone or other</small></html>"
        );

        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( WindowEvent e ) {

                        onCancel();

                    }

                }
        );

        _contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        setResizable( false );

        pack();
        setVisible( true );

    }

    private void onOK() {

        dispose();

    }

    private void onCancel() {

        dispose();

    }

    public static void main( String[] args ) {

        launch();

    }

    public static void launch() {

        @SuppressWarnings({ "UnusedDeclaration", "UnusedAssignment" })
        AboutBox dialog = new AboutBox( null );

    }

}