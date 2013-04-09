package com.obtuse.util;

import com.apple.eawt.*;

import javax.swing.*;
import java.awt.*;

/*
 * Copyright © 2006 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * MacOS-specific customizations.
 * <p/>
 * This method is declared to be package-private to ensure that it is only accessed via reflection. Referencing this
 * class in 'open code' will result in an application which won't launch on non-MacOS systems.
 * <p/>
 */

@SuppressWarnings({ "ClassWithoutToString", "deprecation" })
public class MacCustomization extends OSLevelCustomizations {

    private final Application _app;

    private static QuitCatcher _quitCatcher;

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
     *
     * @param aboutWindowHandler something to call to make an about window visible.
     * @param quitCatcher something to call if a quit event is received (ignored if null).
     */

    public MacCustomization( final AboutWindowHandler aboutWindowHandler, QuitCatcher quitCatcher ) {
        super();

        Trace.event( "doing Mac-specific customizations" );

        System.setProperty( "apple.laf.useScreenMenuBar", "true" );

        _quitCatcher = quitCatcher;

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

        } catch ( IllegalAccessException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        } catch ( UnsupportedLookAndFeelException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        } catch ( InstantiationException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        } catch ( ClassNotFoundException e ) {

            Trace.event( "unable to find " + laf + " look and feel - using system default", e );

        }

        _app = Application.getApplication();
        //noinspection ClassWithoutToString,RefusedBequest
        final ApplicationAdapter basicAdapter = new ApplicationAdapter() {

            @SuppressWarnings( { "RefusedBequest" } )
            public void handleQuit( ApplicationEvent e ) {

                if ( _quitCatcher == null || _quitCatcher.quitAttempted() ) {

                    e.setHandled( true );

                }

            }

            public void handleAbout( ApplicationEvent e ) {

                e.setHandled( true );
                if ( aboutWindowHandler != null ) {

                    aboutWindowHandler.makeVisible();

                }

//                System.out.println( "launch of AboutWindow suppressed as this version of ObtuseUtils does not support it" );
//                AboutWindow.launch();

            }

        };

        _app.addApplicationListener( basicAdapter );

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

    public void setPreferencesHandler( final PreferencesHandler prefsHandler ) {

        //noinspection ClassWithoutToString,RefusedBequest
        ApplicationAdapter preferencesAdapter = new ApplicationAdapter() {

            public void handlePreferences( ApplicationEvent e ) {

                prefsHandler.handlePreferences();

            }

        };

        _app.addApplicationListener( preferencesAdapter );
        _app.setEnabledPreferencesMenu( true );

    }

    @SuppressWarnings("UnusedDeclaration")
    public static OSLevelCustomizations createInstance( AboutWindowHandler aboutWindowHandler, QuitCatcher quitCatcher ) {

        return new MacCustomization( aboutWindowHandler, quitCatcher );

    }

}