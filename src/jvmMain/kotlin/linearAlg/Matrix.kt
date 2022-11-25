package linearAlg

import java.util.*

typealias TwoDimArray<T> = List<List<T>>
typealias MutableTwoDimArray<T> = MutableList<MutableList<T>>

sealed class Matrix<out T> private constructor(private val _rows: TwoDimArray<T>) {
    class NonSquare<out T>(_rows: TwoDimArray<T>) : Matrix<T>(_rows)
    class Square<out T> (_rows: TwoDimArray<T>): Matrix<T>(_rows) {
        init {
            require(height == width)
        }
    }

    companion object {
        fun identity(order: Int) = IndexBuilder.Square<Int>(order).also { builder ->
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


    sealed class IndexBuilder<T, M: Matrix<T>>(val height: Int, val width: Int) {
        protected abstract fun construct(rows: TwoDimArray<T>): M
        class Any<T>(height: Int, width: Int) : IndexBuilder<T, Matrix<T>>(height,width) {
            override fun construct(rows: TwoDimArray<T>): Matrix<T> = if(height == width) Square(rows)
            else NonSquare(rows)
        }
        class Square<T>(val order: Int) : IndexBuilder<T,Matrix.Square<T>>(order,order) {
            override fun construct(rows: TwoDimArray<T>): Matrix.Square<T>  = Square(rows)
        }

        private val array: MutableTwoDimArray<T?> = MutableList(height) { MutableList(width) { null } }
        operator fun set(row: Int, column: Int, value: T) {
            require(row < height)
            require(column < width)
            array[row][column] = value
        }

        fun build(): M {
            val mappedToScalar = array.mapIndexed { rowNum, row ->
                row.mapIndexed { colNum, col ->
                    requireNotNull(col) { "Value was not given to item at ($rowNum,$colNum)" }
                }
            }
            return construct(mappedToScalar)
        }
    }

    sealed class RowBuilder<T, M: Matrix<T>> {
        protected val rows: MutableList<Series<T>> = mutableListOf()

         abstract fun build(): M
        class Any<T> : RowBuilder<T, Matrix<T>>() {
            override fun build(): Matrix<T> = if(rows.size  == rows[0].size) Square(rows)
            else NonSquare(rows)
        }
        class Square<T> : RowBuilder<T,Matrix.Square<T>>() {
            override fun build(): Matrix.Square<T>  = Square(rows)
        }

        fun row(vararg values: T) {
            if (rows.isNotEmpty()) require(values.size == rows[0].size)
            rows.add(Series(values.toList()))
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

fun <T> matrix(builder: Matrix.RowBuilder.Any<T>.() -> Unit): Matrix<T> = Matrix.RowBuilder.Any<T>().apply(builder).build()
fun <T> squareMatrix(builder: Matrix.RowBuilder.Square<T>.() -> Unit): Matrix.Square<T> = Matrix.RowBuilder.Square<T>().apply(builder).build()