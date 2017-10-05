/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.layout.ConstraintTuple;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 A linear layout manager that supports component and container constraints.
 */

public class LinearLayoutManager3 implements LayoutManager2 {

    public interface Constraint {

        String getName();

        boolean isEnabled();

    }

    public static final String TRACK_PARENTS_BREADTH_CONSTRAINT_TITLE = "track parent's breadth";

    public static final LinearLayoutManager3.SimpleConstraint TRACK_PARENTS_BREADTH_CONSTRAINT = new LinearLayoutManager3.SimpleConstraint(
            LinearLayoutManager3.TRACK_PARENTS_BREADTH_CONSTRAINT_TITLE,
            true
    );

    public static class SimpleConstraint implements Constraint {

        private final String _name;

        private final boolean _enabled;

        public SimpleConstraint( @SuppressWarnings("SameParameterValue") @NotNull String name, boolean enabled ) {

            super();

            _name = name;

            _enabled = enabled;

        }

        public String getName() {

            return _name;

        }

        public boolean isEnabled() {

            return _enabled;

        }

        public int hashCode() {

            return getName().hashCode();

        }

        public boolean equals( Object rhs ) {

            return rhs instanceof Constraint && getName().equals( ( (Constraint)rhs ).getName() );

        }

        public int compareTo( Constraint rhs ) {

            return getName().compareTo( rhs.getName() );

        }

        public String toString() {

            return "SimpleConstraint( \"" + getName() + "\", " + isEnabled() + " )";

        }

    }

    public static class ConstraintsTable extends TreeMap<String, Constraint> {

//	public static final ConstraintsTable EMPTY;
//
//	static {
//
//	    EMPTY = new ConstraintsTable() {
//
//		public void putAll(Map<? extends String, ? extends Constraint> map) {
//		    throw new IllegalArgumentException( "EMPTY is immutable" );
//		}
//
//		public Constraint put( String key, Constraint constraint ) {
//		    throw new IllegalArgumentException( "EMPTY is immutable" );
//		}
//
//		public Constraint remove( Object v ) {
//		    throw new IllegalArgumentException( "EMPTY is immutable" );
//		}
//
//		public void clear() {
//		    throw new IllegalArgumentException( "EMPTY is immutable" );
//		}
//
//		public NavigableSet<String> navigableKeySet() {
//		    return Collections.unmodifiableNavigableSet( super.navigableKeySet() );
//		}
//
//		public Set<String> keySet() {
//		    return navigableKeySet();
//		}
//
//		public NavigableSet<String> descendingKeySet() {
//		    return Collections.unmodifiableNavigableSet( descendingMap().navigableKeySet() );
//		}
//
//		public Collection<Constraint> values() {
//		    return Collections.unmodifiableCollection( super.values() );
//		}
//
//
//
//	    };
//
//	}

        public ConstraintsTable() {

            super();

        }

        public ConstraintsTable( Constraint singletonConstraint ) {

            super();

            put( singletonConstraint.getName(), singletonConstraint );

        }

    }

    private final LinearContainer3 _target;

    private final LinearOrientation _orientation;

    private final Hashtable<Component, ConstraintsTable> _constraints = new Hashtable<>();

    private LayoutImplCache _cache;

    public LinearLayoutManager3(
            @NotNull LinearOrientation orientation,
            @NotNull LinearContainer3 linearContainer3
    ) {

        super();

        _orientation = orientation;

        _target = linearContainer3;

        preLoadCacheIfNecessary();
    }

