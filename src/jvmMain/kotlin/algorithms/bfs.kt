package algorithms

import java.util.*

data class RootedTree(val root: Vertex, private val graph: Graph) : Graph by graph

interface VertexTracker<T : MutableCollection<Vertex>> {
    val empty: T
    fun remove(collection: T): Vertex
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

    return RootedTree(root, bfsTree.build())
}

fun Graph.bfsObfuscatedByEvaTardosh(root: Vertex): RootedTree {
    val T = Graph.Builder(directed = this.isDirected)
    T.addVertex(root)
    val discovered = hashSetOf(root)

    var currentL = listOf(root)
    do {
        val nextL = mutableListOf<Vertex>()
        for (u in currentL) {
            for (v in neighborsOf(u)) {
                if (v !in discovered) {
                    T.addEdge(u, v)
                    nextL.add(v)
                }
            }
        }
        currentL = nextL
    } while (currentL.isNotEmpty())

    return RootedTree(root, T.build())
}

fun Graph.dfs(root: Vertex): RootedTree {
    val dfsTree = Graph.Builder(isDirected)
    dfsRecur(root, dfsTree)
    return RootedTree(root, dfsTree.build())
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