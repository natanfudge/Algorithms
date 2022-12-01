package linearAlg.linearspace

import linearAlg.Complex
import kotlin.math.max

class Polynomial<T : Any>(private val coefficients: List<T>) {
    companion object {
        fun <T : Any>of(vararg coefficients: T) = Polynomial(coefficients.toList())
    }
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
            if (i == 0) {
                append(coefficient.toString())
                return@forEachIndexed
            }

            val coefficientString = coefficientString(coefficient)
            append("${coefficientString}x${powers[i]}")

        }
    }

    override fun hashCode(): Int {
        return coefficients.hashCode()
    }
}

// Power of 1 is the same as not having anything
private val powers = listOf("⁰", "", "²", "³", "⁴", "⁵", "⁶", "⁷", "⁸", "⁹")

private fun coefficientString(coefficient: Any): String = when (coefficient) {
    is Number -> {
        if (coefficient.toDouble() >= 0) " + $coefficient"
        else " - ${-coefficient}"
    }

    is Complex -> {
        when {
            coefficient.imaginary == 0.0 -> if(coefficient.real < 0) " - ${-coefficient}" else " + $coefficient"
            coefficient.real == 0.0 -> if(coefficient.imaginary < 0) " - ${-coefficient}" else " + $coefficient"
            coefficient.real < 0.0  -> " - (${-coefficient})"
            else -> " + ($coefficient)"
        }
    }
    else -> error("Unexpected polynomial coefficient type: $coefficient of type ${coefficient::class}")
}


private operator fun Number.unaryMinus() = when (this) {
    is Int -> -this
    else -> -(this.toDouble())
}