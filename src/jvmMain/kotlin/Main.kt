// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import logic.Edge
import logic.Graph
import logic.bfs

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }


    val a = "a"
    val b ="b"
    val c = "c"
    val graph = Graph.Builder(directed = false).apply {
        addVertices(a, b, c)

        a edgeTo b
        b edgeTo c
        c edgeTo a
    }.build()

    //TODO: doesn't seem like changing the root changes the output?
    val newGraph = graph.bfs(root = graph.vertices[1])



    Column {
        GraphUi(graph, Modifier.padding(10.dp).weight(1f))
        GraphUi(newGraph, Modifier.padding(10.dp).weight(1f))
    }



//    MaterialTheme {
//        Button(onClick = {
//            text = "Hello, Desktop!"
//        }) {
//            Text(text)
//        }
//    }
}

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right
private fun Graph.chooseVertexPositions(): List<Offset> = when(vertices.size){
    0 -> listOf()
    1 -> listOf(Offset(0.5f,0.5f))
    2 -> listOf(Offset(0.0f,0.5f), Offset(1f,0.5f))
    3 -> listOf(Offset(0.5f,0f), Offset(0f,1f), Offset(1f, 1f))
    4 -> listOf(Offset(0f,0f), Offset(1f,0f), Offset(0f,1f), Offset(1f,1f))
    else -> TODO()
}

@Composable
fun GraphUi(graph: Graph, modifier: Modifier = Modifier) = Canvas(modifier.fillMaxSize()){


    val positions = graph.chooseVertexPositions()
        .map { placeVertex(it) }
    for(position in positions){
        drawVertex(position)
    }

    if(graph.isDirected){
        TODO()
    } else {
        for(edge in graph.edges){
            drawEdge(edge, positions)
        }
    }
}

fun DrawScope.drawEdge(edge: Edge, vertexPositions: List<Offset>) {
    val start = vertexPositions[edge.start.index]
    val end = vertexPositions[edge.end.index]

    drawLine(Color.Black,start = start, end = end)
}


const val VertexRadius = 40f

private fun DrawScope.placeVertex(relativePosition: Offset): Offset {
    val width = size.width
    val height = size.height
    val initialPositionX = relativePosition.x * width
    val initialPositionY = relativePosition.y * height

    // Make it so vertex doesn't clip out of the bounding box
    val positionX = initialPositionX.coerceIn(0 + VertexRadius, width - VertexRadius)
    val positionY = initialPositionY.coerceIn(0 + VertexRadius, height - VertexRadius)

    return Offset(positionX, positionY)
}

private fun DrawScope.drawVertex(position: Offset) {
    drawCircle(Color.Blue, center = position, radius = VertexRadius)
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
