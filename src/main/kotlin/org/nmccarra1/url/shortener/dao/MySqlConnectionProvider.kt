package org.nmccarra1.url.shortener.dao

import com.google.inject.Inject
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.nmccarra1.url.shortener.services.ConfigService
import org.nmccarra1.url.shortener.services.UrlShortenerDatabase

class MySqlConnectionProvider @Inject constructor(configService: ConfigService) {
    val database: Database

    init {
        database = Database.connect(hikari(configService.configuration.dbApp))
    }

    suspend fun <T> dbQuery(block: (Transaction) -> T): T =
        withContext(Dispatchers.IO){
            transaction(db = database) {
                block(this)
            }
        }

    private fun hikari(db: UrlShortenerDatabase): HikariDataSource {
        val config = HikariConfig()

        with(config){
            jdbcUrl = "${db.hostname}/${db.database}?${db.properties}}"
            username = db.username
            password = db.password
            driverClassName = db.driverClassName
            addDataSourceProperty("dataSource.cachePrepStmts", "true")
            addDataSourceProperty("dataSource.prepStmtCacheSize", "250")
            addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("dataSource.useServerPrepStmts", "true")
            addDataSourceProperty("dataSource.useLocalSessionState", "true")
            addDataSourceProperty("dataSource.rewriteBatchedStatements", "true")
            addDataSourceProperty("dataSource.cacheResultSetMetadata", "true")
            addDataSourceProperty("dataSource.cacheServerConfiguration", "true")
            addDataSourceProperty("dataSource.elideSetAutoCommits", "true")
            addDataSourceProperty("dataSource.maintainTimeStats", "false")

            validate()
        }
        return HikariDataSource(config)
    }
}