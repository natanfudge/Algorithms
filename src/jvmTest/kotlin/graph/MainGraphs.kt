package graph

import algorithms.*
import algorithms.graphui.GraphUi
import org.junit.Test

class MainGraphs {
    @Test
    fun mainGraphs() {
        val (a, b, c, d, e, f,
            a2, b2, c2, d2, e2, f2,
            a3, b3
        ) = vertices("a", "b", "c", "d", "e", "f", "a2", "b2", "c2", "d2", "e2", "f2", "a3", "b3")

        val graph: Graph = buildWeightedGraph(directed = true) {
            a.to(b).weighing(2)
            b.to(a, c, f).weighing(3, 4, 5)
            c.to(b, d)
            d.to(e)
            e.to(f)

            a2 edgeTo b2
            b2 edgeTo c2
            c2 edgeTo a2
            d2 edgeTo a2
            e2 edgeTo c2
            f2 edgeTo e2


            a3 edgeTo b3
        }

//    val weights = graph.edges.associateWith { 1 }.mapKeys { it.key.tag } + mapOf( a to 2)
//
//    val weightedGraph = graph.withWeightTags(weights)

//    val rootedGraph = graph.rootedAt(blue)
        val rootedGraph = graph.rootedAt(a)
        val bfs = rootedGraph.bfs()
        val dfs = rootedGraph.dfs()


        graphTest {
            GraphUi(graph)
        }


//        Column {
////        topologicalGraph( Modifier.padding(horizontal = 10.dp))
//            GraphUi(graph, Modifier.padding(10.dp).weight(1f))
////        GraphUi(bfs, Modifier.padding(horizontal = 10.dp).weight(1f))
////        GraphUi(dfs, Modifier.padding(horizontal = 10.dp).weight(1f))
//        }

    }
}