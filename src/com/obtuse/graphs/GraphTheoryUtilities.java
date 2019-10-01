package com.obtuse.graphs;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseCollections;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 * A home for some useful graph theory algorithms.
 */

public class GraphTheoryUtilities {

    /**
     A wrapper for the {@link HashSet} class which yields sets which are quickly and robustly identified as being
     not equal if two different instances are compared.
     @param <E>
     */

    @SuppressWarnings("unused")
    public static class UniqueSet<E> extends HashSet<E> {

        private final int _id;
        private static int _nextId = 1;

        /**
         Create a wrapped {@link HashSet}.
         Instances of this class have a unique id number.
         This class's {@link #equals(Object)} method uses only this id number when comparing
         two instances of this class for equality via the {@link #equals(Object)} method.
         This avoids the complexity of the standard Java set comparison algorithm
         (see {@link AbstractSet#equals(Object)} for more info).
         */

        public UniqueSet() {
            super();

            _id = _nextId;
            _nextId += 1;

        }

        /**
         Equivalent to {@link HashSet#HashSet(int)}.
         */

        public UniqueSet( int initialCapacity ) {
            super( initialCapacity );

            _id = _nextId;
            _nextId += 1;

        }

        /**
         Equivalent to {@link HashSet#HashSet(int, float)}.
         */

        public UniqueSet( int initialCapacity, float loadFactor ) {
            super( initialCapacity, loadFactor );

            _id = _nextId;
            _nextId += 1;

        }

        /**
         Equivalent to {@link HashSet#HashSet(Collection)};
         */

        public UniqueSet( Collection< ? extends E> c ) {
            super( c );

            _id = _nextId;
            _nextId += 1;

        }

        public boolean equals( Object rhs ) {

            return rhs instanceof UniqueSet && _id == ((UniqueSet)rhs)._id;

        }

        public int hashCode() {

            return Integer.hashCode( _id );

        }

    }

    /**
     * Topologically sort the vertices of a directed graph.
     * <p>Just in case your graph theory is a bit rusty, here are a few points to keep in mind:
     * <ul>
     * <li>a graph is a collection of vertices (points) which might
     * be connected together by directedArcs (lines between the points).</li>
     * <li>A directed graph is a graph in which each
     * arc has a direction (if an arc connects two vertices A and B then it goes from A to B <u>or</u>
     * it goes from B to A). Note that if an arc does go from A to B then there might be another arc
     * that goes in the other direction fro B to A.</li>
     * <li>directedArcs can loop back on themselves in the sense like there might an arc that loops from A back to A.</li>
     * <li>A graph can have multiple parts in the sense that there might be one cluster of vertices which are
     * connected together and a second cluster of vertices which are also connected together but there might not
     * be any connection between any of the nodes in one such cluster to or from nodes in some other such cluster.</li>
     * </ul>
     * <p>Algorithm taken from <tt>http://en.wikipedia.org/wiki/Topological_sorting</tt></p>
     * @param directedArcs the directedArcs of the graph to be sorted.
     * @param foundLoop an optional list which, if provided, will be filled in with the first loop found
     *                  (note that if one loop is found then there could be others).
     *                 If provided, this list will be empty if this method call returns without finding
     *                 any loops.
     * @return an {@link Optional} containing the vertices in topological order if it is acyclic (contains no loops);
     * {@code null} if impossible to topologically sort the graph's vertices because it contains one or more loops.
     */