    @Override
    public synchronized void addLayoutComponent( Component comp, Object xConstraints ) {

//	Logger.maybeLogMsg( () -> "addLayoutComponent( " + comp + ", " + xConstraints + " )" );

        implicitInvalidateLayout( "addLayoutComponent", _target );

        if ( comp == null ) {

            throw new IllegalArgumentException( "LinearLayoutManager3.addLayoutComponent( Component, Object ):  component is null" );

        }

        ConstraintsTable oldConstraints = _constraints.remove( comp );
        ConstraintsTable constraints;

        if ( xConstraints == null ) {

            return;

        }

        if ( xConstraints instanceof Constraint ) {

            Constraint singletonConstraint = (Constraint)xConstraints;

            constraints = new ConstraintsTable( singletonConstraint );

        } else if ( xConstraints instanceof ConstraintsTable ) {

            constraints = (ConstraintsTable)xConstraints;

        } else {

            throw new IllegalArgumentException( "LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraints not a " +
                                                ConstraintsTable.class.getCanonicalName() );

        }

//	SortedMap<Object, Object> original = (SortedMap<Object, Object>) constraints;
        ConstraintsTable copy = new ConstraintsTable();
        for ( Object keyObject : constraints.keySet() ) {

            if ( !( keyObject instanceof String ) ) {

                if ( keyObject == null ) {

                    throw new IllegalArgumentException(
                            "LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraint has null key" );

                }

                throw new IllegalArgumentException(
                        "LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraint object has key that is not a String" +
                        " (it is a " + keyObject.getClass().getCanonicalName() + ")" );

            }

            Constraint value = constraints.get( keyObject );
            String key = (String)keyObject;

            if ( value == null ) {

                throw new IllegalArgumentException(
                        "LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraint named \"" + key + "\" is null" );

            }

//	    if ( !( value instanceof Constraint ) ) {
//
//		throw new IllegalArgumentException(
//			"LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraint named \"" + key +
//			"\" is not a " + Constraint.class.getCanonicalName() + " (it is a " +
//			value.getClass().getCanonicalName() + ")" );
//
//	    }

//	    Constraint value = (Constraint) value;

            copy.put( key, value );

        }

//	if ( oldConstraints == null ) {
//
//	    Logger.logMsg( "constraints for " + comp + " set to " + copy );
//
//	} else {
//
//	    Logger.logMsg( "constraints for " + comp + " changed from " + oldConstraints + " to " + copy );
//
//	}

        _constraints.put( comp, copy );

    }

    private void checkContainer( String who, Container target ) {

        if ( target != _target ) {

            throw new IllegalArgumentException( "LinearLayoutManager3(" +
                                                who +
                                                "):  this instance dedicated to " +
                                                _target +
                                                ", cannot be switched to " +
                                                target );

        }

    }

    private synchronized void preLoadCacheIfNecessary() {

        if ( _cache == null ) {

            _cache = new LinearCache3( this, _target, _constraints );

        }

    }

    @Override
    public synchronized float getLayoutAlignmentX( Container target ) {

        checkContainer( "getLayoutAlignmentX", target );
        preLoadCacheIfNecessary();

        return _cache.getLayoutAlignmentX();

    }

    @Override
    public float getLayoutAlignmentY( Container target ) {

        checkContainer( "getLayoutAlignmentY", target );
        preLoadCacheIfNecessary();

        return _cache.getLayoutAlignmentY();

    }

