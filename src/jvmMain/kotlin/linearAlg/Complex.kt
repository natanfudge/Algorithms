package linearAlg

import linearAlg.linearspace.roundTo5DecimalSpaces
import java.util.Objects
import kotlin.math.sqrt

//data class Imaginary(val value: Double)

operator fun Double.times(complex: Complex) = Complex(this * complex.real, this * complex.imaginary)
operator fun Int.times(complex: Complex) = this.toDouble() * complex
operator fun Double.plus(complex: Complex) = Complex(this + complex.real, complex.imaginary)
operator fun Int.plus(complex: Complex) = this.toDouble() + complex
operator fun Double.minus(complex: Complex) = Complex(this - complex.real, -complex.imaginary)
operator fun Int.minus(complex: Complex) = this.toDouble() - complex

fun Complex.pow(power: Int): Complex {
    check(power >= 0)
    var mult = 1.justReal
    repeat(power) {
        mult *= this
    }
    return mult
}

data class Complex(val real: Double, val imaginary: Double) {
    override fun equals(other: Any?): Boolean =
        other is Complex && real.roundTo5DecimalSpaces() == other.real.roundTo5DecimalSpaces()
                && imaginary.roundTo5DecimalSpaces() == other.imaginary.roundTo5DecimalSpaces()

    override fun hashCode(): Int {
        return Objects.hash(real.roundTo5DecimalSpaces(), imaginary.roundTo5DecimalSpaces())
    }

    constructor(real: Int, imaginary: Int) : this(real.toDouble(), imaginary.toDouble())
    constructor(real: Int, imaginary: Double) : this(real.toDouble(), imaginary)
    constructor(real: Double, imaginary: Int) : this(real, imaginary.toDouble())

    companion object {
        val Zero = Complex(0.0, 0.0)
        val I = Complex(0.0, 1.0)
        val One = Complex(1.0, 0.0)
    }

    operator fun plus(other: Complex): Complex {
        return Complex(real + other.real, imaginary + other.imaginary)
    }

    operator fun minus(other: Complex): Complex {
        return Complex(real - other.real, imaginary - other.imaginary)
    }

    operator fun times(other: Complex): Complex {
        return Complex(
            (this.real * other.real - this.imaginary * other.imaginary)/*.roundTo4DecimalSpaces()*/,
            (this.real * other.imaginary + this.imaginary * other.real)/*.roundTo4DecimalSpaces()*/
        )
    }

    operator fun div(number: Double) = Complex(real / number, imaginary / number)

    operator fun unaryMinus() = Complex(-real, -imaginary)

    val conjugate get() = Complex(real, -imaginary)

    val abs get() = sqrt(real.squared() + imaginary.squared())

    val inverse get() = conjugate / (abs.squared())

    override fun toString(): String {
        val imaginaryStr = imaginary.trimDecimalPoint()
        val realStr = real.trimDecimalPoint()
        val roundedImaginary = imaginary.roundTo5DecimalSpaces()
        val roundedReal = real.roundTo5DecimalSpaces()
        return when {
            roundedReal == 0.0 -> when (roundedImaginary) {
                0.0 -> "0"
                1.0 -> "i"
                -1.0 -> "-i"
                else -> "${imaginaryStr}i"
            }

            roundedImaginary == 0.0 -> realStr
            roundedImaginary == 1.0 -> "$realStr + i"
            roundedImaginary == -1.0 -> "$realStr - i"
            roundedImaginary < 0 -> "$realStr - ${(-imaginary).trimDecimalPoint()}i"
            else -> "$realStr + ${imaginaryStr}i"
        }
    }


    fun asReal() = if(imaginary != 0.0) {
        throw TypeCastException()
    } else real

    private fun Double.trimDecimalPoint() = roundTo5DecimalSpaces().toString().removeSuffix(".0")

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
private val fourthOrderRootsOfUnity = listOf(1.justReal, Complex.I, (-1).justReal, -Complex.I)
private val eightOrderRootsOfUnity = listOf(
    1.justReal,
    (1 plusI 1) / sqrt(2.0),
    Complex.I,
    -(1 minusI 1) / sqrt(2.0),
    (-1).justReal,
    -(1 plusI 1) / sqrt(2.0),
    -Complex.I,
    (1 minusI 1) / sqrt(2.0)
)

//private val rootsOfUnity: List<Pair<linearAlg.Complex,linearAlg.Complex>> = listOf(1.justReal to (-1).justReal, complexNumberI to -complexNumberI)