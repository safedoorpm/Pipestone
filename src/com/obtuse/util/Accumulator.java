package com.obtuse.util;

import java.util.Set;

/*
 * Copyright © 2014 Obtuse Systems Corporation
 */

public interface Accumulator<K> {

    long forceCount( K thing, long newCount );

    long accumulate( K thing, long delta );

    long increment( K thing );

    long getCount( K thing );

    boolean containsKey( K thing );

    Set<K> keySet();

    public long getGrandTotal();

    K firstKey();

    K lastKey();

    int size();

    boolean isEmpty();

    TreeSorter<Long,K> getSortedSums();

}