    private synchronized void implicitInvalidateLayout( String requester, LinearContainer3 target ) {

//        if ( _target != null && _target.isWatched() && _cache != null ) {
//
//            ObtuseUtil.doNothing();
//
//	}

        if ( _cache != null && LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {

            Logger.logMsg( "layout invalidated by " + requester + " (target is " + target + ")" );

        }

        boolean needValidation = _cache != null;

        _cache = null;

//	if ( needValidation ) {
//
//	    for ( int i = 0; i < _target.getComponentCount(); i += 1 ) {
//
//		Component component = _target.getComponent( i );
//		Logger.logMsg( "forcing revalidation of " + component.getName() );
//		component.invalidate();
//
//	    }
//
//	}

    }

    @Override
    public synchronized Dimension preferredLayoutSize( Container target ) {

        checkContainer( "preferredLayoutSize", target );
        preLoadCacheIfNecessary();

        Dimension size = _cache.getPreferredSize();

        if ( "outer".equals( getTarget().getName() ) ) {

            ObtuseUtil.doNothing();

        }

//        Logger.logMsg( getTarget().getName() + "'s preferred size is " + ObtuseUtil.fDim( size ) );

        return size;

    }

    @Override
    public synchronized Dimension minimumLayoutSize( Container target ) {

        checkContainer( "minimumLayoutSize", target );
        preLoadCacheIfNecessary();

        @SuppressWarnings("UnnecessaryLocalVariable")
        Dimension size = _cache.getMinimumSize();

//	if ( getOrientation() == LinearOrientation.HORIZONTAL ) {
//
//	    size = new Dimension( target.applyLengthConstraints( size.width ), target.applyBreadthConstraints( size.height ) );
//
//	} else {
//
//	    size = new Dimension( target.applyBreadthConstraints( size.width ), target.applyLengthConstraints( size.height ) );
//
//	}

        return size;

    }

    @Override
    public synchronized Dimension maximumLayoutSize( Container target ) {

        checkContainer( "maximumLayoutSize", target );
        preLoadCacheIfNecessary();

        @SuppressWarnings("UnnecessaryLocalVariable")
        Dimension size = _cache.getMaximumSize();

        return size;

    }

    @Override
    public synchronized void invalidateLayout( Container target ) {

        checkContainer( "invalidateLayout", target );

        implicitInvalidateLayout( "invalidateLayout", (LinearContainer3)target );

    }

    @Override
    public void addLayoutComponent( String name, Component comp ) {

        Logger.logMsg( "addLayoutComponent( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

        implicitInvalidateLayout( "addLayoutComponent", _target );

    }

    @Override
    public synchronized void removeLayoutComponent( Component comp ) {

//	Logger.logMsg( "removeLayoutComponent( " + comp + " )" );

        implicitInvalidateLayout( "removeLayoutComponent", _target );

        if ( comp == null ) {

            throw new IllegalArgumentException( "LinearLayoutManager3.removeLayoutComponent( Component ):  component is null" );

        } else {

            _constraints.remove( comp );

        }

    }

    @Override
    public void layoutContainer( Container parent ) {

        LayoutImplCache cache;

        synchronized ( this ) {

//	    String levelName = "XXX layoutContainer(" + parent + ")";
//	    try {

//		Logger.pushNesting( levelName );

//		if ( _target.isWatched() ) {
//
////		    Logger.logMsg( "laying out " + parent );
//
//		    for ( int i = 0; i < parent.getComponentCount(); i += 1 ) {
//
//			Component component = parent.getComponent( i );
//			Logger.logMsg( "parent[" + i + "] is " + component );
//
//		    }
//
//		    Logger.pushNesting( "inner" );
//
//		}

            if ( LinearLayoutUtil.isContainerOnWatchlist( parent ) ) {

                ObtuseUtil.doNothing();

            }

            checkContainer( "layoutContainer", parent );
//	        implicitInvalidateLayout( "layoutContainer", _target );

            preLoadCacheIfNecessary();

            if ( _target.isWatched() ) {

                Logger.logMsg( "doing a layout of " + _target );

                _target.doingLayout();

                ObtuseUtil.doNothing();

            }

            cache = _cache;
            _cache.computePositions();

            if ( _target.isWatched() ) {

                Logger.logMsg( "done layout of " + _target );

                _target.doneLayout();

                ObtuseUtil.doNothing();

            }

//	    } finally {
//
//		if ( _target.isWatched() ) {
//
//		    Logger.popNestingLevel( "inner" );
//
//		}
//
//		Logger.popNestingLevel( levelName );
//
//	    }

        }

        cache.setComponentBounds();

        int containerWidth = 0;
        int containerHeight = 0;

        for ( int i = 0; i < cache.getVisibleComponentCount(); i += 1 ) {

            Component c = cache.getVisibleComponent( i );
            Rectangle bounds = c.getBounds();
            containerWidth = Math.max( containerWidth, bounds.x + bounds.width );
            containerHeight = Math.max( containerHeight, bounds.y + bounds.height );

        }

//        Logger.logMsg(
//                "====================================== container " + getTarget().getName() +
//                " should be " + containerWidth + 'x' + containerHeight +
//                " and is " + ObtuseUtil.fBounds( getTarget().getBounds() )
//        );

    }

    public LinearOrientation getOrientation() {

        return _orientation;

    }

    public LinearContainer3 getTarget() {

        return _target;

    }

    private static Thread s_quietThread;
    private static int s_quietCountdown = 0;
    private static final Long s_qLock = 0L;

    private static void startQuietPeriod() {

        synchronized ( s_qLock ) {

            s_quietCountdown = 3;

        }

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "LinearLayoutManager3", "testing", null );

//        s_quietThread = new Thread( "quiet thread" ) {
//
//            public void run() {
//
//                while ( true ) {
//
//                    synchronized ( s_qLock ) {
//
//                        if ( s_quietCountdown > 0 ) {
//
//                            s_quietCountdown -= 1;
//
//			} else {
//
//			    Logger.logMsg( "<><><><><>" );
//
//			}
//
//		    }
//
//		    ObtuseUtil.safeSleepMillis( 1000L );
//
//		}
//
//	    }
//	};
//        s_quietThread.start();

        JFrame frame = new JFrame( "test simple linear layouts" );
        LinearContainer inner = LinearLayoutUtil.getLinearContainer3( "inner", LinearOrientation.VERTICAL, true );
        inner.setBorder( BorderFactory.createEtchedBorder() );

        JButton jb1 = new JButton( "Say Hello" );
        jb1.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        Logger.logMsg( "hello" );

                    }

                }
        );
        inner.add( jb1 );

        JButton jb2 = new JButton( "Say Boo!" );
        jb2.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        Logger.logMsg( "Boo!" );

                    }

                }
        );
        inner.add( jb2 );

