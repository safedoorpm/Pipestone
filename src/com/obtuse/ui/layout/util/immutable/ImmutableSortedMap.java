/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.obtuse.ui.layout.util.immutable;

/**
 %%% Something clever goes here.
 */

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedMap;

/**
 * @serial include
 */
public class ImmutableSortedMap<K,V>
	extends ImmutableMap<K,V>
	implements SortedMap<K,V>, Serializable {
    private static final long serialVersionUID = -8806743815996713206L;

    private final SortedMap<K, ? extends V> sm;

    public ImmutableSortedMap(SortedMap<K, ? extends V> m) {super(m); sm = m; }
    public Comparator<? super K> comparator()   { return sm.comparator(); }
    public SortedMap<K,V> subMap(K fromKey, K toKey)
    { return new ImmutableSortedMap<>( sm.subMap( fromKey, toKey)); }
    public SortedMap<K,V> headMap(K toKey)
    { return new ImmutableSortedMap<>( sm.headMap( toKey)); }
    public SortedMap<K,V> tailMap(K fromKey)
    { return new ImmutableSortedMap<>( sm.tailMap( fromKey)); }
    public K firstKey()                           { return sm.firstKey(); }
    public K lastKey()                             { return sm.lastKey(); }
}