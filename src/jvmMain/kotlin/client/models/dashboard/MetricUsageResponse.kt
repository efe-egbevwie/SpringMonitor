package client.models.dashboard

import common.domain.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlin.time.Duration.Companion.seconds

@Serializable
data class MetricUsageResponse(
    val name: String,
    val description: String,
    val baseUnit: String? = null,
    val measurements: List<Measurement>? = null,
    val availableTags: JsonArray? = null
)

@Serializable
data class Measurement(
    val statistic: String,
    val value: Double
)


fun MetricUsageResponse.toProcessUpTime(): ProcessUpTime? {
    val processUpTimeInSeconds = this.measurements?.firstOrNull() ?: return null
    return ProcessUpTime(processUpTimeInSeconds.value.seconds)
}

fun MetricUsageResponse.toCpuUsage(): CpuUsage? {
    val cpuUsage = this.measurements?.firstOrNull() ?: return null
    return CpuUsage(cpuUsage.value)
}


fun MetricUsageResponse.toMemoryUsed(): MemoryUsed? {
    val memoryUsage = this.measurements?.firstOrNull() ?: return null
    val memoryUsageBytes = memoryUsage.value
    return MemoryUsed(memoryUsageBytes)
}


fun MetricUsageResponse.toMemoryMax(): MaxMemory? {
    val maxMemory = this.measurements?.firstOrNull() ?: return null
    val maxMemoryInBytes = maxMemory.value
    return MaxMemory(maxMemoryInBytes)
}

fun MetricUsageResponse.toDiskTotal(): DiskTotal? {
    val diskTotal = this.measurements?.firstOrNull() ?: return null
    val diskTotalInBytes = diskTotal.value
    return DiskTotal(diskTotalInBytes)
}


fun MetricUsageResponse.toDiskFree(): DiskFree? {
    val diskFree = this.measurements?.firstOrNull() ?: return null
    val diskFreeInBytes = diskFree.value
    return DiskFree(diskFreeInBytes)
}


