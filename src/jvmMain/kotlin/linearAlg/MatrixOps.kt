package linearAlg

context (Field<T>)
fun <T> Matrix<T>.forEach(iterator: (item: T) -> Unit) {
    for (row in rows) {
        for (item in row) {
            iterator(item)
        }
    }
}
context (Field<T>)
fun <T> Matrix<T>.forEachIndexed(iterator: (row: Int, column: Int, item: T) -> Unit) {
    for ((i, row) in rows.withIndex()) {
        for ((j, item) in row.withIndex()) {
            iterator(i, j, item)
        }
    }
}

context (Field<T>)
fun <T> Matrix<T>.multiply(other: Matrix<T>): Matrix<T> {
    require(this.width == other.height)
    val result = Matrix.IndexBuilder<T>(this.height, other.width)
    for ((i, row) in rows.withIndex()) {
        for ((j, column) in other.columns.withIndex()) {
            result[i, j] = row.dotProduct(column)
        }
    }
    return result.build()
}

context (Field<T>)
fun <T> Matrix<T>.determinant(): T {
    require(width == height)
    val size = width
    when (size) {
        1 -> return this[0, 0]
        2 -> return this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
        else -> {

        }
    }
    TODO()
}

//context (Field<T>)
//fun <T> Matrix<T>.minor(removedRow: Int, removedColumn: Int): Matrix<T> {
//    require(width == height)
//    val size = width
//    require(size >= 2)
//    val result = Matrix.IndexBuilder<T>(size - 1, size - 1)
//    forEachIndexed { row, column, item ->
//        if(row == removedRow || column == removedColumn)
//    }
//}