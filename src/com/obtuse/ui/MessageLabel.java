package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public MessageLabel( final String message, final String extraInfo ) {
        super();

	setMessage( message, extraInfo );

    }

    public MessageLabel( final String message ) {
	this( message, null );
    }

    public void clear() {

	setMessage( "", null );

    }

    public void setMessage( final String message ) {

        setMessage( message, null );

    }

    public void setMessage( final Exception e ) {

        if ( e instanceof AugmentedIllegalArgumentException ) {

	    MessageLabel.AugmentedIllegalArgumentException ae = (MessageLabel.AugmentedIllegalArgumentException) e;

	    setMessage( ae.getMessage(), ae.getExtraInfo() );

	} else {

            setMessage( e.getMessage() );

	}

    }

    public void setMessage( final String message, final String extraInfo ) {

        setText( message == null ? "null" : message.startsWith( "<html>" ) ? message : "<html>" + message + "</html>" );

        setExtraInfo( extraInfo );

    }

    public void setExtraInfo( final String extraInfo ) {

	_extraInfo = extraInfo == null ? "" : extraInfo;

    }

    public String getExtraInfo() {

        return ( _extraInfo == null || _extraInfo.isEmpty() ) ? null : _extraInfo;

    }

}
