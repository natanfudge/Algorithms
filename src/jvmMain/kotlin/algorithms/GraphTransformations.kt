package algorithms

/**
 * Returns the undirected version of the graph and the list of neighbors of each vertex in the original graph.
 */
fun Graph.undirect(): Pair<Graph, Map<Vertex, List<Vertex>>> {
    val neighbors = mutableMapOf<Vertex, List<Vertex>>()

    val graph = Graph.Builder()
    for (edge in edges) {
        graph.addEdge(edge)
    }

    for (vertex in vertices) {
        graph.addVertex(vertex)
        neighbors[vertex] = neighborsOf(vertex)
    }

    return  inheritAttributes(graph.buildUndirected()) to neighbors
}

fun Graph.direct(neighborMap: Map<Vertex,List<Vertex>>): DirectedGraph {
    val graph = Graph.Builder()
    for(vertex in vertices){
        graph.addVertex(vertex)
        for(neighbor in neighborMap.getValue(vertex)){
            graph.addEdge(start = vertex, end = neighbor)
        }
    }
    return inheritAttributes(graph.buildDirected())
}