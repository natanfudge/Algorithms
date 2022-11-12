package linearAlg

class Series<T>(private val array: List<T>): List<T> by array {
    override fun toString(): String  = array.toString()
}