package domain.models

import java.time.format.DateTimeFormatter

data class HttpTrace(
    val timeStamp: String,
    val request: TraceRequest,
    val response: TraceResponse
) {
    fun getFormattedTimeStamp(): String {
        val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyyy HH:mm a")
        return timeStamp.format(formatter)
    }
}


data class TraceRequest(
    val requestMethod: String,
    val url: String,
    val host: List<String>,
    val userAgent: List<String>,
)

data class TraceResponse(
    val status: Long,
    val headers: List<TraceResponseHeaders>? = null
)

data class TraceResponseHeaders(
    val transferEncoding: List<String>? = null,
    val keepAlive: List<String>? = null,
    val cacheControl: List<String>? = null,
    val xContentTypeOptions: List<String>? = null,
    val connection: List<String>? = null,
    val vary: List<String>? = null,
    val pragma: List<String>? = null,
    val expires: List<String>? = null,
    val date: List<String>? = null,
    val contentType: List<String>? = null
)

