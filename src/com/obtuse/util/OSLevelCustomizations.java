/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Describe something which does OS-specific customizations.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class OSLevelCustomizations {

    private static boolean _gotOSLevelCustomizations = false;
    private static OSLevelCustomizations s_osLevelCustomizations;

    public static boolean s_forceWindows = false;

    public static String s_forceLookAndFeel = null;

    public abstract void setPreferencesHandler( PreferencesHandler prefsHandler );

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

    public abstract void setQuitCatcher( QuitCatcher quitCatcher );

    public abstract QuitCatcher getQuitCatcher();

    public abstract void setAboutWindowHandler( AboutWindowHandler aboutWindowHandler );

    public abstract AboutWindowHandler getAboutWindowHandler();

    public abstract void setDockBadge( String msg );

    public abstract void setDockIconImage( Image icon );

    public static OSLevelCustomizations getCustomizer() {

        if ( !OSLevelCustomizations._gotOSLevelCustomizations ) {

            String osSpecificCustomizerClassName;
            if ( OSLevelCustomizations.onMacOsX() ) {

                osSpecificCustomizerClassName = "com.obtuse.util.MacCustomization";

            } else if ( OSLevelCustomizations.onWindows() ) {

                osSpecificCustomizerClassName = "com.obtuse.util.WindowsCustomization";

            } else {

                return null;

            }

            String methodName = null;

            try {

                //noinspection RawUseOfParameterizedType
                Class macSpecificCode =
                        OSLevelCustomizations.class.getClassLoader().loadClass( osSpecificCustomizerClassName );
                methodName = "createInstance";
                @SuppressWarnings("unchecked") Method createInstance = macSpecificCode.getDeclaredMethod( methodName );
                createInstance.setAccessible( true );
                //noinspection RedundantArrayCreation
                OSLevelCustomizations.s_osLevelCustomizations = (OSLevelCustomizations)createInstance.invoke( null, new Object[] {} );

            } catch ( ClassNotFoundException e ) {

                Logger.logErr( "unable to find " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );

            } catch ( NoSuchMethodException e ) {

                Logger.logErr( "unable to find " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );

            } catch ( IllegalAccessException e ) {

                Logger.logErr( "unable to invoke " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available" );

            } catch ( InvocationTargetException e ) {

                Logger.logErr(
                        "caught an exception while invoking " + methodName + " method in " + osSpecificCustomizerClassName + " class - assuming customizations are not available"
                );

            }

//            } else if ( OSLevelCustomizations.onWindows() ) {
//
//                // We're on a Windows box!
//
//            }

            OSLevelCustomizations._gotOSLevelCustomizations = true;

        }

        return OSLevelCustomizations.s_osLevelCustomizations;

    }

}
