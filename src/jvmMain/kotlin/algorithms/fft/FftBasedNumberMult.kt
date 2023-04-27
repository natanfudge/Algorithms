package algorithms.fft

import pow
import kotlin.math.ceil
import kotlin.math.log2

fun Int.fftMult(other: Int) {
    val a = this.toDigitRepresentation()
    val b = other.toDigitRepresentation()
    val n = a.size
    check(a.size == b.size)

    val k = log2(n.toDouble()).toInt()
    val partCount = ceil(n.toDouble() / k).toInt()
    val aParts = List(partCount){ j->
        a.part(j, k)
    }
    val bParts = List(partCount) { j->
        b.part(j, k)
    }

    val multValues = aParts.zip(bParts).map { a.ff }


}


fun main() {

}

fun Digits.part(j: Int, k: Int): Digits {
    val end = k * (j + 1) - 1 + 1
    val part = subList(k * j, end.coerceAtMost(size))
    val zeroPadding = List((end - size).coerceAtLeast(0)) {
        false
    }
    return Digits(part + zeroPadding)
}

fun Int.toDigitRepresentation(): Digits {
    val digits = mutableListOf<Boolean>()
    val byteLength = log2(this.toFloat()).toInt() + 1
    var leftover = this
    for (digitIndex in byteLength - 1 downTo 0) {
        val digit = leftover shr digitIndex
        val one = when (digit) {
            0 -> false
            1 -> true
            else -> error("Unexpected digit value $digit")
        }
        digits.add(one)
        if (one) leftover -= 2.pow(digitIndex)

    }
    return Digits(digits.asReversed())
}

data class Digits(private val digits: List<Boolean>) : List<Boolean> by digits {
    companion object {
        fun fromString(digits: String): Digits {
            return Digits(digits.map {
                when (it) {
                    '0' -> false
                    '1' -> true
                    else -> error("Unexpected digit '$it'")
                }
            }.asReversed())
        }
    }

    val value = run {
        var sum = 0
        digits.forEachIndexed { i, digit ->
            val digitValue = if (digit) 2.pow(i) else 0
            sum += digitValue
        }
        sum
    }

    override fun toString(): String {
        return digits.asReversed().joinToString("") { if (it) "1" else "0" }
    }
}

 fun Digits.toPolynomial() = polynomialOf(map { if (it) 1 else 0 })

//typealias Digits = List<Boolean>