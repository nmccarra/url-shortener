package org.nmccarra1.url.shortener.services

import com.google.inject.Inject
import org.nmccarra1.url.shortener.dao.UrlMappingsEntry
import org.nmccarra1.url.shortener.dao.UrlMappingsTable
import org.nmccarra1.url.shortener.routes.UrlShortenerCreateRequest

class UrlMappingsService @Inject constructor(
    private val urlMappingsTable: UrlMappingsTable
) {
    suspend fun search(id: String): UrlMappingsEntry? = urlMappingsTable.select(id)

    suspend fun create(urlShortenerCreateRequest: UrlShortenerCreateRequest) = urlMappingsTable.insert(urlShortenerCreateRequest)
}