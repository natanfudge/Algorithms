package algorithms.graphui

import algorithms.*
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right
 fun chooseVertexPositions(graph: Graph): Map<Vertex, Offset> {
    return when {
        graph.isTree() -> graph.treePositions()
        graph.isDirected && (graph as DirectedGraph).isTopologicallySortable -> graph.topologicalSortPositions()
        else -> graph.regularGraphPositions()
    }
}

private fun DirectedGraph.topologicalSortPositions(): Map<Vertex, Offset> {
    val sorted = topologicalSort!!
    val n = sorted.size
    return sorted.mapIndexed { i, vertex ->
        vertex to Offset((i) / (n - 1f), 0.5f)
    }.toMap()
}

private fun Graph.treePositions(): Map<Vertex, Offset> {
    val layers = mutableListOf(listOf(root()))
    while (layers.sumOf { it.size } < vertices.size) {
        layers.add(layers.last().flatMap { neighborsOf(it) })
    }

    val positions = mutableMapOf<Vertex, Offset>()

    val depth = layers.size
    for ((layerIndex, layerVertices) in layers.withIndex()) {
        val yOffset = (layerIndex + 1f) / (depth + 1f)
        val layerWidth = layerVertices.size
        for ((vertexInLayerIndex, vertexInLayer) in layerVertices.withIndex()) {
            val xOffset = (vertexInLayerIndex + 1f) / (layerWidth + 1f)
            positions[vertexInLayer] = Offset(xOffset, yOffset)
        }
    }

    return positions
}

private fun Graph.regularGraphPositions(): Map<Vertex, Offset> {
    val positionList = when (vertices.size) {
        0 -> listOf()
        1 -> listOf(Offset(0.5f, 0.5f))
        2 -> listOf(Offset(0.0f, 0.5f), Offset(1f, 0.5f))
        3 -> listOf(Offset(0.5f, 0f), Offset(0f, 1f), Offset(1f, 1f))
        4 -> listOf(Offset(0f, 0f), Offset(1f, 0f), Offset(0f, 1f), Offset(1f, 1f))
        else -> List(vertices.size) { i ->
            val degree = 2.0 * PI * i / vertices.size
            Offset(cos(degree).toFloat(), sin(degree).toFloat()) / 2F + Offset(0.5f, 0.5f)
        }
    }

    return buildMap {
        for ((i, position) in positionList.withIndex()) {
            put(vertices[i], position)
        }
    }
}
