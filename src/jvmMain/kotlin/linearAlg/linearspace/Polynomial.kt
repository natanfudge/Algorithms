package linearAlg.linearspace

import algorithms.fft.Vector
import algorithms.fft.convolve
import linearAlg.Complex
import linearAlg.ComplexNumbers
import linearAlg.Field
import linearAlg.justReal
import kotlin.math.max

val <T : Any> Field<T>.zeroPolynomial get() = Polynomial(listOf(), this)

class Polynomial<T : Any>(val coefficients: List<T>, private val field: Field<T>) {
    val isZero get() = this == field.zeroPolynomial

    companion object {
        context (Field<T>)
        fun <T : Any> of(vararg coefficients: T) = Polynomial(coefficients.toList(), this@Field)

        fun ofComplex(vararg coefficients: Number) =
            Polynomial(coefficients.toList().map { it.toDouble().justReal }, ComplexNumbers)
    }


    override fun equals(other: Any?): Boolean {
        if (other !is Polynomial<*>) return false
        if (this.coefficients.isEmpty()) return other.coefficients.isEmpty()
        else if (other.coefficients.isEmpty()) return this.coefficients.isEmpty()
        if (this.coefficients[0] != other.coefficients[0]) return false

        @Suppress("UNCHECKED_CAST")
        return zipCoefficients(other as Polynomial<T>).all { (a, b) -> a.equalsEnough(b) }
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

    val degree: Int
        get() {
            require(this != this.field.zeroPolynomial)
            return coefficients.asReversed().indexOfFirst { it != this.field.Zero }
        }

    operator fun plus(other: Polynomial<T>): Polynomial<T> = with(field) {
        Polynomial(zipCoefficients(other).map { (a, b) -> a + b }, field)
    }

    operator fun times(other: Polynomial<T>): Polynomial<T> = with(field) {
        val result = mutableListOf<T>()
        coefficients.forEachIndexed { i, a ->
            other.coefficients.forEachIndexed { j, b ->
                val prevSum = result.getOrElse(i + j) { field.Zero }
                val newSum = prevSum + (a * b)
                if (result.size > i + j) result[i + j] = newSum else result.add(newSum)

            }
        }
        return Polynomial(result, field)
    }



     fun fftMultiply(other: Polynomial<T>): Polynomial<T> {
         require(this.isZero || coefficients[0] is Complex || coefficients[0] is Number)
        val isComplex = this.isZero || coefficients[0] is Complex
        return this.toVector().convolve(other.toVector()).toPolynomial(isComplex)
    }

    private fun Vector.toPolynomial(complex: Boolean) = Polynomial(
        asReversed().map {
            if (complex) it as T
            else {
                check(it.imaginary == 0.0)
                it.real as T
            }
        }, field
    )

    private fun toVector(): Vector {
        return Vector(coefficients.asReversed().map {
            when (it) {
                is Number -> it.toDouble().justReal
                is Complex -> it
                else -> error("Unexpected type $it")
            }
        })
    }

    private fun zipCoefficients(polynomial: Polynomial<T>): List<Pair<T, T>> = buildList {
        repeat(max(coefficients.size, polynomial.coefficients.size)) {
            val valueA = coefficients.getOrElse(it) { field.Zero }
            val valueB = polynomial.coefficients.getOrElse(it) { field.Zero }
            add(valueA to valueB)
        }
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
            coefficient.imaginary == 0.0 -> if (coefficient.real < 0) " - ${-coefficient}" else " + $coefficient"
            coefficient.real == 0.0 -> if (coefficient.imaginary < 0) " - ${-coefficient}" else " + $coefficient"
            coefficient.real < 0.0 -> " - (${-coefficient})"
            else -> " + ($coefficient)"
        }
    }

    else -> error("Unexpected polynomial coefficient type: $coefficient of type ${coefficient::class}")
}


private operator fun Number.unaryMinus() = when (this) {
    is Int -> -this
    else -> -(this.toDouble())
}