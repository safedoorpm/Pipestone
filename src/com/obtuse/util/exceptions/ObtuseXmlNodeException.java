/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class ObtuseXmlNodeException extends Exception {

    private Integer _elementIndex = null;

    public ObtuseXmlNodeException() {
        super();

    }

    public ObtuseXmlNodeException( final String msg ) {
        super(msg);

    }

    public ObtuseXmlNodeException( final String msg, final Throwable cause ) {
        super( msg, cause );

    }

    public ObtuseXmlNodeException( final String msg, final int elementIndex ) {
        super( msg );

        _elementIndex = elementIndex;

    }

    public ObtuseXmlNodeException( final String msg, final int elementIndex, final Throwable cause ) {
        super( msg, cause );

        _elementIndex = elementIndex;

    }

    public boolean isElementIndexSet() {

        return _elementIndex != null;

    }

    public int getElementIndex() {

        return _elementIndex.intValue();

    }

}
