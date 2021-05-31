package org.nmccarra1.url.shortener.logging

import ch.qos.logback.classic.spi.ILoggingEvent


class JsonLayout: ch.qos.logback.contrib.json.classic.JsonLayout() {
    private val APP_VERSION = System.getenv("APP_VERSION") ?: "MISSING_APP_VERSION"
    private val APP_VERSION_ATTRIBUTE_NAME = "appVersion"

    override fun toJsonMap(event: ILoggingEvent?): Map<String, Any?> {
        val map = super.toJsonMap(event)
            .map { (key, value) -> key.toString() to value }
            .toMap()

        add(APP_VERSION_ATTRIBUTE_NAME, true, APP_VERSION, map)

        return map
    }
}