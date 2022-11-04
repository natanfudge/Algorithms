package logic

/**
 * Returns the undirected version of the graph and the list of neighbors of each vertex in the original graph.
 */
fun Graph.undirect(): Pair<Graph, Map<Vertex, List<Vertex>>> {
    val neighbors = mutableMapOf<Vertex, List<Vertex>>()

    val graph = Graph.Builder(directed = false)
    for (edge in edges) {
        graph.addEdge(edge)
    }

    for (vertex in vertices) {
        graph.addVertex(vertex)
        neighbors[vertex] = neighborsOf(vertex)
    }

    return graph.build() to neighbors
}

fun Graph.direct(neighborMap: Map<Vertex,List<Vertex>>): Graph {
    val graph = Graph.Builder(directed = true)
    for(vertex in vertices){
        graph.addVertex(vertex)
        for(neighbor in neighborMap.getValue(vertex)){
            graph.addEdge(start = vertex, end = neighbor)
        }
    }
    return graph.build()
}