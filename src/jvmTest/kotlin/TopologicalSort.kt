import algorithms.*
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isContainedIn
import strikt.assertions.isEqualTo

class TopologicalSort {
    @Test
    fun testSuccessfulSort(){
        val (v1, v2, v3, v4, v5, v6, v7) = vertices("v1", "v2", "v3", "v4", "v5", "v6", "v7")
        val graph  = buildDirectedGraph {
            v1.to(v5, v4, v7)
            v2.to(v3, v5, v6)
            v3.to(v4, v5)
            v4.to(v5)
            v5.to(v6, v7)
            v6.to(v7)
        }

        expectThat(graph.toplogicallySort()?.map { it.tag }).isContainedIn(listOf(
            listOf(v1, v2, v3, v4, v5, v6, v7),
            listOf(v2, v1, v3, v4, v5, v6, v7)
        ))
    }
    @Test
    fun testSuccessfulSort2(){
        val (v1, v2, v3, v4, v5, v6, v7) = vertices("v1", "v2", "v3", "v4", "v5", "v6", "v7")
        val graph  = buildDirectedGraph {
            v1.to(v2, v5, v4, v7)
            v2.to(v3, v5, v6)
            v3.to(v4, v5)
            v4.to(v5)
            v5.to(v6, v7)
            v6.to(v7)
        }

        expectThat(graph.toplogicallySort()?.map { it.tag }).isEqualTo(listOf(v1, v2, v3, v4, v5, v6, v7),)
    }
    @Test
    fun testUnsuccessfulSort2(){
        val (v1, v2, v3, v4, v5, v6, v7) = vertices("v1", "v2", "v3", "v4", "v5", "v6", "v7")
        val graph  = buildDirectedGraph {
            v1.to(v2, v5, v4, v7)
            v2.to(v3, v5, v6)
            v3.to(v4, v5, v1)
            v4.to(v5)
            v5.to(v6, v7)
            v6.to(v7)
        }

        expectThat(graph.toplogicallySort()?.map { it.tag }).isEqualTo(null)
    }
}