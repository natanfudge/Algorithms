package algorithms

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 * Allows adding attributes to a graph, e.g. direction, weight, root
 */
open class AttributedGraph(val graph: Graph, val attributes: Map<KClass<out GraphAttribute>, GraphAttribute>) :
    Graph by graph


inline fun <reified T : GraphAttribute> Graph.withAttribute(attribute: T): AttributedGraph1<T> =
    if (this is AttributedGraph) {
        AttributedGraph1(graph, T::class, attributes = attributes + (T::class to attribute))
    } else AttributedGraph1(this, T::class, mapOf(T::class to attribute))

sealed interface GraphAttribute {
    class Directed(val topologicalSort: Lazy<List<Vertex>?>) : GraphAttribute
    class Root(val root: Vertex) : GraphAttribute

    class Weights(val weights: Map<Vertex, Int>) : GraphAttribute
}

 inline fun <reified T : GraphAttribute> Graph.getGenericAttribute(): T? =
    if (this is AttributedGraph && T::class in attributes) attributes[T::class] as T
    else null

val Graph.isDirected: Boolean get() = getGenericAttribute<GraphAttribute.Directed>() != null
val Graph.isRootedTree: Boolean get() = getGenericAttribute<GraphAttribute.Root>() != null


//val Graph.topologicalSort: List<Vertex>? get() = getAttribute<GraphAttribute.Directed>()?.topologicalSort?.value
//val Graph.root: Vertex? get() = getAttribute<GraphAttribute.Root>()?.root
//val Graph.weights: Map<Vertex, Int>? get() = getAttribute<GraphAttribute.Weights>()?.weights


//TODO: start out with AttributeGraph1 approach, then try to do real strong typing and see if that works

//TODO: this approach seems awesome.
// for generic usages, use Graph, and relevant extensions
// for specific usages, use type aliases of AttributedGraph1 and tailored extensions


/**
 * Allows adding attributes to a graph, e.g. direction, weight, root
 */
open class AttributedGraph1<T : GraphAttribute>(
    graph: Graph,
    private val clazz: KClass<T>,
    attributes: Map<KClass<out GraphAttribute>, GraphAttribute>
) :
    AttributedGraph(graph, attributes) {
    fun getAttribute(): T = attributes[clazz]!! as T
}

typealias RootedTree = AttributedGraph1<GraphAttribute.Root>
val RootedTree.root get() = getAttribute().root
typealias DirectedGraph = AttributedGraph1<GraphAttribute.Directed>
val DirectedGraph.topologicalSort: List<Vertex>? get() = getAttribute().topologicalSort.value


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


fun AttributedGraph1<GraphAttribute.Directed>.onlyDirected() {
    val shit = getAttribute()
}
