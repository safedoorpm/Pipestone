/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.ui.layout.ConstraintTriplet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Describe the constraints that a component within a {@link LinearContainer3} wants the {@link LinearLayoutManager3} to follow.
 <p/>Instances of this class are immutable. This allows them to be safely shared by multiple components.
 */

public class ComponentConstraints {

    private final LinearFlagMap _componentFlags;

    @Nullable
    private final ConstraintTriplet _widthConstraint;

    @Nullable
    private final ConstraintTriplet _heightConstraint;

    public ComponentConstraints( LinearFlagMap componentFlags, @Nullable ConstraintTriplet widthConstraint, @Nullable ConstraintTriplet heightConstraint ) {
//	this( componentFlags.toArray( new LinearFlagName[ componentFlags.size() ] ), widthConstraint, heightConstraint );

	_componentFlags = componentFlags;

	_widthConstraint = widthConstraint;

	_heightConstraint = heightConstraint;

    }

    public ComponentConstraints( LinearFlagNameValue[] componentFlags, @Nullable ConstraintTriplet widthConstraint, @Nullable ConstraintTriplet heightConstraint ) {
	this( LinearFlagMap.createCleanedMap( componentFlags ), widthConstraint, heightConstraint );

    }

    @Nullable
    public ConstraintTriplet getWidthConstraint() {

	return _widthConstraint;

    }

    @Nullable
    public ConstraintTriplet getHeightConstraint() {

	return _heightConstraint;

    }

    @NotNull
    LinearFlagMap getComponentFlags() {

	return _componentFlags;

    }

    public boolean isLeftJustified() {

	return getComponentFlags().containsKey( LinearFlagName.LEFT_JUSTIFIED );

    }

    public boolean isRightJustified() {

	return ( getComponentFlags().containsKey( LinearFlagName.RIGHT_JUSTIFIED ) );

    }

    public boolean isHorizontallyJustified() {

	return isLeftJustified() && isRightJustified();

    }

    public boolean isTopJustified() {

	return ( getComponentFlags().containsKey( LinearFlagName.TOP_JUSTIFIED ) );

    }

    public boolean isBottomJustified() {

	return ( getComponentFlags().containsKey( LinearFlagName.BOTTOM_JUSTIFIED ) );

    }

    public boolean isVerticallyJustified() {

	return isTopJustified() && isBottomJustified();

    }

    public String toString() {

	return "ComponentConstraints( " + getComponentFlags() + " )";

    }

}
