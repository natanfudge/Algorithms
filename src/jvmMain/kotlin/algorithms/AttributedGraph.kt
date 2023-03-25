package algorithms

import org.jetbrains.annotations.Debug.Renderer
import kotlin.reflect.KClass

/**
 * Allows adding attributes to a graph, e.g. direction, weight, root
 */
@Renderer(text = "\"Graph:\" + toString()")
 class AttributedGraph<T : GraphAttribute>(
    val graph: Graph,
    private val clazz: KClass<T>,
    val attributes: Map<KClass<out GraphAttribute>, GraphAttribute>
) : Graph by graph {
    fun getAttribute(): T = attributes[clazz]!! as T
    override fun toString(): String {
        return graph.toString() + " (${attributes.values.joinToString()})"
    }
}


fun <G : Graph> Graph.inheritAttributes(toGraph: G): G {
    if (this !is AttributedGraph<*>) return toGraph
    var sum: G = toGraph
    for (attribute in attributes.values) {
        sum = attribute.inherit(sum) as G
    }
    return sum
}


inline fun <reified T : GraphAttribute> Graph.withAttribute(attribute: T): AttributedGraph<T> =
    if (this is AttributedGraph<*>) {
        AttributedGraph(graph, T::class, attributes = attributes + (T::class to attribute))
    } else AttributedGraph(this, T::class, mapOf(T::class to attribute))

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

inline fun <reified T : GraphAttribute> Graph.getGenericAttribute(): T? =
    if (this is AttributedGraph<*> && T::class in attributes) attributes[T::class] as T
    else null

inline fun <reified T : GraphAttribute> Graph.hasGenericAttribute(): Boolean = getGenericAttribute<T>() != null


typealias DirectedGraph = AttributedGraph<GraphAttribute.Directed>

val Graph.isDirected: Boolean get() = hasGenericAttribute<GraphAttribute.Directed>()
val Graph.asDirected get() = this as DirectedGraph


typealias RootedTree = AttributedGraph<GraphAttribute.Root>

val RootedTree.root get() = getAttribute().root
val Graph.isRootedTree: Boolean get() = hasGenericAttribute<GraphAttribute.Root>()
val Graph.asRootedTree get() = this as RootedTree

fun Graph.rootedAt(vertexTag: VertexTag): RootedTree {
    return rootedAt(matchingVertex(vertexTag))
}

fun Graph.rootedAt(vertex: Vertex): RootedTree {
    return withAttribute(GraphAttribute.Root(vertex))
}


typealias WeightedGraph = AttributedGraph<GraphAttribute.Weights>

val WeightedGraph.weights get() = getAttribute().weights

val Graph.isWeighted: Boolean get() = hasGenericAttribute<GraphAttribute.Weights>()
val Graph.asWeighted get() = this as WeightedGraph


fun Graph.withWeightTags(weights: Map<EdgeTag, Int>) = withWeights(weights.compileWeights(this))
fun Graph.withWeights(weights: Map<Edge, Int>) = withAttribute(GraphAttribute.Weights(weights))

private fun Map<EdgeTag, Int>.compileWeights(graph: Graph): Map<Edge, Int> {
    return graph.edges.associateWith { this[it.tag] ?: error("No weight for edge $it") }
}


