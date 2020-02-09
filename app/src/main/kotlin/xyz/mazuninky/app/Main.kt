package xyz.mazuninky.app

import xyz.mazuninky.framework.server

fun main() {
    server {
        routing {
            get("/ping") { _, response ->
                response.send("pong")
            }
        }
    }
}
