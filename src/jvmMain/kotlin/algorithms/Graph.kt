package algorithms

import androidx.compose.ui.graphics.Color


sealed interface Graph {
    val vertices: List<Vertex>
    val edges: List<Edge>

//    interface Directed : Graph {
//        val topologicalSort: List<Vertex>?
//    }
//
//    interface Undirected : Graph


    fun neighborsOf(vertex: Vertex): List<Vertex>
    fun edgesOf(vertex: Vertex): List<Edge>

//    companion object {
//        fun Builder(directed: Boolean) = if (directed) Builder.Directed() else Builder.Undirected()
//    }

    class Builder {
        private val vertices = mutableSetOf<VertexTag>()
        private val edges = mutableSetOf<Edge.Builder>()
        fun addVertex(vertex: VertexTag): Boolean {
            if (vertex in vertices) return false
            return vertices.add(vertex)
        }

        operator fun contains(vertex: Vertex) = vertex.tag in vertices

        fun addVertex(vertex: Vertex) = addVertex(vertex.tag)

        fun addEdges(vararg edges: Edge.Builder) {
            for (edge in edges) addEdge(edge)
        }

        infix fun VertexTag.edgeTo(other: VertexTag): Boolean {
            return addEdge(this, other)
        }

        fun VertexTag.to(vararg others: VertexTag) = others.forEach { addEdge(this, it) }

        fun addEdge(start: VertexTag, end: VertexTag) = addEdge(Edge.Builder(start, end))
        fun addEdge(start: Vertex, end: Vertex) = addEdge(start.tag, end.tag)

        fun addEdge(edge: Edge.Builder): Boolean {
            vertices.add(edge.start)
            vertices.add(edge.end)
            return edges.add(edge)
        }

        fun addEdge(edge: Edge) = addEdge(edge.start.tag, edge.end.tag)

        fun buildUndirected() = buildImpl(directed = false)
        fun buildDirected() = buildImpl(directed = true).withAttribute(GraphAttribute.Directed)

        fun build(directed: Boolean) = if(directed) buildDirected() else buildUndirected()

        private fun buildImpl(directed: Boolean): Graph {
            val verticesOrdered = vertices.sortedBy { it.color.value }
            val vertexToIndex = buildMap {
                verticesOrdered.forEachIndexed { index, tag -> put(tag, index) }
            }

            fun VertexTag.build() = Vertex(tag = this, index = vertexToIndex.getValue(this))
            fun Edge.Builder.build(): Edge {
                return Edge(start = start.build(), end = end.build())
            }

            val actualEdges = if(directed) edges else edges.deduplicateIdenticalUndirectedEdges()

            return LinkedListGraph(
                verticesOrdered.map { it.build() },
                actualEdges.map { it.build() },
                directed
            )
        }

        // a -> b and b -> a is the same in an undirected graph
        private fun Set<Edge.Builder>.deduplicateIdenticalUndirectedEdges(): List<Edge.Builder> {
            // By representing edges as a set of the start and end, a -> b and b -> a will be the same set.
            val deduplicated = map { (start, end) -> setOf(start, end) }.toSet()
            return deduplicated.map {
                val (start, end) = it.toList()
                Edge.Builder(start, end)
            }
        }
    }
}


fun buildGraph(directed: Boolean, builder: Graph.Builder.() -> Unit): Graph =
Graph.Builder().apply(builder).build(directed)

fun buildDirectedGraph(builder: Graph.Builder.() -> Unit): DirectedGraph =  Graph.Builder().apply(builder).buildDirected()

fun buildUndirectedGraph(builder: Graph.Builder.() -> Unit): Graph =
    buildGraph(false, builder)

class LinkedListGraph(
    override val vertices: List<Vertex>,
    override val edges: List<Edge>,
    private val directed: Boolean
) : Graph {
//    class Directed(
//        vertices: List<Vertex>,
//        edges: List<Edge>
//    ) : Graph.Directed, LinkedListGraph(vertices, edges) {
//        override val topologicalSort: List<Vertex>? by lazy {toplogicallySort()}
//    }

//    class Undirected(
//        vertices: List<Vertex>,
//        edges: List<Edge>
//    ) : Graph.Undirected, LinkedListGraph(vertices, edges)

    private val neighbors: Map<Vertex, List<Edge>> = run {
        val map = vertices.associateWith { mutableListOf<Edge>() }

        for (edge in edges) {
            map[edge.start]!!.add(edge)
            if (!directed) map[edge.end]!!.add(Edge(start = edge.end, end = edge.start))
        }
        map
    }

    override fun neighborsOf(vertex: Vertex): List<Vertex> = neighbors[vertex]!!.map { it.end }
    override fun edgesOf(vertex: Vertex): List<Edge> = neighbors[vertex]!!

    override fun toString(): String {
        return "${vertices.size} vertices, ${edges.size} edges"
    }


}


data class Vertex(val color: Color, val index: Int, val name: String) {
    constructor(tag: VertexTag, index: Int) : this(tag.color, index, tag.name)

    val tag = VertexTag(color, name)
    override fun toString(): String = "($name)"
    override fun equals(other: Any?): Boolean = other is Vertex && other.name == name
    override fun hashCode(): Int = name.hashCode()
}

data class VertexTag(val color: Color, val name: String) {
    override fun toString(): String  = name
}

infix fun Color.named(name: String) = VertexTag(this, name)

data class Edge(val start: Vertex, val end: Vertex) {
    data class Builder(val start: VertexTag, val end: VertexTag)
}
