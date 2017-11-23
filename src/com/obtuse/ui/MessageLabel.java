package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A JLabel which might provide more information when it is clicked (depends on whether or not more information is actually available).
 */

public class MessageLabel extends JLabel {

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

        @SuppressWarnings("unused")
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

    public MessageLabel() {

        this( null, null );

        addMouseListener(

                new MouseAdapter() {

                    @Override
                    public void mouseClicked( final MouseEvent e ) {

                        Logger.logMsg( "got a mouse click event:  " + e );

                        if ( _extraInfo != null && !_extraInfo.trim().isEmpty() ) {

                            Logger.logMsg( "have extra info:  " + _extraInfo );

                            OkPopupMessageWindow.doit( _extraInfo, "Ok" );

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

    public MessageLabel( @Nullable final String message, @Nullable final String extraInfo ) {

        super();

        setMessage( message, extraInfo );

    }

    /**
     Create a message label which (initially) has no extra information.
     @param message the (optional) message which will become the text in the JLabel.
     */

    public MessageLabel( @Nullable final String message ) {

        this( message, null );

    }

    /**
     Replace this instance's message with {@code ""} and this instance's extra info with {@code null}.
     */

    public void clear() {

        setMessage( "", null );

    }

    /**
     Set this instance's message and clear this instance's extra info.
     @param message the new message for this instance.
     */

    public void setMessage( @Nullable final String message ) {

        setMessage( message, null );

    }

    /**
     Set this instance's message and extra info based on a provided exception's information.
     @param e the exception. The exception's message ({@code e.getMessage()}) becomes this {@link MessageLabel} instance's message.
     If the exception is a {@link AugmentedIllegalArgumentException} then the exception's extra info ({@code ((MessageLabel.AugmentedIllegalArgumentException)e).getExtraInfo()})
     become's this {@link MessageLabel} instance's extra info.
     */

    public void setMessage( @NotNull final Exception e ) {

        if ( e instanceof AugmentedIllegalArgumentException ) {

            MessageLabel.AugmentedIllegalArgumentException ae = (MessageLabel.AugmentedIllegalArgumentException)e;

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

    public void setMessage( @Nullable final String message, @Nullable final String extraInfo ) {

        setText( message == null ? "null" : message.startsWith( "<html>" ) ? message : "<html>" + message + "</html>" );

        setExtraInfo( extraInfo );

    }

    /**
     Set this instance's extra info.
     @param extraInfo what is to become this instance's extra info (saved as {@code ""} if {@code null}. Otherwise, saved as-is.
     */

    public void setExtraInfo( @Nullable final String extraInfo ) {

        _extraInfo = extraInfo == null ? "" : extraInfo;

        setToolTipText( _extraInfo );

    }

    /**
     Get this instance's extra info.
     @return this instance's extra info ({@code null} if the extra info is {@code null} or {@code ""}).
     */
    @SuppressWarnings("unused")
    public Optional<String> getExtraInfo() {

        return Optional.ofNullable (( _extraInfo == null || _extraInfo.isEmpty() ) ? null : _extraInfo );

    }

    public String toString() {

        return getText();

    }

}
