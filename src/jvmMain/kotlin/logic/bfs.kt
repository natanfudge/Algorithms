package logic

fun Graph.bfs(root: Vertex): Graph {
    val bfsTree = Graph.Builder(directed = this.isDirected)
    bfsTree.addVertex(root)
    val visited = hashSetOf(root)

    var currentLayer = listOf(root)
    do {
        val nextLayer = currentLayer.flatMap { vertexInLayer -> edgesOf(vertexInLayer).filter { it.end !in visited } }
        for (edge in nextLayer) {
            bfsTree.addEdge(edge)
            bfsTree.addVertex(edge.end)
            visited.add(edge.end)
        }
        currentLayer = nextLayer.map { it.end }
    } while (currentLayer.isNotEmpty())

    return bfsTree.build()
}


fun Graph.getConnectedComponents(): List<Graph> {
    if (!isDirected) {
        return getUndirectedConnectedComponents()
    } else {
        val (undirected, neighborMap) = undirect()
        return undirected.getUndirectedConnectedComponents().map { it.direct(neighborMap) }
    }
}

private fun Graph.getUndirectedConnectedComponents(): List<Graph> {
    val componentBfsTrees = mutableListOf<Graph>()

    while (componentBfsTrees.sumOf { it.vertices.size } < vertices.size) {
        val visitedTags = componentBfsTrees.flatMap { tree -> tree.vertices.map { it.tag } }.toHashSet()
        val root = vertices.first { it.tag !in visitedTags }
        componentBfsTrees.add(bfs(root))
    }

    return componentBfsTrees.map { bfsTree ->
        buildGraph(isDirected) {
            for (vertex in bfsTree.vertices) {
                addVertex(vertex)
                // Gets all neighbors in the [this] graph
                for (edge in this@getUndirectedConnectedComponents.edgesOf(vertex)) {
                    addEdge(edge)
                }
            }
        }
    }
}