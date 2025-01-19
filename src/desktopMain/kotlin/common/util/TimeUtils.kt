package common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
private val displayFormat = DateTimeFormatter.ofPattern("dd-mm-yyyy HH:mm a")
val gitCommitTimeStampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun parseTimeStampToLocalDateTime(timeStamp: String): LocalDateTime? {
    val formattedTimeStamp = runCatching {
        LocalDateTime.parse(timeStamp, sourceFormat)
    }
    return formattedTimeStamp.getOrNull()
}

fun formateTimeStamp(timeStamp: String, formatter: DateTimeFormatter = sourceFormat): String? {
    return runCatching {
        LocalDateTime.parse(timeStamp, formatter)
            .format(displayFormat)
    }.onFailure { println(it) }
        .getOrNull()
}

fun LocalDateTime.format(): String? {
    return runCatching {
        displayFormat.format(this)
    }.getOrNull()
}