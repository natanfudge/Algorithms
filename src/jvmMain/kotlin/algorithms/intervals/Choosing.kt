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
        fun pickByEarliest(requests: IntervalProblem): IntervalChoice {
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
            return algorithms.intervals.IntervalChoice(solution)
        }

        fun byFewestConflicts(requests: IntervalProblem): IntervalChoice {
            val byLeastConflicts = requests.sortedBy { request -> requests.count { !it.isCompatibleWith(request) } }
            val solution = mutableListOf<Request>()
            for (request in byLeastConflicts) {
                if (solution.isEmpty() || solution.all { request.isCompatibleWith(it) }) {
                    solution.add(request)
                }
            }
            return algorithms.intervals.IntervalChoice(solution)
        }

        fun byEarliestEnding(requests: IntervalProblem): IntervalChoice {
            val byEnding = requests.sortedBy { it.end }
            val solution = mutableListOf<Request>()
            for (request in byEnding) {
                if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
                    solution.add(request)
                }
            }
            return algorithms.intervals.IntervalChoice(solution)
        }
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
    println(caseA.solvedBy(IntervalChoiceAlgorithm::byEarliestEnding))
    println(caseB.solvedBy(IntervalChoiceAlgorithm::byEarliestEnding))
    println(caseC.solvedBy(IntervalChoiceAlgorithm::byEarliestEnding))
}

