// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import logic.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
@Preview
fun App() {

    // (1.0, 0.0, 0.0)
    val a = Color.Red
    // (0.0, 1.0, 0.0)
    val b = Color.Green
    // (0.0, 0.0, 1.0)
    val c = Color.Blue
    // (0.0, 0.0, 0.0)
    val d = Color.Black
    // (0.0, 1.0, 1.0)
    val e = Color.Cyan
    // (1.0, 1.0, 0.0)
    val f = Color.Yellow

    val a2 = Color.Red.copy(alpha = 0.5f)
    val b2 = Color.Green.copy(alpha = 0.5f)
    val c2 = Color.Blue.copy(alpha = 0.5f)
    val d2 = Color.Black.copy(alpha = 0.5f)
    val e2 = Color.Cyan.copy(alpha = 0.5f)
    val f2 = Color.Yellow.copy(alpha = 0.5f)

    val a3 = Color.Red.copy(alpha = 0.2f)
    val b3 = Color.Green.copy(alpha = 0.2f)

    val graph = Graph.Builder(directed = true).apply {
        a edgeTo b
        b edgeTo c
        c edgeTo d
        d edgeTo e
        e edgeTo f
        b edgeTo a
        c edgeTo b
        b edgeTo f

        a2 edgeTo b2
        b2 edgeTo c2
        c2 edgeTo a2
        d2 edgeTo a2
        e2 edgeTo c2
        f2 edgeTo e2

        a3 edgeTo b3
    }.build()



    Column {
        GraphUi(graph, Modifier.padding(10.dp).weight(1f))
        GraphUi( graph.bfs(graph.vertices.first { it.tag == c }), Modifier.padding(10.dp).weight(1f))
    }

}

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right
//TODO: special placement for tree graphs
private fun Graph.chooseVertexPositions(): Map<Vertex,Offset> {
    return if(isTree()) treePositions() else regularGraphPositions()
}

private fun Graph.treePositions(): Map<Vertex,Offset> {
    val layers = mutableListOf(listOf(root()))
    while (layers.sumOf { it.size } < vertices.size){
        layers.add(layers.last().flatMap { neighborsOf(it) })
    }

    val positions=  mutableMapOf<Vertex, Offset>()

    val depth = layers.size
    for((layerIndex, layerVertices) in layers.withIndex()) {
        val yOffset = (layerIndex + 1f) / (depth + 1f)
        val layerWidth = layerVertices.size
        for((vertexInLayerIndex, vertexInLayer) in layerVertices.withIndex()){
            val xOffset = (vertexInLayerIndex + 1f) / (layerWidth + 1f)
            positions[vertexInLayer] = Offset(xOffset,yOffset)
        }
    }

    return positions
}

private fun Graph.regularGraphPositions(): Map<Vertex,Offset> {
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
        for((i, position) in positionList.withIndex()){
            put(vertices[i], position)
        }
    }
}


@Composable
fun GraphUi(graph: Graph, modifier: Modifier = Modifier) = Canvas(modifier.fillMaxSize()) {
    val connectedComponents = graph.getConnectedComponents()
    val n = connectedComponents.size
    for ((i, component) in connectedComponents.withIndex()) {
        val padding = if(i == 0) 0f else 10f
        val componentWidth = size.width / n
        drawGraph(
            component,
            bounds = Rect(Offset(componentWidth * i + padding, 0f), Size(componentWidth - padding, size.height))
        )
    }

}

fun DrawScope.drawGraph(graph: Graph, bounds: Rect) {
    val positions = graph.chooseVertexPositions()
        .mapValues {(_, pos) -> placeVertex(pos, bounds) }
    positions.forEach { (vertex, position) ->
        drawVertex(vertex, position)
    }

    for (edge in graph.edges) {
        drawEdge(edge, positions, directed = graph.isDirected)
    }

}

private fun Offset.length() = sqrt(x * x + y * y)
private fun Offset.normalized() = this / length()

private fun Offset.rotate(radians: Float): Offset {
    val x2 = cos(radians) * x - sin(radians) * y
    val y2 = sin(radians) * x + cos(radians) * y
    return Offset(x2, y2)
}


fun DrawScope.drawArrow(color: Color, start: Offset, end: Offset) {
    drawLine(color, start, end)

    val endOriginalized = end - start
    val normalized = endOriginalized.normalized()
    val length = 30f
    val line = normalized * length
    val directedLineA = line.rotate(2 * PI.toFloat() * (5f / 8f))
    val directedLineB = line.rotate(2 * PI.toFloat() * (3f / 8f))
    val positionedLineA = directedLineA + end
    val positionedLineB = directedLineB + end
    drawLine(color, end, positionedLineA)
    drawLine(color, end, positionedLineB)
}
const val VertexRadius = 20f


fun DrawScope.drawEdge(edge: Edge, vertexPositions: Map<Vertex,Offset>, directed: Boolean) {
    val start = vertexPositions[edge.start]!!
    val end = vertexPositions[edge.end]!!

    val startToVertex = shortenedLine(end, start, shortenBy = VertexRadius)
    val endToVertex = shortenedLine(start, end, shortenBy = VertexRadius)

    if (directed) {
        drawArrow(Color.Black, startToVertex, endToVertex)
    } else {
        drawLine(Color.Black, start = startToVertex, end = endToVertex)
    }
}

private fun shortenedLine(start: Offset, end: Offset, shortenBy: Float): Offset {
    val originalized = end - start
    val normalized = originalized.normalized()
    val shortVector = normalized * (originalized.length() - shortenBy)
    return shortVector + start
}


private fun placeVertex(relativePosition: Offset, bounds: Rect): Offset {
    val width = bounds.width
    val height = bounds.height
    val initialPositionX = bounds.left + relativePosition.x * width
    val initialPositionY = bounds.top + relativePosition.y * height

    // Make it so vertex doesn't clip out of the bounding box
    val positionX = initialPositionX.coerceIn(bounds.left + VertexRadius, bounds.left + width - VertexRadius)
    val positionY = initialPositionY.coerceIn(bounds.top + VertexRadius, bounds.top + height - VertexRadius)

    return Offset(positionX, positionY)
}

private fun DrawScope.drawVertex(vertex: Vertex, position: Offset) {
    drawCircle(vertex.tag, center = position, radius = VertexRadius)
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
