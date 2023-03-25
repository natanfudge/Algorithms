import algorithms.intervals.*
import org.junit.Test

class Intervals {
    @Test
    fun byEarliestStart() {
        val caseA = IntervalProblem.of(
            Request(0,9),
            Request(1,2),
            Request(3,4),
            Request(5,6),
            Request(7,8)
        )

        println(caseA.solvedBy(IntervalChoiceAlgorithm::byEarliestStart))
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

    @Test
    fun byFewestConflicts() {
        val caseC = IntervalProblem.of(
            Request(0,5),
            Request(6,11),
            Request(13,18),
            Request(20,25),

            Request(3,7),
            Request(3,7),
            Request(3,7),

            Request(10,15),

            Request(17,22),
            Request(17,22),
            Request(17,22),
        )
        println(caseC.solvedBy(IntervalChoiceAlgorithm::byFewestConflicts))
    }

    @Test
    fun byEarliestEnd() {
        val caseC = IntervalProblem.of(
            Request(0,5),
            Request(6,11),
            Request(13,18),
            Request(20,25),

            Request(3,7),
            Request(3,7),
            Request(3,7),

            Request(10,15),

            Request(17,22),
            Request(17,22),
            Request(17,22),
        )
        println(caseC.solvedBy(IntervalChoiceAlgorithm::byEarliestEnding))
    }


}