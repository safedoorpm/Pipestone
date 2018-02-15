/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 A JLabel which might provide more information when it is clicked (depends on whether or not more information is actually available).
 */

@SuppressWarnings("unused")
public class ObtuseMessagePanel extends JPanel implements MessageLabelInterface {

    private ObtuseMessageLabel _messageLabel = new ObtuseMessageLabel();

    /**
     Create a blank message label.
     */

    public ObtuseMessagePanel() {

        super();

        setLayout( new BorderLayout() );
        add( _messageLabel, BorderLayout.CENTER );

    }

    /**
     Create a message label.

     @param message   the (optional) message which will become the text in the JLabel.
     @param extraInfo (optional) extra information which is available to whoever is using this instance.
     */

    public ObtuseMessagePanel( @Nullable final String message, @Nullable final String extraInfo ) {

        this();

        _messageLabel.setMessage( message, extraInfo );

    }

    /**
     Create a message label which (initially) has no extra information.

     @param message the (optional) message which will become the text in the JLabel.
     */

    public ObtuseMessagePanel( @Nullable final String message ) {

        this( message, null );

    }

    @Override
    public void clear() {

        _messageLabel.clear();

    }

    @Override
    public void setMessage( final @Nullable String message ) {

        _messageLabel.setMessage( message );

    }

    @Override
    public void setMessage( final @Nullable String message, final @Nullable String extraInfo ) {

        _messageLabel.setMessage( message, extraInfo );

    }

    @Override
    public void setExtraInfo( final @Nullable String extraInfo ) {

        _messageLabel.setExtraInfo( extraInfo );

    }

    @Override
    public void setMessage( final @NotNull Exception e ) {

        _messageLabel.setMessage( e );

    }

    @Nullable
    public String getText() {

        return _messageLabel.getText();

    }

    @NotNull
    public Optional<String> getOptionalText() {

        return _messageLabel.getOptionalText();

    }

    /**
     Get this instance's extra info.

     @return this instance's extra info ({@code null} if the extra info is {@code null} or {@code ""}).
     */

    @NotNull
    public Optional<String> getExtraInfo() {

        return _messageLabel.getExtraInfo();

    }

    public String toString() {

        return getText();

    }

    @NotNull
    public Container getAsContainer() {
        return (Container)this;

    }
}
