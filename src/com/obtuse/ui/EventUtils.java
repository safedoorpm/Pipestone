package com.obtuse.ui;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.Logger;
import com.obtuse.util.Trace;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 Utility methods to log and report UI events.
 */

public class EventUtils {

    private static boolean _logEvents;

    /**
     Make it clear that this is a utilities class.
     */

    private EventUtils() {
	super();
    }

    public static void event( String where, MouseEvent event ) {

	String eventType;
	switch ( event.getID() ) {

	    case MouseEvent.MOUSE_CLICKED: eventType = "clicked"; break;
	    case MouseEvent.MOUSE_PRESSED: eventType = "pressed"; break;
	    case MouseEvent.MOUSE_RELEASED: eventType = "released"; break;
	    case MouseEvent.MOUSE_MOVED: eventType = "moved"; break;
	    case MouseEvent.MOUSE_ENTERED: eventType = "entered"; break;
	    case MouseEvent.MOUSE_EXITED: eventType = "exited"; break;
	    case MouseEvent.MOUSE_DRAGGED: eventType = "dragged"; break;
	    case MouseEvent.MOUSE_WHEEL: eventType = "wheel"; break;
	    default: eventType = "id=" + event.getID();
	}
	String msg = where + ":  " +
		     "mouse event:  " + eventType +
		     ", button " + event.getButton() +
		     ", mod " + MouseEvent.getMouseModifiersText( event.getModifiers() ).trim() +
		     ", modEx " + MouseEvent.getModifiersExText( event.getModifiersEx() ).trim() +
		     ", clickCount " + event.getClickCount() +
		     ", point (" + event.getPoint().x + "," + event.getPoint().y + ")" +
		     ", " + getTopParent( (Component)event.getSource() );
	Trace.event( msg );

    }

    public static void event( String why, PopupMenuEvent event ) {

	String msg = "popup menu event:  " + why + " in " + getTopParent( (Component)event.getSource() );
	maybeLog( msg );

    }

    public static void event( String why, ActionEvent event ) {

	Object source = event.getSource();
	if ( source instanceof JMenuItem ) {

	    JMenuItem menuItem = (JMenuItem)source;
	    String msg = why + ":  menu item \"" + menuItem.getText() + "\" clicked in " + getTopParent( menuItem );

	    maybeLog( msg );

	} else if ( source instanceof JButton ) {

	    JButton button = (JButton)source;
	    String msg = why + ":  button \"" + button.getText() + "\" clicked in " + getTopParent( button );

	    maybeLog( msg );

	} else {

	    maybeLog( why + ":  EventObject:  " + event );

	}

    }

    public static void maybeLog( String msg ) {

	if ( _logEvents ) {

	    Logger.logMsg( "msg:  " + msg );

	} else {

	    Trace.event( msg );

	}

    }

    public static Object getTopParent( Component xComponent ) {

	String in = "";

	Component component = xComponent;
	do {

	    if ( component instanceof JFrame ) {

		return in + "JFrame \"" + ((JFrame)component).getTitle() + "\"";

	    }

	    if ( component instanceof JDialog ) {

		return in + "JDialog \"" + ((JDialog)component).getTitle() + "\"";

	    }

	    if ( component.getParent() instanceof JTabbedPane ) {

		JTabbedPane jTabbedPane = (JTabbedPane) component.getParent();
		for ( int tabIndex = 0; tabIndex < jTabbedPane.getTabCount(); tabIndex += 1 ) {

		    if ( jTabbedPane.getComponentAt( tabIndex ).equals( component ) ) {

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
