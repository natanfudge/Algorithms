// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import algorithms.*
import algorithms.graphui.GraphUi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    val (a, b, c, d, e, f,
        a2, b2, c2, d2, e2, f2,
        a3, b3
    ) = vertices("a", "b", "c", "d", "e", "f", "a2", "b2", "c2", "d2", "e2", "f2", "a3", "b3")

    val graph: Graph = buildDirectedGraph {
        a.to(b)
        b.to(a, c, f)
        c.to(b, d)
        d.to(e)
        e.to(f)
//
        a2 edgeTo b2
        b2 edgeTo c2
        c2 edgeTo a2
        d2 edgeTo a2
        e2 edgeTo c2
        f2 edgeTo e2


        a3 edgeTo b3
    }

    val weights = graph.edges.associateWith { 1 }

    val weightedGraph = graph.withWeights(weights)

//    val rootedGraph = graph.rootedAt(blue)
    val rootedGraph = graph.rootedAt(a)
    val bfs = rootedGraph.bfs()
    val dfs = rootedGraph.dfs()




    Column {
//        topologicalGraph( Modifier.padding(horizontal = 10.dp))
        GraphUi(weightedGraph, Modifier.padding(10.dp).weight(1f))
//        GraphUi(bfs, Modifier.padding(horizontal = 10.dp).weight(1f))
//        GraphUi(dfs, Modifier.padding(horizontal = 10.dp).weight(1f))
    }

}


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 1700.dp, height = 900.dp)
    ) {
        App()
    }
}

@Composable
private fun topologicalGraph(modifier: Modifier = Modifier) {
    val (v1, v2, v3, v4, v5, v6, v7) = vertices("v1", "v2", "v3", "v4", "v5", "v6", "v7")

    val topologicalSortTest = buildDirectedGraph {
        v1.to(v5, v4, v7)
        v4.to(v7)
        v5.to(v7)
//        v2.to(v3, v5, v6)
//        v3.to(v4, v5)
//        v4.to(v5)
//        v5.to(v6, v7)
//        v6.to(v7)
    }

    val weights = topologicalSortTest.edges.associateWith { 1 }
    val weightedGraph = topologicalSortTest.withWeights(weights)

//    GraphUi(topologicalSortTest, modifier)
    GraphUi(weightedGraph, modifier)
}