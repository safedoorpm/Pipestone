package com.obtuse.util.graph;

/**
 * An edge in a directed graph.
 */

public interface DirectedGraphEdge extends Edge {

    DirectedGraphVertex getSourceNode();

    DirectedGraphVertex getDestinationNode();

}
