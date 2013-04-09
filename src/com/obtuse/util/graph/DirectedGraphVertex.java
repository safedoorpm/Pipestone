package com.obtuse.util.graph;

/**
 * A vertex in a directed graph.
 */

public interface DirectedGraphVertex extends Vertex {

    DirectedGraphEdge[] getOutBoundEdges();

}