    @NotNull
    public static <V extends Comparable<V>>
    Optional<List<Vertex<V>>> topologicalSort(
            @NotNull Collection<DirectedArc<V>> directedArcs,
            @Nullable final List<Vertex<V>> foundLoop
    ) {

        Map<V,Set<V>> dependencies = new HashMap<>();
        Map<V,Vertex<V>> allVertices = new HashMap<>();

        initializeMaps( directedArcs, dependencies, allVertices );

        List<V> temporarilyMarkedNodes = new ArrayList<>(  );
        Set<V> permanentlyMarkedNodes = new HashSet<>();

        List<Vertex<V>> sortedNodes = new ArrayList<>();
        List<Vertex<V>> unvisitedNodes = new ArrayList<>( allVertices.values() );
        while ( !unvisitedNodes.isEmpty() ) {

            Vertex<V> node = unvisitedNodes.remove( 0 );

            if ( !permanentlyMarkedNodes.contains( node.getV() ) ) {

                if (
                        !topologicalSortVisitNode(
                                node,
                                temporarilyMarkedNodes,
                                permanentlyMarkedNodes,
                                sortedNodes,
                                dependencies,
                                allVertices
                        )
                ) {

                    if ( foundLoop != null ) {

                        foundLoop.clear();
                        for (V v : temporarilyMarkedNodes ) {

                            foundLoop.add( allVertices.get( v ) );

                        }

                    }

                    return Optional.empty();

                }

            }

        }

        if ( sortedNodes.size() != allVertices.size() ) {

            throw new HowDidWeGetHereError( "topological sort did not return all nodes" );

        }

        return Optional.of( sortedNodes );

    }

    /**
     * Topologically sort the vertices of a directed graph.
     * <p>This is a variant of {@link #topologicalSort(Collection, List)} that does not return a loop in the
     * case that the graph is acyclic. See that method for more information.</p>
     */

    public static <V extends Comparable<V>> Optional<List<Vertex<V>>> topologicalSort( Collection<DirectedArc<V>> directedArcs ) {

        return topologicalSort( directedArcs, null );

    }

    private static <V extends Comparable<V>> boolean topologicalSortVisitNode(
            @NotNull Vertex<V> node,
            @NotNull Collection<V> temporarilyMarkedNodes,
            @NotNull Set<V> permanentlyMarkedNodes,
            @NotNull List<Vertex<V>> sortedNodes,
            @NotNull Map<V,Set<V>> dependencies,
            @NotNull Map<V,Vertex<V>>  allVertices
    ) {

        if ( temporarilyMarkedNodes.contains( node.getV() ) ) {

            return false;

        }

        if ( !permanentlyMarkedNodes.contains( node.getV() ) ) {

            temporarilyMarkedNodes.add( node.getV() );

            Set<V> nodeDependencies = dependencies.getOrDefault( node.getV(), Collections.emptySet() );

            for ( V dependency : nodeDependencies ) {

                if (
                        !topologicalSortVisitNode(
                                allVertices.get(dependency),
                                temporarilyMarkedNodes,
                                permanentlyMarkedNodes,
                                sortedNodes, dependencies,
                                allVertices
                        )
                ) {

                    return false;

                }

            }

            permanentlyMarkedNodes.add( node.getV() );
            sortedNodes.add( node );

            temporarilyMarkedNodes.remove( node.getV() );

        }

        return true;

    }

    /**
     Separate a graph into clusters where each cluster is a standalone connected graph and where
     no pair of clusters shares an arc (i.e. no pair of clusters is connected by an arc).
     @param directedArcs the directedArcs that make up the graph.
     @param <V> the type of value used to name vertices.
     @return a <code>{@link Map}&lt;{@link Vertex}&lt;V>,{@link Set}&lt;{@link Vertex}&lt;V>></code> which
     acts as a directory of which cluster each vertex is a member of.
     If the return value is assigned to the variable {@code directory} and if the variables {@code v1} and {@code v2}
     refer to two vertices in the original graph then
     <ul>
     <li>{@code directory.get(v1)} will yield a <code>{@link Set}&lt;{@link Vertex}&lt;V>></code> containing the vertices in the cluster that includes {@code v1}.</li>
     <li>If both {@code v1} and {@code v2} are in the same cluster then
     {@code directory.get(v1)} and {@code directory.get(v2)} will both yield the same
     <code>{@link Set}&lt;{@link Vertex}&lt;V>></code> instance.</li>
     <li>{@code directory.get(v1) == directory.get(v2)} will be {@code true} if and only if {@code v1} and {@code v2} are members of the same cluster.</li>
     <li>{@code directory.values()} will yield a collection of the clusters in the graph but the collection will almost certainly have duplicates.
     <br>{@code new HashSet<>( directory.values() )} will yield a set with any extra duplicate clusters removed.
     </ul>
     <br><br>Note that the {@code new HashSet<>( directory.values() )} 'trick' used above to eliminate duplicates in what {@code directory.values()} returns
     is faster than it might look because the sets in the directory
     are actually instances of the {@link GraphTheoryUtilities.UniqueSet} which is a wrapper around {@link HashSet} that ensures that
     any two distinct {@link UniqueSet} instances are identified to be distinct very quickly (the algorithm that the Java set classes
     use to determine set equality is (arguably) rather baroque and not exactly blindingly fast).</li>
     */

