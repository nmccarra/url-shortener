package org.nmccarra1.url.shortener.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.MissingRequestParameterException
import io.ktor.features.ParameterConversionException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

data class BadRequestResponse(val message: String)

/**
 * Wrapper function which will catch Bad Request cases
 */
suspend fun PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> Unit) {
    return try {
        block()
    } catch (e: Exception) {
        when(e) {
            is MissingKotlinParameterException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse("Missing parameter: ${e.parameter.name.orEmpty()}")
            )
            is MissingRequestParameterException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse("Missing parameter: ${e.parameterName}")
            )
            is ParameterConversionException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse("Unable to convert parameter: ${e.parameterName}")
            )
            is UnrecognizedPropertyException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse("Unrecognised property: ${e.propertyName}")
            )
            is JsonProcessingException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse(e.originalMessage)
            )
            is MismatchedInputException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = BadRequestResponse(e.originalMessage)
            )
            else -> call.respond(
                status = HttpStatusCode.InternalServerError,
                message = BadRequestResponse(e.toString())
            )
        }
    }
}
