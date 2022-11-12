package linearAlg

fun main(args: Array<String>) {
    val matrix = Matrix.Builder<Int>(3,2).also {
        it[0,0] = 1
        it[0,1] = 2
        it[1,0] = 3
        it[1,1] = 4
        it[0,2] = 5
        it[1,2] = 6
    }.build()

    val matrix2  = Matrix.Builder<Int>(2,3).also {
        it[0,0] = 1
        it[0,1] = 2
        it[1,0] = 3
        it[1,1] = 4
        it[2,0] = 5
        it[2,1] = 6
    }.build()

    println(matrix)
    println(matrix2)

    println(matrix.multiply(matrix2))
//    println(matrix.rows)
//    println(matrix.columns)
}