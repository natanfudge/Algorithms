package algorithms

fun RootedTree.bfs() = bfs(root)
fun RootedTree.dfs() = dfs(root)

fun Graph.bfs(root: Vertex): RootedTree {
    val bfsTree = Graph.Builder()
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

    return bfsTree.build(this.isDirected).withAttribute(GraphAttribute.Root(root))
}


fun Graph.dfs(root: Vertex): RootedTree {
    val dfsTree = Graph.Builder()
    dfsRecur(root, dfsTree)
    return dfsTree.build(isDirected).withAttribute(GraphAttribute.Root(root))
}

private fun Graph.dfsRecur(root: Vertex, dfsTree: Graph.Builder) {
    dfsTree.addVertex(root)
    for (edge in edgesOf(root)) {
        if (edge.end !in dfsTree) {
            dfsTree.addEdge(edge)
            dfsRecur(edge.end, dfsTree)
        }
    }
}


fun Graph.getConnectedComponents(): List<Graph> {
    if (!isDirected) {
        return getUndirectedConnectedComponents()
    } else {
        if (isTree()) return listOf(this)
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
        val componentGraph = buildGraph(isDirected) {
            for (vertex in bfsTree.vertices) {
                addVertex(vertex)
                // Gets all neighbors in the [this] graph
                for (edge in this@getUndirectedConnectedComponents.edgesOf(vertex)) {
                    addEdge(edge)
                }
            }
        }
        inheritAttributes(componentGraph)
    }
}

fun Graph.isTree(): Boolean {
    if (isRootedTree) return true
    if (vertices.isEmpty()) return false
    val bfsTree = bfs(root())
    return bfsTree.edges.size == edges.size && bfsTree.vertices.size == vertices.size
}

fun Graph.root() = if (isRootedTree) asRootedTree.root else vertices[0]