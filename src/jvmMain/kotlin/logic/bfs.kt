package logic

fun Graph.bfs(root: Vertex): Graph {
    val bfsTree = Graph.Builder(directed = this.isDirected)
    bfsTree.addVertex(root)
    val visited = hashSetOf(root)

    var currentLayer = listOf(root)
    var layerNum = 0
    do {
//        println("")
        val nextLayer = currentLayer.flatMap { vertexInLayer -> vertexInLayer.getEdges().filter { it.end !in visited } }
        for (edge in nextLayer) {
            bfsTree.addEdge(edge)
            bfsTree.addVertex(edge.end)
            visited.add(edge.end)
        }
        currentLayer = nextLayer.map { it.end }
    } while (currentLayer.isNotEmpty())

    return bfsTree.build()
}
