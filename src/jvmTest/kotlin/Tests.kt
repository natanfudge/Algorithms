import linearAlg.Matrix
import linearAlg.RealNumbers
import linearAlg.matrix
import linearAlg.multiply
import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testMultiplication() {
        val matrix = Matrix.IndexBuilder<Int>(2, 3).also {
            it[0, 0] = 1
            it[0, 1] = 2
            it[1, 0] = 3
            it[1, 1] = 4
            it[0, 2] = 5
            it[1, 2] = 6
        }.build()

        val matrix2 = Matrix.IndexBuilder<Int>(3, 2).also {
            it[0, 0] = 1
            it[0, 1] = 2
            it[1, 0] = 3
            it[1, 1] = 4
            it[2, 0] = 5
            it[2, 1] = 6
        }.build()

        println(matrix)
        println(matrix2)

        with(RealNumbers) {
            Assert.assertEquals(matrix {
                row(32 ,40)
                row(45,58)
            }, matrix.multiply(matrix2))
        }
    }
}