import algorithms.fft.fft
import algorithms.fft.vectorOf
import org.junit.Test

class FFTTest {
    @Test
    fun testQuestion1() {
        val vector = vectorOf(-1, -3, 2, 1)
        fft(vector, log = true)
    }
}