package algorithms.intervals

import kotlin.math.max

data class Task(val length: Int, val end: Int) {
    override fun toString(): String {
        return rangeToString(length)
    }
}
class IntervalOrdering(private val order: List<Task>) {
    fun getLatency(): Int {
        var timePassed = 0
        var latency = 0
        for(task  in order) {
            timePassed += task.length
            latency += max(0, timePassed - task.end)
        }
        return latency
    }
    override fun toString(): String {
        return order.joinToString("") { it.toString() } +  " (latency = ${getLatency()})"
    }
}

fun TaskProblem.solvedBy(algorithm: IntervalOrderingAlgorithm): String =
    algorithm.solve(this).toString()

class TaskProblem(private val requests: List<Task>): List<Task> by requests {
    companion object {
        fun of(vararg requests: Task) : TaskProblem = TaskProblem(requests.toList())
    }
}


fun interface IntervalOrderingAlgorithm {
    fun solve(problem: TaskProblem) : IntervalOrdering

    companion object {
        fun shortestFirst(problem: TaskProblem): IntervalOrdering {
            return IntervalOrdering(problem.sortedBy { it.length })
        }

        fun smallestDiffFirst(problem: TaskProblem): IntervalOrdering {
            return IntervalOrdering(problem.sortedBy { it.end - it.length })
        }

        fun earliestFirst(problem: TaskProblem) : IntervalOrdering {
            return IntervalOrdering(problem.sortedBy { it.end })
        }
    }
}