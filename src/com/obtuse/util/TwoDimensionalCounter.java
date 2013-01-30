package com.obtuse.util;

/*
 * Copyright © 2012 Obtuse Systems Corporation
 */

public interface TwoDimensionalCounter<K1,K2> {

    void count( K1 key1, K2 key2 );

    int getCount( K1 key1, K2 key2 );

    boolean containsKeys( K1 key1, K2 key2 );

    TwoDimensionalSortedMap<K1,K2,Integer> getTwoDimensionalSortedMap();

}
