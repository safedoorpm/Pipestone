/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * A {@link JTextField} which provides ways to handle the validation and processing of the JTextField's value at key moments in its life.
 * <p/>
 * The key moments are:
 * <ul>
 *     <li>when the ESC key is pressed to abort the current edit operation.</li>
 *     <li>when the TAB key is pressed to cause the JTextField to lose focus and to indicate that the current value is purportedly ready to be stored.</li>
 *     <li>when the RETURN key is pressed to to indicate that the current value is purportedly ready to be stored.</li>
 * </ul>
 * In the former case, implementations of this class provide a way to get the 'original' value so that the value of the JTextField can be 'rolled back' to that value.
 * In the latter two cases, implementations of this class provide a way to perform validation and to store the value should it pass validation.
 */

@SuppressWarnings("UnusedDeclaration")
public abstract class ValidatedJTextField extends JTextField implements EditValueAdvocate<String> {

    private static final Random _rng = new Random( System.currentTimeMillis() );

    @SuppressWarnings("FieldCanBeLocal")
    private final String CANCEL_EDIT = "cancel-edit-" + _rng.nextLong();
    @SuppressWarnings("FieldCanBeLocal")
    private final InputMap _im;
    @SuppressWarnings("FieldCanBeLocal")
    private final ActionMap _am;

    public ValidatedJTextField() {
        this( "" );

    }

    public ValidatedJTextField( String text ) {
        super( text );

        setInputVerifier(
                new InputVerifier() {
                    @Override
                    public boolean verify( JComponent jComponent ) {

                        return isValueValid( getText() );

                    }

                    public boolean shouldYieldFocus( JComponent jComponent ) {

                        if ( verify( jComponent ) ) {

                            storeNewValue( getText(), false );

//                            _displayNameIssuesField.setText( "" );
//
//                            _group.setDisplayName( _displayNameField.getText() );
//
//                            Central.invalidateGroupViewsContainingGroup(
//                                    _group,
//                                    InvalidationReason.GROUP_DISPLAY_NAME_CHANGED
//                            );

                            return true;

                        } else {

//                            _displayNameIssuesField.setText( validateEditedDisplayName() );

                            return false;

                        }

                    }

                }
        );

        addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        if ( isValueValid( getText() ) ) {

                            storeNewValue( getText(), true );

                        }

                    }

                }
        );

        _im = getInputMap();
        _am = getActionMap();
//        _im.put( KeyStroke.getKeyStroke( "ESCAPE" ), CANCEL_DISPLAY_NAME_EDIT );
        _im.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), CANCEL_EDIT );
        _am.put(
                CANCEL_EDIT,
                new AbstractAction() {

                    @Override
                    public void actionPerformed( ActionEvent actionEvent ) {

                        setText( getRollbackValue() );

                    }

                }
        );

    }

    public String toString() {

        return "ValidatedJTextField( text=" + ObtuseUtil.enquoteToJavaString( getText() ) + " )";

    }

}
