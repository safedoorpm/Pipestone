/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 %%% Something clever goes here.
 */

public interface LinearContainer {

    void setBorder( Border border );

    Component add( Component comp );

    Component add( Component comp, int ix );

    void add( Component comp, Object constraints );

//    default Component addLinearContainer( LinearContainer comp ) {
//
//	return add( (Component)comp );
//
//    }

    default Container getAsContainer() {

	return (Container)this;

    }

    default JComponent getAsJComponent() {

	return (JComponent)this;

    }

    LinearOrientation getOrientation();

    void setAlignmentX( float v );

    void setAlignmentY( float v );

    void setLengthConstraints( int minLength, int prefLength, int maxLength );

    void setLengthConstraints( ConstraintTriplet lengthConstraints );

    void setBreadthConstraints( int minBreadth, int prefBreadth, int maxBreadth );

    void setBreadthConstraints( ConstraintTriplet breadthConstraints );

//    public void setTrackParentBreadth( boolean trackParentBreadth );
//
//    public boolean trackParentBreadth();

    default int applyBreadthConstraints( int v ) {

	return applyConstraints( getBreadthConstraints(), v );

    }

    default int applyLengthConstraints( int v ) {

	return applyConstraints( getLengthConstraints(), v );

    }

    default int applyConstraints( ConstraintTriplet triplet, int v ) {

	if ( triplet == null ) {

	    return v;

	} else {

	    @SuppressWarnings("UnnecessaryLocalVariable")
	    int newValue = Math.min( Math.max( v, triplet.getMinimum() ), triplet.getMaximum() );

	    return newValue;

	}

////	ConstraintTriplet bc = getBreadthConstraints();
//	if ( triplet == null ) {
//
//	    return v;
//
//	}
//
//	if ( v <= triplet.getMinimum() ) {
//
//	    return triplet.getMinimum();
//
//	}
//
//	if ( v >= triplet.getMaximum() ) {
//
//	    return triplet.getMaximum();
//
//	}
//
////	if ( bc.getPreferred() >= 0 ) {
////
////	    return bc.getPreferred();
////
////	}
//
//	return v;

    }

    int getComponentCount();

    Component getComponent( int ix );

    Insets getInsets();

    void setMinimumSize( Dimension dimension );

    void setMaximumSize( Dimension dimension );

    void setPreferredSize( Dimension dimension );

    void remove( int ix );

    void revalidate();

    ConstraintTriplet getLengthConstraints();

    ConstraintTriplet getBreadthConstraints();

    boolean isVisible();

    void setVisible( boolean visible );

}
