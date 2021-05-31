package org.nmccarra1.url.shortener.services

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory


class ConfigService {
    private val config: Config = ConfigFactory.load()

    val configuration = Configuration(
        version = System.getenv("APP_VERSION") ?: "0.0.0",
        host = config.getString("url.shortener.host"),
        port = config.getString("url.shortener.port"),
        dbApp = UrlShortenerDatabase(
            hostname = config.getString("mysql.appdb.rw.hostname"),
            database = config.getString("mysql.appdb.rw.dbname"),
            username = config.getString("mysql.appdb.rw.username"),
            password = config.getString("mysql.appdb.rw.password"),
            properties = config.getString("mysql.appdb.rw.properties"),
            driverClassName = config.getString("mysql.appdb.rw.driverclassname")
        )

    )
}

data class Configuration(
    val version: String,
    val host: String,
    val port: String,
    val dbApp: UrlShortenerDatabase
)

data class UrlShortenerDatabase(
    val hostname: String,
    val database: String,
    val username: String,
    val password: String,
    val properties: String,
    val driverClassName: String
)