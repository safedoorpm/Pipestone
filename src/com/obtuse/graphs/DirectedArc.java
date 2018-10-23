package com.obtuse.graphs;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 A directed edge intended to be used to construct directed graphs.
 */

public class DirectedArc<V> implements Edge<V> {

    private final Vertex<V> _src;
    private final Vertex<V> _dst;

    private String _toString;

    public DirectedArc( @NotNull final Vertex<V> src, @NotNull final Vertex<V> dst ) {
        super();

        _src = src;
        _dst = dst;

        _toString = src + " -> " + dst;

    }

    @NotNull
    public Vertex<V> getSrc() {

        return _src;

    }

    @NotNull
    public Vertex<V> getDst() {

        return _dst;

    }

    @Override
    @NotNull
    public Vertex<V> getA() {

        return _src;

    }

    @Override
    @NotNull
    public Vertex<V> getB() {

        return _dst;

    }

    public boolean isDirected() {

        return true;

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof DirectedArc && _src.equals( ( (DirectedArc)rhs)._src ) && _dst.equals( ( (DirectedArc)rhs)._dst );

    }

    public int hashCode() {

        return ( _src.hashCode() << 3 ) ^ _dst.hashCode();

    }

    public String toString() {

        return _toString;

    }

}
