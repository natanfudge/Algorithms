package linearAlg

import kotlin.math.sqrt

data class Complex(val real: Double, val imaginary: Double){
    companion object {
        val Zero = Complex(0.0,0.0)
    }
    operator fun plus(other: Complex): Complex {
        return Complex(real + other.real, imaginary + other.imaginary)
    }

    operator fun minus(other: Complex): Complex {
        return Complex(real - other.real, imaginary - other.imaginary)
    }

    operator fun times(other: Complex): Complex {
        return Complex(
            this.real * other.real - this.imaginary * other.imaginary,
            this.real * other.imaginary + this.imaginary * other.real
        )
    }

    operator fun div(number: Double) = Complex(real / number, imaginary / number)

    operator fun unaryMinus() = Complex(-real, -imaginary)

    override fun toString(): String {
        val imaginaryStr = imaginary.trimDecimalPoint()
        val realStr= real.trimDecimalPoint()
        return when {
            real == 0.0 -> when (imaginary) {
                0.0 -> "0"
                1.0 -> "i"
                -1.0 -> "-i"
                else -> "${imaginaryStr}i"
            }

            imaginary == 0.0 -> realStr
            imaginary == 1.0 -> "$realStr + i"
            imaginary == -1.0 -> "$realStr - i"
            imaginary < 0 -> "$realStr - ${(-imaginary).trimDecimalPoint()}i"
            else -> "$realStr + ${imaginaryStr}i"
        }
    }

    private fun Double.trimDecimalPoint() = toString().removeSuffix(".0")

}

infix fun Double.plusI(imaginary: Double) = Complex(this, imaginary)
infix fun Double.minusI(imaginary: Double) = Complex(this, -imaginary)
infix fun Int.plusI(imaginary: Double) = toDouble().plusI(imaginary)
infix fun Int.plusI(imaginary: Int) = toDouble().plusI(imaginary.toDouble())
infix fun Int.minusI(imaginary: Int) = toDouble().minusI(imaginary.toDouble())
val Double.justReal get() = Complex(this, 0.0)
val Int.justReal get() = toDouble().justReal
val List<Double>.justReal get() = map { it.justReal }
fun listOfComplex(vararg values: Double) = values.toList().map { it.justReal }

val complexNumberI = Complex(0.0, 1.0)

//fun getRootsOfUnity(amount: Int): List<Pair<linearAlg.Complex,linearAlg.Complex>> = rootsOfUnity.take(amount / 2)
fun getRootOfUnity(index: Int, order: Int, reversed: Boolean): Complex {
    val list = when (order) {
        2 -> secondOrderRootsOfUnity
        4 -> fourthOrderRootsOfUnity
        8 -> eightOrderRootsOfUnity
        else -> error("Too big")
    }

    val orderedList = if (reversed) {
        listOf(list[0]) + list.drop(0).asReversed()
    } else list

    return orderedList[index]
}

private val secondOrderRootsOfUnity = listOfComplex(1.0, -1.0)
private val fourthOrderRootsOfUnity = listOf(1.justReal, complexNumberI, (-1).justReal, -complexNumberI)
private val eightOrderRootsOfUnity = listOf(
    1.justReal,
    (1 plusI 1) / sqrt(2.0),
    complexNumberI,
    -(1 minusI 1) / sqrt(2.0),
    (-1).justReal,
    -(1 plusI 1) / sqrt(2.0),
    -complexNumberI,
    (1 minusI 1) / sqrt(2.0)
)

//private val rootsOfUnity: List<Pair<linearAlg.Complex,linearAlg.Complex>> = listOf(1.justReal to (-1).justReal, complexNumberI to -complexNumberI)