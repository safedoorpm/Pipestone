package com.obtuse.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public abstract class ValidatedJTextField extends JTextField {

    private static final Random _rng = new Random( System.currentTimeMillis() );

    private final String CANCEL_EDIT = "cancel-edit-" + _rng.nextLong();
    private final InputMap _im;
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
                new ActionListener() {

                    @Override
                    public void actionPerformed( ActionEvent actionEvent ) {

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

    /**
     * Called when the user has clicked the ESC key to obtain the value to roll the JTextField back to.
     * @return the rollback value.
     */

    public abstract String getRollbackValue();

    /**
     * Vet a candidate value as a pre-condition to storing this value as the model's new stored value.
     * @param candidateValue the value to be validated.
     * @return true if the candidate value is acceptable (will result in {@link #storeNewValue} being called with this same candidate value); false otherwise.
     */

    public abstract boolean isValueValid( String candidateValue );

    /**
     * Store a validated value into the model.
     * <p/>This method is only called and is always called immediately after a call to {@link #isValueValid}.
     * @param newValue the new value for the model.
     * @param causedByReturnKey true if this store is being triggered by an ActionListener.actionPerformed call; false otherwise.
     */

    public abstract void storeNewValue( String newValue, boolean causedByReturnKey );

}
