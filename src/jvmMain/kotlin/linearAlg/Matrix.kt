package linearAlg

import java.util.*

typealias TwoDimArray<T> = List<List<T>>
typealias MutableTwoDimArray<T> = MutableList<MutableList<T>>

class Matrix<out T> private constructor(private val _rows: TwoDimArray<T>) {
    companion object {
        fun identity(order: Int) = IndexBuilder<Int>(order, order).also { builder ->
            repeat(order) { i ->
                repeat(order) { j ->
                    if (i == j) builder[i, j] = 1
                    else builder[i, j] = 0
                }
            }
        }.build()
    }

    init {
        require(_rows.isNotEmpty())
        require(_rows[0].isNotEmpty())
        val width = _rows[0].size
        _rows.forEach { require(it.size == width) }
    }

    val height = _rows.size
    val width = _rows[0].size

    operator fun get(row: Int, column: Int) = _rows[row][column]

    val rows: List<Series<T>> = _rows.map { Series(it) }
    val columns: List<Series<T>> = List(width) { colIndex ->
        Series(List(height) { rowIndex ->
            _rows[rowIndex][colIndex]
        })
    }


    class IndexBuilder<T>(val height: Int, val width: Int) {
        private val array: MutableTwoDimArray<T?> = MutableList(height) { MutableList(width) { null } }
        operator fun set(row: Int, column: Int, value: T) {
            require(row < height)
            require(column < width)
            array[row][column] = value
        }

        fun build(): Matrix<T> {
            val mappedToScalar = array.mapIndexed { rowNum, row ->
                row.mapIndexed { colNum, col ->
                    requireNotNull(col) { "Value was not given to item at ($rowNum,$colNum)" }
                }
            }
            return Matrix(mappedToScalar)
        }
    }

    class RowBuilder<T> {
        private val rows: MutableList<Series<T>> = mutableListOf()

        fun row(vararg values: T) {
            if (rows.isNotEmpty()) require(values.size == rows[0].size)
            rows.add(Series(values.toList()))
        }

        fun build(): Matrix<T> {
            return Matrix(rows)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Matrix<*> && this.rows == other.rows
    }

    override fun hashCode(): Int {
        return Objects.hash(*rows.toTypedArray())
    }

    override fun toString(): String {
        val ceiling = "┏" + floorString('┳', '┓')
        val intermediateFloor = "┣" + floorString('╋', '┫')
        val floor = "┗" + floorString('┻', '┛')
        return buildString {
            append(ceiling)
            for ((i, row) in _rows.withIndex()) {
                append(rowString(row))
                if (i != _rows.size - 1) append(intermediateFloor)
            }
            append(floor)
        }
    }

    private fun rowString(row: List<T>) = buildString {
        append('┃')
        for ((i, item) in row.withIndex()) {
            val space = columnWidth(i) - item.toString().length
            append(" ".repeat(space / 2))
            append(item)
            append(" ".repeat(space / 2))
            if (space % 2 == 1) append(" ")
            append('┃')
        }
        append('\n')
    }

    private fun floorString(edge: Char, ending: Char) = buildString {
        for (colIndex in columns.indices) {
            val suffix = if (colIndex < columns.size - 1) edge else ending
            val width = columnWidth(colIndex)
            append("━".repeat(width) + suffix)
        }
        append('\n')
    }

    private fun columnWidth(column: Int): Int {
        return columns[column].maxOf { it.toString().length }
    }
}

fun <T> matrix(builder: Matrix.RowBuilder<T>.() -> Unit) = Matrix.RowBuilder<T>().apply(builder).build()