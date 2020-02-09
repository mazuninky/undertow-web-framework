package xyz.mazuninky.framework.router

inline class Router<T>(val tree: RadixTree<T>) {
    fun route(path: String): T? {
        return tree.get(path)
    }
}

fun <T> buildRouter(map: Map<String, T>): Router<T> {
    return Router(RadixTree(map.toList()))
}

fun <T> buildRouter(list: List<Pair<String, T>>): Router<T> {
    return Router(RadixTree(list))
}

