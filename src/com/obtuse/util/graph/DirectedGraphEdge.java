/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.graph;

/**
 * An edge in a directed graph.
 */

@SuppressWarnings("UnusedDeclaration")
public interface DirectedGraphEdge {

    DirectedGraphVertex getSourceNode();

    DirectedGraphVertex getDestinationNode();

}
