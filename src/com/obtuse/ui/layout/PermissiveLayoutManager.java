package com.obtuse.ui.layout;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import java.awt.*;

/**
 A layout manager that leaves everything as it found it.
 <p/>
 <p>Handy for folks who like to work with their hands or who have VERY specific ideas about how a container should be laid out.</p>
 <p/>
 <p>Note that this layout manager really does nothing more than go through the motions of laying components out.
 YOU have to invoke each component's {@link Component#setBounds(Rectangle)} or {@link Component#setBounds(int, int, int, int)}
 method before the container gets laid out or what you see will be what you get but it is <b>NOT</b> very likely to be what you want.</p>
 */

public class PermissiveLayoutManager implements LayoutManager2 {

    public boolean _verbose = false;

    public PermissiveLayoutManager() {
        super();

    }

    public void setVerbose( boolean verbose ) {

        _verbose = verbose;

    }

    public boolean isVerbose() {

        return _verbose;

    }

    @Override
    public void addLayoutComponent( final String name, final Component comp ) {
        
        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.addLayoutComponent:  add( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

        ObtuseUtil.doNothing();

    }

    @Override
    public void removeLayoutComponent( final Component comp ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.removeLayoutComponent:  remove( " + comp + " )" );

        ObtuseUtil.doNothing();

    }

    @Override
    public Dimension preferredLayoutSize( final Container parent ) {

        Dimension dimension = new Dimension( 100, 100 );

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.preferredLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

        return dimension;

    }

    @Override
    public Dimension minimumLayoutSize( final Container parent ) {

        Dimension dimension = new Dimension( 100, 100 );

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.minimumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

        return dimension;

    }

    @Override
    public void layoutContainer( final Container parent ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.layoutContainer:  working" );
        if ( isVerbose() ) for ( Component c : parent.getComponents() ) {

            Logger.logMsg(
                    "component " + c.getName() + " is a " + c.getClass().getSimpleName() +
                    " which is " + ( c.isVisible() ? "" : "not " ) +
                    "visible with bounds " + ObtuseUtil.fBounds( c.getBounds() )
            );

        }

        ObtuseUtil.doNothing();

    }

    private void setBounds( String name, Component c, Rectangle r ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.setBounds( " + ObtuseUtil.enquoteToJavaString( name ) + " to " + ObtuseUtil.fBounds( r ) );
        c.setBounds( r );

        ObtuseUtil.doNothing();

    }

    @Override
    public void addLayoutComponent( final Component comp, final Object constraints ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.addLayoutComponent:  add( " + comp + ", " + ObtuseUtil.enquoteJavaObject( constraints ) + " )" );

        ObtuseUtil.doNothing();

    }

    @Override
    public Dimension maximumLayoutSize( final Container target ) {

        Dimension dimension = target.getMaximumSize();

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.maximumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

        return dimension;

    }

    @Override
    public float getLayoutAlignmentX( final Container target ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.getLayoutAlignmentX:  returning target.getAlignmentX() == " + target.getAlignmentX() );

        return target.getAlignmentX();

    }

    @Override
    public float getLayoutAlignmentY( final Container target ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.getLayoutAlignmentY:  returning target.getAlignmentY() == " + target.getAlignmentY() );

        return target.getAlignmentY();

    }

    @Override
    public void invalidateLayout( final Container target ) {

        if ( isVerbose() ) Logger.logMsg( "PermissiveLayoutManager.invalidateLayout:  invalidated" );

        ObtuseUtil.doNothing();

    }

}
