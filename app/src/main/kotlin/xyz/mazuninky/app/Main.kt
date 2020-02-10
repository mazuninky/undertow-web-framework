package xyz.mazuninky.app

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import xyz.mazuninky.app.repository.ListRepository
import xyz.mazuninky.framework.server

fun main() {
    // DI section
    val db = createDB()

    val listRepository = ListRepository(db)
    val json = Json(JsonConfiguration.Stable)

    // Api section
    server {
        routing {
            get("/ping") { request ->
                request.responseSender.send("pong")
            }
        }
    }
}
