package com.obtuse.util.gowing.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingNotPackable;
import com.obtuse.util.gowing.GowingPackable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 %%% Something clever goes here.
 */

public class GowingUtil {

    /**
     Effectively mark this class as a utility class.
     */

    private GowingUtil() {

        super();

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isActuallyPackable( @NotNull final GowingPackable entity ) {

        return !(entity instanceof GowingNotPackable);

    }

    @SuppressWarnings("unused")
    public static String describeGowingEntitySafely( final GowingPackable packable ) {

        if ( packable == null ) {

            return "null";

        }

        try {

            return packable.toString();

        } catch ( Throwable e ) {

            return "safeDescription( " + packable.getClass().getCanonicalName() + ")";

        }

    }

    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static void main( final String[] args ) {

        Byte[] a = new Byte[]{ (byte)1, null, (byte)3, (byte)4 };

        doit( a );

    }

    public static void doit( @Nullable final Byte@NotNull[] a ) {

        System.out.print( '[' );
        String comma = "";
        for ( Byte b : a ) {

            System.out.print( comma );
            comma = ",";

            if ( b == null ) {

                System.out.print( GowingConstants.NULL_VALUE );

            } else {

                int ll = b.intValue();
                int high = ( ll >> 4 ) & 0xf;
                int low = ll & 0xf;

                System.out.print( HEX_CHARS[high] );
                System.out.print( HEX_CHARS[low] );

            }

        }

        System.out.print( ']' );
        System.out.println();

    }

    public static void verifyActuallyPackable( @NotNull final String who, @Nullable final EntityName what, @NotNull final GowingPackable entity ) {

        if ( !isActuallyPackable( entity ) ) {

            throw new IllegalArgumentException( who + ":  " + what + " is not actually packable - " + entity );

        }

    }

}
