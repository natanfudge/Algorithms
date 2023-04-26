import kotlin.math.pow

fun <T> Iterable<T>.minElementAndValue(value: (T) -> Int): Pair<T, Int> {
    var min = Int.MAX_VALUE
    var minElement: T? = null
    for (element in this) {
        val elementValue = value(element)
        if (elementValue <= min) {
            min = elementValue
            minElement = element
        }
    }
    if (minElement == null) throw IllegalArgumentException("No min value in empty list")
    return minElement to min
}

fun Int.pow(other: Int) = toDouble().pow(other).toInt()