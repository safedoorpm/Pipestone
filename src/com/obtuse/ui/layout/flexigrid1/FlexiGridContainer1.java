/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.layoutTracer.TracingLayoutManager;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 A container that supports the {@link FlexiGridLayoutManager}.
 Each instance of this class has a one-to-one relationship with an instance of the {@code FlexiGridLayoutManager}.
 */

public class FlexiGridContainer1 extends JPanel implements FlexiGridContainer {

    private static final java.util.List<FlexiGridContainer> _watchedContainers = new LinkedList<>();

    private final boolean _initialized;

    private final long _modelKey;
    private final boolean _msgTraceMode;
    private final boolean _useLayoutTracer;

    private int _temporaryPass = 0;

    @SuppressWarnings("unused")
    public FlexiGridContainer1(
            final @NotNull String name,
            final boolean msgTraceMode,
            final boolean useLayoutTracer
    ) {
        this( true, name, msgTraceMode, useLayoutTracer );

    }

    public FlexiGridContainer1(
            final boolean doubleBuffered,
            final @NotNull String name,
            final boolean msgTraceMode,
            final boolean useLayoutTracer
    ) {
        this( doubleBuffered, name, 0L, msgTraceMode, useLayoutTracer );

    }

    public FlexiGridContainer1(
            final boolean doubleBuffered,
            final @NotNull String name,
            final long modelKey,
            final boolean msgTraceMode,
            final boolean useLayoutTracer
    ) {
        super( doubleBuffered );

        _msgTraceMode = msgTraceMode;
        _useLayoutTracer = useLayoutTracer;

        setOpaque( false );

        setName( name );

        _modelKey = modelKey;

        FlexiGridLayoutManager mgr = new FlexiGridLayoutManager(
                name,
                this,
                ( row, col, component, constraintsTable ) -> new FlexiGridItemInfo( row, col, component, constraintsTable )
        );

        super.setLayout( useLayoutTracer() ? new TracingLayoutManager( mgr ) : mgr );

        _initialized = true;

    }

    @SuppressWarnings("EmptyMethod")
    public void paint( Graphics g ) {

        super.paint( g );

    }

    public void setLayout( final LayoutManager mgr ) {

        if ( mgr == null || mgr instanceof FlexiGridLayoutManager || !_initialized ) {

            if ( mgr instanceof FlexiGridLayoutManager && useLayoutTracer() ) {

                super.setLayout( new TracingLayoutManager( (FlexiGridLayoutManager)mgr ) );

            } else {

                super.setLayout( mgr );

            }

        } else {

            throw new IllegalArgumentException( "FlexiGridContainer1:  cannot change layout manager to " + mgr.getClass().getCanonicalName() );

        }

    }

    public Component add( @SuppressWarnings("NullableProblems") final @NotNull Component component ) {

        return add( component, null, -1, 0L );

    }

    public Component add( final String name, @SuppressWarnings("NullableProblems") final @NotNull Component component ) {

        return add( component, name, -1, 0L );

    }

    public Component add( @SuppressWarnings("NullableProblems") final @NotNull Component component, final int index ) {

        return add( component, null, index, 0L );

    }

    public void add( @SuppressWarnings("NullableProblems") final @NotNull Component component, final @Nullable Object constraint ) {

        add( component, constraint, 0, 0L );

    }

    public void add( @SuppressWarnings("NullableProblems") final @NotNull Component component, final Object constraints, final int index ) {

        add( component, constraints, index, 0L );

    }

    @Override
    public Component add( final @NotNull Component component, final @Nullable Object constraints, final int index, final long key ) {

        checkKey( key, "add", "to" );

        super.add( component, constraints, index );

        return component;

    }

    @Override
    public void removeAll( final long key ) {

        checkKey( key, "remove", "from" );

        super.removeAll();

    }

    @Override
    public void removeAll() {

        removeAll( 0L );

    }

    @Override
    public void remove( final int index, final long key ) {

        checkKey( key, "remove", "from" );

        super.remove( index );

    }

    @Override
    public void remove( final int index ) {

        remove( index, 0L );

    }

    @Override
    public FlexiGridLayoutManager getFlexiGridLayoutManager() {

        LayoutManager lm = getLayout();
        if ( useLayoutTracer() && lm instanceof TracingLayoutManager ) {

            return (FlexiGridLayoutManager)((TracingLayoutManager)lm).getWrappedLayoutManager2();

        } else if ( lm instanceof FlexiGridLayoutManager ) {

            return (FlexiGridLayoutManager)lm;

        } else {

            throw new HowDidWeGetHereError( "FlexiGridContainer1.getFlexiGridLayoutManager:  we're not using a FGLM (yet?)" );

        }

    }

    @Override
    public void remove( final Component component, final long key ) {

        checkKey( key, "remove", "from" );

        try {

            _temporaryPass += 1;

            super.remove( component );

        } finally {

            if ( _temporaryPass <= 0 ) {

                //noinspection ThrowFromFinallyBlock
                throw new HowDidWeGetHereError( "FlexiGridContainer1: temporary pass handling error - attempt to decrement pass into negative range" );

            }

            _temporaryPass -= 1;

        }

    }

    @Override
    public void remove( @SuppressWarnings("NullableProblems") final @NotNull Component component ) {

        remove( component, 0L );

    }

    public void checkKey( final long key, final @NotNull String action, final @NotNull String direction ) {

        if ( _temporaryPass == 0 && _modelKey != 0 && _modelKey != key ) {

            throw new IllegalArgumentException(
                    "FlexiGridContainer1." + action +
                    ":  this container is managed by a FlexiGridPanelModel - use that model instance to " + action +
                    " things " + direction + " this container"
            );

        }

    }

    @SuppressWarnings("unused")
    public static void watch( final @NotNull FlexiGridContainer watched ) {

        if ( !_watchedContainers.contains( watched ) ) {

            _watchedContainers.add( watched );

        }

    }

    @SuppressWarnings("unused")
    public static void unWatch( final @NotNull FlexiGridContainer watched ) {

        _watchedContainers.remove( watched );

    }

    /**
     A hook for debuggin'.
     */

    @SuppressWarnings({ "EmptyMethod", "unused" })
    public void doingLayout() {

        ObtuseUtil.doNothing();

    }

    /**
     Another hook for debuggin'.
     */

    @SuppressWarnings({ "EmptyMethod", "unused" })
    public void doneLayout() {

        ObtuseUtil.doNothing();

    }

    public String toString() {

        return "FlexiGridContainer1()";

    }

    public boolean isMsgTraceMode() {

        return _msgTraceMode;

    }

    public boolean useLayoutTracer() {

        return _useLayoutTracer;

    }

}
