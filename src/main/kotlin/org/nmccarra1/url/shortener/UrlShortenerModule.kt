package org.nmccarra1.url.shortener

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import org.nmccarra1.url.shortener.dao.MySqlConnectionProvider
import org.nmccarra1.url.shortener.dao.UrlMappingsTable
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.routing.routing
import org.slf4j.event.Level
import org.nmccarra1.url.shortener.routes.urlShortener
import org.nmccarra1.url.shortener.services.ConfigService
import org.nmccarra1.url.shortener.services.UrlMappingsService

fun Application.main(injectorCreator: (Application) -> Injector) {
    install(ContentNegotiation){
        jackson {
            registerModule(JavaTimeModule())
        }
    }
    install(CallLogging) { level = Level.INFO}
    install(Locations)
    install(DataConversion)

    injectorCreator(this)
}

class MainRouting @Inject constructor(
    application: Application,
    urlMappingsService: UrlMappingsService,
    configService: ConfigService
) {
    init {
        application.routing {
            urlShortener(urlMappingsService, configService)
        }
    }
}

class UrlShortenerModule(private val application: Application): AbstractModule() {
    override fun configure() {
        binder().requireExplicitBindings()
        bind(MySqlConnectionProvider::class.java).asEagerSingleton()
        bind(UrlMappingsTable::class.java).`in`(Singleton::class.java)
        bind(Application::class.java).toInstance(application)
        bind(ConfigService::class.java).`in`(Singleton::class.java)
        bind(UrlMappingsService::class.java).`in`(Singleton::class.java)
        bind(MainRouting::class.java).asEagerSingleton()
    }
}