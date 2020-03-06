package com.obtuse.ui;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.Logger;
import com.obtuse.util.Trace;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 Utility methods to log and report UI events.
 */

public class ObtuseGuiEventUtils {

    private static boolean _logEvents;

    /**
     Make it clear that this is a utilities class.
     */

    private ObtuseGuiEventUtils() {

        super();

    }

    public static void event( final String where, final MouseEvent event ) {

        String eventType;
        switch ( event.getID() ) {

            case MouseEvent.MOUSE_CLICKED:
                eventType = "clicked";
                break;

            case MouseEvent.MOUSE_PRESSED:
                eventType = "pressed";
                break;

            case MouseEvent.MOUSE_RELEASED:
                eventType = "released";
                break;

            case MouseEvent.MOUSE_MOVED:
                eventType = "moved";
                break;

            case MouseEvent.MOUSE_ENTERED:
                eventType = "entered";
                break;

            case MouseEvent.MOUSE_EXITED:
                eventType = "exited";
                break;

            case MouseEvent.MOUSE_DRAGGED:
                eventType = "dragged";
                break;

            case MouseEvent.MOUSE_WHEEL:
                eventType = "wheel";
                break;

            default:
                eventType = "id=" + event.getID();

        }

        String msg = where +
                     ":  " +
                     "mouse event:  " +
                     eventType +
                     ", button " +
                     event.getButton() +
                     ", modEx " +
                     MouseEvent.getModifiersExText( event.getModifiersEx() )
                               .trim() +
                     ", clickCount " +
                     event.getClickCount() +
                     ", point (" +
                     event.getPoint().x +
                     "," +
                     event.getPoint().y +
                     ")" +
                     ", " +
                     describeTopParent( (Component)event.getSource() );

        Trace.event( msg );

    }

    public static void event( final String why, final PopupMenuEvent event ) {

        String msg = "popup menu event:  " + why + " in " + describeTopParent( (Component)event.getSource() );
        maybeLog( msg );

    }

    public static void event( final String why, final ActionEvent event ) {

        Object source = event.getSource();
        if ( source instanceof JMenuItem ) {

            JMenuItem menuItem = (JMenuItem)source;
            String msg = why + ":  menu item \"" + menuItem.getText() + "\" clicked in " + describeTopParent( menuItem );

            maybeLog( msg );

        } else if ( source instanceof JButton ) {

            JButton button = (JButton)source;
            String msg = why + ":  button \"" + button.getText() + "\" clicked in " + describeTopParent( button );

            maybeLog( msg );

        } else {

            maybeLog( why + ":  EventObject:  " + event );

        }

    }

    @SuppressWarnings("unused")
    public static void setLogEvents( final boolean logEvents ) {

        _logEvents = logEvents;

    }

    private static void maybeLog( final String msg ) {

        if ( _logEvents ) {

            Logger.logMsg( "msg:  " + msg );

        } else {

            Trace.event( msg );

        }

    }

    @NotNull
    public static Optional<Window> findOurTopWindow( final Component component ) {

        Component c = component;
        for ( int i = 0; i < 1000; i += 1 ) {

            if ( c instanceof Window ) {

                return Optional.of((Window)c);

            }

            c = c.getParent();

        }

        return Optional.empty();

    }

    public static String describeTopParent( final Component xComponent ) {

        String in = "";

        Component component = xComponent;
        do {

            if ( component instanceof JFrame ) {

                return in + "JFrame \"" + ( (JFrame)component ).getTitle() + "\"";

            }

            if ( component instanceof JDialog ) {

                return in + "JDialog \"" + ( (JDialog)component ).getTitle() + "\"";

            }

            if ( component.getParent() instanceof JTabbedPane ) {

                JTabbedPane jTabbedPane = (JTabbedPane)component.getParent();
                for ( int tabIndex = 0; tabIndex < jTabbedPane.getTabCount(); tabIndex += 1 ) {

                    if ( jTabbedPane.getComponentAt( tabIndex )
                                    .equals( component ) ) {

                        in = "tab \"" + jTabbedPane.getTitleAt( tabIndex ) + "\" in ";
                        break;

                    }

                }

            }

            component = component.getParent();

        } while ( component != null );

        return in + "Component " + xComponent;

    }
}
