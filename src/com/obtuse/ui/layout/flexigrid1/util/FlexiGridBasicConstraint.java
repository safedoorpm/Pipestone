/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 A basic FlexiGrid component constraint.
 */

@SuppressWarnings("unused")
public class FlexiGridBasicConstraint implements FlexiGridConstraint {

    public static final int DEFAULT_MARGIN = 1;

    public enum HJustification {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VJustification {
        TOP,
        CENTER,
        BOTTOM
    }

    private final String _name;
    private int _row;
    private int _col;
//    private final FlexiGridConstraintCategory _constraintCategory;

    private HJustification _horizontalJustification = null;
    private VJustification _verticalJustification = null;

    private Insets _margins = null;

    public FlexiGridBasicConstraint(
            final @NotNull String name,
            final int row,
            final int col
    ) {

        super();

        if ( row < 0 || col < 0 ) {

            ObtuseUtil.doNothing();

        }

        _name = name;
        _row = row;
        _col = col;

    }

    @Override
    @NotNull
    public String getName() {

        return _name;

    }

    public void changeRow( int oldRow, int newRow ) {

        // Make sure that the caller is not confused.

        if ( _row == oldRow ) {

            _row = newRow;

        } else {

            throw new IllegalArgumentException(
                    "FlexiGridBasicConstraint.changeRow(" + oldRow + "," + newRow + "):  " +
                    "caller claims old row is " + oldRow + " but it is actually " + _row
            );

        }

    }

    public int getRow() {

        return _row;

    }

    public void changeColumn( int oldColumn, int newColumn ) {

        // Make sure that the caller is not confused.

        if ( _col == oldColumn ) {

            _col = newColumn;

        } else {

            throw new IllegalArgumentException(
                    "FlexiGridBasicConstraint.changeColumn(" + oldColumn + "," + newColumn + "):  " +
                    "caller claims old column is " + oldColumn + " but it is actually " + _col
            );

        }
    }

    public int getCol() {

        return _col;

    }

    @NotNull
    public FlexiGridBasicConstraint setMargins( final int top, final int left, final int bottom, final int right ) {

        if ( _margins == null ) {

            _margins = new Insets( top, left, bottom, right );

        } else {

            throw new IllegalArgumentException( "FlexiGridBasicConstraint.setMargins:  already set to " + getMargins() );

        }

        return this;

    }

    @NotNull
    public Insets getMargins() {

        return _margins == null ? new Insets( DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN ) : _margins;

    }

    /**
     Set horizontal justification.
     <p>Once set, the horizontal justification constraint becomes immutable.</p>
     @param horizontalJustification the horizontal justification constraint value:
     <ul>
     <li>{@link HJustification#LEFT} left-justify this component within its available space.</li>
     <li>{@link HJustification#CENTER} center this component within its available space.</li>
     <li>{@link HJustification#RIGHT} right-justify this component within its available space.</li>
     </ul>
     @return this instance.
     @throws IllegalArgumentException if the horizontal justification constraint has already been set.
     */

    @NotNull
    public FlexiGridBasicConstraint setHorizontalJustification( HJustification horizontalJustification ) {

        if ( _horizontalJustification == null ) {
            _horizontalJustification = horizontalJustification;

        } else {

            throw new IllegalArgumentException( "FlexiGridBasicConstraint.setHorizontalJustification:  already set to " + _horizontalJustification );

        }

        return this;

    }

    /**
     Set vertical justification.
     <p>Once set, the vertical justification constraint becomes immutable.</p>
     @param verticalJustification the vertical justification constraint value:
     <ul>
     <li>{@link VJustification#TOP} place this component at the top of its available space.</li>
     <li>{@link VJustification#CENTER} center this component within its available space.</li>
     <li>{@link VJustification#BOTTOM} place this component at the bottom its available space.</li>
     </ul>
     @return this instance.
     @throws IllegalArgumentException if the vertical justification constraint has already been set.
     */

    @NotNull
    public FlexiGridBasicConstraint setVerticalJustification( VJustification verticalJustification ) {

        if ( _verticalJustification == null ) {
            _verticalJustification = verticalJustification;

        } else {

            throw new IllegalArgumentException( "FlexiGridBasicConstraint.setVerticalJustification:  already set to " + _verticalJustification );

        }

        return this;

    }

    @NotNull
    public FlexiGridBasicConstraint.HJustification getHorizontalJustification() {

        return _horizontalJustification == null ? HJustification.CENTER : _horizontalJustification;

    }

    @NotNull
    public FlexiGridBasicConstraint.VJustification getVerticalJustification() {

        return _verticalJustification == null ? VJustification.CENTER : _verticalJustification;

    }

    @NotNull
    public FlexiGridConstraintCategory getConstraintCategory() {

        return FlexiGridConstraintCategory.BASIC;

    }

    @NotNull
    public String locationString() {

        return locationString( _row, _col );

    }

    @NotNull
    public static String locationString( int r, int c ) {

        if ( r < 0 || c < 0 ) {

            ObtuseUtil.doNothing();

        }

        return "[" + r + "," + c + "]";

    }

    @NotNull
    public String toString() {

        return "FlexiGridBasicConstraint( " +
               _name +
               ", " + locationString() +
//               _name + ", [" +
//               _row + "," +
//               _col + "]," +
               ", category=" + getConstraintCategory() +
               ", hJust=" + getHorizontalJustification() +
               ", vJust=" + getVerticalJustification() +
               " )";

    }

}
