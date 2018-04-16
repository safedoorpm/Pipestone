/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import org.jetbrains.annotations.NotNull;

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

    @SuppressWarnings("unused")
    public FlexiGridContainer1( final @NotNull String name ) {
        this( true, name );

    }

    public FlexiGridContainer1( final boolean doubleBuffered, final @NotNull String name ) {
        this( doubleBuffered, name, 0L );

    }

    public FlexiGridContainer1( boolean doubleBuffered, final @NotNull String name, final long modelKey ) {
        super( doubleBuffered );

        setName( name );

        _modelKey = modelKey;

        super.setLayout(
                new FlexiGridLayoutManager(
                        name,
                        this,
                        ( row, col, component, constraintsTable ) -> new FlexiGridItemInfo( row, col, component, constraintsTable )
                )
        );

        _initialized = true;

    }

    public void setLayout(LayoutManager mgr) {

        if ( mgr == null || mgr instanceof FlexiGridLayoutManager || !_initialized ) {

            super.setLayout( mgr );

        } else {

            throw new IllegalArgumentException( "FlexiGridContainer1:  cannot change layout manager to " + mgr.getClass().getCanonicalName() );

        }

    }

    public Component add( final @NotNull Component component ) {

        return add( component, null, -1, 0L );

    }

    public Component add( final String name, final @NotNull Component component ) {

        return add( component, name, -1, 0L );

    }

//    public Component add( final @NotNull Component component, final Object constraints ) {
//
//        add( component, constraints, -1, 0L );
//
//    }

    public Component add( final @NotNull Component component, final int index ) {

        return add( component, null, index, 0L );

    }

    public void add( final @NotNull Component component, Object constraint ) {

        add( component, constraint, 0, 0L );

    }

    public void add( final @NotNull Component component, final Object constraints, int index ) {

        add( component, constraints, index, 0L );

    }

    @Override
    public Component add( final @NotNull Component component, Object constraints, int index, long key ) {

        checkKey( key, "add", "to" );

        super.add( component, constraints, index );

        return component;

    }

    public void removeAll( final long key ) {

        checkKey( key, "remove", "from" );

        super.removeAll();

    }

    public void removeAll() {

        removeAll( 0L );

    }

    public void remove( final int index, final long key ) {

        checkKey( key, "remove", "from" );

        super.remove( index );

    }

    public void remove( final int index ) {

        remove( index, 0L );

    }

    public void remove( Component component, long key ) {

        checkKey( key, "remove", "from" );

        super.remove( component );

    }

    public void remove( Component component ) {

        remove( component, 0L );

    }

    public void checkKey( final long key, String action, String direction ) {

        if ( _modelKey != 0 && _modelKey != key ) {

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

    }

    /**
     Another hook for debuggin'.
     */

    @SuppressWarnings({ "EmptyMethod", "unused" })
    public void doneLayout() {

    }

}
