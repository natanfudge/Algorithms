package algorithms

import minElementAndValue

//typealias GraphPath = List<Edge>

data class GraphPath(val edges: List<Edge>) : List<Edge> by edges {

//    companion object {
//        fun ofVertices(graph: Graph, vararg vertices: Vertex) : GraphPath {
//
//        }
//    }
    override fun toString(): String {
        return edges.map { it.tag }.pathToString()
    }
}

fun List<EdgeTag>.pathToString(): String {
    if (isEmpty()) return "Empty Path"
    return joinToString("->") { it.start.name } + "->"  +last().end.name
}

fun Graph.pathThrough(vararg vertices: VertexTag): GraphPath {
    val edges = buildList {
        var current = vertices.first()
        for(vertex in vertices.drop(1)) {
            add(EdgeTag(current, vertex))
            current = vertex
        }
    }
    return GraphPath(edges.bindTo(this))
}

fun WeightedDirectedGraph.dijkstra(start: VertexTag, end: VertexTag) =
    dijkstra(matchingVertex(start), matchingVertex(end))

//TODO: important: see Test 2020a-b question2, and try to solve after doing this.
fun WeightedDirectedGraph.dijkstra(start: Vertex, end: Vertex): GraphPath {
    if (start == end) return GraphPath(listOf())

    val exploredNodeDistances = mutableMapOf(start to 0)
    val nodeShortestPathParent = mutableMapOf<Vertex, Edge>()
    while (exploredNodeDistances.size < vertices.size) {
        val outwordEdges = exploredNodeDistances.keys.flatMap { edgesOf(it) }
            .filter { it.end !in exploredNodeDistances }
        val (closestEdge, distance) = outwordEdges.minElementAndValue { exploredNodeDistances.getValue(it.start) + it.weight }
        exploredNodeDistances[closestEdge.end] = distance
        nodeShortestPathParent[closestEdge.end] = closestEdge
    }

    val shortestPathFromEnd = mutableListOf<Edge>()

    do {
        val previous = nodeShortestPathParent.getValue(shortestPathFromEnd.last().start)
        shortestPathFromEnd.add(previous)
    } while (previous.start != start)

    return GraphPath(shortestPathFromEnd.asReversed())
}