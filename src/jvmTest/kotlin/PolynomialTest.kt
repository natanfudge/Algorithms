import linearAlg.*
import linearAlg.Complex.Companion.I
import linearAlg.linearspace.Polynomial
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PolynomialTest {
    @Test
    fun testToString() {
        with(RealNumbers) {
            val p1 = Polynomial.of(1, -4, 2, 3, 9)
            expectThat(p1.toString()).isEqualTo("1 - 4x + 2x² + 3x³ + 9x⁴")
        }

        with(ComplexNumbers) {
            val p2 = Polynomial.of(
                3.justReal,
                2 * I,
                -3 * I,
                4.justReal,
                (-5).justReal,
                1 + I,
                2 - I,
                -3 + 2 * I,
                -4 - 4 * I
            )
            expectThat(p2.toString()).isEqualTo("3 + 2ix - 3ix² + 4x³ - 5x⁴ + (1 + i)x⁵ + (2 - i)x⁶ - (3 - 2i)x⁷ - (4 + 4i)x⁸")
        }
    }


    @Test
    fun testDegree(): Unit = with(RealNumbers) {
        val p = Polynomial.of(1, 2, 0)
        expectThat(p.degree).isEqualTo(1)
    }

    @Test
    fun testSum(): Unit = with(ComplexNumbers) {
        val p1 = Polynomial.ofComplex(1, -4, 2, 3, 9)
        val p2 = Polynomial.of(
            3.justReal,
            2 * I,
            -3 * I,
            4.justReal,
            (-5).justReal,
            1 + I,
            2 - I,
            -3 + 2 * I,
            -4 - 4 * I
        )

        expectThat(p1 + p2).isEqualTo(
            Polynomial.of(
                4.justReal, -4 + 2 * I, 2 - 3 * I, 7.justReal, 4.justReal, 1 + I,
                2 - I,
                -3 + 2 * I,
                -4 - 4 * I
            )
        )
    }

    @Test
    fun testMult(): Unit = with(ComplexNumbers) {
        val p1 = Polynomial.ofComplex(1, -4, 2)
        val p2 = Polynomial.of(
            1 + I,
            2 - I,
            -3 + 2 * I,
            -4 - 4 * I
        )

        println(p1 * p2)

        expectThat(p1 * p2).isEqualTo(
            Polynomial.of(
                1 + I,
                -2 - 5 * I,
                -9 + 8 * I,
                12 - 14 * I,
                10 + 20 * I,
                -8 - 8 * I,
            )
        )
    }

    @Test
    fun testFFTMult(): Unit = with(ComplexNumbers) {
        val p1 = Polynomial.ofComplex(1, -4, 2)
        val p2 = Polynomial.of(
            1 + I,
            2 - I,
            -3 + 2 * I,
            -4 - 4 * I
        )

        expectThat(p1.fftMultiply(p2)).isEqualTo(
            Polynomial.of(
                1 + I,
                -2 - 5 * I,
                -9 + 8 * I,
                12 - 14 * I,
                10 + 20 * I,
                -8 - 8 * I,
            )
        )
    }

    @Test
    fun testEvaluate(): Unit = with(ComplexNumbers) {
        val p = Polynomial.of(
            3.justReal,
            2 * I,
            -3 * I,
            4.justReal,
            (-5).justReal,
            1 + I,
            2 - I,
            -3 + 2 * I,
            -4 - 4 * I
        )

        expectThat(p.evaluate(Complex.Zero)).isEqualTo(3.justReal)
        expectThat(p.evaluate(Complex.One)).isEqualTo(-2 - 3 * I)
        expectThat(p.evaluate(5.justReal)).isEqualTo(-1765122 - 1418815 * I)
        expectThat(p.evaluate(2 - 1 / 3.0 * I)).isEqualTo(-8353249 / 6561.0 + (7855886 * I) / 6561.0)
    }


    @Test
    fun testDerivative(): Unit = with(ComplexNumbers){
        val p = Polynomial.of(
            3.justReal,
            2 * I,
            -3 * I,
            4.justReal,
        )

        expectThat(p.derivative).isEqualTo(Polynomial.of(2 * I, - 6 * I, 12.justReal))
    }
}

