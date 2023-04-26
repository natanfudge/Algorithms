import algorithms.fft.Digits
import algorithms.fft.part
import algorithms.fft.toDigitRepresentation
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.math.ceil
import kotlin.math.log2

class FftMultTest {
    @Test
    fun testDigits() {
        val num = 492305245
        val digits = num.toDigitRepresentation()
        expectThat(digits).isEqualTo(Digits.fromString("11101010101111111101101011101"))
        expectThat(digits.value).isEqualTo(num)
    }

    @Test
    fun testParts() {
        val num = 492305245
        val digits = num.toDigitRepresentation()
        val n = digits.size
        val k = log2(n.toDouble()).toInt()
        println(k)
        expectThat(digits.part(0, k)).isEqualTo(Digits.fromString("1101"))
        expectThat(digits.part(1, k)).isEqualTo(Digits.fromString("0101"))
        expectThat(digits.part(2, k)).isEqualTo(Digits.fromString("1011"))
        expectThat(digits.part(3, k)).isEqualTo(Digits.fromString("1111"))
        expectThat(digits.part(4, k)).isEqualTo(Digits.fromString("0111"))
        expectThat(digits.part(5, k)).isEqualTo(Digits.fromString("0101"))
        expectThat(digits.part(6, k)).isEqualTo(Digits.fromString("1101"))
        expectThat(digits.part(7, k)).isEqualTo(Digits.fromString("0001"))

        val parts = ceil(n.toDouble() / k).toInt()
        var sum = 0
        repeat(parts){ j ->
            // NOTE: correct value is pow(j * k) and not pow(j) like in the solution
            sum += 2.pow(j * k) * digits.part(j, k).value
        }
        expectThat(sum).isEqualTo(num)
    }

}

