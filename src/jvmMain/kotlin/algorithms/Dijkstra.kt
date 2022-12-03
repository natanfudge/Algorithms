package algorithms


fun WeightedGraph.dijkstra(start: EdgeTag, end: EdgeTag) = dijkstra(matchingEdge(start),matchingEdge(end))

//TODO: important: see Test 2020a-b question2, and try to solve after doing this.
fun WeightedGraph.dijkstra(start: Edge, end: Edge): List<Vertex> {
    TODO("Do from book, see how there's different ways to implement the data structure as well. ")
}