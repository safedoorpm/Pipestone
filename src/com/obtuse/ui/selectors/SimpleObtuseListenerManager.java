/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueWidget;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 Somewhat generic support for listeners.
 */

public class SimpleObtuseListenerManager<E> implements ObtuseListenerProxy<E> {

    private List<ListenerInfo<E>> _listeners = new ArrayList<>();

    public static class ListenerInfo<E> extends UniqueWidget {

        private final ObtuseListener<E> _listener;

        public ListenerInfo( final @NotNull String name, final @NotNull ObtuseListener<E> listener ) {
            super( name );

            _listener = listener;

        }

        public ObtuseListener<E> getListener() {

            return _listener;

        }

        @Override
        public String toString() {

            return "ListenerInfo( name=" + ObtuseUtil.enquoteToJavaString( getName() ) + ", listener=" + getListener() + " )";

        }

    }

    public void fireListeners(
            final @NotNull String who,
            final @NotNull String why,
            final @NotNull E item,
            final @NotNull Class<? extends ObtuseListener> selectionChangedListenerClass
    ) {

        List<ListenerInfo<E>> listeners = new ArrayList<>( _listeners );

        for ( ListenerInfo<E> listenerInfo : listeners ) {

            ObtuseUtil.doNothing();
            if (
                    selectionChangedListenerClass.isAssignableFrom(
                            listenerInfo.getListener().getClass()
                    )
            ) {

                listenerInfo.getListener()
                            .actionPerformed( who, why, item );

            } else {

                Logger.logMsg( "skipping " + listenerInfo );

                ObtuseUtil.doNothing();

            }

        }

    }

    @Override
    @NotNull
    public final List<ListenerInfo> getAllListeners() {

        return Collections.unmodifiableList( _listeners );

    }

    @Override
    @NotNull
    public final Optional<ListenerInfo> findListenerByName( final @NotNull String name ) {

        for ( ListenerInfo listenerInfo : _listeners ) {

            if ( listenerInfo.getName().equals( name ) ) {

                return Optional.of( listenerInfo );

            }

        }

        return Optional.empty();

    }

    @Override
    @SuppressWarnings("unused")
    public boolean removeByName( final @NotNull String name ) {

        for ( Iterator<ListenerInfo<E>> iterator = _listeners.iterator(); iterator.hasNext(); ) {

            ListenerInfo listenerInfo = iterator.next();

            if ( listenerInfo.getName()
                             .equals( name ) ) {

                iterator.remove();

                return true;

            }

        }

        return false;

    }

    public final void addObtuseListener(
            final @NotNull String name,
            final @NotNull ObtuseListener<E> actionListener
    ) {


        Optional<ListenerInfo> optListener = findListenerByName( name );
        if ( optListener.isPresent() ) {

            throw new HowDidWeGetHereError( "BasicBurkeDataSource.addCopierActionListener:  we already have a listener named " + ObtuseUtil.enquoteToJavaString( name ) );

        }

        _listeners.add( new ListenerInfo<>( name, actionListener ) );

    }

    public String toString() {

        return "SimpleObtuseListenerManager( " + _listeners.size() + " listener" + ( _listeners.size() == 1 ? "" : "s" ) + " )";

    }

}
