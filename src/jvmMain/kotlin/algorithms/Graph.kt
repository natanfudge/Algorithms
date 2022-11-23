package algorithms

import androidx.compose.ui.graphics.Color

val Graph.isDirected get() = this is Graph.Directed

sealed interface Graph {
    val vertices: List<Vertex>
    val edges: List<Edge>

    interface Directed : Graph

    interface Undirected : Graph

//    val isDirected: Boolean

    fun neighborsOf(vertex: Vertex): List<Vertex>
    fun edgesOf(vertex: Vertex): List<Edge>

    companion object {
        fun Builder(directed: Boolean) = if (directed) Builder.Directed() else Builder.Undirected()
    }

    sealed class Builder<T : Graph>(private val directed: Boolean) {

        protected abstract fun constructLinkedListGraph(
            vertices: List<Vertex>,
            edges: List<Edge>
        ): T

        class Directed : Builder<Graph.Directed>(directed = true) {
            override fun constructLinkedListGraph(vertices: List<Vertex>, edges: List<Edge>): Graph.Directed {
                return LinkedListGraph.Directed(vertices, edges)
            }
        }

        class Undirected : Builder<Graph.Undirected>(directed = false) {
            override fun constructLinkedListGraph(vertices: List<Vertex>, edges: List<Edge>): Graph.Undirected {
                return LinkedListGraph.Undirected(vertices, edges)
            }
        }

        private val vertices = mutableSetOf<VertexTag>()
        private val edges = mutableSetOf<Edge.Builder>()
        fun addVertex(vertex: VertexTag): Boolean {
            if (vertex in vertices) return false
            return vertices.add(vertex)
        }

        operator fun contains(vertex: Vertex) = vertex.tag in vertices

        fun addVertex(vertex: Vertex) = addVertex(vertex.tag)

//        fun addVertices(vararg vertices: VertexTag) {
//            for (vertex in vertices) addVertex(vertex)
//        }

        fun addEdges(vararg edges: Edge.Builder) {
            for (edge in edges) addEdge(edge)
        }

        infix fun VertexTag.edgeTo(other: VertexTag): Boolean {
            return addEdge(this, other)
        }

        fun addEdge(start: VertexTag, end: VertexTag) = addEdge(Edge.Builder.create(start, end, directed))
        fun addEdge(start: Vertex, end: Vertex) = addEdge(start.tag, end.tag)

        fun addEdge(edge: Edge.Builder): Boolean {
            edge.vertices.forEach {
                if (it !in vertices) addVertex(it)
            }
            return edges.add(edge)
        }

        fun addEdge(edge: Edge) = addEdge(edge.start.tag, edge.end.tag)
        fun build(): T {
            val verticesOrdered = vertices.sortedBy { it.color.value }
            val vertexToIndex = buildMap {
                verticesOrdered.forEachIndexed { index, tag -> put(tag, index) }
            }

            fun VertexTag.build() = Vertex(tag = this, index = vertexToIndex.getValue(this))
            fun Edge.Builder.build(): Edge {
                val (start, end) = when (this) {
                    is Edge.Builder.Undirected -> {
                        val asList = this.vertices.toList()
                        asList[0] to asList[1]
                    }

                    is Edge.Builder.Directed -> this.start to this.end
                }
                return Edge(start = start.build(), end = end.build())
            }

            return constructLinkedListGraph(
                verticesOrdered.map { it.build() },
                edges.map { it.build() },
            )
        }
    }
}


fun buildGraph(directed: Boolean, builder: Graph.Builder<*>.() -> Unit): Graph =
    if (directed) buildDirectedGraph(builder) else buildUndirectedGraph(builder)

fun buildDirectedGraph(builder: Graph.Builder.Directed.() -> Unit): Graph.Directed =
    Graph.Builder.Directed().apply(builder).build()

fun buildUndirectedGraph(builder: Graph.Builder.Undirected.() -> Unit): Graph.Undirected =
    Graph.Builder.Undirected().apply(builder).build()

sealed class LinkedListGraph(
    override val vertices: List<Vertex>,
    override val edges: List<Edge>,
) : Graph {
    class Directed(
        vertices: List<Vertex>,
        edges: List<Edge>
    ) : Graph.Directed, LinkedListGraph(vertices, edges)

    class Undirected(
        vertices: List<Vertex>,
        edges: List<Edge>
    ) : Graph.Undirected, LinkedListGraph(vertices, edges)

    private val neighbors: Map<Vertex, List<Edge>> = run {
        val map = vertices.associateWith { mutableListOf<Edge>() }

        for (edge in edges) {
            map[edge.start]!!.add(edge)
            if (!isDirected) map[edge.end]!!.add(Edge(start = edge.end, end = edge.start))
        }
        map
    }

    override fun neighborsOf(vertex: Vertex): List<Vertex> = neighbors[vertex]!!.map { it.end }
    override fun edgesOf(vertex: Vertex): List<Edge> = neighbors[vertex]!!

    override fun toString(): String {
        return "${vertices.size} vertices, ${edges.size} edges (${if (isDirected) "Directed" else "Undirected"})"
    }


}


data class Vertex(val color: Color, val index: Int, val name: String) {
    constructor(tag: VertexTag, index: Int) : this(tag.color, index, tag.name)

    val tag = VertexTag(color, name)
    override fun toString(): String = "($name)"
    override fun equals(other: Any?): Boolean = other is Vertex && other.tag == tag
    override fun hashCode(): Int = tag.hashCode()
}

data class VertexTag(val color: Color, val name: String)

infix fun Color.named(name: String) = VertexTag(this, name)
//data class VertexTag(val tag)

data class Edge(val start: Vertex, val end: Vertex) {
    sealed interface Builder {
        val vertices: Set<VertexTag>

        companion object {
            fun create(start: VertexTag, end: VertexTag, directed: Boolean) = if (directed) Directed(start, end)
            else Undirected(setOf(start, end))
        }

        data class Directed(val start: VertexTag, val end: VertexTag) : Builder {
            override val vertices: Set<VertexTag> = setOf(start, end)
        }

        data class Undirected(override val vertices: Set<VertexTag>) : Builder
    }
//    data class Builder(val start: VertexTag, val end: VertexTag)
}
