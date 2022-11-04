package logic

import androidx.compose.ui.graphics.Color

interface Graph {
    val vertices: List<Vertex>
    val edges: List<Edge>
    val isDirected: Boolean

    fun neighborsOf(vertex: Vertex): List<Vertex>
    fun edgesOf(vertex: Vertex): List<Edge>

    class Builder(private val directed: Boolean) {
        private val vertices = mutableSetOf<VertexTag>()
        private val edges = mutableSetOf<Edge.Builder>()
        fun addVertex(vertex: VertexTag): Boolean {
            require(vertex !in vertices)
            return vertices.add(vertex)
        }

        fun addVertex(vertex: Vertex) = addVertex(vertex.tag)

//        fun addVertices(vararg vertices: VertexTag) {
//            for (vertex in vertices) addVertex(vertex)
//        }

        fun addEdges(vararg edges: Edge.Builder) {
            for (edge in edges) addEdge(edge)
        }

        infix fun VertexTag.edgeTo(other: VertexTag): Boolean {
//            val existingTags = vertices.map { it.tag }
            if (this !in vertices) addVertex(this)
            if (other !in vertices) addVertex(other)
            return addEdge(this, other)
        }

        fun addEdge(start: VertexTag, end: VertexTag) = addEdge(Edge.Builder.create(start, end, directed))
        fun addEdge(start: Vertex, end: Vertex) = addEdge(start.tag, end.tag)

        fun addEdge(edge: Edge.Builder) = edges.add(edge)
        fun addEdge(edge: Edge) = addEdge(edge.start.tag, edge.end.tag)
        fun build(): LinkedListGraph {
            val verticesOrdered = vertices.sortedBy { it.value }
            val vertexToIndex = buildMap {
                verticesOrdered.forEachIndexed { index, tag -> put(tag, index) }
            }

            fun VertexTag.build() = Vertex(tag = this, index = vertexToIndex.getValue(this))
            fun Edge.Builder.build(): Edge {
                val (start,end) = when (this) {
                    is Edge.Builder.Undirected -> {
                        val asList = this.vertices.toList()
                        asList[0] to asList[1]
                    }

                    is Edge.Builder.Directed -> this.start to this.end
                }
                return Edge(start = start.build(), end = end.build())
            }

            return LinkedListGraph(
                verticesOrdered.map { it.build() },
                edges.map { it.build() },
                directed
            )
        }
    }


}


fun buildGraph(directed: Boolean, builder: Graph.Builder.() -> Unit ) : Graph = Graph.Builder(directed).apply(builder).build()

class LinkedListGraph(
    override val vertices: List<Vertex>,
    override val edges: List<Edge>,
    override val isDirected: Boolean
) : Graph {
    private val neighbors: Map<Vertex, List<Edge>> = kotlin.run {
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
        return "${vertices.size} vertices, ${edges.size} edges (${if(isDirected) "Directed" else "Undirected"})"
    }


}


data class Vertex(val tag: Color, val index: Int) {
    override fun toString(): String = "($tag)"
    override fun equals(other: Any?): Boolean  = other is Vertex && other.tag == tag
    override fun hashCode(): Int = tag.hashCode()
}

typealias VertexTag = Color
//data class VertexTag(val tag)

data class Edge(val start: Vertex, val end: Vertex) {
    sealed interface Builder {
        companion object {
            fun create(start: VertexTag, end: VertexTag, directed: Boolean) = if (directed) Directed(start, end)
            else Undirected(setOf(start, end))
        }

        data class Directed(val start: VertexTag, val end: VertexTag) : Builder
        data class Undirected(val vertices: Set<VertexTag>) : Builder
    }
//    data class Builder(val start: VertexTag, val end: VertexTag)
}
