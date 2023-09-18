package client.models

import domain.models.HttpTrace
import domain.models.TraceRequest
import domain.models.TraceResponse
import domain.models.TraceResponseHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Serializable
data class HttpTraceApiResponse(
    val traces: List<Trace>
)

@Serializable
data class Trace(
    val timestamp: Double,
    val principal: String? = null,
    val session: String? = null,
    val request: Request,
    val response: Response,
    val timeTaken: Long
)

@Serializable
data class Request(
    val method: String,
    val uri: String,
    val headers: RequestHeaders? = null,
    val remoteAddress: String? = null
)

@Serializable
data class RequestHeaders(
    val host: List<String>? = null,
    val connection: List<String>? = null,

    @SerialName("accept-encoding")
    val acceptEncoding: List<String>? = null,

    @SerialName("user-agent")
    val userAgent: List<String>? = null
)


@Serializable
data class Response(
    val status: Long,
    val headers: ResponseHeaders
)

@Serializable
data class ResponseHeaders(
    @SerialName("Transfer-Encoding")
    val transferEncoding: List<String>? = null,

    @SerialName("Keep-Alive")
    val keepAlive: List<String>? = null,

    @SerialName("Cache-Control")
    val cacheControl: List<String>? = null,

    @SerialName("X-Content-Type-Options")
    val xContentTypeOptions: List<String>? = null,

    @SerialName("Connection")
    val connection: List<String>? = null,

    @SerialName("Vary")
    val vary: List<String>? = null,

    @SerialName("Pragma")
    val pragma: List<String>? = null,

    @SerialName("Expires")
    val expires: List<String>? = null,

    @SerialName("Date")
    val date: List<String>? = null,

    @SerialName("Content-Type")
    val contentType: List<String>? = null
)

fun Trace.toDomainHttptrace(): HttpTrace {

    val instant = Instant.ofEpochSecond(this.timestamp.toLong(), (this.timestamp % 1 * 1_000_000_000).toLong())

    val zoneId = ZoneId.of("UTC+1") // Replace with your desired time zone
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)

    val requestTimeStamp = zonedDateTime.toLocalDateTime()


    val traceRequest = TraceRequest(
        requestMethod = this.request.method,
        url = this.request.uri,
        host = this.request.headers?.host ?: emptyList(),
        userAgent = this.request.headers?.userAgent ?: emptyList()
    )

    val traceResponseHeaders = TraceResponseHeaders(
        transferEncoding = this.response.headers.transferEncoding,
        keepAlive = this.response.headers.keepAlive,
        cacheControl = this.response.headers.cacheControl,
        xContentTypeOptions = this.response.headers.xContentTypeOptions,
        connection = this.response.headers.connection,
        vary = this.response.headers.vary,
        pragma = this.response.headers.pragma,
        expires = this.response.headers.expires,
        date = this.response.headers.date,
        contentType = this.response.headers.contentType
    )

    val traceResponse = TraceResponse(
        status = this.response.status,
        headers = listOf(traceResponseHeaders)
    )

    return HttpTrace(
        timeStamp = requestTimeStamp,
        request = traceRequest,
        response = traceResponse
    )
}