    @NotNull
    public static <V extends Comparable<V>>
    Map<Vertex<V>, Set<Vertex<V>>> clusterizeGraph( @NotNull Collection<? extends Edge<V>> directedArcs ) {

        // Build a map of clusters where each vertex starts out in its own cluster.
        // This is a directory/index of which cluster each vertex is a member of.

        Map<Vertex<V>,Set<Vertex<V>>> directory = new HashMap<>();
        for ( Edge<V> directedArc : directedArcs ) {

            directory.computeIfAbsent( directedArc.getA(), v -> new UniqueSet<>() ).add( directedArc.getA() );
            directory.computeIfAbsent( directedArc.getB(), v -> new UniqueSet<>() ).add( directedArc.getB() );

        }

        // For each arc, if its vertices are in different clusters then merge the two clusters into one.

        for ( Edge<V> directedArc : directedArcs ) {

            Vertex<V> a = directedArc.getA();
            Vertex<V> b = directedArc.getB();

            // Are the two vertices in different clusters?

            Set<Vertex<V>> c1 = directory.get( a );
            Set<Vertex<V>> c2 = directory.get( b );

            if ( directory.get(a) != directory.get(b) ) {

                // Yes. Merge the smaller cluster into the larger one.
                // We do this by ensuring that c1 is the small cluster if they are of different sizes.

                if ( c1.size() > c2.size() ) {

                    Set<Vertex<V>> c3 = c1;
                    c1 = c2;
                    c2 = c3;

                }

                // Add all of the vertices in the smaller cluster into the larger cluster.

                c2.addAll( c1 );

                // Update the directory for each of the vertices in the smaller cluster.

                for ( Vertex<V> v : c1 ) {

                    directory.put( v, c2 );

                }

            }

        }

        // We now have a directory of which cluster each vertex is in.
        // Since we ensured that no pair of vertices that are connected by an arc are ever in different clusters,
        // we know for an absolute fact that we have correctly partitioned the graph into unconnected clusters that
        // each only contain vertices which are somehow connected.

        // We're going to return the directory.
        // The caller can turn it into a collection of clusters by just calling {@link Map#values} on the map.
        // If this map only contains one cluster then the graph is connected (also easy for the caller to do).
        // The bottom line is that returning the directory intact provides the caller with as much info as we have.

        return directory;

    }

    /**
     Simple utility method for printing the directory returned by {@link #clusterizeGraph(Collection)}.
     @param title the title to appear on the line before the graph's contents.
     @param directory the directory to be printed.
     @param <V> the type of the value used to name indices in the directory (not really but it (probably?) avoids
     compiler and IDE warnings).
     */

    @SuppressWarnings("unused")
    public static <V extends Comparable<V>> void printDirectory(
            final String title, final Map<Vertex<V>, Set<Vertex<V>>> directory
    ) {

        Logger.logMsg( title );
        for ( Vertex<V> v : directory.keySet() ) {

            Logger.logMsg( "    " + v + " is in " + directory.get(v) );

        }
    }

