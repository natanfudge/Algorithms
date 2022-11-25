package linearAlg

interface LinearSpace<T, V> {
    val field: Field<T>

    operator fun V.plus(other: V): V
    operator fun T.times(vector: V): V


//    operator fun

    class SquareMatrix<T>(val order: Int): LinearSpace<T, Matrix.Square<T>> {
        //TODO: in implementation validate order in plus and times
        override val field: Field<T>
            get() = TODO("Not yet implemented")

        override fun Matrix.Square<T>.plus(other: Matrix.Square<T>): Matrix.Square<T> {
            TODO("Not yet implemented")
        }

        override fun T.times(vector: Matrix.Square<T>): Matrix.Square<T> {
            TODO("Not yet implemented")
        }
    }
}


context(LinearSpace<T,V>)
operator fun<T,V> V.div(other: T): V = with(field) {
    return this@div * (field.Identity / other)
}

context(LinearSpace<T,V>)
operator fun<T,V> V.times(other: T): V{
    return other * this
}


