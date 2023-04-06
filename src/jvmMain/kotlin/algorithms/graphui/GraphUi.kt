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
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import linearAlg.squared
import kotlin.math.*


@OptIn(ExperimentalTextApi::class)
@Composable
fun GraphUi(graph: Graph, modifier: Modifier = Modifier, paths: List<GraphPath> = listOf()) {
    validatePaths(graph, paths)
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier.fillMaxSize()) {
        require(size.height >= VertexRadius) { "Not enough vertical space was given to the graph (${size.height})" }
        val connectedComponents = graph.getConnectedComponents()
        val n = connectedComponents.size
        for ((i, component) in connectedComponents.withIndex()) {
            val padding = if (i == 0) 0f else 10f
            val componentWidth = size.width / n
            with(textMeasurer) {
                drawGraph(
                    component,
                    bounds = Rect(Offset(componentWidth * i + padding, 0f), Size(componentWidth - padding, size.height)),
                    paths = paths.relevantToComponent(component)
                )
            }

        }

    }
}

fun List<GraphPath>.relevantToComponent(component: Graph) : List<GraphPath> {
    TODO()
}


private fun validatePaths(graph: Graph, paths: List<GraphPath>) {
    for (path in paths) {
        graph.validatePath(path.map { it.tag })
    }
}

context(TextMeasurer)

fun DrawScope.drawGraph(graph: Graph, bounds: Rect, paths: List<GraphPath>) {
    val positions = chooseVertexPositions(graph)
        .mapValues { (_, pos) -> placeVertex(pos, bounds) }
    positions.forEach { (vertex, position) ->
        drawVertex(vertex, position)
    }

    val weights = if (graph.isWeighted) graph.asWeighted.weights else null

    // In a directed graph, group edges that have the same vertices but a flipped direction
    val edgePairs = graph.edges.groupByVertices()

    val topologicalGraph = graph.isDirected && graph.asDirected.isTopologicallySortable && !graph.isTree()

    edgePairs.forEachIndexed { i, (firstEdgeDirection, secondEdgeDirection) ->
        drawEdge(
            firstEdgeDirection,
            positions,
            directed = graph.isDirected,
            curve = when {
                // Topological graph: alternate between above and below arrows
                topologicalGraph -> {
                    if (i % 2 == 0) CurvedArrowType.Above else CurvedArrowType.Below
                }
                // One sided edge: Straight arrow
                secondEdgeDirection == null -> CurvedArrowType.None
                // Two sided edge: first edge is above, second edge is below
                else -> CurvedArrowType.Above
            },
            weight = weights?.getValue(firstEdgeDirection)
        )

        if (secondEdgeDirection != null) {
            drawEdge(
                secondEdgeDirection,
                positions,
                directed = graph.isDirected,
                // Topological graph can't have a two-sided edge (since it is acyclic),
                // so this just continues the last case in the previous drawEdge.
                curve = CurvedArrowType.Below,
                weight = weights?.getValue(secondEdgeDirection)
            )
        }
    }

}

