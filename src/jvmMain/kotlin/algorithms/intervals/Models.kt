package algorithms.intervals

 class Interval(val start: Int, val end: Int) {

     val length get() = end - start
    init {
        require(start <= end - 1)
//        require()
    }

    fun isCompatibleWith(other: Interval) = this.end <= other.start || this.start >= other.end

     fun overlapsWith(other: Interval) = !isCompatibleWith(other)
    override fun toString(): String {
        return "├" + ("─").repeat(end - start - 1) + "┤"
    }
}


class IntervalProblem(private val requests: List<Interval>): List<Interval> by requests {
    companion object {
        fun of(vararg requests: Interval) : IntervalProblem = IntervalProblem(requests.toList())
    }
}



