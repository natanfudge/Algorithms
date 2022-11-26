package algorithms

sealed interface WeightedGraph : Graph {
    fun weightOf(vertex: Vertex): Int

    class Directed(
        private val graph: Graph.Directed, private val weights: Map<Vertex, Int>
    ) : WeightedGraph, Graph.Directed by graph {
        override fun weightOf(vertex: Vertex): Int  = weights.getValue(vertex)
    }

    class Undirected(
        private val graph: Graph.Undirected, private val weights: Map<Vertex, Int>
    ) : WeightedGraph, Graph.Undirected by graph {
        override fun weightOf(vertex: Vertex): Int  = weights.getValue(vertex)
    }
}