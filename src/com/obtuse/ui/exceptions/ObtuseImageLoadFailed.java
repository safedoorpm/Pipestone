package com.obtuse.ui.exceptions;

/**
 Created by danny on 2018/09/04.
 */
@SuppressWarnings("unused")
public class ObtuseImageLoadFailed extends Exception {

    public ObtuseImageLoadFailed() {

        super();
    }

    public ObtuseImageLoadFailed( final String message ) {

        super( message );
    }

    public ObtuseImageLoadFailed( final String message, final Throwable cause ) {

        super( message, cause );
    }

    public ObtuseImageLoadFailed( final Throwable cause ) {

        super( cause );
    }

}
