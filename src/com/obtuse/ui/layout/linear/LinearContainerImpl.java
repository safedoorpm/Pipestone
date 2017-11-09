package com.obtuse.ui.layout.linear;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.ui.layout.LinearOrientation;
import org.jetbrains.annotations.NotNull;

/**
 The current 'official' linear container implementation.
 */

public class LinearContainerImpl extends LinearContainer3 {

    public LinearContainerImpl( @NotNull final String name, final LinearOrientation orientation ) {

        this( name, orientation, null, null );

    }

    public LinearContainerImpl(
            @NotNull final String name,
            final LinearOrientation orientation,
            @SuppressWarnings("SameParameterValue") final ContainerConstraints containerConstraints,
            @SuppressWarnings("SameParameterValue") final ComponentConstraints componentConstraints
    ) {

        super( name, orientation, containerConstraints, componentConstraints );

    }

}
