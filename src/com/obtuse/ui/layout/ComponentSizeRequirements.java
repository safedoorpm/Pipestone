/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout;

import com.obtuse.ui.layout.linear.LinearLayoutUtil;

import javax.swing.*;
import java.awt.*;

/**
 An augmented version of {@link SizeRequirements} which carries around the corresponding {@link Component} reference.
 */

public class ComponentSizeRequirements extends SizeRequirements {

    public final Component component;

    public ComponentSizeRequirements( final Component component ) {

        super();

        this.component = component;

    }

    public ComponentSizeRequirements(
            final Component component,
            final int minimum,
            final int preferred,
            final int maximum,
            @SuppressWarnings("SameParameterValue") final float alignment
    ) {

        super( minimum, preferred, maximum, alignment );

        this.component = component;

    }

    public static SizeRequirements getAlignedSizeRequirements(
            final SizeRequirements[] children
    ) {

        SizeRequirements totalAscent = new SizeRequirements();
        SizeRequirements totalDescent = new SizeRequirements();
        for ( SizeRequirements req : children ) {

            //	    Logger.logMsg( "req=" + req );

            int ascent = (int)( req.alignment * req.minimum );
            int descent = req.minimum - ascent;
            totalAscent.minimum = Math.max( ascent, totalAscent.minimum );
            totalDescent.minimum = Math.max( descent, totalDescent.minimum );
//	    Logger.logMsg( "[" + i + "]:  ascent=" + ascent + ", descent=" + descent + ", totalAscent.minimum=" + totalAscent.minimum + ", totalDescent
// .minimum=" + totalDescent.minimum );

            ascent = (int)( req.alignment * req.preferred );
            descent = req.preferred - ascent;
            totalAscent.preferred = Math.max( ascent, totalAscent.preferred );
            totalDescent.preferred = Math.max( descent, totalDescent.preferred );
//	    Logger.logMsg( "[" + i + "]:  ascent=" + ascent + ", descent=" + descent + ", totalAscent.preferred=" + totalAscent.preferred + ",
// totalDescent.preferred=" + totalDescent.preferred );

            ascent = (int)( req.alignment * req.maximum );
            descent = req.maximum - ascent;
            totalAscent.maximum = Math.max( ascent, totalAscent.maximum );
            totalDescent.maximum = Math.max( descent, totalDescent.maximum );
//	    Logger.logMsg( "[" + i + "]:  ascent=" + ascent + ", descent=" + descent + ", totalAscent.maximum=" + totalAscent.maximum + ", totalDescent
// .maximum=" + totalDescent.maximum );

        }

        int min = (int)Math.min( (long)totalAscent.minimum + (long)totalDescent.minimum, Integer.MAX_VALUE );
        int pref = (int)Math.min( (long)totalAscent.preferred + (long)totalDescent.preferred, Integer.MAX_VALUE );
        int max = (int)Math.min( (long)totalAscent.maximum + (long)totalDescent.maximum, Integer.MAX_VALUE );

//	Logger.logMsg( "min/pref/max=" + min + "/" + pref + "/" + max );

        float alignment = 0.0f;
        if ( min > 0 ) {
            alignment = (float)totalAscent.minimum / min;
            alignment = alignment > 1.0f ? 1.0f : alignment < 0.0f ? 0.0f : alignment;
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        SizeRequirements rval = new SizeRequirements( min, pref, max, alignment );
//	Logger.logMsg( "rval=" + rval );
//	Logger.logMsg( "" );

        return rval;
    }

    public String toString() {

        return "ComponentSizeRequirements( " + LinearLayoutUtil.fullName( component.getParent(), component ) + ":  " + super.toString() + " )";

    }

}
