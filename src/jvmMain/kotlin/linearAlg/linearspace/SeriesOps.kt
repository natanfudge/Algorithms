package linearAlg.linearspace

import linearAlg.Field
import linearAlg.sumOf

context(Field<T>)
fun <T> Series<T>.dotProduct(other: Series<T>): T {
    require(this.size == other.size)
    return this.zip(other).sumOf { (a,b) -> a * b }
}

