package com.obtuse.util;

/**
 * Something which handles quit events on Mac OS X.
 * <p/>
 * See the {@link com.obtuse.util.MacCustomization} class for more information.
 * <p/>
 * Copyright © 2012 Obtuse Systems Corporation.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface QuitCatcher {

    boolean quitAttempted();

}
