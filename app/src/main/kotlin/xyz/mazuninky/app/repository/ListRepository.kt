package xyz.mazuninky.app.repository

import org.jooq.DSLContext
import xyz.mazuninky.app.DB
import xyz.mazuninky.jooq.tables.List.LIST

class ListRepository(private val db: DB) {

    suspend fun create(name: String): Long? {
        return db.execute {
            val record = insertInto(LIST, LIST.NAME)
                .values(name)
                .returning(LIST.ID)
                .fetchOne()

            record.get(LIST.ID)
        }
    }
}
