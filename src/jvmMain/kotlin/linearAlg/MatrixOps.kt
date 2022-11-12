package linearAlg

fun <T> Matrix<T>.multiply(other: Matrix<T>): Matrix<T> {
    require(this.width == other.height)
    val result = Matrix.Builder<T>(this.height, other.width)
    TODO()
}