package com.obtuse.ui.layout.linear;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.WatchList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public class LinearLayoutUtil {

    static boolean _reportShapeChanges;

    private static WatchList<Container> s_containerWatchlist = new WatchList<>( "container watchlist" );
    private static WatchList<Component> s_componentWatchlist = new WatchList<>( "component watchlist" );

    private LinearLayoutUtil() {
	super();

    }

    public static boolean isContainerOnWatchlist( @NotNull Container c ) {

	return s_containerWatchlist.isEntityOnWatchList( c );

    }

    public static int addContainerToWatchList( @NotNull Container c ) {

	return s_containerWatchlist.addEntityToWatchList( c );

    }

    public static int removeContainerFromWatchList( @NotNull Container c ) {

	return s_containerWatchlist.removeEntityFromWatchList( c );

    }

    public static boolean isComponentOnWatchlist( @NotNull Component c ) {

	return s_componentWatchlist.isEntityOnWatchList( c );

    }

    @SuppressWarnings("UnusedReturnValue")
    public static int addComponentToWatchList( @NotNull Component c ) {

	return s_componentWatchlist.addEntityToWatchList( c );

    }

    public static int removeComponentFromWatchList( @NotNull Component c ) {

	return s_componentWatchlist.removeEntityFromWatchList( c );

    }

    public static String describeBorder( Border border ) {

	if ( border == null ) {

	    return "<<null border>>";

	}

	if ( border instanceof EtchedBorder ) {

	    EtchedBorder b = (EtchedBorder) border;
	    return "EtchedBorder( eType=" + ( b.getEtchType() == EtchedBorder.LOWERED ? "lowered" : "raised" ) + ", hColour=" + b.getHighlightColor() + ", sColor=" + b.getShadowColor() + " )";

	} else if ( border instanceof LineBorder ) {

	    LineBorder b = (LineBorder) border;
	    return "LineBorder( lColour=" + b.getLineColor() + ", thickness=" + b.getThickness() + ( b.getRoundedCorners() ? ", corners=rounded" : "" ) + " )";

	}

	return border.toString();
    }

//    @NotNull
//    static LinearContainer createPanel2( String name, LinearOrientation orientation, @Nullable Integer breadth ) {
//
//	LinearContainer rval = new LinearContainer2( name, orientation, breadth ) {
//
//	    public void setBounds( int x, int y, int w, int h ) {
//
//		super.setBounds( x, y, w, h );
//		Logger.logMsg( "SSLM.createdPanel2( name=\"" + getName() + "\", count=" + getComponentCount() +
//			       " ) setting container bounds to " + ObtuseUtil.fBounds( x, y, w, h ) );
//
//	    }
//
//	    public String toString() {
//
//		return "SSLM.createdPanel2( \"" + getName() + "\", " + super.toString() + ")"; // + '[' + paramString() + ']';
//
//	    }
//
//	};
//
//	return rval;
//
//    }

    @NotNull
    public static LinearContainer createPanel3( String name, LinearOrientation orientation ) {

	@SuppressWarnings("UnnecessaryLocalVariable")
	LinearContainer rval = new LinearContainer3( name, orientation ) {

	    public void setBounds( int x, int y, int w, int h ) {

		super.setBounds( x, y, w, h );
		Logger.logMsg( "SSLM.createdPanel3( name=\"" + getName() + "\", count=" + getComponentCount() + " ) setting container bounds to " + ObtuseUtil.fBounds( x, y, w, h ) );

	    }

	    public String toString() {

		return "SSLM.createdPanel3( \"" + getName() + "\", " + super.toString() + ")"; // + '[' + paramString() + ']';

	    }

	};

	return rval;

    }

    public static String myDrawLine( String who, Graphics2D g2d, int x1, int y1, int x2, int y2 ) {

	String msg = "drawLine( " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + " )";
	g2d.drawLine( x1, y1, x2, y2 );
	Logger.logMsg( who + ":  " + msg );

	return msg;

    }

    @NotNull
    public static JComponent makeThing(
	    @NotNull final String label,
	    @Nullable Dimension minSize,
	    @Nullable Dimension prefSize,
	    @Nullable Dimension maxSize
    ) {

	JPanel rval = new JPanel() {

	    public String getName() {

		return label;

	    }

	    public void setMinimumSize( Dimension size ) {

		super.setMinimumSize( size );
		if ( _reportShapeChanges ) {

		    Logger.logMsg( "Thing \"" + getName() + "\" minSize set to " + ObtuseUtil.fDim( size ) );

		}

	    }

	    public void setPreferredSize( Dimension size ) {

		super.setPreferredSize( size );
		if ( _reportShapeChanges ) {

		    Logger.logMsg( "Thing \"" + getName() + "\" prefSize set to " + ObtuseUtil.fDim( size ) );

		}

	    }

	    public void setMaximumSize( Dimension size ) {

		super.setMaximumSize( size );
		if ( _reportShapeChanges ) {

		    Logger.logMsg( "Thing \"" + getName() + "\" maxSize set to " + ObtuseUtil.fDim( size ) );

		}

	    }

	    public void setBounds( int x, int y, int w, int h ) {

		super.setBounds( x, y, w, h );

		Logger.logMsg( "Thing \"" + getName() + "\" created by makeThing resized from " + ObtuseUtil.fBounds( getBounds() ) + " to " + ObtuseUtil.fBounds( x, y, w, h ) );
		ObtuseUtil.doNothing();

	    }

	    @SuppressWarnings("deprecation")
	    public void reshape( int x, int y, int w, int h ) {

		super.reshape( x, y, w, h );

		Logger.logMsg( "Thing \"" + getName() + "\" created by makeThing RESHAPED from " + ObtuseUtil.fBounds( getBounds() ) + " to " + ObtuseUtil.fBounds( x, y, w, h ) );
		ObtuseUtil.doNothing();

	    }

	    protected void paintComponent( Graphics g ) {

		super.paintComponent( g );

		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor( Color.BLUE );
		Insets insets = getInsets();
		int left = insets.left;
		int top = insets.top;
		int right = getWidth() - insets.right - 1;	// subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1
		int bottom = getHeight() - insets.bottom - 1;	// subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1
		String l1msg = myDrawLine( "thing", g2d, left, top, right, bottom );
		String l2msg = myDrawLine( "thing", g2d, left, bottom, right, top );
		Logger.logMsg(
			"thing painted:  w=" + getWidth() + ", h=" + getHeight() + ", insets=" + ObtuseUtil.fInsets( insets ) +
			", l1=" + l1msg +
			", l2=" + l2msg
		);
//		g2d.drawLine( left, top, right, bottom );
//		g2d.drawLine( left, bottom, right, top );

	    }

	    public String toString() {

		return "Thing( " +
		       "\"" + getName() + "\", " +
		       ObtuseUtil.fDim( "minSize", getMinimumSize() ) + ", " +
		       ObtuseUtil.fDim( "prefSize", getPreferredSize() ) + ", " +
		       ObtuseUtil.fDim( "maxSize", getMaximumSize() ) +
		       ", bounds=" + ObtuseUtil.fBounds( getBounds() ) + ", " +
		       super.toString() +
		       " )";

	    }

	};

	addLocationTracer( rval );

	rval.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );

	rval.setMinimumSize( minSize );
	rval.setPreferredSize( prefSize );
	rval.setMaximumSize( maxSize );

	Logger.logMsg( "Thing \"" + rval.getName() + "\"'s alignment is ( " + rval.getAlignmentX() + ", " + rval.getAlignmentY() + " ) @ " + ObtuseUtil.fBounds( rval.getBounds() ) );

	return rval;

    }

    public static void makeButtons( JComponent component, int nb ) {

	makeButtons( component, nb, "Hello!", null );

    }

    public static void makeButtons( JComponent component, int nb, MyActionListener listener ) {

	makeButtons( component, nb, "Hello!", listener );

    }

    public static void makeButtons( JComponent component, int nb, String label ) {

	makeButtons( component, nb, label, null );

    }

    public static void makeButtons( JComponent component, int nb, String label, MyActionListener listener ) {

	component.setBorder( BorderFactory.createLineBorder( Color.GREEN ) );
	addContainerToWatchList( component );
	component.setAlignmentX( 0.5f );
	component.setAlignmentY( 0.0f );

	for ( int i = 0; i < nb; i += 1 ) {

	    JButton jButton3 = new JButton( label ) {
		public void setBounds( int x, int y, int width, int height ) {

		    super.setBounds( x, y, width, height );

		    Logger.logMsg( "jButton3's bounds set to " + ObtuseUtil.fBounds( x, y, width, height ) );

		}

		public String toString() {

		    return "jButton3:  " + super.toString();

		}

	    };
	    float alignmentX = nb == 1 ? 0.5f : Math.max( Math.min( i / (float) ( nb - 1 ), 1f ), 0f );
	    Logger.logMsg( "alignmentX = " + alignmentX );
	    jButton3.setAlignmentX( alignmentX );
	    addComponentToWatchList( jButton3 );

	    jButton3.addActionListener(
		    new MyActionListener() {

			@Override
			public void myActionPerformed( ActionEvent actionEvent ) {

			    showWhereWeAre( jButton3, actionEvent );
			    if ( listener != null ) {

				listener.actionPerformed( actionEvent );

			    }

			}

		    }
	    );

	    component.add( jButton3 );

	}

//	JComponent thing1 = makeThing( "thing", new Dimension( 15, 30 ), new Dimension( 20, 40 ), new Dimension( 90, 90 ) );
//	thing1.setAlignmentX( 0.0f );
//	component.add( thing1 );

//	JComponent thing2 = makeThing( "thing", new Dimension( 15, 30 ), new Dimension( 20, 40 ), new Dimension( 90, 90 ) );
//	thing2.setAlignmentX( 1.0f );
//	buttons.add( thing2 );
//
//	JComponent thing3 = makeThing( "thing", new Dimension( 15, 30 ), new Dimension( 20, 40 ), new Dimension( 90, 90 ) );
//	thing3.setAlignmentX( Component.CENTER_ALIGNMENT );
//	buttons.add( thing3 );

//	addContainerToWatchList( buttons );
    }

    public void setReportShapeChanges( boolean report ) {

	LinearLayoutUtil._reportShapeChanges = report;

    }

    public boolean reportShapeChanges() {

	return LinearLayoutUtil._reportShapeChanges;

    }

    public static void describeGuiEntity( String why, Container container ) {

	Logger.logMsg( ( why == null ? "" : why + ":  " ) + "structure of container \"" + container.getName() + "\" is " + ObtuseUtil.fBounds( container.getBounds() ) + " - " + container );

	for ( Component c : container.getComponents() ) {

	    describeGuiEntity( 0, c, false, false );

	}

	Logger.logMsg( "" );

    }

    public static void describeGuiEntity( int depth, Component component, boolean recurse, boolean showContents ) {

	String formattedComponentName = fullName( component );
	Logger.logMsg(
		ObtuseUtil.replicate( "  ", depth ) + formattedComponentName + ObtuseUtil.fBounds( component.getBounds() ) +
		":  " + component
//		name
//		+ " is " + ObtuseUtil.fBounds( component.getBounds() ) +
//		", min=" + ObtuseUtil.fDim( component.getMinimumSize() ) +
//		", pref=" + ObtuseUtil.fDim( component.getPreferredSize() ) +
//		", max=" + ObtuseUtil.fDim( component.getMaximumSize() )
	);

	if ( component instanceof Container && showContents ) {

	    for ( Component c : ((Container)component).getComponents() ) {

		describeGuiEntity( depth + 1, c, false, true );

	    }

	}

	if ( recurse ) {

	    Container parent = component.getParent();
	    if ( parent != null ) {

		describeGuiEntity( depth + 1, parent, true, false );

	    }

	}

    }

    public static String fullName( Container parent, Component c ) {

	return "\"" + parent.getName() + "\"/\"" + fullName( c ) + "\"" + ( c instanceof Container ? "(c)" : "(?)" );

    }

    @NotNull
    public static String fullName( Component component ) {

	String name = component.getName();
	if ( name == null ) {

	    if ( component instanceof JButton ) {

		name = "buttonLabel=\"" + ((JButton)component).getText() + '"';

	    } else {

		name = "unknown name";

	    }

	} else {

	    name = "name=\"" + name + "\"";

	}

	return component.getClass().getName() + "(" + name + ")";

    }

    public static void showWhereWeAre( Component start, AWTEvent e ) {

	try {

	    Logger.pushNesting( "showWhereWeAre" );

	    if ( e instanceof MouseEvent ) {

		MouseEvent me = (MouseEvent) e;

		String msg = "click count = " + me.getClickCount() + ", " +
			     "Click position :  ( " + me.getX() + ", " + me.getY() + " )";

		if ( me.getButton() == MouseEvent.NOBUTTON ) {

		    Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "No button clicked, " + msg );

		} else {

		    Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "Button #" + me.getButton() + " clicked, " + msg );

		}

	    }

	    describeGuiEntity( 0, start, true, false );

	} finally {

	    Logger.popNestingLevel( "showWhereWeAre" );

	}

    }

