/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

@SuppressWarnings("SameParameterValue")
public class SimpleXmlContentHandler extends DefaultHandler {

    private Locator _locator;

    private final Stack<String> _nesting = new Stack<>();
    private final boolean _logActivity;

    public SimpleXmlContentHandler( final boolean logActivity ) {
        super();

        _logActivity = logActivity;

    }

    public void setDocumentLocator( final Locator locator ) {

        _locator = locator;

    }

    public void startDocument() {

        if ( _logActivity ) {

            Logger.logMsg( "start document" );

        }

    }

    public void endDocument() {

        if ( _logActivity ) {

            Logger.logMsg( "end document" );

        }

    }

    public void startElement( final String uri, final String localName, final String qName, final Attributes attributes ) {

        _nesting.push( qName );

        if ( _logActivity  ) {

            Logger.logMsg(
                    "start element:  uri = \"" + uri +
                    "\", localName = \"" + localName +
                    "\", qName = \"" + qName +
                    "\", attributes = " + SimpleXmlContentHandler.formatAttributes( attributes )
            );

        }

    }

    public static String formatAttributes( final Attributes attributes ) {

        if ( attributes.getLength() == 0 ) {

            return "{}";

        }

        StringBuilder sb = new StringBuilder();
        sb.append( "{ " );
        String comma = "";
        for ( int ix = 0; ix < attributes.getLength(); ix += 1 ) {

            sb
                    .append( comma )
                    .append( attributes.getQName( ix ) )
                    .append( "=\"" )
                    .append( attributes.getValue( ix ) )
                    .append( "\"" );

            comma = ", ";

        }
        sb.append( " }" );

        return sb.toString();

    }

    public void endElement( final String uri, final String localName, final String qName )
            throws SAXException {

        if ( _nesting.peek().equals( qName ) ) {

            if ( _logActivity ) {

                Logger.logMsg(
                        "end element:  uri = \"" + uri +
                        "\", localName = \"" + localName +
                        "\", qName = \"" + qName + "\""
                );

            }

            _nesting.pop();

        } else {

            throw new SAXParseException(
                    "structural problem:  expected to close \"" + _nesting.peek() + "\" but got \"" + qName + "\"",
                    _locator
            );

        }

    }

    public void characters( final char@NotNull[] chars, final int start, final int length ) {

        if ( _logActivity ) {

            Logger.logMsg( "characters:  \"" + new String( chars, start, length ) + "\"" );

        }

    }

    public void ignorableWhitespace( final char@NotNull[] chars, final int start, final int length ) {

        if ( _logActivity ) {

            Logger.logMsg( "ignorable whitespace:  \"" + new String( chars, start, length ) + "\"" );

        }

    }

    public void processingInstruction( final String s, final String s1 ) {

    }

    public void skippedEntity( final String s ) {

    }

}
