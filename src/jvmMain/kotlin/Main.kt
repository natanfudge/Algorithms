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