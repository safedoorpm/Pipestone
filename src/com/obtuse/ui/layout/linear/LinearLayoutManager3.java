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

        public SimpleConstraint( @SuppressWarnings("SameParameterValue") final @NotNull String name, final boolean enabled ) {

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

        public boolean equals( final Object rhs ) {

            return rhs instanceof Constraint && getName().equals( ( (Constraint)rhs ).getName() );

        }

        public int compareTo( final Constraint rhs ) {

            return getName().compareTo( rhs.getName() );

        }

        public String toString() {

            return "SimpleConstraint( \"" + getName() + "\", " + isEnabled() + " )";

        }

    }

    public static class ConstraintsTable extends TreeMap<String, Constraint> {

      public ConstraintsTable() {

            super();

        }

        public ConstraintsTable( final Constraint singletonConstraint ) {

            super();

            put( singletonConstraint.getName(), singletonConstraint );

        }

    }

    private final LinearContainer3 _target;

    private final LinearOrientation _orientation;

    private final Hashtable<Component, ConstraintsTable> _constraints = new Hashtable<>();

    private LinearLayoutManagerCache _cache;

    public LinearLayoutManager3(
            final @NotNull LinearOrientation orientation,
            final @NotNull LinearContainer3 target
    ) {

        super();

        _orientation = orientation;

        _target = target;

        preLoadCacheIfNecessary();

    }

    @Override
    public synchronized void addLayoutComponent( final Component comp, final Object xConstraints ) {

        implicitInvalidateLayout(
                ( comp == null ? "unknown" : comp.getClass().getCanonicalName() ) + " component added",
                "addLayoutComponent(Component,Object)",
                _target
        );

        if ( comp == null ) {

            throw new IllegalArgumentException(
                    "LinearLayoutManager3.addLayoutComponent( Component, Object ):  component is null"
            );

        }

        _constraints.remove( comp );
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

        ConstraintsTable copy = new ConstraintsTable();
        for ( String key : constraints.keySet() ) {

            Constraint value = constraints.get( key );

            if ( value == null ) {

                throw new IllegalArgumentException(
                        "LinearLayoutManager3.addLayoutComponent( Component, Object ):  constraint named \"" + key + "\" is null" );

            }

            copy.put( key, value );

        }

        _constraints.put( comp, copy );

    }

    private void checkContainer( final String who, final Container target ) {

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
    public synchronized float getLayoutAlignmentX( final Container target ) {

        checkContainer( "getLayoutAlignmentX", target );
        preLoadCacheIfNecessary();

        return _cache.getLayoutAlignmentX();

    }

    @Override
    public float getLayoutAlignmentY( final Container target ) {

        checkContainer( "getLayoutAlignmentY", target );
        preLoadCacheIfNecessary();

        return _cache.getLayoutAlignmentY();

    }

    private synchronized void implicitInvalidateLayout(
            @NotNull String why,
            @NotNull final String requester,
            @NotNull final LinearContainer3 target
    ) {

        if ( _cache != null && LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {

            Logger.logMsg(
                    "layout invalidated by " + requester +
                    " (target is " + target + "," +
                    " why is " + ObtuseUtil.enquoteToJavaString( why ) + ")"
            );

        }

        _cache = null;

    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized Dimension preferredLayoutSize( final Container target ) {

        checkContainer( "preferredLayoutSize", target );
        preLoadCacheIfNecessary();

        Dimension size = _cache.getPreferredSize();

        if ( "outer".equals( getTarget().getName() ) ) {

            ObtuseUtil.doNothing();

        }

        return size;

    }

    @Override
    public synchronized Dimension minimumLayoutSize( final Container target ) {

        checkContainer( "minimumLayoutSize", target );
        preLoadCacheIfNecessary();

        Dimension size = _cache.getMinimumSize();

        return size;

    }

    @Override
    public synchronized Dimension maximumLayoutSize( final Container target ) {

        checkContainer( "maximumLayoutSize", target );
        preLoadCacheIfNecessary();

        Dimension size = _cache.getMaximumSize();

        return size;

    }

    @Override
    public synchronized void invalidateLayout( final Container target ) {

        checkContainer( "invalidateLayout", target );

        implicitInvalidateLayout(
                "somebody asked nicely",
                "invalidateLayout",
                (LinearContainer3)target
        );

    }

    @Override
    public void addLayoutComponent( final String name, final Component comp ) {

        Logger.logMsg( "addLayoutComponent( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

        implicitInvalidateLayout(
                ( comp == null ? "unknown" : comp.getClass().getCanonicalName() ) + " component added",
                "addLayoutComponent(String,Component)",
                _target
        );

    }

    @Override
    public synchronized void removeLayoutComponent( final Component comp ) {

        implicitInvalidateLayout(
                ( comp == null ? "unknown" : comp.getClass().getCanonicalName() ) + " component removed",
                "removeLayoutComponent",
                _target
        );

        if ( comp == null ) {

            throw new IllegalArgumentException( "LinearLayoutManager3.removeLayoutComponent( Component ):  component is null" );

        } else {

            _constraints.remove( comp );

        }

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void layoutContainer( final Container parent ) {

        _cache = null;
        implicitInvalidateLayout(
                "just because",
                "layoutContainer",
                _target
        );

        LinearLayoutManagerCache cache;

        synchronized ( this ) {

            if ( LinearLayoutUtil.isContainerOnWatchlist( parent ) ) {

                ObtuseUtil.doNothing();

            }

            checkContainer( "layoutContainer", parent );

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

    }

    public LinearOrientation getOrientation() {

        return _orientation;

    }

    public LinearContainer3 getTarget() {

        return _target;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "LinearLayoutManager3", "testing" );

        JFrame frame = new JFrame( "test simple linear layouts" );
        LinearContainer inner = LinearLayoutUtil.getLinearContainer3( "inner", LinearOrientation.VERTICAL, true );
        inner.setBorder( BorderFactory.createEtchedBorder() );

        JButton jb1 = new JButton( "Say Hello" );
        jb1.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        Logger.logMsg( "hello" );

                    }

                }
        );
        inner.add( jb1 );

        JButton jb2 = new JButton( "Say Boo!" );
        jb2.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        Logger.logMsg( "Boo!" );

                    }

                }
        );
        inner.add( jb2 );

        LinearContainer outer = LinearLayoutUtil.getLinearContainer3( "outer", LinearOrientation.HORIZONTAL, true );
        outer.setBorder( BorderFactory.createLineBorder( Color.RED ) );
        outer.add( inner.getAsContainer() );
        JButton jb3 = new JButton( "Flip" );
        jb3.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

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

        frame.setContentPane( outer.getAsContainer() );
        frame.pack();
        frame.setVisible( true );

        ObtuseUtil.doNothing();

    }

    public static void mainX( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Testing" );

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

        Box b = new Box( BoxLayout.X_AXIS );
        b.add( main );
        b.add( new Box.Filler( new Dimension( 0, 0 ), new Dimension( 20, 20 ), new Dimension( 32767, 32767 ) ) );
        frame.setContentPane( b );
        frame.pack();
        frame.setVisible( true );

    }

    @NotNull
    public static JPanel makeMainContainer(
            @SuppressWarnings("SameParameterValue") final boolean makeLinearContainer,
            @SuppressWarnings("SameParameterValue") final LinearOrientation orientation,
            final Component[] extraComponents,
            @Nullable final ConstraintTuple breadthConstraints
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
                    public void myActionPerformed( final ActionEvent actionEvent ) {

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

        filler.setName( "filler" );
        LinearLayoutUtil.addLocationTracer( filler );
        main.add( filler );

        return main;

    }

    @NotNull
    public static JPanel makeTestFiller( final String label ) {

        JPanel filler;
        filler = new JPanel() {

            protected void paintComponent( final Graphics g ) {

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

                String l1msg = LinearLayoutUtil.myDrawLine( "filler1", g2d, left, top, right, bottom );
                String l2msg = LinearLayoutUtil.myDrawLine( "filler2", g2d, left, bottom, right, 1 );

                ObtuseUtil.doNothing();

            }

        };

        return filler;

    }

    public String toString() {

        return "LinearLayoutManager3( orientation=" + getOrientation() + ", container=" + getTarget() + " )";

    }

}
