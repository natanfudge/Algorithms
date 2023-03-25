package algorithms.intervals

import kotlin.math.max


class IntervalPartition(private val partitions: List<List<Request>>) : List<List<Request>> by partitions {

    override fun toString(): String = partitions.joinToString("\n") { it.renderToString() }

}

fun IntervalProblem.solvedBy(algorithm: IntervalPartitionAlgorithm): String =
    algorithm.solve(this).toString()


object IntervalPartitionAlgorithm {
    fun solve(problem: IntervalProblem): IntervalChoice {
        val intervalsSorted = problem.sortedBy { it.start }

        (1..problem.size).forEach { j ->

        }
    }

    private fun getDepth(problem: IntervalProblem): Int {
        val sorted = problem.flatMap { listOf(Edge(it.start, isStart = true), Edge(it.end, isStart = false)) }
            .sortedBy { it.value }

        var max = 0
        var current = 0
        for (edge in sorted) {
            if (edge.isStart) {
                current++
                max = max(current, max)
            } else {
                current--
            }
        }
        return max
    }

}

data class Edge(val value: Int, val isStart: Boolean)


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

