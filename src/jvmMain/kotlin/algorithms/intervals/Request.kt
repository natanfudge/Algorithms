package algorithms.intervals

 class Request(val start: Int, val end: Int) {
    init {
        require(start <= end - 1)
    }

    fun isCompatibleWith(other: Request) = this.end <= other.start || this.start >= other.end
    override fun toString(): String {
        return "├" + ("─").repeat(end - start - 1) + "┤"
    }
}

class IntervalSolution(private val requests: List<Request>): List<Request> by requests {
    init {
        requests.zipWithNext().forEach { (prev, next) ->
            require(prev.end != next.start)
        }
    }
    override fun toString(): String = buildString {
        var lastRequest: Request? = null
        for (request in requests.sortedBy { it.start }) {
            val leadingWhitespace = request.start - (lastRequest?.end?.let { it + 1 } ?: 0)
            append(" ".repeat(leadingWhitespace))
            append(request.toString())
            lastRequest = request
        }
    }

}
class IntervalProblem(private val requests: List<Request>): List<Request> by requests {
    fun solvedBy(algorithm: IntervalAlgorithm): String = buildString {
        var currentProblem = this@IntervalProblem
        while(currentProblem.requests.isNotEmpty()){
            val currentSolution = algorithm(currentProblem)
            append(currentSolution.toString())
            val currentSolutionRequests = currentSolution.toHashSet()
            currentProblem = IntervalProblem(currentProblem.requests.filter { it !in currentSolutionRequests })
            append("\n")
        }
    }
    companion object {
        fun of(vararg request: Request) = IntervalProblem(request.toList())
    }
}

typealias IntervalAlgorithm = (IntervalProblem) -> IntervalSolution

//typealias IntervalSolution = List<Request>
//typealias IntervalProblem = List<Request>

fun pickByEarliest(requests: IntervalProblem): IntervalSolution {
    val byStart = requests.sortedBy { it.start }
    val solution = mutableListOf<Request>()
    for (request in byStart) {
        if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
            solution.add(request)
        }
    }
    return IntervalSolution(solution)
}

fun pickBySmallestTimeInterval(requests: IntervalProblem): IntervalSolution {
    val byInterval = requests.sortedBy { it.end - it.start }
    val solution = mutableListOf<Request>()
    for (request in byInterval) {
        if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
            solution.add(request)
        }
    }
    return IntervalSolution(solution)
}
fun pickByFewestConflicts(requests: IntervalProblem): IntervalSolution {
    val byLeastConflicts = requests.sortedBy { request -> requests.count { !it.isCompatibleWith(request) } }
    val solution = mutableListOf<Request>()
    for (request in byLeastConflicts) {
        if (solution.isEmpty() || solution.all { request.isCompatibleWith(it) }) {
            solution.add(request)
        }
    }
    return IntervalSolution(solution)
}

fun pickByEarliestEnding(requests: IntervalProblem): IntervalSolution {
    val byEnding = requests.sortedBy { it.end }
    val solution = mutableListOf<Request>()
    for (request in byEnding) {
        if (solution.isEmpty() || solution.last().isCompatibleWith(request)) {
            solution.add(request)
        }
    }
    return IntervalSolution(solution)
}

fun main() {
    val caseA = IntervalProblem.of(
        Request(0,9),
        Request(1,2),
        Request(3,4),
        Request(5,6),
        Request(7,8)
    )
//    val reqA = Request(0, 5)
//    println(caseA.solvedBy(::pickByEarliest))
    val caseB = IntervalProblem.of(
        Request(0,5),
        Request(7,12),
        Request(4,8)
    )
//    println(caseB.solvedBy(::pickBySmallestTimeInterval))
    val caseC = IntervalProblem.of(
        Request(0,6), Request(8,12), Request(15, 21), Request(23, 29),
             Request(5,9), Request(11, 16), Request(20, 24),
             Request(5,9),                  Request(20,24),
             Request(5,9)                  , Request(20,24)
    )
    println(caseA.solvedBy(::pickByEarliestEnding))
    println(caseB.solvedBy(::pickByEarliestEnding))
    println(caseC.solvedBy(::pickByEarliestEnding))
}

//TODO: problemToString, which keeps drawing out the set of requests that are the solution, and puts that solution on its own line, until we run out of requests.
