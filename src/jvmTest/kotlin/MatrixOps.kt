import linearAlg.*
import linearAlg.linearspace.*
import org.junit.Assert
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MatrixOps {
    @Test
    fun testMultiplication() {
        val matrix = Matrix.IndexBuilder.Any<Int>(2, 3).also {
            it[0, 0] = 1
            it[0, 1] = 2
            it[1, 0] = 3
            it[1, 1] = 4
            it[0, 2] = 5
            it[1, 2] = 6
        }.build()

        val matrix2 = Matrix.IndexBuilder.Any<Int>(3, 2).also {
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
                row(32, 40)
                row(45, 58)
            }, matrix.multiply(matrix2))
        }
    }

    @Test
    fun testMinor() {
        val matrix = squareMatrix {
            row(1, 2, 3, 4, 5)
            row(3, 4, 5, 6, 7)
            row(1, 1, 1, 1, 1)
            row(2, 2, 2, 2, 2)
            row(3, 3, 3, 3, 3)
        }

        val res1 = matrix.minor(0, 0)
        val res2 = matrix.minor(1, 1)

        println(matrix)
        println(res1)
        println(res2)

        Assert.assertEquals(matrix {
            row(4, 5, 6, 7)
            row(1, 1, 1, 1)
            row(2, 2, 2, 2)
            row(3, 3, 3, 3)
        }, res1)


        Assert.assertEquals(matrix {
            row(1, 3, 4, 5)
            row(1, 1, 1, 1)
            row(2, 2, 2, 2)
            row(3, 3, 3, 3)
        }, res2)


    }

    @Test
    fun testDeterminant() = with(RealNumbers) {
        val matrix = squareMatrix {
            row(1, 2, 3, 4, 5)
            row(3, 4, 5, 6, 7)
            row(1, 1, 1, 1, 1)
            row(2, 2, 2, 2, 2)
            row(3, 3, 3, 3, 3)
        }

        val res = matrix.determinant()

        println(res)

        Assert.assertEquals(0, res)

        val matrix2 = squareMatrix {
            row(4, 2, 3, 4, 5)
            row(3, 11, 5, 6, 7)
            row(1, 1, 9, 1, 1)
            row(2, 23, 2, 2, 2)
            row(3, 34, 3, 3, 3)
        }

        val res2 = matrix2.determinant()

        println(res2)

        Assert.assertEquals(24, res2)


    }

    @Test
    fun identity(): Unit = with(RealNumbers){
        expectThat(Matrix.identity(5)).isEqualTo(
            squareMatrix {
                row(1, 0, 0, 0, 0)
                row(0, 1, 0, 0, 0)
                row(0, 0, 1, 0, 0)
                row(0, 0, 0, 1, 0)
                row(0, 0, 0, 0, 1)
            }
        )
    }

    @Test
    fun transposed() {
        expectThat(squareMatrix {
            row(1, 2, 3, 4, 5)
            row(3, 4, 5, 6, 7)
            row(1, 1, 1, 1, 1)
            row(2, 2, 2, 2, 2)
            row(3, 3, 3, 3, 3)
        }.transposed()).isEqualTo(squareMatrix {
            row(1, 3, 1, 2, 3)
            row(2, 4, 1, 2, 3)
            row(3, 5, 1, 2, 3)
            row(4, 6, 1, 2, 3)
            row(5, 7, 1, 2, 3)
        })
    }

    @Test
    fun determinantTest() {
        with(RealNumbers) {
            expectThat(squareMatrix {
                row(4, 2, 3, 4, 5)
                row(3, 11, 5, 6, 7)
                row(1, 1, 9, 1, 1)
                row(2, 23, 2, 2, 2)
                row(3, 34, 3, 3, 3)
            }.determinant()).isEqualTo(24)
        }
    }

    @Test
    fun adjugate(): Unit = with(RealNumbers) {
        expectThat(squareMatrix {
            row(4, 2, 3, 4, 5)
            row(3, 11, 5, 6, 7)
            row(1, 1, 9, 1, 1)
            row(2, 23, 2, 2, 2)
            row(3, 34, 3, 3, 3)
        }.adjugate()).isEqualTo(squareMatrix {
            row(8, -8, 0, -328, 224)
            row(0, 0, 0, 72, -48)
            row(0, 0, 3, 93, -63)
            row(-32, 8, -6, -3794, 2566)
            row(24, 0, 3, 3213, -2175)
        })
    }

    @Test
    fun inverseTest(): Unit = with(RealNumbers) {
        expectThat(squareMatrix {
            row(4, 2, 3, 4, 5)
            row(3, 11, 5, 6, 7)
            row(1, 1, 9, 1, 1)
            row(2, 23, 2, 2, 2)
            row(3, 34, 3, 3, 3)
        }.inverse()).describedAs("Original Matrix").isEqualTo(squareMatrix {
            row(1 / 3.0, -1 / 3.0, 0, -41 / 3.0, 28 / 3.0)
            row(0, 0, 0, 3, -2)
            row(0, 0, 1 / 8.0, 31 / 8.0, -21 / 8.0)
            row(-4 / 3.0, 1 / 3.0, -1 / 4.0, -1897 / 12.0, 1283 / 12.0)
            row(1, 0, 1 / 8.0, 1071 / 8.0, -725 / 8.0)
        })
    }

    @Test
    fun inverseTest2(): Unit = with(RealNumbers) {
        val matrix = squareMatrix {
            row(4, 2, 3, 4, 5)
            row(3, 11, 5, 6, 7)
            row(1, 1, 9, 1, 1)
            row(2, 23, 2, 2, 2)
            row(3, 34, 3, 3, 3)
        }
        expectThat(matrix.multiply(matrix.inverse()!!)).isEqualTo(Matrix.identity(5))
        println(matrix.multiply(matrix.inverse()!!))
    }

    @Test
    fun plus(): Unit = with(RealNumbers){
        val a = squareMatrix {
            row(1,2,3)
            row(3,4,5)
            row(-2,3,1)
        }
        val b = squareMatrix {
            row(0,0,0)
            row(1,1,1)
            row(-1,0,1)
        }

        expectThat(a.add(b)).isEqualTo(squareMatrix {
            row(1,2,3)
            row(4,5,6)
            row(-3,3,2)
        })
    }
    @Test
    fun mult(): Unit = with(RealNumbers){
        val a = squareMatrix {
            row(1,2,3)
            row(3,4,5)
            row(-2,3,1)
        }

        expectThat(a.scalarMult(3)).isEqualTo(squareMatrix {
            row(3,6,9)
            row(9,12,15)
            row(-6,9,3)
        })
    }
}