    private static <V extends Comparable<V>> void initializeMaps(
            final Collection<DirectedArc<V>> directedArcs,
            final Map<V, Set<V>> dependencies,
            final Map<V, Vertex<V>> allVertices
    ) {

        for ( DirectedArc<V> a : directedArcs ) {

            allVertices.put( a.getSrc().getV(), a.getSrc() );
            allVertices.put( a.getDst().getV(), a.getDst() );
            if ( !dependencies.containsKey( a.getSrc().getV() ) ) {

                dependencies.put( a.getSrc().getV(), new HashSet<>() );

            }

            dependencies.get( a.getSrc().getV() ).add( a.getDst().getV() );

        }
    }

    /**
     Try to find a loop in a directed graph which includes a specified vertex.
     @param directedArcs the directedArcs in the graph.
     @param vertex the vertex which the loop must include.
     @param <V> the type of value used to name vertices.
     @return an {@link Optional} containing the loop as a list of vertices if a loop was found.
     Otherwise, {@link Optional#empty()}.
     <br><br>Note that {@link Optional#empty()} is returned if the graph is acyclic or if there just do not happen to be
     any loops in the graph which include the specified vertex.
     <br><br>If you just want a loop if one exists and don't care which vertices it might involve, use
     {@link #topologicalSort(Collection, List)} instead of this method.
     */

    @NotNull
    public static <V extends Comparable<V>> Optional<List<Vertex<V>>> findLoop(
            @NotNull Collection<DirectedArc<V>> directedArcs,
            @NotNull Vertex<V> vertex
    ) {

        Map<V,Set<V>> dependencies = new HashMap<>();
        Map<V,Vertex<V>> allVertices = new HashMap<>();

        initializeMaps( directedArcs, dependencies, allVertices );

        List<V> temporarilyMarkedNodes = new LinkedList<>();
        Set<V> permanentlyMarkedNodes = new HashSet<>();

        List<Vertex<V>> sortedNodes = new LinkedList<>();

        if ( topologicalSortVisitNode( vertex, temporarilyMarkedNodes, permanentlyMarkedNodes, sortedNodes, dependencies, allVertices ) ) {

            return Optional.empty();

        } else {

            List<Vertex<V>> rval = new ArrayList<>();
            for ( V v : temporarilyMarkedNodes ) {

                rval.add( allVertices.get(v) );

            }

            return Optional.of( rval );

        }

    }

    /**
     Determine if a directed graph is acyclic.
     <p>This method is exactly equivalent to
     <blockquote><code>topologicalSort( directedArcs ).isPresent()</code>
     </blockquote></p>
     @param directedArcs the directedArcs in the graph.
     @param <V> the type of value used to name vertices.
     @return {@code true} if the graph is acyclic (has no loops); {@code false} otherwise.
     */

    public static <V extends Comparable<V>> boolean isGraphAcyclic( Collection<DirectedArc<V>> directedArcs ) {

        Optional<List<Vertex<V>>> optSortedNodes = topologicalSort( directedArcs );

        return optSortedNodes.isEmpty();

    }

