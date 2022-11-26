package algorithms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right
private fun Graph.chooseVertexPositions(): Map<Vertex, Offset> {
    return when {
        isTree() -> treePositions()
        this.isDirected && (this as DirectedGraph).isTopologicallySortable -> topologicalSortPositions()
        else -> regularGraphPositions()
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
//TODO: weighted graph positioning

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

//context(TextMeasurer)
        @OptIn(ExperimentalTextApi::class)
@Composable
fun GraphUi(graph: Graph, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier.fillMaxSize()) {
        require(size.height >= VertexRadius){"Not enough vertical space was given to the graph (${size.height})"}
        val connectedComponents = graph.getConnectedComponents()
        val n = connectedComponents.size
        for ((i, component) in connectedComponents.withIndex()) {
            val padding = if (i == 0) 0f else 10f
            val componentWidth = size.width / n
            with(textMeasurer){
                drawGraph(
                    component,
                    bounds = Rect(Offset(componentWidth * i + padding, 0f), Size(componentWidth - padding, size.height))
                )
            }

        }

    }
}
context(TextMeasurer)
        @OptIn(ExperimentalTextApi::class)
fun DrawScope.drawGraph(graph: Graph, bounds: Rect) {

//    drawCurvedArrow(Color.Red, Offset(20f, 100f), Offset(300f, 100f))

    val positions = graph.chooseVertexPositions()
        .mapValues { (_, pos) -> placeVertex(pos, bounds) }
    positions.forEach { (vertex, position) ->
        drawVertex(vertex, position)
    }

    for ((i, edge) in graph.edges.withIndex()) {
        drawEdge(
            edge,
            positions,
            directed = graph.isDirected,
            graph.isDirected && (graph as DirectedGraph).isTopologicallySortable && !graph.isTree(),
            i
        )
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

    drawArrowHead(color, start, end)
}

fun DrawScope.drawCurvedArrowOnCircles(color: Color, start: Offset, end: Offset, up: Boolean, circleRadius: Float) {
    val offset = if (up) -circleRadius else circleRadius
    val circleEdgeStart = start + Offset(0f, offset)
    val circleEdgeEnd = end + Offset(0f, offset)
    drawCurvedArrow(color, circleEdgeStart, circleEdgeEnd, up)
}

/**
 * @param up if true will go above the start, otherwise will go below start
 */
fun DrawScope.drawCurvedArrow(color: Color, start: Offset, end: Offset, up: Boolean) {
    val controlPointX = (start.x + end.x) / 2
    val controlOffset = if (up) -200 else 200
    val controlPointY =  (start.y + controlOffset).coerceIn(0f, size.height)

    drawPath(Path().apply {
        moveTo(start.x, start.y)

        this.quadraticBezierTo(controlPointX, controlPointY, end.x, end.y)
    }, color, style = Stroke(width = 1f))

    drawArrowHead(color, origin = Offset(controlPointX, controlPointY), end)
}

fun DrawScope.drawArrowHead(color: Color, origin: Offset, end: Offset) {
    val endOriginalized = end - origin
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


fun DrawScope.drawEdge(
    edge: Edge,
    vertexPositions: Map<Vertex, Offset>,
    directed: Boolean,
    topologicallySortable: Boolean,
    index: Int
) {
    val start = vertexPositions[edge.start]!!
    val end = vertexPositions[edge.end] ?: error("Vertex ${edge.end} was not positioned, positioned: ${vertexPositions.keys}")

    val startToVertex = shortenedLine(end, start, shortenBy = VertexRadius)
    val endToVertex = shortenedLine(start, end, shortenBy = VertexRadius)

    when {
        topologicallySortable -> drawCurvedArrowOnCircles(
            Color.Black,
            start,
            end,
            up = index % 2 == 0,
            circleRadius = VertexRadius
        )

        directed -> drawArrow(Color.Black, startToVertex, endToVertex)
        else -> drawLine(Color.Black, start = startToVertex, end = endToVertex)
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
context(TextContext)
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawVertex(vertex: Vertex, position: Offset) {
    drawCircle(vertex.color, center = position, radius = VertexRadius, style = Stroke(width = 1f))
    val width = vertex.name.length * 7
    drawText(this@TextContext, topLeft = position - Offset(width / 2f, 8f), text = vertex.name)
}

@OptIn(ExperimentalTextApi::class)
typealias TextContext = TextMeasurer