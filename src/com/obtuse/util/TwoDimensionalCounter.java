/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

@SuppressWarnings("UnusedDeclaration")
public interface TwoDimensionalCounter<K1,K2> {

    void count( K1 key1, K2 key2 );

    int getCount( K1 key1, K2 key2 );

    boolean containsKeys( K1 key1, K2 key2 );

    TwoDimensionalSortedMap<K1,K2,Integer> getTwoDimensionalSortedMap();

}
