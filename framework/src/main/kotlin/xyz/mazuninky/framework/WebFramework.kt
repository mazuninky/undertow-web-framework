package xyz.mazuninky.framework

import xyz.mazuninky.framework.router.Router
import xyz.mazuninky.framework.router.buildRouter
import io.undertow.Undertow
import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange
import java.util.*

typealias RequestHandler = (exchange: HttpServerExchange, response: Sender) -> Unit

typealias WebRouterMap = MutableMap<HttpMethod, Router<RequestHandler>>

enum class HttpMethod {
    GET, POST, HEAD, OPTIONS, DELETE, PUT
}

class ServerFramework(host: String, port: Int, private val routerMap: WebRouterMap) {
    private val undertow: Undertow

    init {
        undertow = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(::requestHandler)
            .build()
    }

    private fun requestHandler(exchange: HttpServerExchange) {
        val httpMethod = HttpMethod.valueOf(exchange.requestMethod.toString())
        val methodRouter = routerMap[httpMethod]

        methodRouter
            ?.route(exchange.requestPath)
            ?.invoke(exchange, exchange.responseSender)
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
    private lateinit var webRouterMap: WebRouterMap

    fun routing(routerBuilder: WebRouterBuilder.() -> Unit) {
        val builder = WebRouterBuilder()

        webRouterMap = builder.apply(routerBuilder).build()
    }

    fun build(host: String, port: Int): ServerFramework {
        return ServerFramework(host, port, webRouterMap)
    }
}

class WebRouterBuilder {
    private val routes: MutableMap<HttpMethod, MutableMap<String, RequestHandler>> =
        EnumMap<HttpMethod, MutableMap<String, RequestHandler>>(HttpMethod::class.java)

    init {
        HttpMethod.values().forEach {
            routes[it] = mutableMapOf()
        }
    }

    private fun handle(method: HttpMethod, url: String, handler: RequestHandler) {
        routes.getValue(method)[url] = handler
    }

    fun get(url: String, handler: RequestHandler) {
        handle(HttpMethod.GET, url, handler)
    }

    fun post(url: String, handler: RequestHandler) {
        handle(HttpMethod.POST, url, handler)
    }

    fun put(url: String, handler: RequestHandler) {
        handle(HttpMethod.PUT, url, handler)
    }

    internal fun build(): WebRouterMap {
        val webRouterMap = EnumMap<HttpMethod, Router<RequestHandler>>(HttpMethod::class.java)
        routes.forEach { (method, list) ->
            webRouterMap[method] = buildRouter(list)
        }
        return webRouterMap
    }
}
