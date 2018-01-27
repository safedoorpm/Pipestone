/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import java.util.Collection;

/**
 Describe an {@link Ix}{@code <T>} instance.
 */

public interface IxInterface<T> {

    /**
     returns the index of this item in the original array or {@link Collection}{@code <T>}.
     @return the index of this item in the original array or {@link Collection}{@code <T>}.
     */

    int ix();

    /**
     Returns the item at index {@code ix} in the original array or {@link Collection}{@code <T>}.
     @return the item at index {@code ix} in the original array or {@link Collection}{@code <T>}.
     */

    T item();

}
