/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 Something that proxies or manages {@link ObtuseListener}s.
 <p>Note that this interface describes all of the aspects of managing listeners except for the adding of new listeners.
 This omission makes it easier for a single class to use this facility to manage multiple types of listeners.
 For example, a class could define classes named {@code TypeOneListener} and {@code TypeTwoListener} which are each
 derived from the {@link ObtuseListener} class. It could then provide a method named {@code addTypeOneListener} which
 adds {@code TypeOneListener}s as well as a method named {@code addTypeTwoListener} which adds {@code TypeTwoListener}s.</p>
 */

public interface ObtuseListenerProxy<ITEM> {

//    void fireListeners( final String who, final ITEM item );

    @NotNull List<SimpleObtuseListenerManager.ListenerInfo> getAllListeners();

    @NotNull Optional<SimpleObtuseListenerManager.ListenerInfo> findListenerByName( @NotNull String name );

    @SuppressWarnings("unused")
    boolean removeByName( @NotNull String name );

//    void addObtuseListener( @NotNull String name, @NotNull ObtuseListener<ITEM> actionListener );

    abstract class ObtuseListener<ITEM> {

        public final void actionPerformed( final @NotNull String who, final @NotNull String why, final @NotNull ITEM dataSource ) {

            try {

                myActionPerformed( who, why, dataSource );

            } catch ( HowDidWeGetHereError e ) {

                throw e;

            } catch ( Throwable e ) {

                throw new HowDidWeGetHereError( "error processing CopierActionListener event", e );

            }

        }

        @SuppressWarnings("EmptyMethod")
        public abstract void myActionPerformed(
                final @NotNull String who,
                final @NotNull String why,
                final @NotNull ITEM dataSource
        );

    }

}
