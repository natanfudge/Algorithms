package algorithms

import java.util.*

sealed interface RootedTree : Graph  {
    companion object {
        fun create(root: Vertex, graph: Graph) = when (graph) {
            is Graph.Directed -> Directed(root,graph)
            is Graph.Undirected -> Undirected(root,graph)
            else -> error("Impossible")
        }
    }
    val root: Vertex
    class Directed(override val root: Vertex, private val graph: Graph.Directed): RootedTree, Graph.Directed by graph
    class Undirected(override val root: Vertex, private val graph: Graph.Undirected): RootedTree, Graph.Undirected by graph
}

fun Graph.bfs(root: Vertex): RootedTree {
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

    return RootedTree.create(root, bfsTree.build())
}


fun Graph.dfs(root: Vertex): RootedTree {
    val dfsTree = Graph.Builder(isDirected)
    dfsRecur(root, dfsTree)
    return RootedTree.create(root, dfsTree.build())
}

private fun Graph.dfsRecur(root: Vertex, dfsTree: Graph.Builder<*>) {
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

fun Graph.isTree(): Boolean {
    if (this is RootedTree) return true
    if (vertices.isEmpty()) return false
    val bfsTree = bfs(vertices[0])
    return bfsTree.vertices.size == vertices.size
}

fun Graph.root() = if (this is RootedTree) root else vertices[0]