package linearAlg
context (Field<T>)
fun <T> Matrix<T>.multiply(other: Matrix<T>): Matrix<T> {
    require(this.width == other.height)
    val result = Matrix.IndexBuilder<T>(this.height, other.width)
    for((i, row) in rows.withIndex()) {
        for ((j, column) in other.columns.withIndex()){
            result[i,j] = row.dotProduct(column)
        }
    }
    return result.build()
}