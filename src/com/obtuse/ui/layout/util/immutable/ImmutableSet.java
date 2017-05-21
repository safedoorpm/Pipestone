/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.obtuse.ui.layout.util.immutable;

import java.io.Serializable;
import java.util.Set;

/**
 An immutable set - once created, instances cannot be changed.
 <p/>'Borrowed' from the Java Collections class.

 @param <E> the class of objects in the set.
 */

public class ImmutableSet<E> extends ImmutableCollection<E>
	implements Set<E>, Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    protected ImmutableSet(Set<? extends E> s)     {super( s);}
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {return o == this || c.equals( o);}
    public int hashCode()           {return c.hashCode();}
}