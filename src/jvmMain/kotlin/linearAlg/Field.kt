package linearAlg

import linearAlg.RealNumbers.plus

interface Field<T> {
    operator fun  T.plus(other: T) : T
    operator fun  T.times(other: T): T
}

object RealNumbers : Field<Number> {
    override fun Number.plus(other: Number): Number {
        return when(this){
            is Int -> {
                require(other is Int)
               this + other
            }
            else -> this.toDouble() + other.toDouble()
        }
    }

    override fun Number.times(other: Number): Number {
        return when(this){
            is Int -> {
                require(other is Int)
                this * other
            }
            else -> this.toDouble() + other.toDouble()
        }
    }

}

context (Field<L>)
fun <T, L> List<T>.sumOf(selector: (T) -> L ): L{
    require(isNotEmpty())
    var sum = selector(this[0])
    for(item in this.drop(1)){
        sum += selector(item)
    }
    return sum
}