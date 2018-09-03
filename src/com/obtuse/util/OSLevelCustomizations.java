/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;

/**
 * Describe something which does OS-specific customizations.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class OSLevelCustomizations {

//    private static boolean _gotOSLevelCustomizations = false;
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

//        if ( supportsFeature( Desktop.Action.APP_PREFERENCES ) ) {
//
//            _pref
//            _desktop.setPreferencesHandler(
//                    new java.awt.desktop.PreferencesWindowHandler() {
//                        @Override
//                        public void handlePreferences(PreferencesEvent e) {
//
//
//                        }
//                    }
//            );
//
//        }

    }

    public static boolean onMacOsX() {

        if ( s_forceWindows ) {

            return false;

        }

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        @SuppressWarnings("UnnecessaryLocalVariable")
        boolean onMacOSX = lcOSName.startsWith( "mac os x" );

        return onMacOSX;

    }

    public static boolean onWindows() {

        if ( s_forceWindows ) {

            return true;

        }

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        @SuppressWarnings("UnnecessaryLocalVariable")
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

//            String osSpecificCustomizerClassName;
//            if ( OSLevelCustomizations.onMacOsX() ) {
//
//                osSpecificCustomizerClassName = "com.obtuse.util.MacCustomization";
//
//            } else if ( OSLevelCustomizations.onWindows() ) {
//
//                osSpecificCustomizerClassName = "com.obtuse.util.WindowsCustomization";
//
//            } else {
//
//                return null;
//
//            }
//
//            String methodName = null;
//
//            try {
//
//                //noinspection RawUseOfParameterizedType
//                Class macSpecificCode =
//                        OSLevelCustomizations.class.getClassLoader().loadClass( osSpecificCustomizerClassName );
//                methodName = "createInstance";
//                @SuppressWarnings("unchecked") Method createInstance = macSpecificCode.getDeclaredMethod( methodName );
//                createInstance.setAccessible( true );
//                //noinspection RedundantArrayCreation
//                OSLevelCustomizations.s_osLevelCustomizations = (OSLevelCustomizations)createInstance.invoke( null, new Object[] {} );
//
//            } catch ( ClassNotFoundException e ) {
//
//                Logger.logErr( "unable to find " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );
//
//            } catch ( NoSuchMethodException e ) {
//
//                Logger.logErr( "unable to find " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );
//
//            } catch ( IllegalAccessException e ) {
//
//                Logger.logErr( "unable to invoke " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );
//
//            } catch ( InvocationTargetException e ) {
//
//                Logger.logErr(
//                        "caught an exception while invoking " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available"
//                );
//
//            }
//
//            OSLevelCustomizations._gotOSLevelCustomizations = true;

//        }
//
//        return OSLevelCustomizations.s_osLevelCustomizations;

//    }

}
