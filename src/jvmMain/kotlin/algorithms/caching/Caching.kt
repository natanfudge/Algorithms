@file:Suppress("MemberVisibilityCanBePrivate")

package algorithms.caching

class Cache(val items: MutableList<String>, val size: Int, val fetchOrder: List<String>) {
    var misses = 0
    var fetches = 0
    fun fetch(item: String) {
        if(!items.contains(item)) {
            misses++
            if(item.length == size) {
                evict()
            }
            items.add(item)
        }
        fetches++
    }

    fun evict() {
        // Eviction algorithm - farthest-in-future

    }
}