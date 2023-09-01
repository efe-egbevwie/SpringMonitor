package common.ui

import common.domain.HttpTrace
import common.domain.TraceRequest
import common.domain.TraceResponse
import java.time.LocalDateTime

val sampleTraceRequest = TraceRequest(
    requestMethod = "GET",
    host = listOf("167.71.6.36:8080"),
    url = "http://167.71.6.36:8080/actuator",
    userAgent = listOf("okhttp/4.11.0")
)
val sampleTraceResponse = TraceResponse(
    status = 200,
)
val sampleHttpTrace =
    HttpTrace(timeStamp = LocalDateTime.now(), request = sampleTraceRequest, response = sampleTraceResponse)

val sampleHttpTraceList = List(50) { index ->
    HttpTrace(
        timeStamp = LocalDateTime.now().plusHours(index.toLong()),
        request = sampleTraceRequest,
        response = sampleTraceResponse
    )
}