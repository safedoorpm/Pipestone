/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TrackedWindow extends JFrame {

//    private final String _appName;

    private final String _windowPrefsName;

    private final String _windowGeometryPrefsKey;

    private boolean _inToString = false;

    public TrackedWindow( final String windowPrefsName ) {
        super();

//        _appName = appName;
        _windowPrefsName = windowPrefsName;
        _windowGeometryPrefsKey = _windowPrefsName + ".geometry";

        //noinspection ClassWithoutToString
        addComponentListener(
                new ComponentListener() {

                    public void componentResized( final ComponentEvent componentEvent ) {

                        saveWindowGeometry();

                    }

                    public void componentMoved( final ComponentEvent componentEvent ) {

                        saveWindowGeometry();

                    }

                    public void componentShown( final ComponentEvent componentEvent ) {

                    }

                    public void componentHidden( final ComponentEvent componentEvent ) {

                    }

                }
        );

//        restoreWindowGeometry();

    }

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public void saveWindowGeometry() {

        Rectangle windowGeometry = getBounds();
        saveWindowGeometry( windowGeometry );

    }

    private void saveWindowGeometry( final Rectangle windowGeometry ) {

//        Logger.logMsg( _windowGeometryPrefsKey + ":  saving window geometry \"" + windowGeometry + "\"" );
        if ( BasicProgramConfigInfo.getPreferences() != null ) {

            BasicProgramConfigInfo.getPreferences().putByteArray(
                    _windowGeometryPrefsKey,
                    ObtuseUtil.getSerializedVersion( windowGeometry, false )
            );

        }

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    protected void restoreWindowLocation() {

        Rectangle windowGeometry = getSavedGeometry();
        Point windowLocation = new Point( windowGeometry.x, windowGeometry.y );
//        Logger.logMsg( _windowGeometryPrefsKey + ":  restoring window location \"" + windowLocation + "\"" );
        setLocation( windowLocation );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    protected void restoreWindowGeometry( final int width, final int height ) {

        Rectangle windowGeometry = getSavedGeometry();
        if ( windowGeometry.width < width ) {

            windowGeometry.width = width;

        }
        if ( windowGeometry.height < height ) {

            windowGeometry.height = height;

        }

        setBounds( windowGeometry );
        setPreferredSize( new Dimension( windowGeometry.width, windowGeometry.height ) );

    }

    public Rectangle getSavedGeometry() {

        byte[] savedLocationBytes = BasicProgramConfigInfo.getPreferences() == null
                ?
                null

                : BasicProgramConfigInfo.getPreferences().getByteArray(
                _windowGeometryPrefsKey,
                null
        );

        Rectangle savedGeometry;
        if ( savedLocationBytes == null ) {

            savedGeometry = getBounds();
            saveWindowGeometry( savedGeometry );

        } else {

            // De-serialize the saved window geometry.
            // If de-serialization fails then save the current window geometry as the saved window geometry.

            savedGeometry = (Rectangle) ObtuseUtil.recoverSerializedVersion( savedLocationBytes, false );
            if ( savedGeometry == null ) {

                savedGeometry = getBounds();
                saveWindowGeometry( savedGeometry );

            }

        }

        //noinspection StatementWithEmptyBody
        if ( !_inToString ) {

//            Logger.logMsg( _windowGeometryPrefsKey + ":  fetching window geometry " + savedGeometry + "\"" );

        }

        return savedGeometry;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public String getTrackedWindowName() {

        return _windowPrefsName;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public String getTrackedWindowLocationPrefsKey() {

        return _windowGeometryPrefsKey;

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String toString() {

        _inToString = true;
        String rval = "TrackedWindow( " + _windowPrefsName + ", " + getSavedGeometry() + " )";
        _inToString = false;

        return rval;

    }

}
