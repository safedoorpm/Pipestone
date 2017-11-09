package com.obtuse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TransferActionListener implements ActionListener, PropertyChangeListener {

    private JComponent focusOwner;

    public TransferActionListener() {
        super();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener( "permanentFocusOwner", this );

    }

    @Override
    public void actionPerformed( final ActionEvent e ) {

        if ( focusOwner != null ) {

            String actionCommand = e.getActionCommand();
            Action action = focusOwner.getActionMap().get( actionCommand );
            if ( action != null ) {

                action.actionPerformed( new ActionEvent( focusOwner, ActionEvent.ACTION_PERFORMED, null ) );

            }

        }

    }

    @Override
    public void propertyChange( final PropertyChangeEvent evt ) {

        Object o = evt.getNewValue();
        if ( o instanceof JComponent ) {

            focusOwner = (JComponent)o;

        } else {

            focusOwner = null;

        }

    }

    public String toString() {

        return "TransferActionListener()";

    }

}