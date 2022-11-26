package algorithms.fft

import linearAlg.Complex
import linearAlg.getRootOfUnity
import kotlin.math.pow

private val powerOfTwo = List(10) { 2.0.pow(it.toDouble()).toInt() }.toHashSet()

fun main() {
    val A = polynomialOf(1, -3)
    val B = polynomialOf(1, 8)
    println(A.convolve(B))

//    val res = algorithms.fft.fft(polynomialOf(1,2,-3,-1))
//    println(algorithms.fft.ifft(res))
//    val res = algorithms.fft.fft(vectorOf(1, 2, 3, 4, 5, 6, 7, 8))
//    println(algorithms.fft.ifft(res))
//    println(algorithms.fft.fft(vectorOf(1, 2, 3, 4)))
}

fun Vector.convolve(other: Vector): Vector {
    val pointsRequired = this.size + other.size
    val thisAsPoints = fft(this.padToSize(pointsRequired))
    val otherAsPoints = fft(other.padToSize(pointsRequired))

    val multiplied = thisAsPoints.dotProduct(otherAsPoints)

    return ifft(multiplied)
}

fun fft(vector: Vector): Vector {
    require(vector.size in powerOfTwo)

    return fftRecur(vector, indent = 0, reverse = false)
}

fun ifft(vector: Vector) = fftRecur(vector, indent = 0, reverse = true).asReversed().vector / vector.size

private fun fftRecur(vector: Vector, indent: Int, reverse: Boolean): Vector {
    if (vector.size == 1) {
        printlnIndent("FFT($vector), Return ${vectorOf(vector[0])}", indent)
        return vectorOf(vector[0])
    }
    printlnIndent("FFT($vector):", indent)

    val evenPart = vector.filterIndexed { i, _ -> i % 2 == 0 }.vector
    val oddPart = vector.filterIndexed { i, _ -> i % 2 == 1 }.vector

    check(evenPart.size == oddPart.size)

    val evenPartEvaluations = fftRecur(evenPart, indent = indent + 1, reverse)
    val oddPartEvaluations = fftRecur(oddPart, indent = indent + 1, reverse)

    val n = vector.size

//    val pointsToEvaluate = getRootsOfUnity(n).let { if (reverse) it.asReversed().map { (pos,neg) -> neg to pos } else it }

    val result = arrayOfNulls<Complex>(n)

    repeat(n / 2) { i ->
        val evenPartValue = evenPartEvaluations[i]
        val oddPartValue = oddPartEvaluations[i]

        val root = getRootOfUnity(i, order = n, reversed = reverse)

        // P(x) = Peven(x) + xPodd(x)
        val rootOfUnityPositiveValue = evenPartValue + root * oddPartValue
        // P(-x) = Peven(x) - xPodd(x)
        val rootOfUnityNegativeValue = evenPartValue - root * oddPartValue

        result[i] = rootOfUnityPositiveValue
        result[i + n / 2] = rootOfUnityNegativeValue

        printlnIndent(
            "Calculate f($root) = $evenPartValue + $root * ($oddPartValue) = $rootOfUnityPositiveValue",
            indent
        )
        printlnIndent(
            "Calculate f(-$root) = $evenPartValue - $root * ($oddPartValue) = $rootOfUnityNegativeValue",
            indent
        )
    }

    val resultValues = result.filterNotNull().vector
    printlnIndent("Return $resultValues", indent)
    return resultValues
}

private fun printlnIndent(string: String, indent: Int) = println("\t".repeat(indent) + string)
private fun printIndent(string: String, indent: Int) = print("\t".repeat(indent) + string)