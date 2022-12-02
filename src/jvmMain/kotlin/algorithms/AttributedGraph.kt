package algorithms

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass


//open class AttributedGraph(val graph: Graph, val attributes: Map<KClass<out GraphAttribute>, GraphAttribute>) :
//    Graph by graph


inline fun <reified T : GraphAttribute> Graph.withAttribute(attribute: T): AttributedGraph<T> =
    if (this is AttributedGraph<*>) {
        AttributedGraph(graph, T::class, attributes = attributes + (T::class to attribute))
    } else AttributedGraph(this, T::class, mapOf(T::class to attribute))

sealed interface GraphAttribute {
    object Directed : GraphAttribute
    class Root(val root: Vertex) : GraphAttribute

    class Weights(val weights: Map<Vertex, Int>) : GraphAttribute
}

 inline fun <reified T : GraphAttribute> Graph.getGenericAttribute(): T? =
    if (this is AttributedGraph<*> && T::class in attributes) attributes[T::class] as T
    else null
 inline fun <reified T : GraphAttribute> Graph.hasGenericAttribute(): Boolean = getGenericAttribute<T>() != null




typealias DirectedGraph = AttributedGraph<GraphAttribute.Directed>
//val DirectedGraph.topologicalSort: List<Vertex>? get() = getAttribute().topologicalSort.value
val Graph.isDirected: Boolean get() = hasGenericAttribute<GraphAttribute.Directed>()
val Graph.asDirected get() = this as DirectedGraph


typealias RootedTree = AttributedGraph<GraphAttribute.Root>
val RootedTree.root get() = getAttribute().root
val Graph.isRootedTree: Boolean get() = hasGenericAttribute<GraphAttribute.Root>()
val Graph.asRootedTree get() = this as GraphAttribute.Root

fun Graph.rootedAt(vertexTag: VertexTag): RootedTree {
    val matchingVertex = vertices.find { it.tag == vertexTag } ?: throw IllegalArgumentException("No such vertex $vertexTag in graph.")
    return withAttribute(GraphAttribute.Root(matchingVertex))
}


typealias WeightedGraph = AttributedGraph<GraphAttribute.Weights>
val Graph.isWeighted: Boolean get() = hasGenericAttribute<GraphAttribute.Weights>()
val Graph.asWeighted get() = this as WeightedGraph





/**
 * Allows adding attributes to a graph, e.g. direction, weight, root
 */
open class AttributedGraph<T : GraphAttribute>(
    val graph: Graph,
    private val clazz: KClass<T>,
    val attributes: Map<KClass<out GraphAttribute>, GraphAttribute>
)  : Graph by graph{
    fun getAttribute(): T = attributes[clazz]!! as T
}






///**
// * Allows adding attributes to a graph, e.g. direction, weight, root
// */
//open class AttributedGraph2<T1: GraphAttribute, T2>(graph: Graph,clazz1: KClass<T1>,  attributes: Map<KClass<out GraphAttribute>, GraphAttribute>) :
//     AttributedGraph1<T1>(graph,attributes)
//
//
///**
// * Allows adding attributes to a graph, e.g. direction, weight, root
// */
//class AttributedGraph3<T1: GraphAttribute, T2, T3>(graph: Graph,attributes: Map<KClass<out GraphAttribute>, GraphAttribute>) :
//     AttributedGraph2<T1,T2>(graph,attributes)


//fun AttributedGraph1<GraphAttribute.Directed>.onlyDirected() {
//    val shit = getAttribute()
//}
