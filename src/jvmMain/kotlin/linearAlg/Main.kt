package linearAlg

fun main() {
    val matrix = Matrix.RowBuilder<Int>().apply {
        row(1,2,5)
        row(3,4,6)
    }.build()

    val matrix2  = Matrix.RowBuilder<Int>().apply {
        row(1,2, 9)
        row(3,4, 8)
        row(5,6, 7)
    }.build()

    println(matrix)
    println(matrix2)

    with(RealNumbers){
        println(matrix.multiply(matrix2))
    }
//    println(matrix.rows)
//    println(matrix.columns)
}

fun mainOld(args: Array<String>) {
    val matrix = Matrix.IndexBuilder<Int>(2,3).also {
        it[0,0] = 1
        it[0,1] = 2
        it[1,0] = 3
        it[1,1] = 4
        it[0,2] = 5
        it[1,2] = 6
    }.build()

    val matrix2  = Matrix.IndexBuilder<Int>(3,2).also {
        it[0,0] = 1
        it[0,1] = 2
        it[1,0] = 3
        it[1,1] = 4
        it[2,0] = 5
        it[2,1] = 6
    }.build()

    println(matrix)
    println(matrix2)

    with(RealNumbers){
        println(matrix.multiply(matrix2))
    }
//    println(matrix.rows)
//    println(matrix.columns)
}