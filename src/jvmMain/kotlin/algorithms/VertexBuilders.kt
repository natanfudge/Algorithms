package algorithms

import androidx.compose.ui.graphics.Color

private val baseColors = listOf(
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

private val colors = baseColors + baseColors.map { it.copy(alpha = 0.5f) }

fun vertices(vararg vertexNames: String): List<VertexTag> {
    return vertexNames.mapIndexed { index, s -> VertexTag(colors[index], s) }
}

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]
operator fun <T> List<T>.component9() = this[8]
operator fun <T> List<T>.component10() = this[9]
operator fun <T> List<T>.component11() = this[10]
operator fun <T> List<T>.component12() = this[11]
operator fun <T> List<T>.component13() = this[12]
operator fun <T> List<T>.component14() = this[13]
operator fun <T> List<T>.component15() = this[14]