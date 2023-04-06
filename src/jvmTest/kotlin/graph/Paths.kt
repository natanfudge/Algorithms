package graph

import algorithms.buildDirectedGraph
import algorithms.buildWeightedGraph
import algorithms.graphui.GraphUi
import algorithms.pathThrough
import algorithms.vertices
import org.junit.Test

class Paths {
    @Test
    fun testPaths() {
        val (a,b,c,d, e) = vertices("a","b","c","d", "e")
        val graph = buildWeightedGraph(directed = true) {
            a.to(b, c).weighing(2,2)
            c.to(d).weighing(1)
            d.to(e).weighing(1)
            b.to(e).weighing(3)
        }

        val path = graph.pathThrough(b,c, d, a ,e)

        graphTest {
            GraphUi(graph)
        }
    }
}