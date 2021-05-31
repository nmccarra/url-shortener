package org.nmccarra1.url.shortener.routes

import org.nmccarra1.url.shortener.dao.UrlMappingsEntry
import org.nmccarra1.url.shortener.utils.IdManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.nmccarra1.url.shortener.services.ConfigService
import org.nmccarra1.url.shortener.services.UrlMappingsService
import org.nmccarra1.url.shortener.utils.errorAware


/**
 * Endpoint definitions
 **/
fun Routing.urlShortener(urlMappingsService: UrlMappingsService, configService: ConfigService){
    val host = configService.configuration.host
    val port = configService.configuration.port

    /**
     * Redirects to the URL which maps from the given ID
     **/
    get<UrlShortenerSearchRequest> {
        errorAware {
            when (val entry = urlMappingsService.search(it.id)) {
                is UrlMappingsEntry -> call.respondRedirect(entry.url)
                else -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = UrlShortenerResponse(message = "No url for ID ${it.id}", id = it.id, url = null)
                )
            }
        }
    }
    /**
     * Create a mapping from the provided ID to the provided URL, if no ID is provided then one will be created base64
     **/
    post("/v1/shortened/url") {
        errorAware {
            val input = call.receive<UrlShortenerCreateRequest>()
            when (IdManager.isValidId(input.id)) {
                false -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = UrlShortenerResponse(
                        message = "Invalid choice of ID, if providing an ID it must be 6 characters and base64",
                        id = input.id,
                        url = input.url
                    )
                )
                else -> {
                    when (urlMappingsService.search(input.id)) {
                        is UrlMappingsEntry -> call.respond(
                            status = HttpStatusCode.Conflict,
                            message = UrlShortenerResponse(
                                message = "There already exists a url with ID ${input.id}",
                                id = input.id,
                                url = input.url
                            )
                        )
                        else -> {
                            urlMappingsService.create(input)
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = UrlShortenerResponse(
                                    message = "ID Created, please use shortened URL: http://${host}:${port}/v1/shortened/${input.id}",
                                    id = input.id,
                                    url = input.url
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Location("/v1/shortened/{id}")
data class UrlShortenerSearchRequest(
    val id: String
)

data class UrlShortenerCreateRequest(
    val id: String = IdManager.generate(),
    val url: String
)

data class UrlShortenerResponse(
    val message: String,
    val id: String,
    val url: String?
)
