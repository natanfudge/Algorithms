package linearAlg

import linearAlg.linearspace.matrix
import linearAlg.linearspace.multiply
import linearAlg.linearspace.squareMatrix

fun main() {
    val matrix = matrix {
        row(1,2,5)
        row(3,4,6)
    }

    val matrix2  = squareMatrix {
        row(1,2, 9)
        row(3,4, 8)
        row(5,6, 7)
    }

    println(matrix)
    println(matrix2)

    with(RealNumbers){
        println(matrix.multiply(matrix2))
    }
//    println(matrix.rows)
//    println(matrix.columns)
}

