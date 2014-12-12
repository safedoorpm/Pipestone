/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.ObtuseXmlNodeException;

/**
 * Mark something that can be serialized to XML format and deserialized from XML format.
 */

@SuppressWarnings("UnusedDeclaration")
public interface InstanceFromXML {

    /**
     * Serialize this instance to XML.
     * @param ps where to send things to.
     */

    void emitAsXml( NestedXMLPrinter ps )
            throws ObtuseXmlNodeException;

}
