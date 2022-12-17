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


class IntervalProblem(private val requests: List<Request>): List<Request> by requests {
    companion object {
        fun of(vararg requests: Request) : IntervalProblem = IntervalProblem(requests.toList())
    }
}



