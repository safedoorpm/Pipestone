/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.VerbsList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class AcceleratedPopupMenu2 extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList _wordsList;
    private JScrollPane _wordsScrollPane;

    private final String[] _words;
    private final DefaultListModel<String> _listModel = new DefaultListModel<>();
    private final StringBuilder _accelerator = new StringBuilder();

    public AcceleratedPopupMenu2( final Collection<String> words ) {
        this( words.toArray( new String[0] ) );
    }

    @SuppressWarnings("unchecked")
    public AcceleratedPopupMenu2( final @NotNull String@NotNull [] words ) {
        super();

        _words = words;

        _wordsList.setModel( _listModel );

        setContentPane( contentPane );
        setModal( true );
        getRootPane().setDefaultButton( buttonOK );

        buttonOK.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onOK();

                    }

                }
        );

        buttonCancel.addActionListener(

                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onCancel();

                    }

                }
        );

        // call onCancel() when cross is clicked

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( final WindowEvent e ) {
                onCancel();
            }
        } );

        // call onCancel() on ESCAPE

        contentPane.registerKeyboardAction(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onCancel();

                    }

                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        setWordVisibility();

    }

    private void setWordVisibility() {

        _listModel.clear();
        int visibleCount = 0;
        String prefix = _accelerator.toString();

        Logger.logMsg( "accelerator = \"" + prefix + "\" (" + prefix.length() + " chars)" );

        for ( String word : _words ) {

            if ( word.startsWith( prefix ) ) {

                //noinspection unchecked
                _listModel.addElement( word );

                visibleCount += 1;

            }

        }

        pack();
    }

    private void onOK() {

        dispose();

    }

    private void onCancel() {

        dispose();

    }

    public String toString() {

        return "AcceleratedPopupMenu2()";

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Test", null );

        List<String> words = new LinkedList<>();
        for ( int i = 0; i < 25; i += 1 ) {

            words.add( VerbsList.pickVerb() );

        }

        AcceleratedPopupMenu2 dialog = new AcceleratedPopupMenu2( words );
        dialog.pack();
        dialog.setVisible( true );
        System.exit( 0 );

    }

}
