package com.obtuse.util;

import java.util.Set;

/**
 * Count unique occurrences of things.
 * <p>
 * Copyright © 2009 Obtuse Systems Corporation
 */

@SuppressWarnings("UnusedDeclaration")
public interface Counter<K> {

    void count( K thing );

    int getCount( K thing );

    boolean containsKey( K thing );

    Set<K> keySet();

    public int getGrandTotal();

    K firstKey();

    K lastKey();

    int size();

    boolean isEmpty();

}