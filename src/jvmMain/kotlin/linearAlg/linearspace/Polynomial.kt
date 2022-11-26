package linearAlg.linearspace

import kotlin.math.max

//TODO: test printing out normal and complex numbers
class Polynomial<T>(private val coefficients: List<T>) {
    override fun equals(other: Any?): Boolean {
        if (other !is Polynomial<*>) return false
        repeat(max(coefficients.size, other.coefficients.size)) {
            val valueA = coefficients.getOrNull(it) ?: 0
            val valueB = coefficients.getOrNull(it) ?: 0
            if (valueA != valueB) return false
        }
        return true
    }

    override fun toString(): String = buildString {
        coefficients.forEachIndexed { i, coefficient ->
            if (i == 0) append(coefficient.toString())
            else {
                //TODO: properly handle complex numbers
                val prefix = if (coefficient is Number && coefficient.toDouble() < 0) " - ${-coefficient}"
                else " + $coefficient"
                append(" + ${prefix}x${powers[i]}")
            }
        }
    }
}

// Power of 1 is the same as not having anything
private val powers = listOf("⁰", "", "²", "³", "⁴", "⁵", "⁶", "⁷", "⁸", "⁹")

private operator fun Number.unaryMinus() = when (this) {
    is Int -> -this
    else -> -(this.toDouble())
}