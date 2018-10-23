package com.obtuse.graphs;

import org.jetbrains.annotations.NotNull;

/**
 A vertex in a directed or undirected graph.
 */

public class Vertex<V> {

    private final V _us;

    public Vertex( @NotNull final V us ) {

        super();

        _us = us;

    }

    public V getV() {

        return _us;

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof Vertex && _us.equals( ((Vertex)rhs)._us );

    }

    public int hashCode() {

        return _us.hashCode();

    }

    public String toString() {

        return "" + getV();

    }

}
