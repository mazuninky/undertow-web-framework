package xyz.mazuninky.app

import kotlinx.coroutines.*
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.DriverManager
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DB(private val context: DSLContext) {
    suspend fun <T> execute(stmt: suspend DSLContext.() -> T): T {
        return withContext(Dispatchers.IO) {
            stmt(context)
        }
    }
}

fun createDB(): DB {
    val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "undertow", "12345")
    val ctx = DSL.using(connection, SQLDialect.POSTGRES)

    return DB(ctx)
}

