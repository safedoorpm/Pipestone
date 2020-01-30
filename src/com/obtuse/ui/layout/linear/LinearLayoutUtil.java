package com.obtuse.ui.layout.linear;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.ObtuseSwingUtils;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.WatchList;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
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

    private static final WatchList<Container> s_containerWatchlist = new WatchList<>( "container watchlist" );
    private static final WatchList<Component> s_componentWatchlist = new WatchList<>( "component watchlist" );

    private LinearLayoutUtil() {

        super();

    }

    public static boolean isContainerOnWatchlist( final @NotNull Container c ) {

        return s_containerWatchlist.isEntityOnWatchList( c );

    }

    public static int addContainerToWatchList( final @NotNull Container c ) {

        return s_containerWatchlist.addEntityToWatchList( c );

    }

    public static int removeContainerFromWatchList( final @NotNull Container c ) {

        return s_containerWatchlist.removeEntityFromWatchList( c );

    }

    public static boolean isComponentOnWatchlist( final @NotNull Component c ) {

        return s_componentWatchlist.isEntityOnWatchList( c );

    }

    @SuppressWarnings("UnusedReturnValue")
    public static int addComponentToWatchList( final @NotNull Component c ) {

        return s_componentWatchlist.addEntityToWatchList( c );

    }

    public static int removeComponentFromWatchList( final @NotNull Component c ) {

        return s_componentWatchlist.removeEntityFromWatchList( c );

    }

    @NotNull
    public static LinearContainer createPanel3( final String name, final LinearOrientation orientation ) {

        LinearContainer rval = new LinearContainer3( name, orientation ) {

            @SuppressWarnings("EmptyMethod")
            public void setBounds( final int x, final int y, final int w, final int h ) {

                super.setBounds( x, y, w, h );

            }

            public String toString() {

                return "LLU.createdPanel3( name=\"" +
                       getName() +
                       "\", cc=" +
                       getComponentCount() +
                       " ), bounds=" +
                       ObtuseUtil.fBounds( getBounds() );

            }

            public void setLayout( final LayoutManager lm ) {

                super.setLayout( lm );

                ObtuseUtil.doNothing();

            }

        };

        return rval;

    }

    @NotNull
    public static LinearContainer createPanel3(
            final @NotNull String name,
            final LinearOrientation orientation,
            @SuppressWarnings("SameParameterValue") final ContainerConstraints containerConstraints,
            @SuppressWarnings("SameParameterValue") final ComponentConstraints componentConstraints
    ) {

        LinearContainer rval = new LinearContainer3( name, orientation, containerConstraints, componentConstraints ) {

            @SuppressWarnings("EmptyMethod")
            public void setBounds( final int x, final int y, final int w, final int h ) {

                super.setBounds( x, y, w, h );

            }

            public String toString() {

                return "LLU.createdPanel3( name=\"" +
                       getName() +
                       "\", cc=" +
                       getComponentCount() +
                       " ), bounds=" +
                       ObtuseUtil.fBounds( getBounds() );

            }

        };

        return rval;

    }

    public static String myDrawLine( final String who, final Graphics2D g2d, final int x1, final int y1, final int x2, final int y2 ) {

        String msg = "drawLine( " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + " )";
        g2d.drawLine( x1, y1, x2, y2 );
        Logger.logMsg( who + ":  " + msg );

        return msg;

    }

    @NotNull
    public static JComponent makeThing(
            final @NotNull String label,
            @Nullable final Dimension minSize,
            @Nullable final Dimension prefSize,
            @Nullable final Dimension maxSize
    ) {

        JPanel rval = new JPanel() {

            public String getName() {

                return label;

            }

            public void setMinimumSize( final Dimension size ) {

                super.setMinimumSize( size );
                if ( _reportShapeChanges ) {

                    Logger.logMsg( "Thing \"" + getName() + "\" minSize set to " + ObtuseUtil.fDim( size ) );

                }

            }

            public void setPreferredSize( final Dimension size ) {

                super.setPreferredSize( size );
                if ( _reportShapeChanges ) {

                    Logger.logMsg( "Thing \"" + getName() + "\" prefSize set to " + ObtuseUtil.fDim( size ) );

                }

            }

            public void setMaximumSize( final Dimension size ) {

                super.setMaximumSize( size );
                if ( _reportShapeChanges ) {

                    Logger.logMsg( "Thing \"" + getName() + "\" maxSize set to " + ObtuseUtil.fDim( size ) );

                }

            }

            public void setBounds( final int x, final int y, final int w, final int h ) {

                super.setBounds( x, y, w, h );

                Logger.logMsg( "Thing \"" +
                               getName() +
                               "\" created by makeThing resized from " +
                               ObtuseUtil.fBounds( getBounds() ) +
                               " to " +
                               ObtuseUtil.fBounds( x, y, w, h ) );
                ObtuseUtil.doNothing();

            }

            @SuppressWarnings("deprecation")
            public void reshape( final int x, final int y, final int w, final int h ) {

                super.reshape( x, y, w, h );

                Logger.logMsg( "Thing \"" +
                               getName() +
                               "\" created by makeThing RESHAPED from " +
                               ObtuseUtil.fBounds( getBounds() ) +
                               " to " +
                               ObtuseUtil.fBounds( x, y, w, h ) );
                ObtuseUtil.doNothing();

            }

            protected void paintComponent( final Graphics g ) {

                super.paintComponent( g );

                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor( Color.BLUE );
                Insets insets = getInsets();
                int left = insets.left;
                int top = insets.top;
                int right = getWidth() -
                            insets.right -
                            1;    // subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1
                int bottom = getHeight() -
                             insets.bottom -
                             1;    // subtracting 1 accounts for fact that last pixel in a n pixel wide space is at location n-1
                String l1msg = myDrawLine( "thing", g2d, left, top, right, bottom );
                String l2msg = myDrawLine( "thing", g2d, left, bottom, right, top );
                Logger.logMsg(
                        "thing painted:  w=" + getWidth() + ", h=" + getHeight() + ", insets=" + ObtuseUtil.fInsets( insets ) +
                        ", l1=" + l1msg +
                        ", l2=" + l2msg
                );

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

        Logger.logMsg( "Thing \"" +
                       rval.getName() +
                       "\"'s alignment is ( " +
                       rval.getAlignmentX() +
                       ", " +
                       rval.getAlignmentY() +
                       " ) @ " +
                       ObtuseUtil.fBounds( rval.getBounds() ) );

        return rval;

    }

    public static void makeButtons( final JComponent component, final int nb ) {

        makeButtons( component, nb, "Hello!", null );

    }

    public static void makeButtons( final JComponent component, final int nb, final MyActionListener listener ) {

        makeButtons( component, nb, "Hello!", listener );

    }

    public static void makeButtons( final JComponent component, final int nb, final String label ) {

        makeButtons( component, nb, label, null );

    }

    public static void makeButtons( final JComponent component, final int nb, final String label, final MyActionListener listener ) {

        component.setBorder( BorderFactory.createLineBorder( Color.GREEN ) );
        addContainerToWatchList( component );
        component.setAlignmentX( 0.5f );
        component.setAlignmentY( 0.0f );

        for ( int i = 0; i < nb; i += 1 ) {

            JButton jButton3 = new JButton( label ) {
                public void setBounds( final int x, final int y, final int width, final int height ) {

                    super.setBounds( x, y, width, height );

                    Logger.logMsg( "jButton3's bounds set to " + ObtuseUtil.fBounds( x, y, width, height ) );

                }

                public String toString() {

                    return "jButton3:  " + super.toString();

                }

            };
            float alignmentX = nb == 1 ? 0.5f : Math.max( Math.min( i / (float)( nb - 1 ), 1f ), 0f );
            Logger.logMsg( "alignmentX = " + alignmentX );
            jButton3.setAlignmentX( alignmentX );
            addComponentToWatchList( jButton3 );

            jButton3.addActionListener(
                    new MyActionListener() {

                        @Override
                        public void myActionPerformed( final ActionEvent actionEvent ) {

                            ObtuseSwingUtils.showWhereWeAre( jButton3, actionEvent );
                            if ( listener != null ) {

                                listener.actionPerformed( actionEvent );

                            }

                        }

                    }
            );

            component.add( jButton3 );

        }

    }

    @NotNull
    public static LinearContainer getLinearContainer3( final String title, final LinearOrientation orientation, final boolean watch ) {

        final boolean logTrace = false;

        final String interesting = "inner";

        LinearContainer3 rval = new LinearContainer3( title, orientation ) {

            public void logIt( final Throwable e ) {

                //noinspection ConstantConditions
                if ( logTrace ) {

                    Logger.logStackTrace( e );

                } else {

                    Logger.logMsg( e.getMessage() );

                }

            }

            public void invalidate() {

                super.invalidate();
                logIt( new IllegalArgumentException( title + "'s invalidate method called" ) );

            }

            public void validate() {

                super.invalidate();
                logIt( new IllegalArgumentException( title + "'s validate method called" ) );

            }

            public void revalidate() {

                super.invalidate();
                logIt( new IllegalArgumentException( title + "'s revalidate method called" ) );

            }

            public void setBounds( final Rectangle r ) {

                super.setBounds( r );
                if ( interesting.equals( title ) ) {

                    Logger.logMsg( "" );
                    Logger.logMsg( "" );
                    Logger.logMsg( "### " + interesting + " bounds set to " + ObtuseUtil.fBounds( r ) + " using rectangle" );
                    Logger.logMsg( "" );
                    Logger.logMsg( "" );

                }

                logIt( new IllegalArgumentException( title + "'s setBounds( " + ObtuseUtil.fBounds( r ) + " ) called" ) );

            }

            public void setBounds( final int x, final int y, final int w, final int h ) {

                super.setBounds( x, y, w, h );
                if ( interesting.equals( title ) ) {

                    Logger.logMsg( "" );
                    Logger.logMsg( "" );
                    Logger.logMsg( "### " + interesting + " bounds set to " + ObtuseUtil.fBounds( x, y, w, h ) + " using x, y, w and h" );
                    Logger.logMsg( "" );
                    Logger.logMsg( "" );

                }
                logIt( new IllegalArgumentException( title + "'s setBounds( " + ObtuseUtil.fBounds( x, y, w, h ) + " ) called" ) );

            }

        };

        if ( watch ) {

            LinearContainer3.watch( rval );

        }

        return rval;

    }

    public void setReportShapeChanges( final boolean report ) {

        LinearLayoutUtil._reportShapeChanges = report;

    }

    public boolean reportShapeChanges() {

        return LinearLayoutUtil._reportShapeChanges;

    }

    public static void addLocationTracer( final Container container ) {

        container.addMouseListener(
                new MouseAdapter() {

                    public void mouseClicked( final MouseEvent e ) {

                        ObtuseSwingUtils.showWhereWeAre( container, e );

                        ObtuseUtil.doNothing();

                    }

                }

        );

    }

    public static void rePackWindow( final Container container ) {

        Container c = container;
        while ( c != null ) {

            if ( c instanceof Window ) {

                Container cx = c;

                SwingUtilities.invokeLater(
                        () -> {

                            ((Window)cx).pack();

                            Logger.logMsg( cx.getClass().getCanonicalName() + " re-packed" );

                        }
                );

                return;

            }

            c = c.getParent();

        }

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

        public MyDimension( final int width, final int height ) {

            super( width, height );
        }

        // Having a re-wrapper is a bad idea since the owner of the original Dimension instance
        // might change their instance by directly changing the width and height fields.
        // Mutable information classes are, as a general rule, EVIL!!!

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

        public SpaceBrick( final int width, final int height ) {
            this( width, height, width, height, width, height );

        }

        public SpaceBrick( final int minWidth, final int minHeight, final int prefWidth, final int prefHeight, final int maxWidth, final int maxHeight ) {

            this( new MyDimension( minWidth, minHeight ), new MyDimension( prefWidth, prefHeight ), new MyDimension( maxWidth, maxHeight ) );
        }

        public SpaceBrick( final Dimension min, final Dimension pref, final Dimension max ) {

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

        public void setMinimumSize( final Dimension min ) {

            Logger.logMsg( "SpaceBrick:  request to change min from " + _min + " to " + min + " ignored" );

        }

        public void setPreferredSize( final Dimension pref ) {

            Logger.logMsg( "SpaceBrick:  request to change pref from " + _pref + " to " + pref + " ignored" );

        }

        public void setMaximumSize( final Dimension max ) {

            Logger.logMsg( "SpaceBrick:  request to change max from " + _max + " to " + max + " ignored" );

        }

        public String toString() {

            return "SpaceBrick( min=" + _min + ", pref=" + _pref + ", max=" + _max + " )";

        }

    }

    public static class WeightedSimpleFiller extends JPanel {

        private final float _weight;

        public WeightedSimpleFiller( final @NotNull String name, final float weight ) {

            super();

            setName( name );

            if ( weight <= 0 ) {

                throw new IllegalArgumentException( "WeightedSimpleFiller:  weight must be positive (is " + weight + ")" );

            }

            _weight = weight;

            setOpaque( false );

            addLocationTracer( this );

        }

        public LinearOrientation getOrientation() {

            Container parent = getParent();

            if ( parent == null ) {

                throw new IllegalArgumentException(
                        "LinearLayoutManager.WeightedSimpleFiller:  unable to get orientation until we are placed in a LinearContainer2" );

            }

            if ( parent instanceof LinearContainer ) {

                return ( (LinearContainer)parent ).getOrientation();

            } else {

                throw new IllegalArgumentException(
                        "LinearLayoutManager.WeightedSimpleFiller:  we must reside within a LinearContainer2 (we are in a " +
                        parent.getClass().getName() +
                        ")" );

            }

        }

        public void paintComponent( final Graphics g ) {

            super.paintComponent( g );

            Graphics2D g2D = (Graphics2D)g.create();

            if ( isOpaque() ) {

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

        }

        public float getWeight() {

            return _weight;

        }

        public String toString() {

            return "WeightedSimpleFiller( " + getWeight() + " )";

        }

    }

    public static class SimpleFiller extends JPanel /*implements LinearMagic*/ {

        public SimpleFiller( final @NotNull String name ) {

            super();

            setName( name );

            setOpaque( false );

            addLocationTracer( this );

        }

        public LinearOrientation getOrientation() {

            Container parent = getParent();

            if ( parent == null ) {

                throw new IllegalArgumentException(
                        "LinearLayoutManager.SimpleFiller:  unable to get orientation until we are placed in a LinearContainer2" );

            }

            if ( parent instanceof LinearContainer ) {

                return ( (LinearContainer)parent ).getOrientation();

            } else {

                throw new IllegalArgumentException( "LinearLayoutManager.SimpleFiller:  we must reside within a LinearContainer2 (we are in a " +
                                                    parent.getClass().getName() +
                                                    ")" );

            }

        }

        public boolean isVertical() {

            return getOrientation() == LinearOrientation.VERTICAL;

        }

        public boolean isHorizontal() {

            return getOrientation() == LinearOrientation.HORIZONTAL;

        }

        public void paintComponent( final Graphics g ) {

            super.paintComponent( g );

            Graphics2D g2D = (Graphics2D)g.create();
            
            if ( isOpaque() ) {

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
