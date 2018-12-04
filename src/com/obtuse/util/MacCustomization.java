/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * MacOS-specific customizations.
 * <p>This class is obsolete.</p>
 * @deprecated
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-MacOS systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
@Deprecated
public class MacCustomization {

    private MacCustomization() {
        super();
    }

}