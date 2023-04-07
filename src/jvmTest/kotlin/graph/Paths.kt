package graph

import algorithms.*
import algorithms.graphui.GraphUi
import org.junit.Test

class Paths {
    @Test
    fun testPaths() {
        val (a,b,c,d, e) = vertices("a","b","c","d", "e")
        val graph = buildWeightedDirectedGraph {
            a.to(b, c).weighing(2,2)
            c.to(d).weighing(1)
            d.to(e).weighing(1)
            b.to(e).weighing(3)
        }

        val path = graph.pathThrough(a, c, d)
        val path2 = graph.pathThrough(b, e)

        graphTest {
            GraphUi(graph, paths = listOf(path, path2))
        }
    }

    @Test
    fun testDjikstra() {
        val (a,b,c,d, e) = vertices("a","b","c","d", "e")
        val graph = buildWeightedDirectedGraph {
            a.to(b, c).weighing(2,2)
            c.to(d).weighing(1)
            d.to(e).weighing(1)
            b.to(e).weighing(3)
        }

        val path = graph.dijkstra(a, e)

        graphTest {
            GraphUi(graph, paths = listOf(path))
        }
    }
}