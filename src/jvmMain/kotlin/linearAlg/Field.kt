package linearAlg

interface Field<T> {
    operator fun <T> T.plus(other: T) : T
    operator fun <T> T.times(other: T): T
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