//	inner.setMaximumSize( new Dimension( 300, 300 ) );

        LinearContainer outer = LinearLayoutUtil.getLinearContainer3( "outer", LinearOrientation.HORIZONTAL, true );
        outer.setBorder( BorderFactory.createLineBorder( Color.RED ) );
        outer.add( inner.getAsContainer() );
        JButton jb3 = new JButton( "Flip" );
        jb3.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        if ( "Flip".equals( jb3.getText() ) ) {

                            jb3.setText( "Flop" );

                        } else {

                            jb3.setText( "Flip" );

                        }

                    }

                }
        );
        JPanel inner2 = new JPanel();
        inner2.setLayout( new BoxLayout( inner2, BoxLayout.Y_AXIS ) );
        outer.add( inner2 );
        inner2.add( jb3 );
        inner2.setBorder( BorderFactory.createEtchedBorder( Color.GREEN, Color.WHITE ) );
//	outer.add( jb3 );

        frame.setContentPane( outer.getAsContainer() );
        frame.pack();
        frame.setVisible( true );

        ObtuseUtil.doNothing();

    }

    public static void mainX( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Testing", null );

        JFrame frame = new JFrame( "LinearLayoutManager3" );
        JPanel inner = makeMainContainer(
                true,
                LinearOrientation.VERTICAL,
                null,
                new ConstraintTuple( 0, 32767 )
        );

        JScrollPane scrollPane = new JScrollPane(
                inner
        );
        scrollPane.setMaximumSize( new Dimension( 32767, 250 ) );
        scrollPane.setMinimumSize( new Dimension( 0, 250 ) );
        scrollPane.setPreferredSize( new Dimension( 0, 250 ) );

        scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        JPanel main = makeMainContainer(
                true,
                LinearOrientation.VERTICAL,
                new Component[]{ makeTestFiller( "blorf" ), scrollPane },
                new ConstraintTuple( 300, 600 )
        );
//	main.add( scrollPane );

        Box b = new Box( BoxLayout.X_AXIS );
        b.add( main );
        b.add( new Box.Filler( new Dimension( 0, 0 ), new Dimension( 20, 20 ), new Dimension( 32767, 32767 ) ) );
        frame.setContentPane( b );
        frame.pack();
        frame.setVisible( true );

//	JFrame frame2 = new JFrame( "BoxLayout" );
//	main = makeMainContainer( false );
//	frame2.setContentPane( main );
//	frame2.pack();
//	frame2.setVisible( true );
//
//	JFrame frame3 = new JFrame( "naked JPanel" );
//	JPanel filler = makeTestFiller( "just a filler" );
//	frame3.setContentPane( filler );
//	frame3.pack();
//	frame3.setVisible( true );

    }

    @NotNull
    public static JPanel makeMainContainer(
            @SuppressWarnings("SameParameterValue") boolean makeLinearContainer,
            @SuppressWarnings("SameParameterValue") LinearOrientation orientation,
            Component[] extraComponents,
            @Nullable ConstraintTuple breadthConstraints
    ) {

        JPanel main;
        if ( makeLinearContainer ) {

            LinearContainer3 lc3 = new LinearContainer3( "main", orientation, null, null );

            lc3.setBreadthConstraints( breadthConstraints );

            main = lc3;

        } else {

            main = new JPanel();
            main.setLayout( new BoxLayout( main, BoxLayout.Y_AXIS ) );

        }

        main.setBorder( BorderFactory.createLineBorder( Color.RED ) );
        main.setAlignmentX( 0.0f );
        main.setAlignmentY( 0.0f );

        String label = makeLinearContainer ? "linear" : "box";
        LinearLayoutUtil.makeButtons(
                main,
                1,
                label,
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        if ( actionEvent.getSource() instanceof JButton ) {

                            JButton button = (JButton)actionEvent.getSource();

                            Logger.logMsg( "removing button " + button );
                            Container parent = button.getParent();
                            parent.remove( button );
                            parent.revalidate();

                        }

                    }

                }
        );

        if ( extraComponents != null ) {

            for ( Component extra : extraComponents ) {

                main.add( extra );

            }

        }

        JPanel filler;
        filler = makeTestFiller( label );
        filler.setDoubleBuffered( false );

