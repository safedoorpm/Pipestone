package com.obtuse.ui.macos;

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ManageMainMacMenu {

    private static boolean s_initialized = false;

    private static final java.util.List<ObtuseQuitHandler> s_quitHandlers = new ArrayList<>();

    static {

        initialize();

    }

    /**
     Add a {@link ObtuseQuitHandler} to the quit event queue.
     <p>When a quit event occurs, quit handlers are handled in oldest (longest on the queue) to newest (most recently added to the queue) order.</p>
     <p>Calls to this method are synchronized and may occur from any thread.</p>
     @param quitHandler the {@link ObtuseQuitHandler} to add to the quit event queue.
     <p>
     If the specified quit handler is already on the quit event queue then it is removed from the queue before being added.
     Note that this makes what might have been a rather old quit handler into the newest quit handler.
     <u>This can cause a change to program behaviour if quit handlers make assumptions about the order that they are invoked in.</u></p>
     */

    public static synchronized void addQuitHandler( @NotNull final ObtuseQuitHandler quitHandler ) {

        removeQuitHandler( quitHandler );

        s_quitHandlers.add( 0, quitHandler );

    }

    /**
     Remove a {@link ObtuseQuitHandler} from the quit event queue.
     <p>Calls to this method are synchronized and may occur from any thread.</p>
     @param quitHandler the {@link ObtuseQuitHandler} to be removed.
     @return {@code true} if the specified handler was actually on the quit event queue at the moment this method was called;
     {@code false} otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static synchronized boolean removeQuitHandler( @NotNull final ObtuseQuitHandler quitHandler ) {

        return s_quitHandlers.remove( quitHandler );

    }

    private static synchronized boolean fireQuitHandlers( final QuitEvent e ) {

        java.util.List<ObtuseQuitHandler> handlers = new ArrayList<>( s_quitHandlers );
        boolean quitWhenDone = true;
        for ( ObtuseQuitHandler quitHandler : handlers ) {

            if ( !quitHandler.quitRequested( e ) ) {

                Logger.logMsg( quitHandler.getFullName() + " aborted quit event" );

                quitWhenDone = false;

            }

        }

        return quitWhenDone;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "MacOS Utilities", "testing", null );

        JFrame jFrame = new JFrame( "My JFrame" );

        JButton jb = new JButton( "Click Here" );
        jb.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        Logger.logMsg( "\"Click Here\" button clicked" );

//                        File userHome = new File( System.getProperty( "user.home" ) + "/xxx" );
//                        Desktop.getDesktop().browseFileDirectory( userHome );

                        _startTime = System.currentTimeMillis();
                        Logger.logMsg( "total of " + ObtuseUtil.readable( countBytesRecursively( new File( "/Users/danny/Baddeck" ), 0 ) ) + " found" );

                    }

                }
        );

        jFrame.setContentPane( jb );
        jFrame.pack();
        jFrame.setVisible( true );

    }

    public static void initialize() {

        if ( s_initialized ) {

            return;

        }

        if ( !Desktop.isDesktopSupported() ) {

            Logger.logMsg( "Desktop stuff not supported on this platform" );

            System.exit(1 );

        }

        Desktop desktop = Desktop.getDesktop();
        desktop.setAboutHandler(
                new AboutHandler() {

                    @Override
                    public void handleAbout( final AboutEvent e ) {

                        handleAboutRequest( e );

//                        JOptionPane.showMessageDialog( null, "About dialog" );

                    }

                }
        );

        desktop.setPreferencesHandler(
                new PreferencesHandler() {

                    @Override
                    public void handlePreferences( final PreferencesEvent e ) {

                        handlePreferencesRequest( e );

//                        JOptionPane.showMessageDialog( null, "Preferences dialog" );

                    }

                }
        );

        desktop.setQuitHandler(
                new QuitHandler() {

                    @Override
                    public void handleQuitRequestWith( final QuitEvent e, final QuitResponse response ) {

                        Logger.logMsg( "ManageMainMacMenu.quitHandler:  got a quit request, calling ObtuseQuitHandler's" );

                        boolean quitWhenDone = handleQuitRequest( e );

                        if ( quitWhenDone ) {

                            Logger.logMsg( "ManageMainMacMenu.quitHandler:  everybody agrees we should quit - bye!" );

                            response.performQuit();

                        } else {

                            Logger.logMsg( "ManageMainMacMenu.quitHandler:  someone vetoed the quit event - life goes on . . ." );

                            response.cancelQuit();

                        }

//                        JOptionPane.showMessageDialog( null, "Quit dialog" );

                    }

                }
        );

        s_initialized = true;

    }

    private static void handleAboutRequest( final AboutEvent e ) {

        Logger.logMsg( "ManageMainMacMenu:  got an about request from the desktop facility" );

    }

    private static boolean handleQuitRequest( final QuitEvent e ) {

        Logger.logMsg( "ManageMainMacMenu:  got a quit request from the Desktop facility" );

        return fireQuitHandlers( e );

    }

    private static void handlePreferencesRequest( final PreferencesEvent e ) {

        Logger.logMsg( "ManageMainMacMenu:  got a preferences request from the desktop facility" );

    }

    private static long _startTime;
    private static boolean _done = false;

    private static long countBytesRecursively( @NotNull final File where, int depth ) {

        if ( _done ) {

            return 0L;

        }

        long allBytesCount = 0L;

        File[] files = where.listFiles();
        if ( files != null ) {

            for ( File f : files ) {

                _done = _done || _startTime + 10 * 1000L < System.currentTimeMillis();

                if ( _done ) {

                    return allBytesCount;

                }

                if ( f.isDirectory() ) {

                    allBytesCount += countBytesRecursively( f, depth + 1 );

                } else if ( f.isFile() ) {

                    try ( FileInputStream fis = new FileInputStream( f ) ) {

                        int fileByteCount = 0;
                        byte[] buffer = new byte[1024 * 1024];
                        int rlen;
                        while ( ( rlen = fis.read( buffer ) ) > 0 ) {

                            fileByteCount += rlen;

                        }

                        allBytesCount += fileByteCount;
                        Logger.logMsg( /*ObtuseUtil.replicate( "   ", depth + 1 ) +*/ ObtuseUtil.lpadReadable( fileByteCount, 15 ) + " bytes in " + ObtuseUtil.enquoteJavaObject( f ) );

                        ObtuseUtil.doNothing();

                    } catch ( FileNotFoundException e ) {

                        Logger.logErr( "java.io.FileNotFoundException caught opening " + ObtuseUtil.enquoteJavaObject( f ), e );

                    } catch ( IOException e ) {

                        Logger.logErr( "java.io.IOException caught reading " + ObtuseUtil.enquoteJavaObject( f ), e );

                    }

                }

            }

            Logger.logMsg( /*ObtuseUtil.replicate( "   ", depth ) +*/ ObtuseUtil.lpadReadable( allBytesCount, 15 ) + " bytes in " + ObtuseUtil.enquoteJavaObject( where ) );

            ObtuseUtil.doNothing();

        }

        return allBytesCount;

    }

}
