package algorithms.intervals

data class Task(val length: Int, val end: Int)
class IntervalOrdering(val order: List<Task>)

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
    }
}