/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * Windows-specific customizations.
 * <p>This class is obsolete.</p>
 * @deprecated
 * <p/>
 *
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-Windows systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
@Deprecated
public class WindowsCustomization {

    private AboutWindowHandler _aboutWindowHandler;

    private QuitCatcher _quitCatcher;

    private WindowsCustomization() { super(); }

}