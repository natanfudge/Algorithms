package linearAlg

import linearAlg.linearspace.LinearSpace

interface InnerProduct<T, V> {
    val linearSpace: LinearSpace<T, V>
}