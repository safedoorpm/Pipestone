/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import javax.swing.*;
import java.awt.*;

/**
 * Windows-specific customizations.
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-Windows systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "deprecation", "UnusedDeclaration" })
public class WindowsCustomization extends OSLevelCustomizations {

//    private final Application _app;

    private AboutWindowHandler _aboutWindowHandler;

    private QuitCatcher _quitCatcher;

    /**
     * Perform various Windows specific customizations.
     * <p/>
     * At the present time, this method sets the look and feel to
     * <p/>
     * Handles "Quit Application" events in a manner which
     * ensures that the application quits. Without this customization, the "Quit Application" event is effectively
     * rejected. One consequence of this rejection is that attempts to logoff or shutdown while the application is
     * running are aborted.  With this customization in place, a logoff or shutdown attempt is "approved" by the
     * application.
     */

    public WindowsCustomization() {
        super();

        Trace.event( "doing Windows-specific customizations" );

//        System.setProperty( "apple.laf.useScreenMenuBar", "true" );

//        String laf = "apple.laf.AquaLookAndFeel";
//        String laf = "javax.swing.plaf.metal.MetalLookAndFeel";
        String laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

        if ( OSLevelCustomizations.s_forceLookAndFeel != null ) {

            laf = OSLevelCustomizations.s_forceLookAndFeel;

        }

        Logger.logMsg( "Windows laf = " + laf );

        try {

            UIManager.setLookAndFeel( laf );

        } catch ( IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        }

    }

    public void setDockBadge( String msg ) {

//        _app.setDockIconBadge( msg );

    }

    public void setDockIconImage( Image icon ) {

//        _app.setDockIconImage( icon );

    }

    public void setQuitCatcher( QuitCatcher quitCatcher ) {

        _quitCatcher = quitCatcher;

    }

    public QuitCatcher getQuitCatcher() {

        return _quitCatcher;

    }

    public void setAboutWindowHandler( AboutWindowHandler aboutWindowHandler ) {

        _aboutWindowHandler = aboutWindowHandler;

    }

    public AboutWindowHandler getAboutWindowHandler() {

        return _aboutWindowHandler;

    }

    public void setPreferencesHandler( final PreferencesHandler prefsHandler ) {

    }

    @SuppressWarnings("UnusedDeclaration")
    public static OSLevelCustomizations createInstance() {

        return new WindowsCustomization();

    }

}