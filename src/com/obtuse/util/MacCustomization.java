/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.apple.eawt.*;

import javax.swing.*;
import java.awt.*;

/**
 * MacOS-specific customizations.
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-MacOS systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "deprecation", "UnusedDeclaration" })
public class MacCustomization extends OSLevelCustomizations {

    private final Application _app;

    private static QuitCatcher _quitCatcher;

    private AboutWindowHandler _aboutWindowHandler;
    private PreferencesHandler _prefsHandler;

    /**
     * Perform various Mac OS X specific customizations.
     * <p/>
     * At the present time, this method does the following:
     * <p/>
     * Handles "Quit Application" events in a manner which
     * ensures that the application quits. Without this customization, the "Quit Application" event is effectively
     * rejected. One consequence of this rejection is that attempts to logoff or shutdown while the application is
     * running are aborted.  With this customization in place, a logoff or shutdown attempt is "approved" by the
     * application.
     */

    public MacCustomization() {
        super();

        Trace.event( "doing Mac-specific customizations" );

        System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        String laf = "apple.laf.AquaLookAndFeel";
        // String laf = "javax.swing.plaf.metal.MetalLookAndFeel";
        // String laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

        if ( OSLevelCustomizations.s_forceLookAndFeel != null ) {

            laf = OSLevelCustomizations.s_forceLookAndFeel;

        }

//        Logger.logMsg( "skipping laf" );

        Logger.logMsg( "Mac laf = " + laf );

        try {

            UIManager.setLookAndFeel( laf );

        } catch ( IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        }

	_app = Application.getApplication();
        //noinspection ClassWithoutToString,RefusedBequest
        ApplicationAdapter basicAdapter = new ApplicationAdapter() {

            @SuppressWarnings( { "RefusedBequest" } )
            public void handleQuit( ApplicationEvent e ) {

                if ( _quitCatcher == null || _quitCatcher.quitAttempted() ) {

                    e.setHandled( true );

                }

            }

            public void handleAbout( ApplicationEvent e ) {

                e.setHandled( true );
                if ( _aboutWindowHandler != null ) {

                    _aboutWindowHandler.makeVisible();

                }

            }

        };

        _app.addApplicationListener( basicAdapter );

        ApplicationAdapter preferencesAdapter = new ApplicationAdapter() {

            public void handlePreferences( ApplicationEvent e ) {

                e.setHandled( true );
                if ( _prefsHandler != null ) {

                    _prefsHandler.handlePreferences();

                }

            }

        };

        _app.addApplicationListener( preferencesAdapter );

        _app.addAboutMenuItem();

    }

    public void setDockBadge( String msg ) {

        _app.setDockIconBadge( msg );

    }

    public void setDockIconImage( Image icon ) {

        _app.setDockIconImage( icon );

    }

    public QuitCatcher getQuitCatcher() {

        return _quitCatcher;

    }

    public void setQuitCatcher( QuitCatcher quitCatcher ) {

        _quitCatcher = quitCatcher;

    }

    public void setPreferencesHandler( PreferencesHandler prefsHandler ) {

        //noinspection ClassWithoutToString,RefusedBequest
        _prefsHandler = prefsHandler;
        _app.setEnabledPreferencesMenu( prefsHandler != null );

    }

    @SuppressWarnings("UnusedDeclaration")
    public static OSLevelCustomizations createInstance() {

        return new MacCustomization();

    }

    public void setAboutWindowHandler( AboutWindowHandler aboutWindowHandler ) {

        _aboutWindowHandler = aboutWindowHandler;

    }

    public AboutWindowHandler getAboutWindowHandler() {

        return _aboutWindowHandler;

    }

}