package org.nmccarra1.url.shortener.services

import org.nmccarra1.url.shortener.UrlShortenerCreateRequests.urlShortenerCreateRequests
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.nmccarra1.url.shortener.InMemoryDatabase
import org.nmccarra1.url.shortener.routes.UrlShortenerCreateRequest
import kotlin.test.assertEquals

class UrlMappingsServiceTest {

    private val urlMappingsService = UrlMappingsService(InMemoryDatabase.urlMappingsTable)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            InMemoryDatabase.createTable()
            InMemoryDatabase.insertUrlShortenerEntries(urlShortenerCreateRequests)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            InMemoryDatabase.dropTables()
        }
    }

    @Nested
    @DisplayName("UrlShortenerService.search")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchUrlShortenerEntries {

        @Test
        fun `should return the url shortener entry`() {
            runBlocking {
                val responseOne = urlMappingsService.search("xdef45")
                val responseTwo = urlMappingsService.search("qwe123")
                val responseThree = urlMappingsService.search("poj2k3")
                assertEquals("https://bandcamp.com/fraktalscrng", responseOne?.url)
                assertEquals("https://phasefatale.bandcamp.com", responseTwo?.url)
                assertEquals("https://bicep.bandcamp.com/music", responseThree?.url)
            }
        }

        @Test
        fun `should return null`() {
            runBlocking {
                assertEquals(null, urlMappingsService.search("xxxxxx"))
            }
        }
    }

    @Nested
    @DisplayName("UrlShortenerService.create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CreateShortenedUrl {

        @Test
        fun `should create a shortened url for given url`() {
            runBlocking {
                urlMappingsService.create(
                    UrlShortenerCreateRequest(
                        "21weqd",
                        "https://amiina.bandcamp.com/album/pharology"
                    )
                )

                assertEquals("https://amiina.bandcamp.com/album/pharology", urlMappingsService.search("21weqd")?.url)
            }
        }

        @Test
        fun `should throw an exception a new record is attempted to be created with an existing shortened url`() {
            runBlocking {
                Assertions.assertThrows(ExposedSQLException::class.java) {
                    runBlocking {
                        urlMappingsService.create(
                            UrlShortenerCreateRequest(
                                "sfew22",
                                "https://whitelabrecs.bandcamp.com/album/svalbar"
                            )
                        )
                        urlMappingsService.create(
                            UrlShortenerCreateRequest(
                                "sfew22",
                                "https://whitelabrecs.bandcamp.com/album/svalbar"
                            )
                        )
                    }
                }
            }
        }
    }
}