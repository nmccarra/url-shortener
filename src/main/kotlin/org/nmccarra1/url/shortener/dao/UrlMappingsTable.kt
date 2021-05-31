package org.nmccarra1.url.shortener.dao

import com.google.inject.Inject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.nmccarra1.url.shortener.dao.MySqlConnectionProvider
import org.nmccarra1.url.shortener.routes.UrlShortenerCreateRequest
import java.time.Instant


class UrlMappingsTable @Inject constructor(
    private val connection: MySqlConnectionProvider
): Table(name = "URL_MAPPINGS")
{
    private val id = varchar("id", 6)
    private val url = varchar("url", 255)
    private val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)

    suspend fun select(id: String) = connection.dbQuery {
        this.select {
            this@UrlMappingsTable.id eq id
        }.map { toUrlMappingsEntry(it) }.firstOrNull()
    }

    suspend fun insert(urlShortenerCreateRequest: UrlShortenerCreateRequest) = connection.dbQuery {
        this.insert { toRow(urlShortenerCreateRequest, it) }
    }

    private fun toUrlMappingsEntry(row: ResultRow): UrlMappingsEntry {
        val id = row[this.id]
        val url = row[this.url]
        val createdAt = row[this.createdAt]

        return UrlMappingsEntry(id, url, createdAt)
    }

    private fun toRow(urlShortenerCreateRequest: UrlShortenerCreateRequest, insertStatement: InsertStatement<Number>) {
        insertStatement[id] = urlShortenerCreateRequest.id
        insertStatement[url] = urlShortenerCreateRequest.url
        insertStatement[createdAt] = Instant.now()
    }
}

data class UrlMappingsEntry(
    val id: String,
    val url: String,
    val createdAt: Instant
)