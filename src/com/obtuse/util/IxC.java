/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 A variant of the {@link Ix}{@code <T>} that implements the {@link Comparable} interface.
 @param <U>
 */

public class IxC<U extends Comparable<U>> extends Ix<U> implements Comparable<IxC<U>> {

    public final U item;

    public IxC( final int ix, final U item ) {
        super(ix, item );

        this.item = item;

    }

    @Override
    public int compareTo( final @NotNull com.obtuse.util.IxC<U> o ) {

        return item.compareTo( o.item );

    }

    public U item() {

        return this.item;

    }

    public String toString() {

        return super.toString();

    }

}