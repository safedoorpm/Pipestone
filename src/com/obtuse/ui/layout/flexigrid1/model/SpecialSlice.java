/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintCategory;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Created by danny on 2018/04/29.
 */
public abstract class SpecialSlice extends FlexiGridModelSlice {

    public enum SpecialSliceType {

    }

    private boolean _ready = false;

    public static class DividerSegment extends JPanel implements FlexiGridConstraint {

        @NotNull private final String _name;
        private final FlexiGridDivider.DividerStyle _dividerStyle;
        private final boolean _fullWidth;
        private Divider _owner;
        private int _ix;

        public DividerSegment( final @NotNull String name, final @NotNull FlexiGridDivider.DividerStyle dividerStyle ) {
            super();

            _name = name;
            _dividerStyle = dividerStyle;
            _fullWidth = true;
            _ix = 0;

        }

        public DividerSegment( final @NotNull String name, final @NotNull FlexiGridDivider.DividerStyle dividerStyle, final int ix ) {
            super();

            _name = name;
            _dividerStyle = dividerStyle;
            _fullWidth = false;
            if ( ix < 0 ) {

                throw new IllegalArgumentException(
                        "SpecialSlice.DividerSegment:  ix=" + ix + " is invalid (must be non-negative)"
                );

            }

            _ix = ix;

        }

        @Override
        @NotNull
        public String getName() {

            return _name;
        }

        @Override
        public FlexiGridConstraintCategory getConstraintCategory() {

            return FlexiGridConstraintCategory.DIVIDER_SEGMENT;

        }

        public int getIx() {

            return _ix;

        }

        public FlexiGridDivider.DividerStyle getDividerStyle() {

            return _dividerStyle;

        }

        public void setOwner( final @Nullable Divider owner ) {

            _owner = owner;

        }

        public boolean isFullWidth() {

            return _fullWidth;

        }

        public String toString() {

            return "DividerSegment( style=" + _dividerStyle + ", fullWidth=" + _fullWidth + ", owner=" + _owner + " )";

        }

    }

    public static class Divider extends SpecialSlice {

        private final SortedMap<Integer,DividerSegment> _segmentMap = new TreeMap<>();
        private final boolean _fullBreadth;

        public Divider(
                final @NotNull String name,
                final FlexiGridPanelModel.@NotNull Orientation orientation,
                boolean fullBreadth
        ) {
            super( name, orientation );

            _fullBreadth = fullBreadth;

        }

        public void setDivider( final @NotNull DividerSegment dividerSegment ) {

            ObtuseUtil.doNothing();

        }

        public boolean isFullBreadth() {

            return _fullBreadth;

        }

        @NotNull
        public String toString() {

            return "SpecialSlice.Divider( <<unimplemented> )";

        }

    }

    private SpecialSlice(
            final @NotNull String name,
            final FlexiGridPanelModel.@NotNull Orientation orientation

    ) {

        super( name, orientation );

    }

    protected void setReady( boolean ready ) {

        _ready = ready;

    }

    public boolean isSpecialSliceReady() {

        return _ready;

    }

    void setOwner( final @NotNull FlexiGridPanelModel<? extends FlexiGridModelSlice> owner, @SuppressWarnings("SameParameterValue") final boolean verify ) {

        if ( _ready ) {

            super.setOwner( owner, verify );

        } else {

            throw new IllegalArgumentException( "SpecialSlice.setOwner:  cannot set owner until the divider is ready" );

        }

    }

    @NotNull
    public abstract String toString();

}
