package linearAlg

interface LinearSpace<T, V> {
    val field: Field<T>

    operator fun V.plus(other: V)
    operator fun T.times(vector: V)

    class SquareMatrix<T>(val order: Int): LinearSpace<T, Matrix.Square<T>> {
        //TODO: in implementation validate order in plus and times
        override val field: Field<T>
            get() = TODO("Not yet implemented")

        override fun Matrix.Square<T>.plus(other: Matrix.Square<T>) {
            TODO("Not yet implemented")
        }

        override fun T.times(vector: Matrix.Square<T>) {
            TODO("Not yet implemented")
        }
    }
}

