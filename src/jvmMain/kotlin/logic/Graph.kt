package logic

import java.util.TreeSet

interface Graph {
    val vertices: List<Vertex>
    val edges: List<Edge>
    val isDirected: Boolean

    fun Vertex.getNeighbors(): List<Vertex>
    fun Vertex.getEdges(): List<Edge>

    class Builder(private val directed: Boolean) {
        private val vertices = mutableListOf<VertexTag>()
        private val edges = mutableListOf<Edge.Builder>()
        fun addVertex(vertex: VertexTag): Boolean {
            require(vertex !in vertices)
            return vertices.add(vertex)
        }

        fun addVertex(vertex: Vertex) = addVertex(vertex.tag)

        fun addVertices(vararg vertices: VertexTag) {
            for (vertex in vertices) addVertex(vertex)
        }

        fun addEdges(vararg edges: Edge.Builder) {
            for (edge in edges) addEdge(edge)
        }

        infix fun VertexTag.edgeTo(other: VertexTag): Boolean {
//            val existingTags = vertices.map { it.tag }
            if (this !in vertices) addVertex(this)
            if (other !in vertices) addVertex(other)
            return addEdge(Edge.Builder(this, other))
        }

        fun addEdge(edge: Edge.Builder) = edges.add(edge)
        fun addEdge(edge: Edge) = edges.add(Edge.Builder(edge.start.tag, edge.end.tag))
        fun build(): LinkedListGraph {
            val vertexToIndex = buildMap {
                vertices.forEachIndexed { index, tag -> put(tag, index) }
            }

            fun VertexTag.build() = Vertex(tag = this, index = vertexToIndex.getValue(this))
            fun Edge.Builder.build() = Edge(start = start.build(), end = end.build())

            return LinkedListGraph(
                vertices.map { it.build() },
                edges.map { it.build() },
                directed
            )
        }
    }


}

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

    override fun Vertex.getNeighbors(): List<Vertex> = neighbors[this]!!.map { it.end }
    override fun Vertex.getEdges(): List<Edge> = neighbors[this]!!

    override fun toString(): String = buildString {
        val orderedVertices = vertices.sortedBy { it.tag }

        append("Edges: ${orderedVertices.joinToString(" , ")}\n")
        for (vertex in orderedVertices)
            append("$vertex --- ${vertex.getNeighbors().joinToString(",")}\n")
        }

}




data class Vertex(val tag: String, val index: Int) {
    override fun toString(): String  = "($tag)"
}

typealias VertexTag = String
//data class VertexTag(val tag)

data class Edge(val start: Vertex, val end: Vertex) {
    data class Builder(val start: VertexTag, val end: VertexTag)
}
