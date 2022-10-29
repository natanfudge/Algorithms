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

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }


    val a = Color.Red
    val b = Color.Green
    val c = Color.Blue
    val d = Color.Black
    val e = Color.Cyan
    val f = Color.Yellow

    val a2 = Color.Red.copy(alpha = 0.5f)
    val b2 = Color.Green.copy(alpha = 0.5f)
    val c2 = Color.Blue.copy(alpha = 0.5f)
    val d2 = Color.Black.copy(alpha = 0.5f)
    val e2 = Color.Cyan.copy(alpha = 0.5f)
    val f2 = Color.Yellow.copy(alpha = 0.5f)

    val a3 = Color.Red.copy(alpha = 0.2f)
    val b3 = Color.Green.copy(alpha = 0.2f)

    val graph = Graph.Builder(directed = false).apply {
        a edgeTo b
        b edgeTo c
        c edgeTo a
        d edgeTo a
        e edgeTo c
        f edgeTo e

        a2 edgeTo b2
        b2 edgeTo c2
        c2 edgeTo a2
        d2 edgeTo a2
        e2 edgeTo c2
        f2 edgeTo e2

        a3 edgeTo b3
    }.build()

    //TODO: doesn't seem like changing the root changes the output?
    val newGraph = graph.bfs(root = graph.vertices[1])



    Column {
        GraphUi(graph, Modifier.padding(10.dp).weight(1f))
        GraphUi(newGraph, Modifier.padding(10.dp).weight(1f))
    }

}

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right
private fun Graph.chooseVertexPositions(): List<Offset> {
    return when (vertices.size) {
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
}

//private fun Graph.chooseComponentVertexPositions(): List<Offset> {
//    return when(vertices.size){
//        0 -> listOf()
//        1 -> listOf(Offset(0.5f,0.5f))
//        2 -> listOf(Offset(0.0f,0.5f), Offset(1f,0.5f))
//        3 -> listOf(Offset(0.5f,0f), Offset(0f,1f), Offset(1f, 1f))
//        4 -> listOf(Offset(0f,0f), Offset(1f,0f), Offset(0f,1f), Offset(1f,1f))
//        else -> List(vertices.size) { i ->
//            val degree = 2.0 * PI * i / vertices.size
//            Offset(cos(degree).toFloat(), sin(degree).toFloat()) / 2F + Offset(0.5f, 0.5f)
//        }
//    }
//}


@Composable
fun GraphUi(graph: Graph, modifier: Modifier = Modifier) = Canvas(modifier.fillMaxSize()) {
    val connectedComponents = graph.getConnectedComponents()
    val n = connectedComponents.size
    for ((i, component) in graph.getConnectedComponents().withIndex()) {
        val padding = if(i == 0) 0f else 10f
        val componentWidth = size.width / n
        drawGraph(
            component,
            bounds = Rect(Offset(componentWidth * i + padding , 0f), Size(componentWidth - padding, size.height))
        )
    }

}

fun DrawScope.drawGraph(graph: Graph, bounds: Rect) {
    val positions = graph.chooseVertexPositions()
        .map { placeVertex(it, bounds) }
    for ((position, vertex) in positions.zip(graph.vertices)) {
        drawVertex(vertex, position)
    }

    if (graph.isDirected) {
        TODO()
    } else {
        for (edge in graph.edges) {
            drawEdge(edge, positions)
        }
    }
}

fun DrawScope.drawEdge(edge: Edge, vertexPositions: List<Offset>) {
    val start = vertexPositions[edge.start.index]
    val end = vertexPositions[edge.end.index]

    drawLine(Color.Black, start = start, end = end)
}


const val VertexRadius = 40f

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
