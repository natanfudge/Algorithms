package algorithms

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun DirectedGraph.toplogicallySort(): List<Vertex>? {
    val activeIncomingEdges: MutableMap<Vertex,Int> = vertices.associateWith { 0 }.toMutableMap()
    val noIncomingActiveEdges = mutableSetOf<Vertex>()

    for(edge in edges){
        activeIncomingEdges[edge.end] = activeIncomingEdges[edge.end]!! + 1
    }

    noIncomingActiveEdges.addAll(activeIncomingEdges.filter { it.value == 0 }.keys)

    val order = mutableListOf<Vertex>()
    while (order.size < vertices.size){
        val next = noIncomingActiveEdges.firstOrNull()?.also { noIncomingActiveEdges.remove(it) } ?: return null
        order.add(next)
        for(neighbor in neighborsOf(next)){
            activeIncomingEdges[neighbor] = activeIncomingEdges[neighbor]!! - 1
            if(activeIncomingEdges[neighbor] == 0) noIncomingActiveEdges.add(neighbor)
        }
    }
    return order
}



val DirectedGraph.isTopologicallySortable: Boolean get() = topologicalSort != null

private val sortCache = mutableMapOf<DirectedGraph,List<Vertex>?>()

val DirectedGraph.topologicalSort get() = sortCache.computeIfAbsent(this){
    toplogicallySort()
}