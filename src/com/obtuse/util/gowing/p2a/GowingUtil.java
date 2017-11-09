package com.obtuse.util.gowing.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.GowingPackable;

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

}
