package common.ui

import domain.models.Application
import domain.models.HttpTrace
import domain.models.TraceRequest
import domain.models.TraceResponse
import domain.models.info.AppInfoDetail
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
    HttpTrace(timeStamp = LocalDateTime.now().toString(), request = sampleTraceRequest, response = sampleTraceResponse)

val sampleHttpTraceList = List(50) { index ->
    HttpTrace(
        timeStamp = LocalDateTime.now().plusHours(index.toLong()).toString(),
        request = sampleTraceRequest,
        response = sampleTraceResponse
    )
}


val sampleApplication = Application(
    alias = "Payments Server",
    actuatorUrl = "http://mypaymentserver.com/actuator",
    bearerToken = ""
)

val sampleApplications = List(20) { index ->
    Application(
        alias = "Server: $index",
        actuatorUrl = "http://myserver$index.com/actuator",
        bearerToken = ""
    )
}


val appInfoPreviewData = mapOf(
    "Git" to listOf(
        AppInfoDetail(title = "Branch", value = "main"),
        AppInfoDetail(title = "Time", value = "9/16/2023, 9:45 am "),
        AppInfoDetail(title = "User", value = "Efe Egbevwie"),
        AppInfoDetail(title = "Message", value = "fix bug with ID: 50389")
    ),

    "OS" to listOf(
        AppInfoDetail(title = "Operating system", value = "Windows 10 (amd64)"),
    ),

    "Java" to listOf(
        AppInfoDetail(title = "Version", value = "11"),
        AppInfoDetail(title = "Vendor", value = "IBM temurin"),
        ),
    "Build" to listOf(
        AppInfoDetail(title = "Version", value = "0.9"),
        AppInfoDetail(title = "Time", value = "9/16/2023, 9:45 am "),
        AppInfoDetail(title = "Group", value = "com.myApplication"),
        AppInfoDetail(title = "Artifact", value = "myApplication"),
        AppInfoDetail(title = "Name", value = "MyServer"),
        )
)



val gitInfoPreview = listOf(
    AppInfoDetail(title = "Branch", value = "main"),
    AppInfoDetail(title = "Time", value = "9/16/2023, 9:45 am "),
    AppInfoDetail(title = "User", value = "Efe Egbevwie"),
    AppInfoDetail(title = "Message", value = "fix bug with ID: 50389")
)

val operatingSystemInfoPreview = listOf(
    AppInfoDetail(title = "Operating system", value = "Windows 10 (amd64)"),
)

val javaInfoPreview = listOf(
    AppInfoDetail(title = "Version", value = "11"),
    AppInfoDetail(title = "Vendor", value = "IBM temurin"),
    )

val appInfoPreview = listOf(
    AppInfoDetail(title = "Version", value = "0.9"),
    AppInfoDetail(title = "Time", value = "9/16/2023, 9:45 am "),
    AppInfoDetail(title = "Group", value = "com.myApplication"),
    AppInfoDetail(title = "Artifact", value = "myApplication"),
    AppInfoDetail(title = "Name", value = "MyServer"),
    )