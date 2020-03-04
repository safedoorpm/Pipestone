/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.graph;

import com.obtuse.exceptions.HowDidWeGetHereError;

import java.util.*;

/**
 * Represent a directed graph.
 * @deprecated Strictly speaking, not deprecated but untested and not ready for prime time - DO NOT USE
 */

@SuppressWarnings("UnusedDeclaration")
@Deprecated
public class DirectedGraph {

    /**
     * Perform a topological sort on a directed graph.
     * <p/>
     * This algorithm is taken from <a href="http://en.wikipedia.org/wiki/Topological_sorting">Topological sorting</a>
     * on Wikipedia.  That articles states that the article appears to have first been described in print by Robert Tarjan.
     *
     * @param vertices the vertices which makeup the directed graph.
     * @return the topologically sorted vertices in the specified directed graph; null if graph has any cycles.
     */

    public List<DirectedGraphVertex> topologicalSort( final Collection<DirectedGraphVertex> vertices ) {

        SortedSet<DirectedGraphVertex> temporarilyMarkedNodes = new TreeSet<>();
        SortedSet<DirectedGraphVertex> permanentlyMarkedNodes = new TreeSet<>();

        List<DirectedGraphVertex> sortedNodes = new LinkedList<>();
        List<DirectedGraphVertex> unvisitedNodes = new LinkedList<>( vertices );
        while ( !unvisitedNodes.isEmpty() ) {

            DirectedGraphVertex node = unvisitedNodes.remove( 0 );

            if ( !permanentlyMarkedNodes.contains( node ) ) {

                if ( !topologicalSortVisitNode( node, temporarilyMarkedNodes, permanentlyMarkedNodes, sortedNodes ) ) {

                    return null;

                }

            }

        }

        if ( sortedNodes.size() != vertices.size() ) {

            throw new HowDidWeGetHereError( "topological sort did not return all nodes" );

        }

        return sortedNodes;

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean topologicalSortVisitNode(
            final DirectedGraphVertex node,
            final Collection<DirectedGraphVertex> temporarilyMarkedNodes,
            final SortedSet<DirectedGraphVertex> permanentlyMarkedNodes,
            final List<DirectedGraphVertex> sortedNodes
    ) {

        if ( temporarilyMarkedNodes.contains( node ) ) {

            return false;

        }

        if ( !permanentlyMarkedNodes.contains( node ) ) {

            temporarilyMarkedNodes.add( node );

            for ( DirectedGraphEdge dependency : node.getOutBoundEdges() ) {

                if ( !topologicalSortVisitNode( dependency.getDestinationNode(), temporarilyMarkedNodes, permanentlyMarkedNodes, sortedNodes ) ) {

                    return false;

                }

            }

            permanentlyMarkedNodes.add( node );
            sortedNodes.add( node );

            temporarilyMarkedNodes.remove( node );

        }

        return true;

    }

}
