/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.ui.ObtuseSwingUtils;
import com.obtuse.ui.layout.ConstraintTuple;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;

/**
 A container which is intended to be managed by a {@link LinearLayoutManager3} instance.
 */

public class LinearContainer3 extends JPanel implements LinearContainer {

    private static final java.util.List<LinearContainer> _watchedContainers = new LinkedList<>();

    private ContainerConstraints _containerConstraints;
    private final ComponentConstraints _componentConstraints;

    private ConstraintTuple _lengthConstraints;

    private ConstraintTuple _breadthConstraints;

    private boolean _trackParentBreadth;

    public LinearContainer3( final @NotNull String name, final LinearOrientation orientation ) {

        this( name, orientation, null, null );

    }

    LinearContainer3(
            final @NotNull String name,
            final LinearOrientation orientation,
            @SuppressWarnings("SameParameterValue") final ContainerConstraints containerConstraints,
            @SuppressWarnings("SameParameterValue") final ComponentConstraints componentConstraints
    ) {

        super();

        setName( name );

        setLayout( new LinearLayoutManager3( orientation, this ) );

        _containerConstraints = containerConstraints;
        _componentConstraints = componentConstraints;

    }

    public static void watch( final @NotNull LinearContainer watched ) {

        if ( !_watchedContainers.contains( watched ) ) {

            _watchedContainers.add( watched );

        }

    }

    public static void unWatch( final @NotNull LinearContainer watched ) {

        _watchedContainers.remove( watched );

    }

    public static java.util.List<LinearContainer> getWatched() {

        return Collections.unmodifiableList( _watchedContainers );

    }

    @Override
    public void watch() {

        watch( this );

    }

    @Override
    public boolean isWatched() {

        return _watchedContainers.contains( this );

    }

    /**
     A hook for debuggin'.
     */

    public void doingLayout() {

        ObtuseUtil.doNothing();

    }

    /**
     Another hook for debuggin'.
     */

    public void doneLayout() {

        ObtuseUtil.doNothing();

    }

    public LinearOrientation getOrientation() {

        LayoutManager lm = getLayout();
        if ( lm instanceof LinearLayoutManager3 ) {

            return ( (LinearLayoutManager3)lm ).getOrientation();

        } else if ( lm == null ) {

            throw new IllegalArgumentException( "LinearContainer3:  no layout manager" );

        } else {

            throw new IllegalArgumentException( "LinearContainer3:  we need to be managed by a LinearLayoutManager3 (we are being managed by a " +
                                                lm.getClass().getName() +
                                                ")" );

        }

    }

    @Override
    public boolean isVertical() {

        return getOrientation() == LinearOrientation.VERTICAL;

    }

    @Override
    public boolean isHorizontal() {

        return getOrientation() == LinearOrientation.HORIZONTAL;

    }

    @Override
    public void setLengthConstraints( final int minLength, final int maxLength ) {

        setLengthConstraints( new ConstraintTuple( minLength, maxLength ) );
        revalidate();

    }

    @Override
    public void setLengthConstraints( final ConstraintTuple lengthConstraints ) {

        _lengthConstraints = lengthConstraints;

    }

    @Override
    public void setBreadthConstraints( final int minBreadth, final int maxBreadth ) {

        setBreadthConstraints( new ConstraintTuple( minBreadth, maxBreadth ) );
        revalidate();

    }

    @Override
    public void setBreadthConstraints( final ConstraintTuple breadthConstraints ) {

        _breadthConstraints = breadthConstraints;

    }

    @Override
    public ConstraintTuple getLengthConstraints() {

        return _lengthConstraints;

    }

    @Override
    public ConstraintTuple getBreadthConstraints() {

        return _breadthConstraints;

    }

    public void setConstraints(
            final int minBreadth, final int maxBreadth,
            final int minLength, final int maxLength
    ) {

        setBreadthConstraints( minBreadth, maxBreadth );
        setLengthConstraints( minLength, maxLength );

    }

    public String toString() {

        return "LinearContainer3(" +
               " name=\"" + getName() + "\"," +
               " nComponents=" + getComponentCount() + "," +
               " alignment=(" + getAlignmentX() + "," + getAlignmentY() + ")," +
               " containerConstraints=" + _containerConstraints + "," +
               " componentConstraints=" + _componentConstraints + "," +
               " border=" + ObtuseSwingUtils.describeBorder( getBorder() ) +
               " )";

    }

    public void setContainerConstraints( final ContainerConstraints containerConstraints ) {

        _containerConstraints = containerConstraints;

        revalidate();

    }

    public boolean hasContainerConstraints() {

        return _containerConstraints != null;

    }

    public ContainerConstraints getContainerConstraints() {

        return _containerConstraints;

    }

    public void setComponentConstraints( final ContainerConstraints containerConstraints ) {

        _containerConstraints = containerConstraints;

        revalidate();

    }

    public boolean hasComponentConstraints() {

        return _componentConstraints != null;

    }

    public ComponentConstraints getComponentConstraints() {

        return _componentConstraints;

    }

}
