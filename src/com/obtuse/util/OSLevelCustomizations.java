/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Describe something which does OS-specific customizations.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class OSLevelCustomizations {

    private static OSLevelCustomizations s_osLevelCustomizations;

    public static boolean s_forceWindows = false;

    public static String s_forceLookAndFeel = null;
    private static QuitCatcher _quitCatcher;
    private final Desktop _desktop;
    private final Taskbar _taskbar;
    private AboutWindowHandler _aboutWindowHandler;
    private PreferencesWindowHandler preferencesWindowHandler;

    private OSLevelCustomizations() {

        super();

        if ( Desktop.isDesktopSupported() ) {

            _desktop = Desktop.getDesktop();

            if ( supportsFeature( Desktop.Action.APP_ABOUT ) ) {

                _desktop.setAboutHandler(
                        e -> {

                            if ( _aboutWindowHandler != null ) {

                                _aboutWindowHandler.makeVisible();

                            }

                        }
                );

            }

            if ( supportsFeature( Desktop.Action.APP_PREFERENCES ) ) {

                _desktop.setPreferencesHandler(

                        e -> {

                            if ( preferencesWindowHandler != null ) {

                                preferencesWindowHandler.handlePreferences();

                            }

                        }

                );

            }

        } else {

            _desktop = null;

        }

        if ( Taskbar.isTaskbarSupported() ) {

            _taskbar = Taskbar.getTaskbar();

        } else {

            _taskbar = null;

        }

    }

    public void setPreferencesHandler( final PreferencesWindowHandler prefsHandler ) {

        preferencesWindowHandler = prefsHandler;

    }

    public static boolean onMacOsX() {

        if ( s_forceWindows ) {

            return false;

        }

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        boolean onMacOSX = lcOSName.startsWith( "mac os x" );

        return onMacOSX;

    }

    public static boolean onWindows() {

        if ( s_forceWindows ) {

            return true;

        }

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        boolean onWindows = lcOSName.startsWith( "windows" );

        return onWindows;
    }

    public void setQuitCatcher( final QuitCatcher quitCatcher ) {

        _quitCatcher = quitCatcher;

    }

    public QuitCatcher getQuitCatcher() {

        return _quitCatcher;

    }

    public void setAboutWindowHandler( final AboutWindowHandler aboutWindowHandler ) {

        _aboutWindowHandler = aboutWindowHandler;

    }

    public AboutWindowHandler getAboutWindowHandler() {

        return _aboutWindowHandler;

    }

    public boolean supportsFeature( final @NotNull Taskbar.Feature feature ) {

        return _taskbar != null && _taskbar.isSupported( feature );

    }

    public boolean supportsFeature( final @NotNull Desktop.Action action ) {

        return _desktop != null && _desktop.isSupported( action );

    }

    public boolean setDockBadge(final String msg ) {

        if ( supportsFeature( Taskbar.Feature.ICON_BADGE_TEXT ) ) {

            _taskbar.setIconBadge( msg );

            return true;

        } else {

            return false;

        }

    }

    public boolean setDockIconImage( final Image icon ) {

        if ( supportsFeature( Taskbar.Feature.ICON_IMAGE ) ) {

            _taskbar.setIconImage( icon );

            return true;

        } else {

            return false;

        }

    }

    public static OSLevelCustomizations getCustomizer() {

        if (s_osLevelCustomizations == null) {

            s_osLevelCustomizations = new OSLevelCustomizations();

        }

        return s_osLevelCustomizations;

    }

    public String toString() {

        return "OSLevelCustomizations( " + ( onMacOsX() ? "Mac OS X" : "MS Windows" ) + " )";

    }

}
