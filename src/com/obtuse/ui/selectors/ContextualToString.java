/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Provide an easy way to wrap something with a customized {@code toString()} value.
 */

@SuppressWarnings("unused")
public class ContextualToString<T extends Comparable<T>> implements Comparable<ContextualToString<T>> {

    private final T _item;
    private final String _toString;

    public ContextualToString( final @NotNull T item, final @NotNull String toString ) {
        super();

        _item = item;

        _toString = toString;

    }

    public T getItem() {

        return _item;

    }

    public String toString() {

        return _toString;

    }

    @Override
    public int compareTo( @NotNull final ContextualToString<T> o ) {

        return getItem().compareTo( o.getItem() );

    }

    @Override
    public int hashCode() {

        return getItem().hashCode();

    }

    public boolean equals( final @Nullable Object rhs ) {

        if ( rhs instanceof ContextualToString ) {

            @SuppressWarnings("unchecked") ContextualToString<T> cRhs = (ContextualToString<T>)rhs;

            return getItem().equals( cRhs.getItem() );

        }

        return false;

    }

}
