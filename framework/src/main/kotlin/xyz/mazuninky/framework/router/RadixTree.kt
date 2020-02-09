package xyz.mazuninky.framework.router

class RadixTree<T>(values: List<Pair<String, T>>) {
    private var root = RadixNode<T>()

    init {
        values.forEach {
            if (it.first.isEmpty())
                return@forEach
            var currentNode = root.getOrCreateAndAdd(it.first.first())
            //Find more elegant way
            for (item in it.first.removePrefix(it.first.first().toString())) {
                RadixNode<T>().also {
                    currentNode.add(item, it)
                    currentNode = it
                }
            }
            currentNode.value = it.second
        }
    }

    fun get(path: String): T? {
        if (path.isEmpty())
            return null

        var currentNode = root
        for (item in path) {
            val nextNode = currentNode.get(item) ?: return null
            currentNode = nextNode
        }
        return currentNode.value
    }
}

class RadixNode<T>(var value: T? = null, private val childs: MutableMap<Char, RadixNode<T>> = mutableMapOf()) {
    fun add(key: Char, item: RadixNode<T>) {
        childs[key] = item
    }

    fun contains(key: Char): Boolean = childs.containsKey(key)

    fun get(key: Char): RadixNode<T>? = childs[key]

    fun getOrCreateAndAdd(key: Char): RadixNode<T> {
        var node = get(key)
        if (node == null) {
            node = RadixNode()
            add(key, node)
        }
        return node
    }
}
