import linearAlg.Complex
import linearAlg.linearspace.times
import linearAlg.plus
import linearAlg.times
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ComplexOps {
    @Test
    fun testInverse() {
        val c1 = Complex(0.2, 23)
        val c2 = Complex(2, 3)
        val c3 = 3 + 2 * Complex.I
        expectThat(c1.inverse * c1).isEqualTo(Complex.One)
        expectThat(c2.inverse * c2).isEqualTo(Complex.One)
        expectThat(c3.inverse * c3).isEqualTo(Complex.One)
        expectThat(c3).isEqualTo(Complex(3, 2))
    }
}