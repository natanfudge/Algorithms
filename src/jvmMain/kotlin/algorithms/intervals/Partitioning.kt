package algorithms.intervals


class IntervalPartition(private val partitions: List<List<Request>>) : List<List<Request>> by partitions {

    override fun toString(): String = partitions.joinToString("\n") { it.renderToString() }

}

//fun IntervalProblem.solvedBy(algorithm: IntervalPartitionAlgorithm): String = buildString {
//    var currentProblem = this@IntervalProblem
//    while (currentProblem.isNotEmpty()) {
//        val currentSolution = algorithm(currentProblem)
//        append(currentSolution.toString())
//        val currentSolutionRequests = currentSolution.toHashSet()
//        currentProblem = IntervalProblem(currentProblem.filter { it !in currentSolutionRequests })
//        append("\n")
//    }
//}


fun interface IntervalPartitionAlgorithm {
    fun solve(problem: IntervalProblem): IntervalChoice

    companion object {

    }
}


fun main() {
    val caseA = IntervalProblem.of(
        Request(0, 9),
        Request(1, 2),
        Request(3, 4),
        Request(5, 6),
        Request(7, 8)
    )
    val caseB = IntervalProblem.of(
        Request(0, 5),
        Request(7, 12),
        Request(4, 8)
    )
    val caseC = IntervalProblem.of(
        Request(0, 6), Request(8, 12), Request(15, 21), Request(23, 29),
        Request(5, 9), Request(11, 16), Request(20, 24),
        Request(5, 9), Request(20, 24),
        Request(5, 9), Request(20, 24)
    )

}

