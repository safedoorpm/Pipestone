/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

public interface MessageProxy {

    void fatal( String msg );

    void fatal( String msg1, String msg2 );

    void fatal( String msg, Throwable e );

    void fatal(
            String msg1,
            @Nullable
            String msg2,
            String buttonContents
    );

    void error( String msg );

    void error( String msg1, String msg2 );

    void error( String msg, Throwable e );

    void error( String msg1,
                @Nullable
                String msg2, String buttonContents
    );

    void info( String msg );

    void info( String msg1, String msg2 );

    void info(
            String msg1,
            @Nullable
            String msg2, String buttonContents
    );

}