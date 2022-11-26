package linearAlg.linearspace

import linearAlg.*

context (Field<T>)
fun <T> Matrix<T>.forEach(iterator: (item: T) -> Unit) {
    for (row in rows) {
        for (item in row) {
            iterator(item)
        }
    }
}

fun <T> Matrix<T>.forEachIndexed(iterator: (row: Int, column: Int, item: T) -> Unit) {
    for ((i, row) in rows.withIndex()) {
        for ((j, item) in row.withIndex()) {
            iterator(i, j, item)
        }
    }
}


fun <T, M : Matrix<T>> M.mapIndexed(map: (row: Int, column: Int, item: T) -> T): M =
    Matrix.IndexBuilder.Any<T>(height, width)
        .also { builder ->
            forEachIndexed { row, column, item ->
                builder[row, column] = map(row, column, item)
            }
        }.build() as M
fun <T, M : Matrix<T>> M.map(map: (item: T) -> T): M = mapIndexed { _, _, item -> map(item)}


context (Field<T>)
fun <T> Matrix<T>.multiply(other: Matrix<T>): Matrix<T> {
    require(this.width == other.height)
    val result = Matrix.IndexBuilder.Any<T>(this.height, other.width)
    for ((i, row) in rows.withIndex()) {
        for ((j, column) in other.columns.withIndex()) {
            result[i, j] = row.dotProduct(column)
        }
    }
    return result.build()
}

context(Field<T>)
fun <T, M : Matrix<T>> M.multiplySquare(other: M): M = multiply(other) as M

context (Field<T>)
fun <T> Matrix.Square<T>.determinant(): T {
    val size = width
    when (size) {
        1 -> return this[0, 0]
        2 -> return this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
        else -> {
            return List(size) { i ->
                (this[0, i] * minor(0, i).determinant()).negativeOnOddIndex(i)
            }.sum()
        }
    }
}


fun <T> Matrix.Square<T>.minor(removedRow: Int, removedColumn: Int): Matrix.Square<T> {
    require(width == height)
    val size = width
    require(size >= 2)
    val result = Matrix.IndexBuilder.Square<T>(size - 1)
    forEachIndexed { row, column, item ->
        if (row == removedRow || column == removedColumn) return@forEachIndexed
        val insertedRow = if (row < removedRow) row else row - 1
        val insertedColumn = if (column < removedColumn) column else column - 1
        result[insertedRow, insertedColumn] = item
    }
    return result.build()
}

fun <T, M : Matrix<T>> M.transposed(): M = Matrix.IndexBuilder.Any<T>(width, height).apply {
    forEachIndexed { i, j, item ->
        this[j, i] = item
    }
}.build() as M


context (Field<T>)
fun <T> Matrix.Square<T>.adjugate() = mapIndexed { i, j, _ ->
    minor(j, i).determinant().negativeOnOddIndex(i + j)
}

context (Field<T>)
fun <T> Matrix.Square<T>.inverse(): Matrix.Square<T>? {
    val det = determinant()
    if (det == Zero) return null
    val inverse = (Identity / det)
    return inverse * adjugate()
}