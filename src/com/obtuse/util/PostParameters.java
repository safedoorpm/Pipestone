/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class PostParameters implements Serializable {

    private final Map<String, String> _parameters = new TreeMap<>();

    private static final byte POSTPARAMETERS_FORMAT_VERSION = (byte)1;

    public static final String SAVROLA_PAYPAL_BUSINESS_EMAIL = "subscriptions@savrola.com";

    /**
     * The only acceptable value for the receiver_email field. Completed payment notifications are rejected by VanHorne
     * if any other value is found so BE CAREFUL!
     */

    public static final String SAVROLA_PAYPAL_RECEIVER_EMAIL = "subscriptions@savrola.com";

    public PostParameters() {
        super();
    }

    public PostParameters( final String encodedParms )
            throws
            InterruptedException {
        super();

        PostParameters.decodeParms( encodedParms, this );
    }

    public PostParameters( final PostParameters source ) {
        super();

        for ( String key : source.getKeys() ) {

            _parameters.put( key, source.getParameter( key ) );

        }

    }

    public Set<String> getKeys() {

        return _parameters.keySet();

    }

    public Collection<String> values() {

        return _parameters.values();

    }

    public void setParameter( final String key, final String value ) {

        _parameters.put( key, value );

    }

    public String getParameter( final String key ) {

        return _parameters.get( key );

    }

    public int size() {

        return _parameters.size();

    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them
     * to given Properties.
     *
     * @param parms the parameters of the URL.
     * @param p     where to put the parsed parameters.
     *
     * @throws InterruptedException if the operation needs to be aborted due to an error.
     */

    @SuppressWarnings("RedundantThrows")
    public static void decodeParms( final String parms, final PostParameters p )
            throws InterruptedException {

        if ( parms == null ) {

            return;

        }

        StringTokenizer st = new StringTokenizer( parms, "&" );
        while ( st.hasMoreTokens() ) {

            String e = st.nextToken();
            int sep = e.indexOf( '=' );

            if ( sep >= 0 ) {

                p.setParameter(
                        ObtuseUtil.decodePercent( e.substring( 0, sep ) ).trim(),
                        ObtuseUtil.decodePercent( e.substring( sep + 1 ) )
                );

            }

        }

    }

    public String encode() {

        StringBuilder buf = new StringBuilder();
        String ampersand = "";
        for ( String key : getKeys() ) {

            String value = getParameter( key );
            String encoding = "UTF-8";
            try {

                buf.append( ampersand );
                buf.append( URLEncoder.encode( key, encoding ) );
                buf.append( "=" );
                buf.append( URLEncoder.encode( value, encoding ) );

                ampersand = "&";

            } catch ( UnsupportedEncodingException e ) {

                Logger.logErr( "ERROR:  payment notification request lost!" );
                Trace.emitTrace(
                        "Unsupported encoding reported when trying to encode post parameters" +
                        " (requested encoding was \"" + encoding + "\") - bye!",
                        e
                );
                System.exit( 1 );

            }

        }

        return buf.toString();

    }

    public static PostParameters makeProperties( final String encodedProperties ) {

        try {

            PostParameters rval = new PostParameters( encodedProperties );

            return rval;

        } catch ( InterruptedException e ) {

            Logger.logErr(
                    "ERROR:  unable to decode \"" + encodedProperties + "\"", e
            );
            return null;

        }

    }

    public static boolean crossCompare( final String encodedV1String, final String encodedV2String ) {

        boolean worked = true;

        try {

            PostParameters v1 = new PostParameters( encodedV1String );

            PostParameters v2 = new PostParameters( encodedV2String );

            for ( String key : v1.getKeys() ) {

                String value1 = v1.getParameter( key );

                if ( value1 == null ) {

                    Logger.logMsg( "v1's key \"" + key + "\"'s value is missing from v1" );
                    worked = false;

                } else {

                    String value2 = v2.getParameter( key );

                    if ( value2 == null ) {

                        Logger.logMsg( "v1's key \"" + key + "\" is missing from v2" );
                        worked = false;

                    } else if ( !value1.equals( value2 ) ) {

                        Logger.logMsg(
                                "v1's key \"" + key + "\" yielded \"" + value1 + "\" but v2 yielded \"" + value2 + "\""
                        );
                        worked = false;

                    }

                }

            }

            for ( String key : v2.getKeys() ) {

                String value2 = v2.getParameter( key );

                if ( value2 == null ) {

                    Logger.logMsg( "v2's key \"" + key + "\"'s value is missing from v2" );
                    worked = false;

                } else {

                    String value1 = v1.getParameter( key );

                    if ( value1 == null ) {

                        Logger.logMsg( "v2's key \"" + key + "\" is missing from v1" );
                        worked = false;

                    } else if ( !value2.equals( value1 ) ) {

                        Logger.logMsg(
                                "v2's key \"" + key + "\" yielded \"" + value2 + "\" but v1 yielded \"" + value1 + "\""
                        );
                        worked = false;

                    }

                }

            }

        } catch ( InterruptedException e ) {

            Logger.logErr(
                    "cross compare of \"" + encodedV1String + "\" vs \"" + encodedV2String +
                    "\" failed with an exception", e
            );
            worked = false;

        }

        return worked;

    }
}
