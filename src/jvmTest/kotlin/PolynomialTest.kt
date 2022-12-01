import linearAlg.*
import linearAlg.Complex.Companion.I
import linearAlg.linearspace.Polynomial
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PolynomialTest {
    @Test
    fun testToString() {
        val p1 = Polynomial.of(1,-4,2,3,9)
        expectThat(p1.toString()).isEqualTo("1 - 4x + 2x² + 3x³ + 9x⁴")

        val p2 = Polynomial.of(3, 2 * I, -3 * I, 4.justReal, (-5).justReal, 1 + I, 2 - I, -3 + 2 * I, -4 - 4 * I)
        expectThat(p2.toString()).isEqualTo("3 + 2ix - 3ix² + 4x³ - 5x⁴ + (1 + i)x⁵ + (2 - i)x⁶ - (3 - 2i)x⁷ - (4 + 4i)x⁸")
    }
}

