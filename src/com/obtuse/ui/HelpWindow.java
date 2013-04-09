package com.obtuse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A simple help window.
 */

public class HelpWindow extends JDialog {

    private JPanel _contentPane;
    private JTextPane _helpTextPane;
    private JButton _okButton;
    private JScrollPane _helpScrollPane;

    private static String _preferredFontTag;

    public HelpWindow( String title, String helpText ) {
        super();

        setContentPane( _contentPane );

        setMinimumSize( new Dimension( 800, 600 ) );

        setModal( false );
        getRootPane().setDefaultButton( _okButton );

        setTitle( title );

        _helpTextPane.setEditable( false );
        _helpTextPane.setContentType( "text/html" );

        String inputText = helpText;
        String fontTag = getFontTag();

        // Append a <font face="Lucida Grande"> immediately after every <h1>, </h1>, <h2>, </h2>, ... tag.
        // This is a much better font to use than the default serif font.
        // There must be a better way to do this . . .

        for ( String before : new String[]{ "h1>", "h2>", "h3>", "h4>", "blockquote>" } ) {

            String fixedText = "";
            while ( true ) {

                int offset = inputText.indexOf( before );
                if ( offset >= 0 ) {

                    fixedText += inputText.substring( 0, offset + before.length() );
                    inputText = inputText.substring( offset + before.length() );
                    if ( !inputText.startsWith( fontTag ) ) {

                        fixedText += fontTag;

                    }

                } else {

                    break;

                }

            }

            inputText = fixedText + inputText;

        }

        // Show the help text with a <font face="Lucida Grande"> tag to start things of with the right font.

        _helpTextPane.setText( fontTag + inputText );

        _helpTextPane.setCaretPosition( 0 );


        _okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {

                        done();

                    }

                }
        );

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( WindowEvent e ) {

                        done();

                    }

                }
        );

        _contentPane.registerKeyboardAction(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {

                        done();

                    }

                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        pack();

        setVisible( true );

    }

    public static String getFontTag() {

        if ( _preferredFontTag == null ) {

            String fontFace = "Lucida Grande";
            _preferredFontTag = "<font face=\"" + fontFace + "\">";
//            _preferredFontTag = "";

        }

        return _preferredFontTag;

    }

    private void done() {

        dispose();

    }

    public static void launch( String title, String helpText ) {

        @SuppressWarnings({ "UnusedDeclaration", "UnusedAssignment" })
        HelpWindow dialog = new HelpWindow( title, helpText );

    }

    public static void main( String[] args ) {

        HelpWindow dialog = new HelpWindow(
                "Help Window Title",
                "Words and more words go here.<br><br>This is a very long sentence that needs to be wrapped around on one or more lines.  There is even another sentence after it that makes the paragraph even longer."
        );
        dialog.pack();
        dialog.setVisible( true );

    }
}
