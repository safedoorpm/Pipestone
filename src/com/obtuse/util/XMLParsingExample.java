/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import java.io.*;

public class XMLParsingExample {

    private static final String INPUT_FILENAME = ".idea/compiler.xml";

    private XMLParsingExample() {
        super();

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "XML", "ParsingExample", null );

        Measure.setGloballyEnabled( true );

        XMLParsingExample.parseOnTheFly( XMLParsingExample.INPUT_FILENAME );

        XMLParsingExample.loadEntireDocument( XMLParsingExample.INPUT_FILENAME );

        Measure.showStats();

    }

    @SuppressWarnings("SameParameterValue")
    private static void parseOnTheFly( final String inputFilename ) {

        try ( Measure ignored = new Measure( "entire on-the-fly" ) ) {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            @SuppressWarnings("TooBroadScope")
            SAXParser parser;
            XMLReader xmlReader;
            try {

                parser = spf.newSAXParser();
                xmlReader = parser.getXMLReader();

            } catch ( ParserConfigurationException e ) {

                Logger.logErr( "parser configuration problem", e );
                System.exit( 1 );
                return;

            } catch ( SAXException e ) {

                Logger.logErr( "parser initialization problem", e );
                System.exit( 1 );
                return;

            }

            try {

                SimpleXmlContentHandler contentHandler = new SimpleXmlContentHandler( false );
                xmlReader.setContentHandler( contentHandler );
                xmlReader.parse(
                        new InputSource( new BufferedInputStream( new FileInputStream( inputFilename ) ) )
                );

            } catch ( SAXParseException e ) {

                Logger.logErr(
                        inputFilename + " (" + e.getLineNumber() + "," + e.getColumnNumber() + "):  " + e.getMessage()
                );
                System.exit( 1 );

            } catch ( SAXException e ) {

                Logger.logErr( "SAX exception caught parsing file", e );
                System.exit( 1 );

            } catch ( FileNotFoundException e ) {

                Logger.logErr( "file not found", e );
                System.exit( 1 );

            } catch ( IOException e ) {

                Logger.logErr( "I/O error", e );
                System.exit( 1 );

            }

        }

    }

    @SuppressWarnings("SameParameterValue")
    private static void loadEntireDocument( final String inputFilename ) {

        Document doc;
        try ( Measure ignored = new Measure( "load-entire-document" ) ) {

            try {

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                doc = docBuilder.parse( new File( inputFilename ) );

            } catch ( ParserConfigurationException e ) {

                Logger.logErr( "Parser configuration error", e );
                System.exit( 1 );
                return;

            } catch ( SAXException e ) {

                Logger.logErr( "Parsing error", e );
                System.exit( 1 );
                return;

            } catch ( IOException e ) {

                Logger.logErr( "I/O error", e );
                System.exit( 1 );
                return;

            }

        }

        try ( Measure ignored = new Measure( "clean things up" ) ) {

            // Clean things up.

            doc.getDocumentElement().normalize();

        }

        Logger.logMsg( "root element is " + doc.getDocumentElement().getNodeName() );

    }

}
