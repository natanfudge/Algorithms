package linearAlg

class Series<out T>(private val array: List<T>): List<T> by array {
    override fun toString(): String  = "[${array.joinToString()}]"
}