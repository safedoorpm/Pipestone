/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CreateSomethingDialog<T> extends JDialog {

    private JPanel contentPane;

    private JButton _createSomethingButton;

    private JButton _cancelButton;

    private JTextField _newSomethingNameTextField;

    @SuppressWarnings("unused") private JLabel _errorMessageJLabel;
    private JLabel _promptLabel;

    private ObtuseMessageLabel _errorMessageLabel;

    private final CreateSomethingHelper<T> _createSomethingHelper;

    private final CreateSomethingNotifier<T> _createSomethingNotifier;

    // The Frame class is derived from the Window class - this should not be needed!

//    @SuppressWarnings("unused")
//    public CreateSomethingDialog(
//            final Frame owner,
//            @NotNull final CreateSomethingHelper<T> createSomethingHelper,
//            @Nullable final CreateSomethingNotifier<T> createSomethingNotifier
//    ) {
//        super( owner );
//
//        _createSomethingHelper = createSomethingHelper;
//        _createSomethingNotifier = createSomethingNotifier;
//
//        configDialog();
//
//    }

//    @SuppressWarnings("unused")
//    public CreateSomethingDialog(
//            @Nullable final Window owner,
//            @NotNull final CreateSomethingHelper<T> createSomethingHelper,
//            @Nullable final CreateSomethingNotifier<T> createSomethingNotifier
//    ) {
//
//        super( owner );
//
//        _createSomethingHelper = createSomethingHelper;
//        _createSomethingNotifier = createSomethingNotifier;
//
//        configDialog();
//
//    }

    @SuppressWarnings("unused")
    public CreateSomethingDialog(
            @Nullable final Component owner,
            @NotNull final CreateSomethingHelper<T> createSomethingHelper,
            @Nullable final CreateSomethingNotifier<T> createSomethingNotifier
    ) {

        super( findOurWindow( owner ) );

        _createSomethingHelper = createSomethingHelper;
        _createSomethingNotifier = createSomethingNotifier;

        configDialog();

    }

    private static Window findOurWindow( @Nullable Component c ) {

        Component tmp = c;
        while ( true ) {

            if ( tmp == null ) {

                if ( c != null ) {

                    // This seems strange.
                    // We should only get called when our component is visible.
                    // That should mean that it MUST have an enclosing Window.
                    // Let's not get grumpy but let's make it possible to notice this with a breakpoint.

                    ObtuseUtil.doNothing();

                }

                return null;

            }

            if ( tmp instanceof Window ) {

                return (Window)tmp;

            }

            tmp = tmp.getParent();

        }

    }

    private void configDialog() {

        setContentPane( contentPane );
        setModalityType( Dialog.DEFAULT_MODALITY_TYPE );
        getRootPane().setDefaultButton( _createSomethingButton );

        _promptLabel.setText( "Name of new " + _createSomethingHelper.getSingularTypeName() );

        _createSomethingButton.addActionListener(

                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onCreateSomething();

                    }

                }
        );

        _cancelButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onCancelCreate();

                    }

                }
        );

        _newSomethingNameTextField.setText( null );
        _newSomethingNameTextField.enableInputMethods( false );
        Document newMediaLibraryTextFieldDoc = _newSomethingNameTextField.getDocument();
        newMediaLibraryTextFieldDoc.addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate( final DocumentEvent e ) {

                        _createSomethingButton.setEnabled( newMediaLibraryTextFieldDoc.getLength() > 0 );

                    }

                    @Override
                    public void removeUpdate( final DocumentEvent e ) {

                        _createSomethingButton.setEnabled( newMediaLibraryTextFieldDoc.getLength() > 0 );

                    }

                    @Override
                    public void changedUpdate( final DocumentEvent e ) {

                        _createSomethingButton.setEnabled( newMediaLibraryTextFieldDoc.getLength() > 0 );

                    }

                }
        );

        _createSomethingButton.setEnabled( newMediaLibraryTextFieldDoc.getLength() > 0 );

        // call onCancelCreate() when cross is clicked
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( final WindowEvent e ) {

                        onCancelCreate();

                    }

                }
        );

        // call onCancelCreate() on ESCAPE
        contentPane.registerKeyboardAction(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onCancelCreate();

                    }
                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

    }

    private void onCreateSomething() {

        Logger.logMsg( "creating a new " + _createSomethingHelper.getSingularTypeName() );

        String rawSomethingName = _newSomethingNameTextField.getText();
        try {

//            if ( rawSomethingName == null ) {
//
//                throw new HowDidWeGetHereError( "What can a text field possibly be null???" );
//
//            }

            String trimmedSomethingString = rawSomethingName.trim();
            if ( trimmedSomethingString.isEmpty() ) {

                return;

            }

            if ( _createSomethingHelper.doesSomethingExist( trimmedSomethingString ) ) {
//            if ( LancotLibraryRoot.doesMediaLibraryExist( trimmedSomethingString ) ) {

                _errorMessageLabel.setMessage( "That " + _createSomethingHelper.getSingularTypeName() + " already exists" );

                return;

            }

            T newSomething = _createSomethingHelper.createSomething( trimmedSomethingString );

            if ( newSomething == null ) {

                if ( _createSomethingNotifier.creationFailed( trimmedSomethingString, _createSomethingHelper ) ) {

                    _errorMessageLabel.setMessage( "Creation failed (no idea why - sorry)" );

                } else {

                    onCancelCreate();

                }

                return;

            }

            if ( _createSomethingNotifier != null ) {

                _createSomethingNotifier.somethingHasBeenCreated( newSomething );

            }

            dispose();

        } catch ( ObtuseMessageLabel.AugmentedIllegalArgumentException e ) {

            _errorMessageLabel.setMessage( e.getMessage(), e.getExtraInfo() );
            pack();

        } catch ( Exception e ) {

            Logger.logErr( "unable to create " + _createSomethingHelper.getSingularTypeName() + " " + ObtuseUtil.enquoteToJavaString( rawSomethingName.trim() ), e );
            _errorMessageLabel.setMessage( e.getMessage(), null );
            pack();

        }

    }

    private void onCancelCreate() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {

        _errorMessageLabel = new ObtuseMessageLabel();
        _errorMessageJLabel = _errorMessageLabel;

    }

    public String toString() {

        return "CreateSomethingDialog()";

    }

}
