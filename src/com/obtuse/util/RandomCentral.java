/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

/**
 * A thread-safe centralized random number generator.
 */

@SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject", "UnusedDeclaration" })
public class RandomCentral {

    private static boolean s_beSecure = true;

    private final Random _generator;

    private static RandomCentral _ourInstance;

    private RandomCentral() {
        super();

        _generator = s_beSecure ? new SecureRandom() : new Random();

    }

    /**
     Specify if we should use an instance of {@link SecureRandom}.
     <p>This class starts out in secure mode.
     Checking the return value of a call to {@link #areWeSecure()} is a simple and safe way to determine if this JVM is using an
     instance of {@link SecureRandom}.
     </p>
     @param beSecure {@code true} if we should use a {@code SecureRandom} instance; {@code false} if we should use a{@link Random} instance.
     @throws IllegalArgumentException if this class has already created its random number generator instance.
     */

    public static void setBeSecure( boolean beSecure ) {

        if ( _ourInstance == null ) {

            s_beSecure = beSecure;

        } else {

            throw new IllegalArgumentException( "RandomCentral:  static instance already set - too late to call RandomCentral.setBeSecure" );

        }

    }

    /**
     Terminate this JVM IMMEDIATELY if this class has a non-secure random number generator
     (not an instance of {@link SecureRandom}).
     <p>Note that by "IMMEDIATELY" we really do mean IMMEDIATELY.
     In particular . . .
     <ul>
     <li>No JVM shutdown hooks are launched</li>
     <li>No uninvoked finalizers are launched if finalization-on-exit has been enabled</li>
     <li>If the shutdown sequence has already been initiated then this method does not wait
     for any running shutdown hooks or finalizers to finish their work</li>
     </ul>
     </p>
     */

    public static synchronized void mustBeSecure() {

        try {

            setBeSecure( true );

        } catch ( IllegalArgumentException e ) {

            // Just ignore the exception

        }

        if ( areWeSecure() ) {

            System.err.println( "RandomCentral:  we are running with an instance of SecureRandom" );

        } else {

            System.err.println( "RandomCentral:  we are not running with an instance of SecureRandom - bye!" );
            Runtime.getRuntime().halt( 1 );

        }

    }

    public static boolean getBeSecure() {

        return s_beSecure;

    }

    public static synchronized boolean areWeSecure() {

        return getInstance()._generator instanceof SecureRandom;

    }

    @NotNull
    public static synchronized RandomCentral getInstance() {

        if ( _ourInstance == null ) {

            _ourInstance = new RandomCentral();

        }

        return RandomCentral._ourInstance;

    }

    public static long nextLong() {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextLong();

        }

    }

    public static int nextInt() {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextInt();

        }

    }

    public static int nextInt( final int n ) {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextInt( n );

        }

    }

    public String toString() {

        return "RandomCentral( <<singleton>> )";

    }

    public static void main( String[] args ) {

        mustBeSecure();

    }

}
