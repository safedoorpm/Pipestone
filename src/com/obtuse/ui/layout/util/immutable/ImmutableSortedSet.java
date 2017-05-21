/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.obtuse.ui.layout.util.immutable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;

/**
 An immutable sorted set - once created, instances cannot be changed.
 <p/>'Borrowed' from the Java Collections class.

 @param <E> the class of objects in the set.
 */

public class ImmutableSortedSet<E>
	extends ImmutableSet<E>
	implements SortedSet<E>, Serializable {
    private static final long serialVersionUID = -4929149591599911165L;
    private final SortedSet<E> ss;

    protected ImmutableSortedSet(SortedSet<E> s) {super( s); ss = s;}

    public Comparator<? super E> comparator() {return ss.comparator();}

    public SortedSet<E> subSet(E fromElement, E toElement) {
	return new ImmutableSortedSet<>( ss.subSet( fromElement, toElement));
    }
    public SortedSet<E> headSet(E toElement) {
	return new ImmutableSortedSet<>( ss.headSet( toElement));
    }
    public SortedSet<E> tailSet(E fromElement) {
	return new ImmutableSortedSet<>( ss.tailSet( fromElement));
    }

    public E first()                   {return ss.first();}
    public E last()                    {return ss.last();}
}
