package linearAlg.linearspace

import kotlin.math.roundToInt

class Series<out T>(private val array: List<T>) : List<T> by array {
    override fun toString(): String = "[${array.joinToString()}]"
    override fun equals(other: Any?): Boolean {
        return other is Series<*> && this.size == other.size && this.zip(other).all { (a, b) -> a.equalsEnough(b) }
    }

    override fun hashCode(): Int {
        return array.hashCode()
    }
}

/**
 * Rounds number to a few decimal spaces to prevent rounding errors from screwing comparisons up
 */
private fun <T> T.equalsEnough(other: T): Boolean {
    return if (this is Number && other is Number) {
        val a = this.toDouble()
        val b = other.toDouble()
        return a.roundTo5DecimalSpaces() == b.roundTo5DecimalSpaces()
    } else this == other
}

 fun Double.roundTo5DecimalSpaces(): Double {
    return (this * 100000).roundToInt() / 100000.0
}