private fun List<Edge>.groupByVertices(): List<Pair<Edge, Edge?>> = groupBy { setOf(it.start, it.end) }
    .map {
        val group = it.value
        if (group.size == 1) group[0] to null else group[0] to group[1]
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

context(TextContext)
fun DrawScope.drawCurvedArrowOnCircles(
    color: Color,
    start: Offset,
    end: Offset,
    up: Boolean,
    circleRadius: Float,
    text: String?
) {
    val offset = if (up) -circleRadius else circleRadius
    val circleEdgeStart = start + Offset(0f, offset)
    val circleEdgeEnd = end + Offset(0f, offset)
    drawCurvedArrow(color, circleEdgeStart, circleEdgeEnd, up, text)
}

/**
 * @param up if true will go above the start, otherwise will go below start
 */
context(TextContext)
fun DrawScope.drawCurvedArrow(color: Color, start: Offset, end: Offset, up: Boolean, text: String?) {
    // control point calculation isn't so good when start and end have the same X
    val controlPointX = (start.x + end.x) / 2
    val controlOffset = if (up) -200 else 200
    val controlPointY = (start.y + controlOffset).coerceIn(0f, size.height)
    val controlPoint = Offset(controlPointX, controlPointY)

    drawPath(Path().apply {
        moveTo(start.x, start.y)

        this.quadraticBezierTo(controlPointX, controlPointY, end.x, end.y)
    }, color, style = Stroke(width = 1f))

    drawArrowHead(color, origin = controlPoint, end)

    if (text != null) {
        val centerPoint = quadraticBezierPoint(start, end, controlPoint, fractionOfWay = 0.5f)
        drawTextBetweenTwoPoints(start = start, end = end, center = centerPoint, text)
    }
}

// I just asked chatGPT and this is what it gave. It works xd
private fun quadraticBezierPoint(start: Offset, end: Offset, control: Offset, fractionOfWay: Float): Offset {
    return start * (1 - fractionOfWay).squared() +
            control * 2f * (1 - fractionOfWay) * fractionOfWay +
            end * fractionOfWay.squared()
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

enum class CurvedArrowType {
    None,
    Above,
    Below
}

context(TextContext)
fun DrawScope.drawEdge(
    edge: Edge,
    vertexPositions: Map<Vertex, Offset>,
    directed: Boolean,
    curve: CurvedArrowType,
    weight: Int?,
) {
    val start = vertexPositions[edge.start]
        ?: error("Vertex ${edge.start} was not positioned, positioned: ${vertexPositions.keys}")
    val end =
        vertexPositions[edge.end] ?: error("Vertex ${edge.end} was not positioned, positioned: ${vertexPositions.keys}")

    val startToVertex = shortenedLine(end, start, shortenBy = VertexRadius)
    val endToVertex = shortenedLine(start, end, shortenBy = VertexRadius)

    val text = weight?.toString()

    when {
        curve != CurvedArrowType.None -> drawCurvedArrowOnCircles(
            Color.Black,
            start,
            end,
            up = curve == CurvedArrowType.Above,
            circleRadius = VertexRadius,
            text
        )

        directed -> drawArrow(Color.Black, startToVertex, endToVertex, text)
        else -> drawLine(Color.Black, start = startToVertex, end = endToVertex, text)
    }
}

context (TextContext)
private fun DrawScope.drawLine(color: Color, start: Offset, end: Offset, label: String?) {
    drawLine(color, start, end)
    if (label != null) {
        val center = (start + end) / 2f - Offset(0f, 2f)
        drawTextBetweenTwoPoints(start, end, center, label)
    }
}

context(TextContext)
private fun DrawScope.drawTextBetweenTwoPoints(
    start: Offset,
    end: Offset,
    center: Offset,
    label: String
) {
    val angle = angleBetweenTwoPointsRad(start, end)
    val upsideDownRange = 3 / 4f * PI..5 / 4f * PI
    // If the angle causes the text to be upside down, flip it by adding pi
    val actualAngle = if (angle in upsideDownRange) angle + PI.toFloat() else angle
    rotateRad(actualAngle, pivot = center) {
        drawText(center, label, textStyle = TextStyle(fontSize = 24.sp))
    }
}

// see https://www.quora.com/How-would-you-find-the-angle-of-a-line-given-two-points-on-a-coordinate-plan
private fun angleBetweenTwoPointsRad(a: Offset, b: Offset): Float {
    return atan2(b.y - a.y, b.x - a.x)
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
    drawText(center = position, text = vertex.name)
}


context (TextContext)
fun DrawScope.drawText(center: Offset, text: String, textStyle: TextStyle = TextStyle.Default) {
    val width = text.length * 8f
    val pxSize = if (textStyle.fontSize.type == TextUnitType.Sp) textStyle.fontSize.toPx() else 8f
    drawText(
        this@TextContext,
        topLeft = center - Offset(width / 2f, pxSize),
        text = text,
        style = textStyle.copy(fontFamily = FontFamily.Monospace)
    )
}


@OptIn(ExperimentalTextApi::class)
typealias TextContext = TextMeasurer