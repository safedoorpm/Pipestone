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
import java.util.*;
import java.util.function.Function;

/**
 Provide a unified view of the button and combo-box selectors.
 */

public abstract class SelectorPanel<E,C extends Container> extends JPanel {

    private final Function<E, C> _componentGetter;
    private boolean _isZeroASelection;

    private final JPanel _postSelectionPanel;

    private final Map<E,C> _selectionCache;

    private final String _ourName;

    private LayoutManager _ourLayoutManager = null;

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
        add( new JLabel( "post south" ), BorderLayout.SOUTH );
        _postSelectionPanel.add( new JLabel( "North" ), BorderLayout.NORTH );

        ObtuseUtil.doNothing();

    }

    @NotNull
    public String getOurName() {

        return _ourName;

    }

    /**
     This method throws an {@link IllegalArgumentException} if an attempt is made to set
     our layout manager once it has been set to be a {@link BorderLayout} instance.
     <p>This intercept method exists because we spent about a day and a half discovering that we had
     set our layout manager to our intended {@code BorderLayout} layout manager, added some components in specified places,
     set out layout manager to a second {@code BorderLayout} layout manager instance, added more components in specified places.
     The symptom was that the components added before we assigned ourselves our second {@code BorderLayout} manager instance
     simply and silently disappeared (probably because the {@code BorderLayout} manager keeps track of its components itself
     so if you change to a different {@code BorderLayout} manager instance halfway through the ball game then you simply lose
     the components added before the change.</p>
     <p>Double drat rat fooey!!!</p>
     <p>-Danny</p>
     @param mgr the layout manager that we should use.
     @throws IllegalArgumentException if an attempt is made to set our layout manager after it has been set to be a {@code BorderLayout} manager.
     */

    public void setLayout( final LayoutManager mgr ) {

        if ( _ourLayoutManager != null && _ourLayoutManager instanceof BorderLayout ) {

            throw new IllegalArgumentException( "SelectorPanel:  really bad idea to change layout manager once we've given ourselves a BorderLayout manager - bye!" );

        }

        if ( _ourLayoutManager == null ) {

            Logger.logMsg(
                    "SelectorPanel.setLayout:  setting LayoutManager to " +
                    ( mgr == null ? "null" : mgr.getClass().getCanonicalName() )
            );

        } else {

            Logger.logMsg(
                    "SelectorPanel.setLayout:  replacing LayoutManager " + _ourLayoutManager.getClass().getCanonicalName() +
                    " with " +
                    ( mgr == null ? "null" : mgr.getClass().getCanonicalName() )
            );

        }

        super.setLayout( mgr );

        _ourLayoutManager = mgr;

        ObtuseUtil.doNothing();

    }

    public void add(
            final @NotNull Component component,
            final @Nullable Object constraints
    ) {

        Logger.logMsg(
                getOurName() + ":  add( " +
                LinearLayoutUtil.describeComponent( component ) +
//                component.getClass().getCanonicalName() +
//                "( " +
//                ObtuseUtil.enquoteToJavaString( component.getName() ) +
//                " )" +
                ", " +
                ObtuseUtil.enquoteJavaObject( constraints ) +
                " )"
        );

        super.add( component, constraints );

        LinearLayoutUtil.describeFullyContainerContents(
                "************ at end of add( " + getName() + " )",
                this
        );

        ObtuseUtil.doNothing();

    }

    protected JPanel getPostSelectionPanel() {

        return _postSelectionPanel;

    }

    protected void setPostSelectionPanelContents( final @Nullable Component c ) {

        if ( c == null ) {

            clearPostSelectionPanelContents();

        } else {

            _postSelectionPanel.add( c, BorderLayout.CENTER );
            c.setVisible( true );
            _postSelectionPanel.setVisible( true );
            c.revalidate();

            Logger.logMsg( "postSelectionPanel name is " + _postSelectionPanel.getName() );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
            Logger.logMsg( "visible visible visible visible visible visible visible visible visible visible visible visible visible visible visible " );
        }

    }

    protected void clearPostSelectionPanelContents() {

        _postSelectionPanel.setVisible( false );
        _postSelectionPanel.revalidate();
        _postSelectionPanel.removeAll();

        Logger.logMsg( "postSelectionPanel name is " + _postSelectionPanel.getName() );
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");
        Logger.logMsg( "INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE INVISIBLE ");

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

                Logger.logMsg( "SelectorPanel.getSelectedComponent:  no component returned for " + key + " - showing no selection panel" );

                return Optional.empty();

            }

            LinearLayoutUtil.describeFullyContainerContents( "got back from _componentGetter.apply( " + key + " )", component );

        }


        if ( _selectionCache != null && !_selectionCache.containsKey( key ) ) {

            cacheSelection( key, component );

        }

        return Optional.of( component );

    }

    public abstract String toString();

}
