/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.explore;

import com.obtuse.util.SSLUtilities;
import com.obtuse.util.*;
import com.obtuse.util.exceptions.PipestoneSSLException;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;

/**
 * Explore what we can do with SSL sockets.
 */

public class MyExploreSSL {

    private static final SSLContext SSL_CLIENT_CONTEXT;

    @SuppressWarnings("UnusedDeclaration")
    private static final SSLSocketFactory s_sslClientSocketFactory;

//    private String _serverComponentName = null;

    @SuppressWarnings("FieldCanBeLocal")
    private static int s_myIx = 0;

    private static class MyTrustManager implements X509TrustManager {

        private final X509TrustManager _realTrustManager;

        private X509Certificate[] _certChain = null;

        @SuppressWarnings("UnusedDeclaration")
        private String _authType = null;

        private MyTrustManager( X509TrustManager realTrustManager ) {

            super();

            _realTrustManager = realTrustManager;

        }

        public X509Certificate[] getAcceptedIssuers() {

            throw new UnsupportedOperationException( "we don't support getting the accepted issuers" );

        }

        public void checkClientTrusted( X509Certificate[] certChain, String authType ) {

            throw new UnsupportedOperationException( "we don't support checking if the client is trustworthy" );

        }

        public void checkServerTrusted( X509Certificate[] certChain, String authType )
                throws CertificateException {

            //noinspection AssignmentToCollectionOrArrayFieldFromParameter
            _certChain = certChain;
            _authType = authType;

            X509Certificate[] certs = _certChain;
            if ( certs == null ) {

                Logger.logErr( "no certs captured" );
                System.exit( 1 );
            }

            try {

                MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
                MessageDigest md5 = MessageDigest.getInstance( "MD5" );

                Logger.logMsg( "here are the certs:" );
                Logger.logMsg( "" );

                for ( X509Certificate cert : certs ) {

                    Logger.logMsg( "Subject:        " + cert.getSubjectX500Principal() );
                    Logger.logMsg( "Issuer:         " + cert.getIssuerX500Principal() );
                    Logger.logMsg( "effective:      from " + cert.getNotBefore() + " through " + cert.getNotAfter() );
                    Logger.logMsg( "serial number:  " + cert.getSerialNumber() );
                    Logger.logMsg( "sig algorithm:  " + cert.getSigAlgName() );
                    Logger.logMsg( "version:        " + cert.getVersion() );

                    sha1.update( cert.getEncoded() );
                    Logger.logMsg( "SHA1:     " + ObtuseUtil.hexvalue( sha1.digest() ) );

                    md5.update( cert.getEncoded() );
                    Logger.logMsg( "MD5:      " + ObtuseUtil.hexvalue( md5.digest() ) );

                    Logger.logMsg( "serialized form is " + ObtuseUtil.getSerializedSize( cert ) + " bytes long" );
                    Logger.logMsg( "encoded form is " + cert.getEncoded().length + " bytes long" );
                    Logger.logMsg( "cert's class is " + cert.getClass() );

//                    _myIx += 1;
//                    ks.setCertificateEntry( "balzac-" + _myIx, cert );

//                    Logger.logMsg( "added to trusted certs" );
//                    Logger.logMsg( "" );

                }

            } catch ( NoSuchAlgorithmException e ) {

                Logger.logErr( "got a NoSuchAlgorithmException looking for SHA1 or MD5 algorithm", e );

            }

            _realTrustManager.checkServerTrusted( certChain, authType );

        }

    }

    static {

        BasicProgramConfigInfo.init( "Kenosee", "McLuhan", "Test", null );

        SSLContext sslContext = null;
        try {

            Logger.logMsg( "default algorithm is " + TrustManagerFactory.getDefaultAlgorithm() );

            sslContext = SSLUtilities.getOurClientSSLContext();

        } catch ( PipestoneSSLException | IOException e ) {

            Logger.logErr( "initializing SSL client socket factory", e );
            System.exit( 1 );

        }
	SSL_CLIENT_CONTEXT = sslContext;

        s_sslClientSocketFactory = MyExploreSSL.SSL_CLIENT_CONTEXT.getSocketFactory();

    }

    private MyExploreSSL() {

        super();
    }

