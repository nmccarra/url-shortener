package org.nmccarra1.url.shortener.setup

import org.nmccarra1.url.shortener.dao.MySqlConnectionProvider
import org.nmccarra1.url.shortener.dao.UrlMappingsTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.nmccarra1.url.shortener.logger
import org.nmccarra1.url.shortener.services.ConfigService

/**
 * Creates the URL_MAPPINGS table
 */
fun createUrlShortenerAppDatabase() {
    logger.info("Connecting to local database")
    val configService = ConfigService()
    val connection = MySqlConnectionProvider(configService)
    val urlMappingsTable = UrlMappingsTable(connection)
    logger.info("Connected to local database")

    transaction(db = connection.database) {
        addLogger(StdOutSqlLogger)
        logger.info("Creating Tables")
        SchemaUtils.create(urlMappingsTable)
    }
}

fun main() {
    createUrlShortenerAppDatabase()
}