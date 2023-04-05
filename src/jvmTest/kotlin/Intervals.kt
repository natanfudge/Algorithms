import algorithms.intervals.*
import org.junit.Test

class Intervals {
    @Test
    fun byEarliestStart() {
        val caseA = IntervalProblem.of(
            Interval(0,9),
            Interval(1,2),
            Interval(3,4),
            Interval(5,6),
            Interval(7,8)
        )

        println(caseA.solvedBy(IntervalChoiceAlgorithm::byEarliestStart))
    }

    @Test
    fun bySmallestInterval() {
        val caseB = IntervalProblem.of(
            Interval(0,5),
            Interval(7,12),
            Interval(4,8)
        )
        println(caseB.solvedBy(IntervalChoiceAlgorithm::bySmallestTimeInterval))
    }

    @Test
    fun byFewestConflicts() {
        val caseC = IntervalProblem.of(
            Interval(0,5),
            Interval(6,11),
            Interval(13,18),
            Interval(20,25),

            Interval(3,7),
            Interval(3,7),
            Interval(3,7),

            Interval(10,15),

            Interval(17,22),
            Interval(17,22),
            Interval(17,22),
        )
        println(caseC.solvedBy(IntervalChoiceAlgorithm::byFewestConflicts))
    }

    @Test
    fun byEarliestEnd() {
        val caseC = IntervalProblem.of(
            Interval(0,5),
            Interval(6,11),
            Interval(13,18),
            Interval(20,25),

            Interval(3,7),
            Interval(3,7),
            Interval(3,7),

            Interval(10,15),

            Interval(17,22),
            Interval(17,22),
            Interval(17,22),
        )
        println(caseC.solvedBy(IntervalChoiceAlgorithm::byEarliestEnding))
    }

    @Test
    fun intervalPartitionBook(){
        val problem = IntervalProblem.of(
            Interval(0, 4), // a
            Interval(0, 4), // c
            Interval(6, 10), //d
            Interval(0,10), // b
            Interval(6, 16), // e
            Interval(14, 18), //f
            Interval(14, 18), //g
            Interval(17, 23), // h
            Interval(19, 23), //i
            Interval(19, 23), //j
        )

        println(problem.solvedBy(IntervalPartitionAlgorithm::solveByTheBook))
    }
    @Test
    fun intervalPartitionFast(){
        val problem = IntervalProblem.of(
            Interval(0, 4), // a
            Interval(0, 4), // c
            Interval(6, 10), //d
            Interval(0,10), // b
            Interval(6, 16), // e
            Interval(14, 18), //f
            Interval(14, 18), //g
            Interval(17, 23), // h
            Interval(19, 23), //i
            Interval(19, 23), //j
        )

        println(problem.solvedBy(IntervalPartitionAlgorithm::solveFast))
    }


    @Test
    fun reducingLatencyShortestFirst() {
        val problem = TaskProblem.of(
            Task(1, 100),
            Task(10, 10)
        )

        println(problem.solvedBy(IntervalOrderingAlgorithm::shortestFirst))
    }
    @Test
    fun reducingLatencySmallestDiffFirst() {
        val problemA = TaskProblem.of(
            Task(1, 100),
            Task(10, 10)
        )

        val problemB = TaskProblem.of(
            Task(1, 2),
            Task(10,10)
        )

        println(problemA.solvedBy(IntervalOrderingAlgorithm::smallestDiffFirst))
        println(problemB.solvedBy(IntervalOrderingAlgorithm::smallestDiffFirst))
    }
    @Test
    fun reducingLatencyEarliestFirst() {
        val problemA = TaskProblem.of(
            Task(1, 100),
            Task(10, 10)
        )

        val problemB = TaskProblem.of(
            Task(1, 2),
            Task(10,10)
        )

        println(problemA.solvedBy(IntervalOrderingAlgorithm::earliestFirst))
        println(problemB.solvedBy(IntervalOrderingAlgorithm::earliestFirst))
    }
}