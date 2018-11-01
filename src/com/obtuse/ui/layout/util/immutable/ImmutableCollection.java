/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.obtuse.ui.layout.util.immutable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 An immutable collection - once created, instances cannot be changed.
 <p/>'Borrowed' from the Java Collections class.
 @param <E> the class of objects in the set.
 */

public class ImmutableCollection<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = 1820017752578914078L;

    final Collection<? extends E> c;

    protected ImmutableCollection( final Collection<? extends E> c) {
	if (c==null)
	    throw new NullPointerException();
	this.c = c;
    }

    public int size()                   {return c.size();}
    public boolean isEmpty()            {return c.isEmpty();}
    public boolean contains( final Object o)   {return c.contains( o);}
    @NotNull
    public Object[] toArray()           {return c.toArray();}
    @NotNull
    public <T> T[] toArray( @NotNull final T[] a)       {return c.toArray( a);}
    public String toString()            {return c.toString();}

    @NotNull
    public Iterator<E> iterator() {
	return new Iterator<>() {
	    private final Iterator<? extends E> i = c.iterator();

	    public boolean hasNext() {return i.hasNext();}
	    public E next()          {return i.next();}
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	    @Override
	    public void forEachRemaining( final Consumer<? super E> action) {
		// Use backing collection version
		i.forEachRemaining(action);
	    }
	};
    }

    public boolean add( final E e) {
	throw new UnsupportedOperationException();
    }
    public boolean remove( final Object o) {
	throw new UnsupportedOperationException();
    }

    public boolean containsAll( @NotNull final Collection<?> coll) {
	return c.containsAll(coll);
    }
    public boolean addAll( @NotNull final Collection<? extends E> coll) {
	throw new UnsupportedOperationException();
    }
    public boolean removeAll( @NotNull final Collection<?> coll) {
	throw new UnsupportedOperationException();
    }
    public boolean retainAll( @NotNull final Collection<?> coll) {
	throw new UnsupportedOperationException();
    }
    public void clear() {
	throw new UnsupportedOperationException();
    }

    // Override default methods in Collection
    @Override
    public void forEach( final Consumer<? super E> action) {
	c.forEach(action);
    }
    @Override
    public boolean removeIf( final Predicate<? super E> filter) {
	throw new UnsupportedOperationException();
    }
    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
	return (Spliterator<E>)c.spliterator();
    }
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
	return (Stream<E>)c.stream();
    }
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
	return (Stream<E>)c.parallelStream();
    }
}