/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.PipestoneSSLException;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class SSLUtilities {

    private static final Map<SSLContextWrapper, SSLContextWrapper> _sslContexts =
	    new HashMap<>();

    /**
     * Carry an {@link javax.net.ssl.SSLContext} around in a package that identifies it by the keystore that was used to create it.
     * This allows us to avoid having dozens of different {@link javax.net.ssl.SSLContext}s which all reference the same keystores.
     */

    private static class SSLContextWrapper {

        private final boolean _clientMode;

        private SSLContext _sslContext;

        private final String _keystoreFname;

        private final char[] _keystorePassword;

        private SSLContextWrapper( final boolean clientMode, final String keystoreFname, final char[] keystorePassword ) {
            super();

            _clientMode = clientMode;
            _keystoreFname = keystoreFname;
            _keystorePassword = keystorePassword.clone();

        }

        private void setSSLContext( final SSLContext sslContext ) {

            _sslContext = sslContext;

        }

        private SSLContext getSSLContext() {

            return _sslContext;

        }

        public int hashCode() {

            return _keystoreFname.hashCode() ^ new Integer( _keystorePassword.length ).hashCode();

        }

        @SuppressWarnings( { "EqualsWhichDoesntCheckParameterClass" } )
        public boolean equals( final Object xrhs ) {

            try {

                SSLContextWrapper rhs = (SSLContextWrapper)xrhs;
                if ( rhs == null ) {

                    return false;

                }

                if ( _keystoreFname.equals( rhs._keystoreFname ) &&
                     _clientMode == rhs._clientMode &&
                     _keystorePassword.length == rhs._keystorePassword.length ) {

                    for ( int i = 0; i < _keystorePassword.length; i += 1 ) {

                        if ( _keystorePassword[i] != rhs._keystorePassword[i] ) {

                            return false;

                        }

                    }

                    return true;

                }

                return false;

            } catch ( ClassCastException e ) {

                return false;

            }

        }

        public String toString() {

            return "SSLContextWrapper( client mode = " + _clientMode + ", keystore = " + _keystoreFname + " )";

        }

    }

    private SSLUtilities() {
        super();

    }

    /**
     * Get or create an {@link javax.net.ssl.SSLContext} associated with a specified keystore file and password.
     *
     * @param clientMode          true if we want a client-mode context, false otherwise.
     * @param keystoreFileName       the keystore file.
     * @param keystoreInputStream an input stream referring to the keystore file.
     * @param keystorePassword    its password.
     * @param keyPassword         optional key password.
     *
     * @return the SSL context associated with the keystore file and password.
     *
     * @throws com.obtuse.util.exceptions.PipestoneSSLException if the attempt fails.
     */

    public static SSLContext getSSLContext(
            final boolean clientMode,
            final String keystoreFileName,
            final InputStream keystoreInputStream,
            final char[] keystorePassword,
            @Nullable final char[] keyPassword
    )
            throws
            PipestoneSSLException {

        synchronized ( SSLUtilities._sslContexts ) {

            SSLContextWrapper tmp = new SSLContextWrapper( clientMode, keystoreFileName, keystorePassword );
            if ( SSLUtilities._sslContexts.containsKey( tmp ) ) {

                return SSLUtilities._sslContexts.get( tmp ).getSSLContext();

            } else {

                tmp.setSSLContext(
                        SSLUtilities.createWrappedSSLContext(
                                clientMode,
                                keystoreInputStream,
                                keystorePassword,
                                keyPassword
                        )
                );

                SSLUtilities._sslContexts.put( tmp, tmp );
                return tmp.getSSLContext();

            }

        }

    }

    public static SSLContext getOurClientSSLContext()
            throws PipestoneSSLException, IOException {

        return SSLUtilities.getSSLContext(
                true,
                "McLuhanClient.keystore",
                ResourceUtils.openResource( "McLuhanClient.keystore", "net/kenosee/vitruvius/resources" ),
                // new char[] { 'p', 'i', 'c', 'k', 'l', 'e', 's' }
                "LondonStrumps".toCharArray(),
                null
        );

    }

    private static SSLContext createSSLContext(
            final boolean clientMode,
            final InputStream keyStoreInputStream,
            final char[] keystorePassword,
            final char[] keyPassword
    )
            throws
            KeyStoreException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyManagementException,
            UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( keyStoreInputStream, keystorePassword );

        SSLContext sslContext = SSLContext.getInstance( "TLS" );

        if ( clientMode ) {

            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
            tmf.init( keyStore );
            sslContext.init( null, tmf.getTrustManagers(), null );

        } else {

            KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
            kmf.init( keyStore, keyPassword );
            sslContext.init( kmf.getKeyManagers(), null, null );

        }

        return sslContext;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static SSLContext createWrappedSSLContext(
            final boolean clientMode,
            final File keystoreFile,
            final char[] keystorePassword,
            final char[] keyPassword
    )
            throws FileNotFoundException, PipestoneSSLException {

        FileInputStream keyStoreInputStream = null;

        try {

            keyStoreInputStream = new FileInputStream( keystoreFile );
            return SSLUtilities.createWrappedSSLContext( clientMode, keyStoreInputStream, keystorePassword, keyPassword );

        } finally {

            ObtuseUtil.closeQuietly( keyStoreInputStream );

        }

    }

    public static SSLContext createWrappedSSLContext(
            final boolean clientMode, final InputStream keyStoreInputStream, final char[] keystorePassword, final char[] keyPassword
    )
            throws
            PipestoneSSLException {

        try {

            return SSLUtilities.createSSLContext( clientMode, keyStoreInputStream, keystorePassword, keyPassword );

        } catch ( NoSuchAlgorithmException e ) {

            throw new PipestoneSSLException( "caught a NoSuchAlgorithmException", e );

        } catch ( KeyManagementException e ) {

            throw new PipestoneSSLException( "caught a KeyManagementException", e );

        } catch ( FileNotFoundException e ) {

            throw new PipestoneSSLException( "caught a FileNotFoundException", e );

        } catch ( IOException e ) {

            throw new PipestoneSSLException( "caught an IOException", e );

        } catch ( CertificateException e ) {

            throw new PipestoneSSLException( "caught a CertificateException", e );

        } catch ( KeyStoreException e ) {

            throw new PipestoneSSLException( "caught a KeyStoreException", e );

        } catch ( UnrecoverableKeyException e ) {

            throw new PipestoneSSLException( "caught an UnrecoverableKeyException", e );

        }

    }

}
