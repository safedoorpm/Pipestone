package com.obtuse.util;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Copyright © 2012 Obtuse Systems Corporation.
 */

/**
 * Describe something which does OS-specific customizations.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class OSLevelCustomizations {

    private static boolean _gotOSLevelCustomizations = false;
    private static OSLevelCustomizations _osLevelCustomizations;

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

    public abstract void setDockBadge( String msg );

    public abstract void setDockIconImage( Image icon );

    public static OSLevelCustomizations getCustomizer( AboutWindowHandler aboutWindowHandler ) {

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
                //noinspection RedundantArrayCreation
                Method createInstance =
                        macSpecificCode.getDeclaredMethod( methodName, new Class[] { AboutWindowHandler.class, QuitCatcher.class } );
                createInstance.setAccessible( true );
                //noinspection RedundantArrayCreation
                OSLevelCustomizations._osLevelCustomizations = (OSLevelCustomizations)createInstance.invoke( null, new Object[] { aboutWindowHandler, null } );

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

        return OSLevelCustomizations._osLevelCustomizations;

    }

}
