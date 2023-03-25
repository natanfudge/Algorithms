package algorithms.intervals


class IntervalChoice(private val requests: List<Request>) : List<Request> by requests {
    init {
        requests.zipWithNext().forEach { (prev, next) ->
            require(prev.end != next.start)
        }
    }

    override fun toString(): String = renderToString()
}

fun List<Request>.renderToString() = buildString {
    var lastRequest: Request? = null
    for (request in sortedBy { it.start }) {
        val leadingWhitespace = request.start - (lastRequest?.end?.let { it + 1 } ?: 0)
        append(" ".repeat(leadingWhitespace))
        append(request.toString())
        lastRequest = request
    }
}

/**
 * On the first line, prints out the solution calculated by the [algorithm].
 * It then takes out all the segments that were chosen, and runs the [algorithm] again with what is remaining,
 * and prints out the result on the next line.
 * It keeps doing this until no more intervals are left.
 */
fun IntervalProblem.solvedBy(algorithm: IntervalChoiceAlgorithm): String = buildString {
    var currentProblem = this@IntervalProblem
    while (currentProblem.isNotEmpty()) {
        val currentSolution = algorithm.solve(currentProblem)
        append(currentSolution.toString())
        val currentSolutionRequests = currentSolution.toHashSet()
        currentProblem = IntervalProblem(currentProblem.filter { it !in currentSolutionRequests })
        append("\n")
    }
}

fun interface IntervalChoiceAlgorithm {
    fun solve(problem: IntervalProblem): IntervalChoice

    companion object {
        fun byEarliestStart(requests: IntervalProblem): IntervalChoice {
            val byStart = requests.sortedBy { it.start }
            val solution = mutableListOf<Request>()
            for (request in byStart) {
                if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
                    solution.add(request)
                }
            }
            return IntervalChoice(solution)
        }

        fun bySmallestTimeInterval(requests: IntervalProblem): IntervalChoice {
            val byInterval = requests.sortedBy { it.end - it.start }
            val solution = mutableListOf<Request>()
            for (request in byInterval) {
                if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
                    solution.add(request)
                }
            }
            return IntervalChoice(solution)
        }

        fun byFewestConflicts(requests: IntervalProblem): IntervalChoice {
            val byLeastConflicts = requests.sortedBy { request -> requests.count { !it.isCompatibleWith(request) } }
            val solution = mutableListOf<Request>()
            for (request in byLeastConflicts) {
                if (solution.isEmpty() || solution.all { request.isCompatibleWith(it) }) {
                    solution.add(request)
                }
            }
            return IntervalChoice(solution)
        }

        fun byEarliestEnding(requests: IntervalProblem): IntervalChoice {
            val byEnding = requests.sortedBy { it.end }
            val solution = mutableListOf<Request>()
            for (request in byEnding) {
                if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
                    solution.add(request)
                }
            }
            return IntervalChoice(solution)
        }
    }
}

