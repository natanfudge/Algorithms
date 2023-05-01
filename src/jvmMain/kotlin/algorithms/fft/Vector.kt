package algorithms.fft

import linearAlg.Complex
import linearAlg.justReal
import linearAlg.pow

//typealias Polynomial = List<Complex>
class Vector(private val list: List<Complex>) : List<Complex> by list {
    override fun toString(): String = "(${list.joinToString(", ")})"

//    override fun toPolynomialString(): String

    operator fun plus(other: Vector): Vector {
        require(this.size == other.size)
        return Vector(this.zip(other).map { (i, j) -> i + j })
    }

    operator fun div(num: Double) = Vector(list.map { it / num })
    operator fun div(num: Int) = Vector(list.map { it / num.toDouble() })

    fun dotProduct(other: Vector) = Vector(zip(other).map { (i, j) -> i * j })
    fun padToSize(size: Int): Vector {
        val newList = buildList {
            repeat(size) {
                if (it < list.size) add(list[it])
                else add(Complex.Zero)
            }
        }
        return Vector(newList)
    }

    fun trimZeroes(): Vector {
        val newList = mutableListOf<Complex>()
        var allZeroes = true
        for (item in this) {
            if (item == Complex.Zero) {
                if (!allZeroes) newList.add(item)
            } else {
                allZeroes = false
                newList.add(item)
            }
        }

        return Vector(newList)
    }

    fun evaluateAsPolynomialAt(x: Complex): Complex {
        var sum = 0.justReal
        list.asReversed().forEachIndexed { i, element ->
            sum += x.pow(i) * element
        }
        return sum
    }

    fun flip() = Vector(asReversed())
}

fun Vector.evaluateAsPolynomialAt(x: Int) = evaluateAsPolynomialAt(x.justReal)


fun vectorOf(vararg values: Complex) = Vector(values.toList())
fun vectorOf(vararg values: Double) = Vector(values.toList().map { it.justReal })
fun vectorOf(vararg values: Int) = vectorOf(values.toList())
fun vectorOf(values: List<Int>) = Vector(values.map { it.justReal })

fun polynomialOf(vararg values: Complex) = Vector(values.toList().asReversed())

//fun polynomialOf(vararg values: List<Complex>) = Vector(values.asReversed())
fun polynomialOf(vararg values: Double) = Vector(values.toList().asReversed().map { it.justReal })
fun polynomialOf(vararg values: Int) = polynomialOf(values.toList())
fun polynomialOf(values: List<Int>) = Vector(values.asReversed().map { it.justReal })

fun listOfComplex(vararg values: Double) = values.toList().map { it.justReal }

val List<Complex>.vector get() = Vector(this)