    /**
     * Take this critter out for a test drive.
     * @param args the program's args.
     */

    public static void main( String[] args ) {

        try {

//            InputStream keystoreInputStream = new ByteArrayInputStream( SavrolaClientKeystore.keystore );

            // Get an empty keystore.

            KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
            ks.load( null, null );

            while ( true ) {

                boolean worked = MyExploreSSL.trialRun( ks, "localhost" );

                if ( worked ) {

                    Logger.logMsg( "it worked!" );

                    break;

                }

            }

            System.exit( 0 );

        } catch ( NoSuchAlgorithmException e ) {

            Logger.logErr( "NoSuchAlgorithmException!", e );
            System.exit( 1 );

        } catch ( KeyStoreException e ) {

            Logger.logErr( "KeyStoreException!", e );
            System.exit( 1 );

        } catch ( IOException e ) {

            Logger.logErr( "IOException!", e );
            System.exit( 1 );

        } catch ( CertificateException e ) {

            Logger.logErr( "CertificateException!", e );
            System.exit( 1 );

        } catch ( KeyManagementException e ) {

            Logger.logErr( "KeyManagementException!", e );
            System.exit( 1 );

        }
    }

    private static boolean trialRun( KeyStore ks, @SuppressWarnings("SameParameterValue") String targetHost )
            throws
            NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException,
            IOException,
            CertificateEncodingException {

        // Do the usual SSL magic.

        SSLContext sslContext = SSLContext.getInstance( "TLS" );

        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
        tmf.init( ks );
        TrustManager[] tms = tmf.getTrustManagers();
        for ( TrustManager tm : tms ) {
            Logger.logMsg( "got trust manager " + tm );
        }

        MyTrustManager myTrustManager = new MyTrustManager( (X509TrustManager)tms[0] );

        sslContext.init( null, new TrustManager[] { myTrustManager }, null );

        SSLSocketFactory factory = sslContext.getSocketFactory();

        Logger.logMsg( "Getting the socket to " + targetHost );

        @SuppressWarnings("MagicNumber")
        SSLSocket sock = (SSLSocket)factory.createSocket( targetHost, 1234 );

        try {

            Logger.logMsg( "Starting the handshake" );

            sock.startHandshake();

            Logger.logMsg( "handshake worked" );

            sock.close();

            return true;

        } catch ( IOException e ) {

            Logger.logErr( "caught an exception during handshake", e );

        }

        X509Certificate[] certs = myTrustManager._certChain;
        if ( certs == null ) {

            Logger.logErr( "no certs captured" );
            System.exit( 1 );
        }

        MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
        MessageDigest md5 = MessageDigest.getInstance( "MD5" );

        Logger.logMsg( "here are the certs:" );
        Logger.logMsg( "" );

        for ( X509Certificate cert : certs ) {

            Logger.logMsg( "Subject:        " + cert.getSubjectX500Principal() );
            Logger.logMsg( "Issuer:         " + cert.getIssuerX500Principal() );
            Logger.logMsg( "effective:      from " + cert.getNotBefore() + " through " + cert.getNotAfter() );
            Logger.logMsg( "serial number:  " + cert.getSerialNumber() );
            Logger.logMsg( "sig algorithm:  " + cert.getSigAlgName() );
            Logger.logMsg( "version:        " + cert.getVersion() );

            sha1.update( cert.getEncoded() );
            Logger.logMsg( "SHA1:     " + ObtuseUtil.hexvalue( sha1.digest() ) );

            md5.update( cert.getEncoded() );
            Logger.logMsg( "MD5:      " + ObtuseUtil.hexvalue( md5.digest() ) );

            Logger.logMsg( "serialized form is " + ObtuseUtil.getSerializedSize( cert ) + " bytes long" );
            Logger.logMsg( "encoded form is " + cert.getEncoded().length + " bytes long" );
            Logger.logMsg( "cert's class is " + cert.getClass() );

            MyExploreSSL.s_myIx += 1;
            ks.setCertificateEntry( "balzac-" + MyExploreSSL.s_myIx, cert );

            Logger.logMsg( "added to trusted certs" );
            Logger.logMsg( "" );

        }

        return false;

    }

}