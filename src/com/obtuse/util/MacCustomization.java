/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.*;

/**
 * MacOS-specific customizations.
 * <p>This class is obsolete.</p>
 * @deprecated
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-MacOS systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "deprecation", "UnusedDeclaration" })
@Deprecated
public class MacCustomization {

    private MacCustomization() {
        super();
    }

//    /**
//     * Perform various Mac OS X specific customizations.
//     * <p/>
//     * At the present time, this method does the following:
//     * <p/>
//     * Handles "Quit Application" events in a manner which
//     * ensures that the application quits. Without this customization, the "Quit Application" event is effectively
//     * rejected. One consequence of this rejection is that attempts to logoff or shutdown while the application is
//     * running are aborted.  With this customization in place, a logoff or shutdown attempt is "approved" by the
//     * application.
//     */
//
//    public MacCustomization() {
//        super();
//
//    }
//            private OSLevelCustomizations() {
//
//
//        }
//
//    @SuppressWarnings("UnusedDeclaration")
//    public static OSLevelCustomizations createInstance() {
//
//        return new MacCustomization();
//
//    }
//
//    public String toString() {
//
//        return "MacCustomization()";
//
//    }

}