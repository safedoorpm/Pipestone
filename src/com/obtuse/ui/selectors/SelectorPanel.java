/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 Provide a unified view of the button and combo-box selectors.
 */

public abstract class SelectorPanel<E,C extends Container> extends JPanel {

    private final Function<E, C> _componentGetter;
    private boolean _isZeroASelection;

    private final Map<E,C> _selectionCache;

    protected SelectorPanel( final boolean isZeroASelection, final boolean cacheSelections, final Function<E, C> componentGetter ) {
        super();

        _isZeroASelection = isZeroASelection;

        _selectionCache = cacheSelections ? new HashMap<>() : null;

        _componentGetter = componentGetter;

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

        }


        if ( _selectionCache != null && !_selectionCache.containsKey( key ) ) {

            cacheSelection( key, component );

        }

        return Optional.of( component );

    }

    public abstract String toString();

}
