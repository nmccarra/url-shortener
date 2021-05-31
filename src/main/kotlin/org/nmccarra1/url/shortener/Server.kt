package org.nmccarra1.url.shortener

import com.google.inject.Guice
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KLogger
import mu.KotlinLogging
import org.nmccarra1.url.shortener.setup.createUrlShortenerAppDatabase
import kotlin.system.exitProcess


val logger: KLogger = KotlinLogging.logger { }

val defaultMain: Application.() -> Unit = fun Application.() {
    this.main {
        Guice.createInjector(
            UrlShortenerModule(it)
        )
    }
}

fun main() {
    logger.info { "Creating database if non-existent..."}
    createUrlShortenerAppDatabase()
    logger.info { "Starting URL Shortener API..."}
    embeddedServer(Netty, port = 8080, configure = {}, module = defaultMain)
        .start(wait = true)
    logger.info { "Stopping URL Shortener API..."}
    exitProcess(1)
}