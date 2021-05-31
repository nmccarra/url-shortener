package org.nmccarra1.url.shortener

import org.nmccarra1.url.shortener.dao.MySqlConnectionProvider
import org.nmccarra1.url.shortener.dao.UrlMappingsTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.nmccarra1.url.shortener.routes.UrlShortenerCreateRequest
import org.nmccarra1.url.shortener.services.ConfigService

object InMemoryDatabase {
    private val connection = MySqlConnectionProvider(ConfigService())
    val urlMappingsTable = UrlMappingsTable(connection)

    fun createTable() {
        transaction(db = connection.database) { SchemaUtils.create(urlMappingsTable) }
    }

    fun dropTables() {
        transaction(db = connection.database) { SchemaUtils.drop(urlMappingsTable) }
    }

    fun insertUrlShortenerEntries(urlShortenerCreateRequests: List<UrlShortenerCreateRequest>) {
        runBlocking {
            urlShortenerCreateRequests.forEach { urlMappingsTable.insert(it) }
        }
    }
}