    private static <V extends Comparable<V>> void doit( String testName, DirectedArc<V>[] directedArcs, boolean expectedResult ) {

        Logger.logMsg( "########### directedArcs = " + Arrays.toString( directedArcs ) );

        ArrayList<DirectedArc<V>> arcsAsList = ObtuseCollections.arrayList( directedArcs );
        List<Vertex<V>> foundLoop = new ArrayList<>();
        Optional<List<Vertex<V>>> optResult = topologicalSort( arcsAsList, foundLoop );

        if ( optResult.isPresent() && isGraphAcyclic( arcsAsList ) ) {

            Logger.logMsg( "found a loop but isGraphAcyclic claims that the graph is acyclic" );

            ObtuseUtil.doNothing();

        } else if ( optResult.isEmpty() && !isGraphAcyclic( arcsAsList ) ) {

            Logger.logMsg( "did NOT find a loop but isGraphAcyclic claims that the graph is NOT acyclic" );

            ObtuseUtil.doNothing();

        }

        if ( optResult.isPresent() == expectedResult ) {

            Logger.logMsg(
                    "test " + testName + (
                            optResult.map( vertices -> " worked " + vertices ).orElse( " worked empty" )
                    ) +
                    " for " + arcsAsList
            );

            if ( !expectedResult ) {

                Map<V,Vertex<V>> allIndices = new HashMap<>();
                for ( DirectedArc<V> a : directedArcs ) {

                    allIndices.put( a.getSrc().getV(), a.getSrc() );
                    allIndices.put( a.getDst().getV(), a.getDst() );

                }

                for ( Vertex<V> vertex : allIndices.values() ) {

                    @NotNull Optional<List<Vertex<V>>> loop = findLoop( arcsAsList, vertex );
                    if ( loop.isPresent() ) {

                        Logger.logMsg( "    one loop is " + formatLoop( loop.get() ) );
                        Logger.logMsg( "    this could be a different loop:  " + formatLoop( foundLoop ) );

                        break;

                    }

                }

            }

            ObtuseUtil.doNothing();

        } else {

            Logger.logMsg(
                    "test " + testName + (
                            optResult.map( vertices -> " yielded false " + vertices ).orElse( " failed empty" )
                    ) +
                    " for " + arcsAsList
            );

            ObtuseUtil.doNothing();

        }

        Logger.logMsg( "original graph is " + arcsAsList );
        @NotNull Map<Vertex<V>, Set<Vertex<V>>> directory = clusterizeGraph( arcsAsList );
        HashSet<Set<Vertex<V>>> cleanedValues = new HashSet<>( directory.values() );
        if ( cleanedValues.size() == 1 ) {

            Logger.logMsg( "    The graph is connected." );

        } else {

            Logger.logMsg( "    The graph is not connected. Its clusters are:" );
            for ( Set<Vertex<V>> cluster : cleanedValues ) {

                Logger.logMsg( "        " + cluster );

            }

        }

    }

    @NotNull
    private static <V extends Comparable<V>> StringBuilder formatLoop( final @NotNull List<Vertex<V>> loop ) {

        StringBuilder sb = new StringBuilder();

        for ( Vertex<V> vv : loop ) {

            sb.append( vv.getV() ).append( " -> " );

        }

        sb.append( loop.get( 0 ) );

        return sb;

    }

    @SuppressWarnings("unchecked")
    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "TopoSort", "testing" );

        Vertex<String> a = new Vertex<>( "a" );
        Vertex<String> b = new Vertex<>( "b" );
        Vertex<String> c = new Vertex<>( "c" );
        Vertex<String> d = new Vertex<>( "d" );
        Vertex<String> e = new Vertex<>( "e" );
        Vertex<String> f = new Vertex<>( "f" );

        doit(
                "simple #1",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( c, b )
                },
                true
        );

        doit(
                "simple #2",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( b, c ),
                        new DirectedArc<>( b, d ),
                        new DirectedArc<>( c, e ),
                        new DirectedArc<>( d, e )
                },
                true
        );

        doit(
                "simple #3",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( b, c ),
                        new DirectedArc<>( c, a )
                },
                false
        );

        doit(
                "simple #4",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( b, c ),
                        new DirectedArc<>( b, d ),
                        new DirectedArc<>( c, e ),
                        new DirectedArc<>( d, e ),
                        new DirectedArc<>( f, f )
                },
                false
        );

        doit(
                "simple #5",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( b, c ),
                        new DirectedArc<>( c, d ),
                        new DirectedArc<>( d, a ),
                        new DirectedArc<>( c, e )
                },
                false
        );

        doit(
                "simple #5",
                new DirectedArc[] {
                        new DirectedArc<>( a, b ),
                        new DirectedArc<>( c, d )
                },
                true
        );

    }

}
