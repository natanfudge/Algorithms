package linearAlg

typealias TwoDimArray<T> = List<List<T>>
typealias MutableTwoDimArray<T> = MutableList<MutableList<T>>

class Matrix<T> private constructor(private val array: TwoDimArray<T>) {
    init {
        require(array.isNotEmpty())
        require(array[0].isNotEmpty())
        val width = array[0].size
        array.forEach { require(it.size == width) }
    }

    val height = array.size
    val width = array[0].size

    operator fun get(row: Int, column: Int) = array[row][column]

    val rows: List<Series<T>> = array.map { Series(it) }
    val columns: List<Series<T>> = List(width) { colIndex ->
        Series(List(height) { rowIndex ->
            array[rowIndex][colIndex]
        })
    }


    class Builder<T>(val width: Int, val height: Int) {
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

    override fun toString(): String {
        val ceiling = "┏" + "━┳".repeat(width - 1) + "━┓\n"
        val intermediateFloor = "┣" + "━╋".repeat(width - 1) + "━┫\n"
        val floor = "┗" + "━┻".repeat(width - 1) + "━┛"
        return buildString {
            append(ceiling)
            for ((i, row) in array.withIndex()) {
                val rowString = "┃" + row.joinToString("┃") + "┃\n"
                append(rowString)
                if (i != array.size - 1) append(intermediateFloor)
            }
            append(floor)
        }
    }
}
