package com.obtuse.graphs;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 An undirected edge intended to be used to construct undirected graphs.
 <p>The term <em>edge</em> is used in graph theory to describe a connection between a pair of vertices
 in either directed or undirected graphs.
 Consequently, this 'narrowing' of the term to only refer to a connection between a pair of
 vertices in an undirected graph is incorrect but seems to work reasonably well.</p>
 */

public class UndirectedEdge<V> implements Edge<V> {

    public final Vertex<V> a;
    public final Vertex<V> b;

    public final String toString;

    public UndirectedEdge( @NotNull Vertex<V> a, @NotNull Vertex<V> b ) {
        super();

        this.a = a;
        this.b = b;

        toString = "{" + a + "," + b + "}";

    }

    @Override
    @NotNull
    public Vertex<V> getA() {

        return a;

    }

    @Override
    @NotNull
    public Vertex<V> getB() {

        return b;

    }

    public boolean isDirected() {

        return false;

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof UndirectedEdge && a.equals( ( (UndirectedEdge)rhs).a ) && b.equals( ( (UndirectedEdge)rhs).b );

    }

    public int hashCode() {

        return ( a.hashCode() << 3 ) ^ b.hashCode();

    }

    public String toString() {

        return toString;

    }
}
