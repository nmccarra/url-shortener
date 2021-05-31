package org.nmccarra1.url.shortener

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Singleton
import org.nmccarra1.url.shortener.dao.MySqlConnectionProvider
import org.nmccarra1.url.shortener.dao.UrlMappingsTable
import io.ktor.application.Application
import io.mockk.mockk
import org.nmccarra1.url.shortener.services.ConfigService
import org.nmccarra1.url.shortener.services.UrlMappingsService

class TestModule(
    private val application: Application,
    private val urlShortenerModule: UrlShortenerModule
) : AbstractModule() {
    override fun configure() {
        binder().requireExplicitBindings()
        bind(MySqlConnectionProvider::class.java).asEagerSingleton()
        bind(UrlMappingsTable::class.java).`in`(Singleton::class.java)
        bind(Application::class.java).toInstance(application)
        bind(ConfigService::class.java).`in`(Singleton::class.java)
        bind(UrlMappingsService::class.java).`in`(Singleton::class.java)
        bind(MainRouting::class.java).asEagerSingleton()
        bind(UrlShortenerModule::class.java).toInstance(urlShortenerModule)
    }
}

val urlShortenerModuleMock: UrlShortenerModule = mockk(relaxed = true)

fun testMain(urlShortenerModule: UrlShortenerModule = urlShortenerModuleMock): Application.() -> Unit = fun Application.() {
    this.main {
        Guice.createInjector(TestModule(this, urlShortenerModule))
    }
}
