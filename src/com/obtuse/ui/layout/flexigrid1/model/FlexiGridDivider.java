/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 Created by danny on 2018/04/30.
 */

public class FlexiGridDivider extends JPanel {


    private final DividerStyle _dividerStyle;
    private final FlexiGridPanelModel.Orientation _orientation;
    private final int _slotIndex;
    private final boolean _fullLength;

    public enum DividerStyle {
        SINGLE_LINE,
        DOUBLE_LINE
    }

    public FlexiGridDivider(
            final @NotNull DividerStyle dividerStyle,
            final @NotNull FlexiGridPanelModel.Orientation orientation,
            final int slotIndex,
            final boolean fullLength
    ) {
        super();

        _dividerStyle = dividerStyle;
        _orientation = orientation;

        _slotIndex = slotIndex;

        _fullLength = fullLength;

        if ( slotIndex < 0 ) {

            ObtuseUtil.doNothing();

        }

    }

    public FlexiGridBasicConstraint generateConstraint( final int iy ) {

        @NotNull FlexiGridBasicConstraint rval;

        if ( getOrientation().isRowOrientation() ) {

            rval = new FlexiGridBasicConstraint(
                    ( "divider r=" + _slotIndex + ", c=" + iy ),
                    getSlotIndex(),
                    iy
                    ).setHorizontalJustification( FlexiGridBasicConstraint.HJustification.CENTER )
             .setVerticalJustification( FlexiGridBasicConstraint.VJustification.CENTER );

            Logger.logMsg( "" + this + " has row-oriented constraint " + rval );

        } else {

            rval = new FlexiGridBasicConstraint(
                    ( "divider r=" + iy + ", c=" + _slotIndex ),
                    iy,
                    getSlotIndex()
                    ).setHorizontalJustification( FlexiGridBasicConstraint.HJustification.CENTER )
             .setVerticalJustification( FlexiGridBasicConstraint.VJustification.CENTER );

            Logger.logMsg( "" + this + " has column-oriented constraint " + rval );

        }

        return rval;

    }

    @NotNull
    public DividerStyle getDividerStyle() {

        return _dividerStyle;

    }

    @NotNull
    public FlexiGridPanelModel.Orientation getOrientation() {

        return _orientation;

    }

    public int getSlotIndex() {

//        return _slotIndex < 0 ? 0 : _slotIndex;

        return _slotIndex;

    }

    public boolean isFullLength() {

        return _fullLength;

    }

    public int computeBreadth() {

        int breadth;

        switch ( getDividerStyle() ) {

            case SINGLE_LINE:

                breadth = 1;

                break;


            case DOUBLE_LINE:

                breadth = 3;

                break;

            default:

                throw new HowDidWeGetHereError( "FlexiGridDivider.computeBreadth:  invalid style " + getDividerStyle() );

        }

        return breadth;

    }

    public void paint( final Graphics g ) {

        if ( ObtuseUtil.never() ) {

            return;

        }

        Logger.logMsg( "drawing divider " + this );

        Graphics gCopy = g.create();

        if ( isOpaque() ) {

            Color background = getBackground();
            Logger.logMsg( "background is " + background );
            gCopy.setColor( background );
            gCopy.fillRect( 0, 0, getWidth(), getHeight() );

        }

//        Color foreground = isFullLength() ? Color.CYAN : getForeground();
        Color foreground = getForeground();
        Logger.logMsg( "foreground is " + foreground );
        gCopy.setColor( foreground );
        switch ( getDividerStyle() ) {

            case SINGLE_LINE:

                if ( getOrientation().isRowOrientation() ) {

                    gCopy.drawLine( 0, 0, 32767, 0 );

                } else {

                    gCopy.drawLine( 0, 0, 0, 32767 );

                }

                break;

            case DOUBLE_LINE:

                if ( getOrientation().isRowOrientation() ) {

                    gCopy.drawLine( 0, 0, 32767, 0 );
                    gCopy.drawLine( 0, 2, 32767, 2 );

                } else {

                    gCopy.drawLine( 0, 0, 0, 32767 );
                    gCopy.drawLine( 2, 0, 2, 32767 );

                }

                break;

            default:

                throw new HowDidWeGetHereError( "FlexiGridDivider.paint:  invalid style " + getDividerStyle() );

        }

    }

    public Dimension getMinimumSize() {

        Dimension rval;

        if ( getOrientation().isRowOrientation() ) {

            rval = new Dimension( 1, computeBreadth() );

        } else {

            rval = new Dimension( computeBreadth(), 1 );

        }

        return rval;

    }

    public Dimension getPreferredSize() {

        Dimension rval;

        if ( getOrientation().isRowOrientation() ) {

            rval = new Dimension( 1, computeBreadth() );

        } else {

            rval = new Dimension( computeBreadth(), 1 );

        }

        return rval;

    }

    public Dimension getMaximumSize() {

        Dimension rval;

        if ( getOrientation().isRowOrientation() ) {

            rval = new Dimension( 32767, computeBreadth() );

        } else {

            rval = new Dimension( computeBreadth(), 32767 );

        }

        return rval;

    }

    public void setBounds( int x, int y, int w, int h ) {

        super.setBounds( x, y, w, h );

        Logger.logMsg( "" + this + generateConstraint( 0 ) + " is " + ObtuseUtil.fBounds( x, y, w, h ) );

        ObtuseUtil.doNothing();

    }

    public String toString() {

        return "FlexiGridDivider( " + getDividerStyle() + ", " + getOrientation() + ", " + getSlotIndex() + ", " + ( isFullLength() ? "full width" : "NOT full width" ) + " )";

    }

}
