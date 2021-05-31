package org.nmccarra1.url.shortener.routes

import org.nmccarra1.url.shortener.UrlShortenerCreateRequests.urlShortenerCreateRequests
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.nmccarra1.url.shortener.InMemoryDatabase
import org.nmccarra1.url.shortener.testMain
import kotlin.test.assertEquals

class UrlShortenerTest {

    val mapper = jacksonObjectMapper()

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
    @DisplayName("/v1/shortened/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUrlFromId {

        @Test
        fun `should redirect request to the corresponding url`(): Unit = withTestApplication(testMain()) {
            with(handleRequest(HttpMethod.Get, "/v1/shortened/xdef45")) {
                assertEquals(HttpStatusCode.Found, response.status())
            }
        }

        @Test
        fun `should return Not Found when ID doesn't exist`(): Unit = withTestApplication(testMain()) {
            with(handleRequest(HttpMethod.Get, "/v1/shortened/xxxxxx")) {
                assertEquals(HttpStatusCode.NotFound, response.status())

                val content: UrlShortenerResponse = mapper.readValue(response.content!!)
                assertEquals(UrlShortenerResponse("No url for ID xxxxxx", "xxxxxx", null), content)
            }
        }

    }

    @Nested
    @DisplayName("/v1/shortened/url")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostUrlWithId {

        @Test
        fun `should create a shortened url for given new ID`(): Unit = withTestApplication(testMain()) {

            val urlShortenerCreateRequest = mapper.writeValueAsString(UrlShortenerCreateRequest("qw34gh", "https://sambarker.bandcamp.com"))
            with(handleRequest(HttpMethod.Post, "/v1/shortened/url") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(urlShortenerCreateRequest)
                }
            ) {
                assertEquals(HttpStatusCode.OK, response.status())

                val content: UrlShortenerResponse = mapper.readValue(response.content!!)
                assertEquals(UrlShortenerResponse("ID Created, please use shortened URL: http://localhost:8080/v1/shortened/qw34gh", "qw34gh", "https://sambarker.bandcamp.com"), content)
            }
        }

        @Test
        fun `should generate a random ID base64 when ID is not provided in request body`(): Unit = withTestApplication(testMain()) {

            with(handleRequest(HttpMethod.Post, "/v1/shortened/url") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("{ \"url\" : \"https://sambarker.bandcamp.com\" }")
            }
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        @Test
        fun `should return Conflict for existing ID`(): Unit = withTestApplication(testMain()) {

            val urlShortenerCreateRequest = mapper.writeValueAsString(urlShortenerCreateRequests.first())
            with(handleRequest(HttpMethod.Post, "/v1/shortened/url") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(urlShortenerCreateRequest)
            }
            ) {
                assertEquals(HttpStatusCode.Conflict, response.status())

                val content: UrlShortenerResponse = mapper.readValue(response.content!!)
                assertEquals(UrlShortenerResponse("There already exists a url with ID xdef45", "xdef45", "https://bandcamp.com/fraktalscrng"), content)
            }
        }

        @Test
        fun `should return Bad Request if the ID is over 6 characters`(): Unit = withTestApplication(testMain()) {

            val urlShortenerCreateRequest = mapper.writeValueAsString(UrlShortenerCreateRequest("qw34gh2222", "https://sambarker.bandcamp.com"))
            with(handleRequest(HttpMethod.Post, "/v1/shortened/url") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(urlShortenerCreateRequest)
            }
            ) {
                assertEquals(HttpStatusCode.BadRequest, response.status())

                val content: UrlShortenerResponse = mapper.readValue(response.content!!)
                assertEquals(UrlShortenerResponse(message="Invalid choice of ID, if providing an ID it must be 6 characters and base64", id="qw34gh2222", url="https://sambarker.bandcamp.com"), content)
            }
        }

        @Test
        fun `should return Bad Request if the ID is not base64`(): Unit = withTestApplication(testMain()) {

            val urlShortenerCreateRequest = mapper.writeValueAsString(UrlShortenerCreateRequest("qw/34g", "https://sambarker.bandcamp.com"))
            with(handleRequest(HttpMethod.Post, "/v1/shortened/url") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(urlShortenerCreateRequest)
            }
            ) {
                assertEquals(HttpStatusCode.BadRequest, response.status())

                val content: UrlShortenerResponse = mapper.readValue(response.content!!)
                assertEquals(UrlShortenerResponse(message="Invalid choice of ID, if providing an ID it must be 6 characters and base64", id="qw/34g", url="https://sambarker.bandcamp.com"), content)
            }
        }
    }
}