//	filler.setBorder( BorderFactory.createLineBorder( Color.PINK ) );
        filler.setName( "filler" );
        LinearLayoutUtil.addLocationTracer( filler );
        main.add( filler );

        return main;

    }

    @NotNull
    public static JPanel makeTestFiller( final String label ) {

        JPanel filler;
        filler = new JPanel() {

            protected void paintComponent( Graphics g ) {

                super.paintComponent( g );

                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor( Color.YELLOW );
                g2d.fillRect( 0, 0, getWidth(), getHeight() );
                g2d.setColor( Color.RED );
                g2d.fillRect( 0, 0, 3, 3 );
                g2d.fillRect( getWidth() - 3, 0, 3, 3 );
                g2d.fillRect( 0, getHeight() - 3, 3, 3 );
                g2d.fillRect( getWidth() - 3, getHeight() - 3, 3, 3 );
                g2d.setColor( Color.BLACK );
                Insets insets = getInsets();
                int left = insets.left;
                int top = insets.top;
                int right = getWidth() -
                            insets.right -
                            1;    // subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1
                int bottom = getHeight() -
                             insets.bottom -
                             1;    // subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1

//		g2d.drawLine( left, top, right, bottom );
//		g2d.drawLine( left, bottom, right, top );
//		String l1msg = LinearLayoutUtil.myDrawLine( "filler1", g2d, left, top, right, bottom );
//		String l2msg = LinearLayoutUtil.myDrawLine( "filler2", g2d, left, bottom, right, top );
//		java.util.List<Integer> increasing = new FormattingLinkedList<>();
//		java.util.List<Integer> decreasing = new FormattingLinkedList<>();
//		for ( int y = top; y <= bottom; y += 1 ) {
//
//		    increasing.add( y );
//		    decreasing.add( 0, y );
//
//		}
//		for ( int y : decreasing ) {
//
//		    float v = ( (float) ( y - top ) ) / ( bottom - top );
//		    Logger.logMsg( "v=" + v );
//		    g2d.setColor( new Color( v, v, v ) );
//
//		    LinearLayoutUtil.myDrawLine( "filler@" + y, g2d, left, y, right, bottom );
//
//		}
                String l1msg = LinearLayoutUtil.myDrawLine( "filler1", g2d, left, top, right, bottom );
//		String l3msg = LinearLayoutUtil.myDrawLine( "filler3", g2d, left, bottom - 1, right, ( top + bottom ) / 2 );
                String l2msg = LinearLayoutUtil.myDrawLine( "filler2", g2d, left, bottom, right, 1 );
//		String l2msg = LinearLayoutUtil.myDrawLine( "filler2", g2d, right - 10, 1, left, bottom );
//		String l4msg = LinearLayoutUtil.myDrawLine( "filler4", g2d, right - 10, top + 10, right, top );

                Logger.logMsg(
                        label + " main painted:  w=" + getWidth() + ", h=" + getHeight() + ", insets=" + ObtuseUtil.fInsets( insets ) // +
//			", l1=" + l1msg +
//			", l2=" + l2msg
                );

                ObtuseUtil.doNothing();

//		g2d.drawLine( insets.left, insets.top, getWidth() - insets.right, getHeight() - insets.bottom );
//		g2d.drawLine( insets.left, getHeight() - insets.bottom, getWidth() - insets.right, insets.top );

            }

        };

        return filler;

    }

}
