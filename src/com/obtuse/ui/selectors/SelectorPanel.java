/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 Provide a unified view of the button and combo-box selectors.
 */

public abstract class SelectorPanel<E,C extends Container> extends BorderLayoutPanel implements ObtuseListenerProxy<E> {

    public abstract static class SelectionChangedListener<T> extends ObtuseListener<T> {

        @Override
        public abstract void myActionPerformed(
                final @NotNull String who,
                final @NotNull String why,
                final @NotNull T dataSource
        );

    }

//    public abstract static class VetSelectionListener<T> extends ObtuseListener<T> {
//
//        @Override
//        public abstract void myActionPerformed(
//                final @NotNull String who,
//                final @NotNull String why,
//                final @NotNull T dataSource
//        );
//
//    }

    private final SimpleObtuseListenerManager<E> _listenerManager = new SimpleObtuseListenerManager<>();

    private final Function<E, C> _componentGetter;

    private boolean _isZeroASelection;

    private final JPanel _postSelectionPanel;

    private final Map<E,C> _selectionCache;

    private final String _ourName;

    protected SelectorPanel(
            final @NotNull String ourName,
            final boolean isZeroASelection,
            final boolean cacheSelections,
            final Function<E, C> componentGetter
    ) {
        super();

        _ourName = ourName;

        _isZeroASelection = isZeroASelection;

        _selectionCache = cacheSelections ? new HashMap<>() : null;

        _componentGetter = componentGetter;

        setLayout( new BorderLayout() );

        _postSelectionPanel = new JPanel( new BorderLayout() );
        _postSelectionPanel.setName( ourName + " postSelectionPanel" );
        add( _postSelectionPanel, BorderLayout.CENTER );

        ObtuseUtil.doNothing();

    }

    @NotNull
    public String getOurName() {

        return _ourName;

    }

    public void add(
            final @NotNull Component component,
            final @Nullable Object constraints
    ) {

        super.add( component, constraints );

        ObtuseUtil.doNothing();

    }

    protected JPanel getPostSelectionPanel() {

        return _postSelectionPanel;

    }

    protected void setPostSelectionPanelContents( final String who, final @Nullable Component c ) {

        if ( c == null ) {

            clearPostSelectionPanelContents();

        } else {

            if ( _postSelectionPanel.getComponentCount() != 0 ) {

                Logger.logMsg( "there is something in the post selection panel: " + Arrays.toString( _postSelectionPanel.getComponents() ) );

                ObtuseUtil.doNothing();

            }

            _postSelectionPanel.add( c, BorderLayout.CENTER );
            c.setVisible( true );
            _postSelectionPanel.setVisible( true );
            c.revalidate();

            LinearLayoutUtil.describeFullyContainerContents( who, _postSelectionPanel );

            ObtuseUtil.doNothing();

        }

    }

    protected void clearPostSelectionPanelContents() {

        _postSelectionPanel.setVisible( false );
        _postSelectionPanel.revalidate();
        _postSelectionPanel.removeAll();

    }

    protected void setSubsidiaryPanelBorder( final Border border ) {

        _postSelectionPanel.setBorder( border );

    }

    public boolean isZeroASelection() {

        return _isZeroASelection;

    }

    protected void cacheSelection( final E itemAt, @Nullable final C selection ) {

        if ( selection == null ) {

            _selectionCache.remove( itemAt );

        } else {

            _selectionCache.put( itemAt, selection );

        }

    }

    @NotNull
    protected Optional<C> getSelectedComponent( final E key ) {

        C component = null;
        if ( _selectionCache != null ) {

            component = _selectionCache.get( key );

        }

        if ( component == null ) {

            component = _componentGetter.apply( key );

            if ( component == null ) {

                return Optional.empty();

            }

        }

        if ( _selectionCache != null && !_selectionCache.containsKey( key ) ) {

            cacheSelection( key, component );

        }

        return Optional.of( component );

    }

//    protected void fireSelectionChange( final @NotNull String who, final @NotNull E choice ) {
//
//        Logger.logMsg( who + ":  selected " + ObtuseUtil.enquoteJavaObject( choice ) );
//
//        ObtuseUtil.doNothing();
//
//    }

    public abstract String toString();

    public void fireSelectionChangedListeners( final String who, final @NotNull String why, final @NotNull E item ) {

        _listenerManager.fireListeners( who, why, item, SelectionChangedListener.class );

    }

    @Override
    public @NotNull List<SimpleObtuseListenerManager.ListenerInfo> getAllListeners() {

        return _listenerManager.getAllListeners();

    }

    @Override
    public @NotNull Optional<SimpleObtuseListenerManager.ListenerInfo> findListenerByName( @NotNull final String name ) {

        return _listenerManager.findListenerByName( name );

    }

    @Override
    public boolean removeByName( @NotNull final String name ) {

        return _listenerManager.removeByName( name );

    }

//    public void addVetSelectionListener(
//            @NotNull final String name, @NotNull final VetSelectionListener actionListener
//    ) {
//
//        @SuppressWarnings("unchecked") ObtuseListener<E> listener = (ObtuseListener<E>)actionListener;
//        _listenerManager.addObtuseListener( name, listener );
//
//    }

    public void addSelectionChangedListener(
            @NotNull final String name, @NotNull final SelectionChangedListener actionListener
    ) {

        @SuppressWarnings("unchecked") ObtuseListener<E> listener = (ObtuseListener<E>)actionListener;
        _listenerManager.addObtuseListener( name, listener );

    }

}
