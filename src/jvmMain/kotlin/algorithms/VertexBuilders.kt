package algorithms

import androidx.compose.ui.graphics.Color

private val colors = listOf(
    Color.Black,
    Color.Blue,
    Color.Cyan,
    Color.Red,
    Color.Green,
    Color.DarkGray,
    Color.Gray,
    Color.LightGray,
    Color.Magenta,
    Color.Yellow
)

fun vertices(vararg vertexNames: String): List<VertexTag> {
    return vertexNames.mapIndexed { index, s -> VertexTag(colors[index], s) }
}

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]