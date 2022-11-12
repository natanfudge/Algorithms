package linearAlg

interface Field<T> {
    operator fun  T.plus(other: T) : T
    operator fun  T.times(other: T): T
}

object RealNumbers : Field<Int> {
    override fun Number.plus(other: Number): Number {
        return this + other
    }

    override fun Number.times(other: Number): Number {
        TODO("Not yet implemented")
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