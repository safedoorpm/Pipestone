package com.obtuse.ui.layout.layoutTracer;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 Trace the calls to an arbitrary {@link LayoutManager2}.
 */

public class LayoutTracer implements LayoutManager2 {

    enum MinMaxPref {
        MIN { public String getFullName() { return "minimum"; } },
        PREF { public String getFullName() { return "preferred"; } },
        MAX { public String getFullName() { return "maximum"; } };

        public abstract String getFullName();
    }

    private final LayoutManager2 _actualLayoutManager;

    public LayoutTracer( final @NotNull LayoutManager2 actualLayoutManager ) {
        super();

        _actualLayoutManager = actualLayoutManager;

        Logger.logMsg( "LayoutTracer:  tracing " + actualLayoutManager );

    }

    public String toString() {

        return "LayoutTracer( " + _actualLayoutManager.getClass().getCanonicalName() + " )";

    }

    @Override
    public void addLayoutComponent( final Component comp, final Object constraints ) {

        Logger.pushNesting( "" );

        try {

            Logger.logMsg( "LayoutTracer.addLayoutComponent( " + comp + ", " + constraints + " )" );

            _actualLayoutManager.addLayoutComponent( comp, constraints );

        } finally {

            Logger.popNestingLevel( "" );

        }

    }

    private Dimension minMaxPref( final Container target, final @NotNull MinMaxPref mmp ) {

        Logger.pushNesting( "." );

        boolean gotRval = false;
        Dimension rval = null;
        try {

            Logger.logMsg( "LayoutTracer." + mmp.getFullName() + "LayoutSize( " + target + " )" );

            switch ( mmp ) {

                case MIN:
                    rval = _actualLayoutManager.minimumLayoutSize( target );
                    break;

                case PREF:
                    rval = _actualLayoutManager.preferredLayoutSize( target );
                    break;

                case MAX:
                    rval = _actualLayoutManager.maximumLayoutSize( target );
                    break;

            }

            gotRval = true;

            return rval;

        } finally {

            if ( gotRval ) {

                Logger.logMsg( "returned " + ObtuseUtil.fDim( rval ) );

            } else {

                Logger.logMsg( "failed" );

            }

            Logger.popNestingLevel( "." );

        }

    }

    private float getLayoutAlignment( boolean doX, final Container target ) {

        Logger.pushNesting( "." );

        boolean gotRval = false;
        float rval = 0f;
        try {

            Logger.logMsg( "LayoutTracer.getLayoutAlignment" + ( doX ? "X" : "Y" ) + "( " + target + " )" );

            rval = doX ? _actualLayoutManager.getLayoutAlignmentX( target ) : _actualLayoutManager.getLayoutAlignmentY( target );

            gotRval = true;

            return rval;

        } finally {

            if ( gotRval ) {

                Logger.logMsg( "returned " + rval );

            } else {

                Logger.logMsg( "failed" );

            }

            Logger.popNestingLevel( "." );

        }

    }

    @Override
    public float getLayoutAlignmentX( final Container target ) {

        return getLayoutAlignment( true, target );

    }

    @Override
    public float getLayoutAlignmentY( final Container target ) {

        return getLayoutAlignment( false, target );

    }

    @Override
    public void invalidateLayout( final Container target ) {

        Logger.pushNesting( "" );

        try {

            Logger.logMsg( "LayoutTracer.invalidateLayout( " + target + " )" );

            _actualLayoutManager.invalidateLayout( target );

        } finally {

            Logger.popNestingLevel( "" );

        }

    }

    @Override
    public void addLayoutComponent( final String name, final Component comp ) {

        Logger.pushNesting( "" );

        try {

            Logger.logMsg( "LayoutTracer.addLayoutComponent( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

            _actualLayoutManager.addLayoutComponent( name, comp );

        } finally {

            Logger.popNestingLevel( "" );

        }

    }

    @Override
    public void removeLayoutComponent( final Component comp ) {

        Logger.pushNesting( "" );

        try {

            Logger.logMsg( "LayoutTracer.removeLayoutComponent( " + comp + " )" );

            _actualLayoutManager.removeLayoutComponent( comp );

        } finally {

            Logger.popNestingLevel( "" );

        }

    }

    @Override
    public Dimension minimumLayoutSize( final Container target ) {

        return minMaxPref( target, MinMaxPref.MIN );

    }

    @Override
    public Dimension preferredLayoutSize( final Container target ) {

        return minMaxPref( target, MinMaxPref.PREF );

    }

    @Override
    public Dimension maximumLayoutSize( final Container target ) {

        return minMaxPref( target, MinMaxPref.MAX );

    }

    @Override
    public void layoutContainer( final Container parent ) {

        Logger.logMsg( "" );

        Logger.pushNesting( "" );

        try {

            Logger.logMsg( "LayoutTracer.layoutContainer( " + parent + " )" );

            _actualLayoutManager.layoutContainer( parent );

        } finally {

            Logger.popNestingLevel( "" );

        }

    }

    public LayoutManager2 getWrappedLayoutManager2() {

        return _actualLayoutManager;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "LayoutTracer", "testing", null );

        JFrame jf = new JFrame( "LayoutTracer tester" );
        JPanel jp = new JPanel();
        jp.setLayout( new LayoutTracer( new BorderLayout() ) );
        jp.add( new JLabel( "North" ), BorderLayout.NORTH );
        jp.add( new JLabel( "South" ), BorderLayout.SOUTH );
        jp.add( new JLabel( "East" ), BorderLayout.EAST );
        jp.add( new JLabel( "West" ), BorderLayout.WEST );
        jp.add( new JLabel( "Center" ), BorderLayout.CENTER );

        jf.setContentPane( jp );
        jf.pack();
        jf.setVisible( true );

    }

}
