package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A JLabel which might provide more information when it is clicked (depends on whether or not more information is actually available).
 */

@SuppressWarnings("unused")
public class ObtuseMessageLabel extends JLabel implements MessageLabelInterface {

    @NotNull
    public Container getAsContainer() {
        return (Container)this;

    }

    public static class AugmentedIllegalArgumentException extends IllegalArgumentException {

        private final String _extraInfo;

        public AugmentedIllegalArgumentException( final String message, final String extraInfo, final Throwable cause ) {

            super( message, cause );

            _extraInfo = extraInfo;

        }

        public AugmentedIllegalArgumentException( final AugmentedMessage augmentedMessage ) {

            this( augmentedMessage.getMessage(), augmentedMessage.getExtraInfo() );
        }

        public AugmentedIllegalArgumentException( final String message, final String extraInfo ) {

            this( message, extraInfo, null );
        }

        public AugmentedIllegalArgumentException( final String message ) {

            this( message, null, null );

        }

        public AugmentedIllegalArgumentException( final String message, final Throwable cause ) {

            this( message, null, cause );

        }

        public AugmentedIllegalArgumentException( final IllegalArgumentException iae ) {

            this( iae.getMessage(), null, iae.getCause() );

        }

        public String getExtraInfo() {

            return _extraInfo;

        }

        public String toString() {

            return "AugmentedIllegalArgumentException( " + new AugmentedMessage( getMessage(), _extraInfo ) + " )";

        }

    }

    public static class AugmentedMessage {

        private final String _message;

        private final String _extraInfo;

        public AugmentedMessage( final String message, final String extraInfo ) {

            super();

            _message = message;

            _extraInfo = extraInfo;

        }

        public AugmentedMessage( final String message ) {

            this( message, null );

        }

        public String getMessage() {

            return _message;

        }

        public String getExtraInfo() {

            return _extraInfo;

        }

        public String toString() {

            return "AugmentedMessage( " + ObtuseUtil.enquoteToJavaString( _message ) + ", " + ObtuseUtil.enquoteToJavaString( _extraInfo ) + " )";

        }

    }

    private String _extraInfo;

    /**
     Create a blank message label.
     */

    public ObtuseMessageLabel() {
        this( null, null );

        addMouseListener(

                new MouseAdapter() {

                    @Override
                    public void mouseClicked( final MouseEvent e ) {

                        Logger.logMsg( "got a mouse click event:  " + e );

                        if ( _extraInfo != null && !_extraInfo.trim().isEmpty() ) {

                            Logger.logMsg( "have extra info:  " + _extraInfo );

                            OkPopupMessageWindow.doit( _extraInfo, "OK" );

                        }

                    }

                }

        );

    }

    /**
     Create a message label.
     @param message the (optional) message which will become the text in the JLabel.
     @param extraInfo (optional) extra information which is available to whoever is using this instance.
     */

    public ObtuseMessageLabel( @Nullable final String message, @Nullable final String extraInfo ) {
        super();

        setMessage( message, extraInfo );

    }

    /**
     Create a message label which (initially) has no extra information.
     @param message the (optional) message which will become the text in the JLabel.
     */

    public ObtuseMessageLabel( @Nullable final String message ) {

        this( message, null );

    }

    /**
     Replace this instance's message with {@code ""} and this instance's extra info with {@code null}.
     */

    @Override
    public void clear() {

        setMessage( "", null );

    }

    /**
     Set this instance's message and clear this instance's extra info.
     @param message the new message for this instance.
     */

    @Override
    public void setMessage( @Nullable final String message ) {

        setMessage( message, null );

    }

    /**
     Set this instance's message and extra info based on a provided exception's information.
     @param e the exception. The exception's message ({@code e.getMessage()}) becomes this {@link ObtuseMessageLabel} instance's message.
     If the exception is a {@link AugmentedIllegalArgumentException} then the exception's extra info ({@code ((ObtuseMessageLabel.AugmentedIllegalArgumentException)e).getExtraInfo()})
     become's this {@link ObtuseMessageLabel} instance's extra info.
     */

    @Override
    public void setMessage( final @NotNull Exception e ) {

        if ( e instanceof AugmentedIllegalArgumentException ) {

            ObtuseMessageLabel.AugmentedIllegalArgumentException ae = (ObtuseMessageLabel.AugmentedIllegalArgumentException)e;

            setMessage( ae.getMessage(), ae.getExtraInfo() );

        } else {

            setMessage( e.getMessage() );

        }

    }

    /**
     Set this instance's message and extra info.
     @param message the message that will appear in our {@link JLabel}.
     If the message is {@code null} then the message will be {@code "<html>null</html>} so choose wisely (we're a {@link JLabel} so we pretty much have to have a message).
     @param extraInfo what is to become this instance's extra info (saved as {@code ""} if {@code null}. Otherwise, saved as-is.
     */

    @Override
    public void setMessage( @Nullable final String message, @Nullable final String extraInfo ) {

        setText( message == null ? "null" : message.startsWith( "<html>" ) ? message : "<html>" + message + "</html>" );

        setExtraInfo( extraInfo );

    }

    /**
     Set this instance's extra info.
     @param extraInfo what is to become this instance's extra info (saved as {@code ""} if {@code null}. Otherwise, saved as-is.
     */

    @Override
    public void setExtraInfo( @Nullable final String extraInfo ) {

        _extraInfo = extraInfo == null ? "" : extraInfo;

        setToolTipText( _extraInfo );

    }

    /**
     Get this instance's extra info.
     @return this instance's extra info ({@code null} if the extra info is {@code null} or {@code ""}).
     */

    @NotNull
    public Optional<String> getExtraInfo() {

        return Optional.ofNullable (( _extraInfo == null || _extraInfo.isEmpty() ) ? null : _extraInfo );

    }

    /**
     Get this instance's text as an {@link Optional}{@code <String>}.
     @return this instance's text ({@code null} if the text is {@code null} or {@code ""}).
     */

    @NotNull
    public Optional<String> getOptionalText() {

        String txt = getText();

        return Optional.ofNullable (( txt == null || txt.isEmpty() ) ? null : txt );

    }

    public String toString() {

        return getText();

    }

}
