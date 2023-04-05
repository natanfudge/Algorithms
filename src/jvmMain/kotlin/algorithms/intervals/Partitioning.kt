package algorithms.intervals

import kotlin.math.max


class IntervalPartition(private val partitions: List<List<Interval>>) : List<List<Interval>> by partitions {

    override fun toString(): String = partitions.joinToString("\n") { it.renderToString() }

}

fun IntervalProblem.solvedBy(algorithm: IntervalPartitionAlgorithm): String =
    algorithm.solve(this).toString()


fun interface IntervalPartitionAlgorithm {
    fun solve(problem: IntervalProblem): IntervalPartition

    companion object {
        fun solveByTheBook(problem: IntervalProblem): IntervalPartition {
            val depth = getDepth(problem)
            val labels = List(depth) { it + 1 }
            val intervalsSorted = problem.sortedBy { it.start }

            val intervalLabels = mutableMapOf<Interval, Int>()

            repeat(problem.size - 1) { j ->
                val currentInterval = intervalsSorted[j]
                val consideredLabels = labels.toMutableList()
                for (i in 0 until j) {
                    val precedingInterval = intervalsSorted[i]
                    if (precedingInterval.overlapsWith(currentInterval)) {
                        consideredLabels.remove(intervalLabels.getValue(precedingInterval))
                    }
                }
                intervalLabels[currentInterval] = consideredLabels.first()
            }

            val labelsToIntervals = labels.map { label ->
                intervalLabels.filter { (_, intervalLabel) -> intervalLabel == label }.keys.toList()
            }


            return IntervalPartition(labelsToIntervals)
        }

        fun solveFast(problem: IntervalProblem): IntervalPartition {
            val depth = getDepth(problem)
            // labels: numbers from 0 to depth-1
            val labels = 0 until depth
            val intervalsSorted = problem.sortedBy { it.start }

            val intervalLabels = mutableListOf<Int>()

            repeat(problem.size - 1) { currentIntervalIndex ->
                val currentInterval = intervalsSorted[currentIntervalIndex]
                val consideredLabels = labels.toHashSet()
                for (precedingIntervalIndex in 0 until currentIntervalIndex) {
                    val precedingInterval = intervalsSorted[precedingIntervalIndex]
                    if (precedingInterval.overlapsWith(currentInterval)) {
                        consideredLabels.remove(intervalLabels[precedingIntervalIndex])
                    }
                }
                intervalLabels.add(consideredLabels.first())
            }

            val labelsToIntervals = labels.map { label ->
                intervalLabels.mapIndexed { intervalIndex, intervalLabel -> intervalIndex to intervalLabel }
                    .filter { (_, intervalLabel) -> intervalLabel == label }
                    .map {
                        val intervalIndex = it.first
                        intervalsSorted[intervalIndex]
                    }
            }


            return IntervalPartition(labelsToIntervals)
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


}

private data class Edge(val value: Int, val isStart: Boolean)


fun main() {
    val caseA = IntervalProblem.of(
        Interval(0, 9),
        Interval(1, 2),
        Interval(3, 4),
        Interval(5, 6),
        Interval(7, 8)
    )
    val caseB = IntervalProblem.of(
        Interval(0, 5),
        Interval(7, 12),
        Interval(4, 8)
    )
    val caseC = IntervalProblem.of(
        Interval(0, 6), Interval(8, 12), Interval(15, 21), Interval(23, 29),
        Interval(5, 9), Interval(11, 16), Interval(20, 24),
        Interval(5, 9), Interval(20, 24),
        Interval(5, 9), Interval(20, 24)
    )

}

