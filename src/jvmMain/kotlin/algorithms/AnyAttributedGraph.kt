package algorithms

import kotlin.reflect.KClass

/**
 * Allows adding attributes to a graph, e.g. direction, weight, root
 */

typealias SingleAttributedGraph<T1> = AttributedGraph<T1, T1>

class AttributedGraphImpl(private val graph: Graph, override val attributes: GraphAttributes) :
    AttributedGraph<Nothing, Nothing>, Graph by graph

typealias DoubleAttributedGraph<T1, T2> = AttributedGraph<T1, T2>


// Access to weight will be available, access to directed will be need to made through a cast, unfortunately.
// That is because we can't implement the same interface twice with different type arguments.
typealias WeightedDirectedGraph = DoubleAttributedGraph<GraphAttribute.Weights, GraphAttribute.Directed>


typealias AnyAttributedGraph = AttributedGraph<*, *>

interface AttributedGraph<T1, T2> : Graph {
    val attributes: GraphAttributes
}

inline fun <reified T : GraphAttribute> AttributedGraph<T, *>.getAttribute(): T = attributes[T::class] as T

typealias GraphAttributes = Map<KClass<out GraphAttribute>, GraphAttribute>

val Graph.attributes
    get() = when (this) {
        is AnyAttributedGraph -> attributes
        else -> mapOf()
    }


fun <G : Graph> Graph.inheritAttributes(toGraph: G): G {
    if (this !is AnyAttributedGraph) return toGraph
    var sum: G = toGraph
    for (attribute in attributes.values) {
        sum = attribute.inherit(sum) as G
    }
    return sum
}


inline fun <reified T : GraphAttribute> Graph.withAttribute(attribute: T): SingleAttributedGraph<T> =
    AttributedGraphImpl(this, attributes = attributes + (T::class to attribute)) as SingleAttributedGraph<T>


sealed interface GraphAttribute {
    fun inherit(graph: Graph): Graph

    object Directed : GraphAttribute {
        override fun inherit(graph: Graph): Graph {
            return graph
        }

        override fun toString(): String = "Directed"
    }

    class Root(val root: Vertex) : GraphAttribute {
        override fun inherit(graph: Graph): Graph {
            return if (root in graph.vertices) graph.rootedAt(root) else graph
        }

        override fun toString(): String {
            return "Rooted at $root"
        }
    }

    class Weights(val weights: Map<Edge, Int>) : GraphAttribute {
        override fun toString(): String {
            return "Weighing ${weights.values.sum()}"
        }

        override fun inherit(graph: Graph): Graph {
            return graph.withWeights(graph.edges.associateWith { weights.getOrElse(it) { weights.getValue(it.flipped()) } })
        }
    }
}

inline fun <reified T : GraphAttribute> Graph.getGenericAttribute(): T? = attributes[T::class] as T?


inline fun <reified T : GraphAttribute> Graph.hasGenericAttribute(): Boolean = getGenericAttribute<T>() != null


typealias DirectedGraph = SingleAttributedGraph<GraphAttribute.Directed>

val Graph.isDirected: Boolean get() = hasGenericAttribute<GraphAttribute.Directed>()
val Graph.asDirected get() = this as DirectedGraph


typealias RootedTree = SingleAttributedGraph<GraphAttribute.Root>

val RootedTree.root: Vertex get() = getAttribute().root
val Graph.isRootedTree: Boolean get() = hasGenericAttribute<GraphAttribute.Root>()
val Graph.asRootedTree get() = this as RootedTree

fun Graph.rootedAt(vertexTag: VertexTag): RootedTree {
    return rootedAt(matchingVertex(vertexTag))
}

fun Graph.rootedAt(vertex: Vertex): RootedTree {
    return withAttribute(GraphAttribute.Root(vertex))
}


typealias WeightedGraph = AttributedGraph<GraphAttribute.Weights, *>

val WeightedGraph.weights get() = getAttribute().weights

context(WeightedGraph)
val Edge.weight
    get() = weights.getValue(this)

val Graph.isWeighted: Boolean get() = hasGenericAttribute<GraphAttribute.Weights>()
val Graph.asWeighted get() = this as WeightedGraph


fun Graph.withWeightTags(weights: Map<EdgeTag, Int>) = withWeights(weights.compileWeights(this))
fun Graph.withWeights(weights: Map<Edge, Int>) = withAttribute(GraphAttribute.Weights(weights))

private fun Map<EdgeTag, Int>.compileWeights(graph: Graph): Map<Edge, Int> {
    return graph.edges.associateWith { this[it.tag] ?: error("No weight for edge $it") }
}