//    public static void showWhereWeAre( Component start, ActionEvent e ) {
//
//	try {
//
//	    Logger.pushNesting( "showWhereWeAre" );
//
//	    String msg = "click count = " + e. getClickCount() + ", " +
//			 "Click position :  ( " + e.getX() + ", " + e.getY() + " )";
//
//	    if ( e.getButton() == MouseEvent.NOBUTTON ) {
//
//		Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "No button clicked, " + msg );
//
//	    } else {
//
//		Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "Button #" + e.getButton() + " clicked, " + msg );
//
//	    }
//
//	    describeGuiEntity( 0, start, true, false );
//
//	} finally {
//
//	    Logger.popNestingLevel( "showWhereWeAre" );
//
//	}
//
//    }

    public static void showStructure( Container start ) {

	int depth = 0;
	for ( Container c = start; c != null; c = c.getParent() ) {

	    Logger.logMsg(
		    ObtuseUtil.replicate( "    ", depth ) + c.getClass().getName() + ":  " + toString( c ) //  c.getName(), c.getComponents() )
	    );

	    depth += 1;

	}

	Logger.logMsg( "" );
    }

    public static void addLocationTracer( final Container container ) {

	container.addMouseListener(
		new MouseAdapter() {

		    public void mouseClicked( MouseEvent e ) {

			showWhereWeAre( container, e );

			ObtuseUtil.doNothing();

		    }

		}

	);

    }

    public static String toString( Container container ) {

	StringBuilder sb = new StringBuilder( "{" );
	String comma = "";
	int count = container.getComponents().length;
	int cc = container.getComponentCount();
	ObtuseUtil.doNothing();

	for ( Component c : container.getComponents() ) {

	    if ( comma.length() == 0 ) {

		sb.append( ' ' );

	    }

	    sb.append( comma ).append( fullName( container, c ) ); // ) append( '"' ).append( container.getName() ).append( "\"/\"" ).append( c.getName() ).append( '"' );
	    comma = ", ";

	}

	if ( comma.length() > 0 ) {

	    sb.append( ' ' );

	}

	sb.append( '}' );

	return "" + container.getClass().getSimpleName() + "( \"" + container.getName() + "\", " + container.getComponentCount() + " component" + ( container.getComponentCount() == 1 ? "" : "s" ) + ", " + sb + " )";

    }

    public interface LinearMagic {

	Dimension getInitialMinimumSize();
	Dimension getInitialPreferredSize();
	Dimension getInitialMaximumSize();

    }

    public static class MyDimension extends Dimension {

        public MyDimension() {
            super();
	}

	public MyDimension( int width, int height ) {
            super( width, height );
	}

	// Having a re-wrapper is a bad idea since the owner of the original Dimension instance
	// might change their instance by directly changing the width and height fields.
	// Mutable information classes are, as a general rule, EVIL!!!

//	public MyDimension( Dimension d ) {
//            super( d.width, d.height );
//
//	}

	public String toString() {

            return ObtuseUtil.fDim( this );

	}

    }

    /**
     Something that will grow or shrink on demand.
     <p/>This essentially becomes the component where extra space gets put if needed to fill out the linear container.
     If there's more than one sponge in a container then the extra space should get split evenly between the sponges.
     */

    public static class SpaceSponge extends JPanel {

        public SpaceSponge() {
            super();

	}

	public String toString() {

            return "SpaceSponge( " + super.toString() + " )";

	}

    }

    /**
     Something that will firmly maintain a particular range of sizes.
     */

    public static class SpaceBrick extends JPanel {

	private final MyDimension _min;
	private final MyDimension _pref;
	private final MyDimension _max;

	public SpaceBrick( int minWidth, int minHeight, int prefWidth, int prefHeight, int maxWidth, int maxHeight ) {
            this( new MyDimension( minWidth, minHeight ), new MyDimension( prefWidth, prefHeight ), new MyDimension( maxWidth, maxHeight ) );
	}

	public SpaceBrick( Dimension min, Dimension pref, Dimension max ) {
	    super();

	    _min = min instanceof MyDimension ? (MyDimension)min : new MyDimension( min.width, min.height );
	    _pref = pref instanceof MyDimension ? (MyDimension)pref : new MyDimension( pref.width, pref.height );
	    _max = max instanceof MyDimension ? (MyDimension)max : new MyDimension( max.width, max.height );

	}

	public Dimension getMinimumSize() {

	    return _min;

	}

	public Dimension getPreferredSize() {

	    return _min;

	}

	public Dimension getMaximumSize() {

	    return _max;

	}

	public void setMinimumSize( Dimension min ) {

	    Logger.logMsg( "SpaceBrick:  request to change min from " + _min + " to " + min + " ignored" );

	}

	public void setPreferredSize( Dimension pref ) {

	    Logger.logMsg( "SpaceBrick:  request to change pref from " + _pref + " to " + pref + " ignored" );

	}

	public void setMaximumSize( Dimension max ) {

	    Logger.logMsg( "SpaceBrick:  request to change max from " + _max + " to " + max + " ignored" );

	}

	public String toString() {

	    return "SpaceBrick( min=" + _min + ", pref=" + _pref + ", max=" + _max + " )";

	}

    }

    public static class WeightedSimpleFiller extends JPanel {

	private final float _weight;

	public WeightedSimpleFiller( @NotNull String name, float weight ) {
	    super();

	    setName( name );

	    if ( weight <= 0 ) {

		throw new IllegalArgumentException( "WeightedSimpleFiller:  weight must be positive (is " + weight + ")" );

	    }

	    _weight = weight;

//	    setBackground( Color.RED );
	    setOpaque( false );

	    addLocationTracer( this );
//	    addMouseListener(
//		new MouseAdapter() {
//
//		    public void mouseClicked( MouseEvent e ) {
//
//			showWhereWeAre( WeightedSimpleFiller.this, e );
//
//			ObtuseUtil.doNothing();
//
//		    }
//
//		}
//
//	    );

	}

	public LinearOrientation getOrientation() {

	    Container parent = getParent();

	    if ( parent == null ) {

		throw new IllegalArgumentException( "LinearLayoutManager.WeightedSimpleFiller:  unable to get orientation until we are placed in a LinearContainer2" );

	    }

	    if ( parent instanceof LinearContainer ) {

		return ((LinearContainer)parent).getOrientation();

	    } else {

		throw new IllegalArgumentException( "LinearLayoutManager.WeightedSimpleFiller:  we must reside within a LinearContainer2 (we are in a " + parent.getClass().getName() + ")" );

	    }

	}

	public void paintComponent( Graphics g ) {

	    super.paintComponent( g );

	    Graphics2D g2D = (Graphics2D)g.create();

//	    Color oldColour = g2D.getColor();

	    if (isOpaque() ) {

		g2D.setColor( getBackground() );

		g2D.fillRect( 0, 0, getWidth(), getHeight() );

	    }

	    g2D.setColor( Color.BLACK );

	    for ( int x = 0; x < getWidth(); x += 100 ) {

		g2D.drawLine( x, 0, x, getHeight() );

	    }

	    for ( int y = 0; y < getHeight(); y += 100 ) {

		g2D.drawLine( 0, y, getWidth(), y );

	    }

//	    g2D.setColor( oldColour );

	}

	public float getWeight() {

	    return _weight;

	}

	public String toString() {

	    return "WeightedSimpleFiller( " + getWeight() + " )";

	}

    }

    public static class SimpleFiller extends JPanel /*implements LinearMagic*/ {

	public SimpleFiller( @NotNull String name ) {
	    super();

	    setName( name );

//	    setBackground( Color.RED );
	    setOpaque( false );

	    addLocationTracer( this );
//	    addMouseListener(
//		new MouseAdapter() {
//
//		    public void mouseClicked( MouseEvent e ) {
//
//			showWhereWeAre( SimpleFiller.this, e );
//
//			ObtuseUtil.doNothing();
//
//		    }
//
//		}
//
//	    );

	}

	public LinearOrientation getOrientation() {

	    Container parent = getParent();

	    if ( parent == null ) {

		throw new IllegalArgumentException( "LinearLayoutManager.SimpleFiller:  unable to get orientation until we are placed in a LinearContainer2" );

	    }

	    if ( parent instanceof LinearContainer ) {

		return ((LinearContainer)parent).getOrientation();

	    } else {

		throw new IllegalArgumentException( "LinearLayoutManager.SimpleFiller:  we must reside within a LinearContainer2 (we are in a " + parent.getClass().getName() + ")" );

	    }

	}

	public boolean isVertical() {

	    return getOrientation() == LinearOrientation.VERTICAL;

	}

	public boolean isHorizontal() {

	    return getOrientation() == LinearOrientation.HORIZONTAL;

	}

	public void paintComponent( Graphics g ) {

	    super.paintComponent( g );

	    Graphics2D g2D = (Graphics2D)g.create();

//	    Color oldColour = g2D.getColor();

	    if (isOpaque() ) {

		g2D.setColor( getBackground() );

		g2D.fillRect( 0, 0, getWidth(), getHeight() );

	    }

	    g2D.setColor( Color.RED );

	    for ( int x = 0; x < getWidth(); x += 100 ) {

		g2D.drawLine( x, 0, x, getHeight() );

	    }

	    for ( int y = 0; y < getHeight(); y += 100 ) {

		g2D.drawLine( 0, y, getWidth(), y );

	    }

	}

	public String toString() {

	    return "SimpleFiller( \"" + getOrientation() + "\" )";

	}

	public Dimension getMaximumSize() {

	    Dimension rval = super.getMaximumSize();

	    Logger.logMsg( "SimpleFiller.getMaximumSize() = " + ObtuseUtil.fDim( rval ) );

	    return rval;

	}

    }

}
