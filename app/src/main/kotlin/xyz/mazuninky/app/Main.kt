package xyz.mazuninky.app

import io.undertow.server.HttpServerExchange

fun main() {
    server {
        routing {
            get("/ping") { _, response ->
                response.send("pong")
            }
        }
    }
}
