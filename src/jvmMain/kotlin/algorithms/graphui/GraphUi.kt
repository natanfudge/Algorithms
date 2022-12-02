@file:OptIn(ExperimentalTextApi::class, ExperimentalTextApi::class, ExperimentalTextApi::class)

package algorithms.graphui

import algorithms.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Returns offset values from (0,0) to (1,1), (0,0) being top left and (1,1) being bottom right

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

fun DrawScope.drawGraph(graph: Graph, bounds: Rect) {
    val positions = chooseVertexPositions(graph)
        .mapValues { (_, pos) -> placeVertex(pos, bounds) }
    positions.forEach { (vertex, position) ->
        drawVertex(vertex, position)
    }

    val weights = if(graph.isWeighted) graph.asWeighted.weights else null

    for ((i, edge) in graph.edges.withIndex()) {
        drawEdge(
            edge,
            positions,
            directed = graph.isDirected,
            graph.isDirected && graph.asDirected.isTopologicallySortable && !graph.isTree(),
            i,
            weight = weights?.getValue(edge)
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

context (TextContext)
fun DrawScope.drawArrow(color: Color, start: Offset, end: Offset, text: String?) {
    drawLine(color, start, end, text)

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

context(TextContext)
fun DrawScope.drawEdge(
    edge: Edge,
    vertexPositions: Map<Vertex, Offset>,
    directed: Boolean,
    topologicallySortable: Boolean,
    index: Int,
    weight: Int?,
    ) {
    val start = vertexPositions[edge.start]?: error("Vertex ${edge.start} was not positioned, positioned: ${vertexPositions.keys}")
    val end = vertexPositions[edge.end] ?: error("Vertex ${edge.end} was not positioned, positioned: ${vertexPositions.keys}")

    val startToVertex = shortenedLine(end, start, shortenBy = VertexRadius)
    val endToVertex = shortenedLine(start, end, shortenBy = VertexRadius)

    val text = weight?.toString()

    when {
        topologicallySortable -> drawCurvedArrowOnCircles(
            Color.Black,
            start,
            end,
            up = index % 2 == 0,
            circleRadius = VertexRadius
        )

        directed -> drawArrow(Color.Black, startToVertex, endToVertex, text)
        else -> drawLine(Color.Black, start = startToVertex, end = endToVertex, text)
    }
}
context (TextContext)
private fun DrawScope.drawLine(color: Color, start: Offset, end: Offset, label: String?) {
    drawLine(color,start,end)
    if(label != null){
        val center = (start + end) / 2f
        drawText(center,label)
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
private fun DrawScope.drawVertex(vertex: Vertex, position: Offset) {
    drawCircle(vertex.color, center = position, radius = VertexRadius, style = Stroke(width = 1f))
    drawText(center = position , text = vertex.name)
}


context (TextContext)
fun DrawScope.drawText(center: Offset, text: String) {
    val width = text.length * 7
    drawText(this@TextContext, topLeft = center - Offset(width / 2f, 8f), text = text)
}


@OptIn(ExperimentalTextApi::class)
typealias TextContext = TextMeasurer