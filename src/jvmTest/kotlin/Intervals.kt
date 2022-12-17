import algorithms.intervals.*
import org.junit.Test

class Intervals {
    @Test
    fun byEarliest() {
        val caseA = IntervalProblem.of(
            Request(0,9),
            Request(1,2),
            Request(3,4),
            Request(5,6),
            Request(7,8)
        )
//    val reqA = Request(0, 5)
        println(caseA.solvedBy(IntervalChoiceAlgorithm::byEarliest))
    }

    @Test
    fun bySmallestInterval() {
        val caseB = IntervalProblem.of(
            Request(0,5),
            Request(7,12),
            Request(4,8)
        )
        println(caseB.solvedBy(IntervalChoiceAlgorithm::bySmallestTimeInterval))
    }
}