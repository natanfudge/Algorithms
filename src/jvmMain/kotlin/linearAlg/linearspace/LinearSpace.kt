package linearAlg.linearspace

import linearAlg.Field
import linearAlg.div

interface LinearSpace<T, V> {
    val field: Field<T>
    val Zero: V


    operator fun V.plus(other: V): V
    operator fun T.times(vector: V): V

    operator fun V.unaryMinus(): V


    class SquareMatrix<T>(val fieldOrder: Int, override val field: Field<T>): LinearSpace<T, Matrix.Square<T>> {
        override val Zero: Matrix.Square<T> = with(field){Matrix.zero(fieldOrder)}

        override fun Matrix.Square<T>.unaryMinus(): Matrix.Square<T> = with(field){
            require(order == fieldOrder)
            return map { -it }
        }

        override fun Matrix.Square<T>.plus(other: Matrix.Square<T>): Matrix.Square<T> = with(field){
            require(order == fieldOrder && other.order == fieldOrder)
            return this@plus.add(other)
        }

        override fun T.times(vector: Matrix.Square<T>): Matrix.Square<T> = with(field){
            require(vector.order == fieldOrder)
            return this@times.scalarMult(vector)
        }
    }

    //TODO: complete Series linear space definition
    class Series<T>(val length: Int, override val field: Field<T>): LinearSpace<T,linearAlg.linearspace.Series<T>> {
        override val Zero: linearAlg.linearspace.Series<T>
            get() = TODO("Not yet implemented")

        override fun linearAlg.linearspace.Series<T>.unaryMinus(): linearAlg.linearspace.Series<T> {
            TODO("Not yet implemented")
        }

        override fun T.times(vector: linearAlg.linearspace.Series<T>): linearAlg.linearspace.Series<T> {
            TODO("Not yet implemented")
        }

        override fun linearAlg.linearspace.Series<T>.plus(other: linearAlg.linearspace.Series<T>): linearAlg.linearspace.Series<T> {
            TODO("Not yet implemented")
        }
    }

    //TODO: attempt to have a programmatic representation of sub-spaces
}



context(LinearSpace<T, V>)
operator fun<T,V> V.div(other: T): V = with(field) {
    return this@div * (field.One / other)
}

context(LinearSpace<T, V>)
operator fun<T,V> V.times(other: T): V{
    return other * this
}

context(LinearSpace<T, V>)
operator fun<T,V> V.minus(other: V): V{
    return this + -other
}


