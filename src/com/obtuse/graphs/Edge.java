package com.obtuse.graphs;

/**
 An edge in a graph.
 <p>This interface allows instances of {@link DirectedArc} and of {@link UndirectedEdge} to be used interchangeably where appropriate.</p>
 */

public interface Edge<V> {

    /**
     Get the vertex at one end of this edge/arc.
     @return the vertex at one end of this edge/arc.
     <p>This method returns the same value as {@link #getB()} if and only if this edge/arc connects a vertex to itself.</p>
     */

    Vertex<V> getA();

    /**
     Get the vertex at the other end of this edge/arc.
     @return the vertex at the other end of this edge/arc.
     <p>This method returns the same value as {@link #getA()} if and only if this edge/arc connects a vertex to itself.</p>
     */

    Vertex<V> getB();

    boolean isDirected();

}