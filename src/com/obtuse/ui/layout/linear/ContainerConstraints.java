/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import org.jetbrains.annotations.NotNull;

/**
 Describe the constraints that a {@link LinearLayoutManager3} wants imposed on the components that it contains.
 <p/>Instances of this class are immutable. This allows them to be safely shared by multiple containers.
 */

public class ContainerConstraints {

    private final LinearFlagMap _containerFlags;

    public ContainerConstraints( LinearFlagMap containerFlags ) {
	super();

	_containerFlags = containerFlags;

    }

    @NotNull
    LinearFlagMap getContainerFlags() {

	return _containerFlags;

    }

    public boolean isDefaultLeftJustified() {

	return getContainerFlags().containsKey( LinearFlagName.LEFT_JUSTIFIED );

    }

    public boolean isDefaultRightJustified() {

	return ( getContainerFlags().containsKey( LinearFlagName.RIGHT_JUSTIFIED ) );

    }

    public boolean isDefaultHorizontallyJustified() {

	return isDefaultLeftJustified() && isDefaultRightJustified();

    }

    public boolean isDefaultTopJustified() {

	return ( getContainerFlags().containsKey( LinearFlagName.TOP_JUSTIFIED ) );

    }

    public boolean isDefaultBottomJustified() {

	return ( getContainerFlags().containsKey( LinearFlagName.BOTTOM_JUSTIFIED ) );

    }

    public boolean isDefaultVerticallyJustified() {

	return isDefaultTopJustified() && isDefaultBottomJustified();

    }

    public String toString() {

	return "ContainerConstraints( " + getContainerFlags() + " )";

    }

//    void setContainerFlags( int containerFlags );

//    public boolean isDefaultLeftJustified() {
//
//	return ( getContainerFlags() & LinearFlagName.LEFT_JUSTIFIED.value ) != 0;
//
//    }
//
//    public boolean isDefaultRightJustified() {
//
//	return ( getContainerFlags() & LinearFlagName.RIGHT_JUSTIFIED.value ) != 0;
//
//    }
//
//    public boolean isDefaultTopJustified() {
//
//	return ( getContainerFlags() & LinearFlagName.TOP_JUSTIFIED.value ) != 0;
//
//    }
//
//    public boolean isDefaultBottomJustified() {
//
//	return ( getContainerFlags() & LinearFlagName.BOTTOM_JUSTIFIED.value ) != 0;
//
//    }
//
//    public boolean isDefaultHorizontallyJustified() {
//
//	return isDefaultLeftJustified() && isDefaultRightJustified();
//
//    }
//
//    public boolean isDefaultVerticallyJustified() {
//
//	return isDefaultTopJustified() && isDefaultBottomJustified();
//
//    }

}