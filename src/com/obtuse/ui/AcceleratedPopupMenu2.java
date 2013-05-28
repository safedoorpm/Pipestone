package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.VerbsList;

import javax.swing.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * Copyright © 2012 Obtuse Systems Corporation
 */

public class AcceleratedPopupMenu2 extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList _wordsList;
    private JScrollPane _wordsScrollPane;

    private final String[] _words;
    private final DefaultListModel _listModel = new DefaultListModel();
    private final StringBuilder _accelerator = new StringBuilder();

    public AcceleratedPopupMenu2( Collection<String> words ) {
        this( words.toArray( new String[words.size()] ) );
    }

    public AcceleratedPopupMenu2( String[] words ) {
        super();

        _words = words;

        _wordsList.setModel( _listModel );

        setContentPane( contentPane );
        setModal( true );
        getRootPane().setDefaultButton( buttonOK );

        buttonOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOK();
            }
        } );

        buttonCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onCancel();
            }
        } );

// call onCancel() when cross is clicked
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                onCancel();
            }
        } );

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        setWordVisibility();

    }

    private void setWordVisibility() {

        _listModel.clear();
        int visibleCount = 0;
        String prefix = _accelerator.toString();

        Logger.logMsg( "accelerator = \"" + prefix + "\" (" + prefix.length() + " chars)" );

        for ( String word : _words ) {

            if ( word.startsWith( prefix ) ) {

                _listModel.addElement( word );

                visibleCount += 1;

            }

        }

//        if ( visibleCount == 0 ) {
//
//            _firstMenuItem.setVisible( true );
//
//        } else {
//
//            _firstMenuItem.setVisible( true );
//
//        }
//
//        RulesSpreadsheetCanvas.setMenuEnabled( _firstMenuItem, false );
//        RulesSpreadsheetCanvas.setMenuEnabled( _secondMenuItem, false );

        pack();
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Test", null );

        List<String> words = new LinkedList<String>();
        for ( int i = 0; i < 25; i += 1 ) {

            words.add( VerbsList.pickVerb() );

        }

        AcceleratedPopupMenu2 dialog = new AcceleratedPopupMenu2( words );
        dialog.pack();
        dialog.setVisible( true );
        System.exit( 0 );

    }

}
