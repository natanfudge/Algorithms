package graph

import algorithms.Graph
import algorithms.buildUndirectedGraph
import algorithms.graphui.GraphUi
import algorithms.vertices
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.junit.Test

fun graphTest(graph: @Composable () -> Unit ) = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 1700.dp, height = 900.dp)
    ) {
        graph()
    }
}


class GraphTests  {
    @Test
    fun basicGraphTest() {
        val (a,b,c) = vertices("a","b","c")

        val graph = buildUndirectedGraph {
            a.to(b)
            b.to(c)
        }
        graphTest {
            GraphUi(graph)
        }
    }
}