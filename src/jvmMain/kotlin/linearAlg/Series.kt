package linearAlg

import java.util.Objects

class Series<out T>(private val array: List<T>): List<T> by array {
    override fun toString(): String  = "[${array.joinToString()}]"
    override fun equals(other: Any?): Boolean {
        return other is Series<*> && this.size == other.size && this.zip(other).all { (a,b) -> a == b }
    }

    override fun hashCode(): Int {
        return array.hashCode()
    }
}