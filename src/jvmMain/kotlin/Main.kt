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
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
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

    val a2 = Color.Red.copy(alpha = 0.5f) named "ORed"
    val b2 = Color.Green.copy(alpha = 0.5f) named "OGreen"
    val c2 = Color.Blue.copy(alpha = 0.5f) named "OBlue"
    val d2 = Color.Black.copy(alpha = 0.5f) named "OBlack"
    val e2 = Color.Cyan.copy(alpha = 0.5f) named "OCyan"
    val f2 = Color.Yellow.copy(alpha = 0.5f) named "OYellow"

    val a3 = Color.Red.copy(alpha = 0.2f) named "X"
    val b3 = Color.Green.copy(alpha = 0.2f) named "Y"

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

    val (v1, v2, v3, v4, v5, v6, v7) = vertices("v1", "v2", "v3", "v4", "v5", "v6", "v7")

    val topologicalSortTest = buildDirectedGraph {
        v1.to(v5, v4, v7)
        v2.to(v3, v5, v6)
        v3.to(v4, v5)
        v4.to(v5)
        v5.to(v6, v7)
        v6.to(v7)
    }



    Column {
        GraphUi(topologicalSortTest, Modifier.padding(horizontal = 10.dp).weight(1f))
        GraphUi(graph, Modifier.padding(horizontal = 10.dp).weight(1f))
        GraphUi(bfs, Modifier.padding(horizontal = 10.dp).weight(1f))
        GraphUi(dfs, Modifier.padding(horizontal = 10.dp).weight(1f))
    }

}


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 1700.dp, height = 900.dp, /*position = WindowPosition(0.dp, 0.dp)*/)
    ) {
        App()
    }
}
