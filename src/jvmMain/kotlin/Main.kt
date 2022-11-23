// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import algorithms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {

    // (1.0, 0.0, 0.0)
    val a = Color.Red named "Red"
    // (0.0, 1.0, 0.0)
    val b = Color.Green named "Green"
    // (0.0, 0.0, 1.0)
    val c = Color.Blue named "Blue"
    // (0.0, 0.0, 0.0)
    val d = Color.Black named "Black"
    // (0.0, 1.0, 1.0)
    val e = Color.Cyan named "Cyan"
    // (1.0, 1.0, 0.0)
    val f = Color.Yellow named "Yellow"

    val a2 = Color.Red.copy(alpha = 0.5f) named "OffRed"
    val b2 = Color.Green.copy(alpha = 0.5f) named "OffGreen"
    val c2 = Color.Blue.copy(alpha = 0.5f) named "OffBlue"
    val d2 = Color.Black.copy(alpha = 0.5f) named "OffBlack"
    val e2 = Color.Cyan.copy(alpha = 0.5f) named "OffCyan"
    val f2 = Color.Yellow.copy(alpha = 0.5f) named "OffYellow"

    val a3 = Color.Red.copy(alpha = 0.2f) named "OffOffRed"
    val b3 = Color.Green.copy(alpha = 0.2f) named "OffOffGreen"

    val graph = Graph.Builder(directed = true).apply {
        a edgeTo b
        b edgeTo c
        c edgeTo d
        d edgeTo e
        e edgeTo f
        b edgeTo a
        c edgeTo b
        b edgeTo f

        a2 edgeTo b2
        b2 edgeTo c2
        c2 edgeTo a2
        d2 edgeTo a2
        e2 edgeTo c2
        f2 edgeTo e2

        a3 edgeTo b3
    }.build()

    val orderedGraph = graph.vertices.first { it.tag == c }
    val bfs = graph.bfs(orderedGraph)
    val dfs = graph.dfs(orderedGraph)

    val v1 = Color.Red named "v1"
    val v2 = Color.Green named "v2"
    val v3 = Color.Blue named "v3"
    val v4 = Color.Black named "v4"
    val v5 = Color.Yellow named "v5"
    val v6 = Color.Cyan named "v6"
    val v7 = Color.Magenta named "v7"

    val topologicalSortTest = buildDirectedGraph {
        v1 edgeTo v5
        v1 edgeTo v4
        v1 edgeTo v7
        v2 edgeTo v3
        v2 edgeTo v5
        v2 edgeTo v6
        v3 edgeTo v4
        v3 edgeTo v5
        v4 edgeTo v5
        v5 edgeTo v6
        v5 edgeTo v7
        v6 edgeTo v7
    }

    println(topologicalSortTest.toplogicallySort())


    Column {
        GraphUi(topologicalSortTest)
//        GraphUi(graph, Modifier.padding(10.dp).weight(1f))
//        GraphUi(bfs, Modifier.padding(10.dp).weight(1f))
//        GraphUi(dfs, Modifier.padding(10.dp).weight(1f))
    }

}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
