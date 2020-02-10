package xyz.mazuninky.framework

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.RoutingHandler
import io.undertow.util.SameThreadExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

typealias RequestHandler = (exchange: HttpServerExchange) -> Unit

typealias CoroutinesRequestHandler = suspend (exchange: HttpServerExchange) -> Unit

fun coroutinesHandlerAdapter(handler: CoroutinesRequestHandler): RequestHandler {
    return { exchange ->
        exchange.dispatch(
            SameThreadExecutor.INSTANCE,
            Runnable {
                GlobalScope.launch(Dispatchers.Default) {
                    handler(exchange)
                }
            }
        )
    }
}

enum class HttpMethod {
    GET, POST, HEAD, OPTIONS, DELETE, PUT
}

data class RouteDescription(val method: HttpMethod, val route: String, val handler: CoroutinesRequestHandler)

class ServerFramework(host: String, port: Int, routeList: List<RouteDescription>) {
    private val undertow: Undertow

    init {
        val builder = Undertow.builder()
            .addHttpListener(port, host)

        val router = RoutingHandler()
        routeList.forEach {
            val (method, route, handler) = it
            router.add(method.toString(), route, coroutinesHandlerAdapter(handler))
        }

        builder.setHandler(router)

        undertow = builder.build()
    }

    fun run() {
        undertow.start()
    }
}

fun server(
    host: String = "0.0.0.0",
    port: Int = 8080,
    builder: ServerBuilder.() -> Unit
) {
    val serverBuilder = ServerBuilder()
    serverBuilder.apply(builder)

    val server = serverBuilder.build(host, port)
    server.run()
}

class ServerBuilder {
    private lateinit var routeList: List<RouteDescription>

    fun routing(routerBuilder: WebRouteDSL.() -> Unit) {
        val dsl = WebRouteDSL()
        routeList = dsl.apply(routerBuilder).routes
    }

    fun build(host: String, port: Int): ServerFramework {
        return ServerFramework(host, port, routeList)
    }
}

class WebRouteDSL {
    internal val routes = mutableListOf<RouteDescription>()

    fun get(url: String, handler: CoroutinesRequestHandler) {
        routes.add(RouteDescription(HttpMethod.GET, url, handler))
    }

    fun post(url: String, handler: CoroutinesRequestHandler) {
        routes.add(RouteDescription(HttpMethod.POST, url, handler))
    }

    fun put(url: String, handler: CoroutinesRequestHandler) {
        routes.add(RouteDescription(HttpMethod.PUT, url, handler))
    }
}
