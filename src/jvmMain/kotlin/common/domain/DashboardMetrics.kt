package common.domain

import kotlin.time.Duration

data class DashboardMetrics(
    var status: String,
    val upTime: ProcessUpTime?,
    val cpuUsagePercent: CpuUsage?,
    val maxMemory: MaxMemory?,
    val memoryUsed: MemoryUsed?,
    val diskTotal: DiskTotal?,
    val diskFree: DiskFree?
) {
    fun getMemoryUsedPercentage(): Double? {

        val maxMemory = this.maxMemory?.value() ?: return null
        val memoryUsed = this.memoryUsed?.value() ?: return null
        return (memoryUsed / maxMemory) * 100
    }

    fun getMemoryUsedFormattedPercentage(): String {
        return "${String.format("%.0f", getMemoryUsedPercentage())}%"
    }


    fun getDiskUsedPercentage(): Double? {
        val diskTotal = this.diskTotal?.value() ?: return null
        val diskFree = this.diskFree?.value() ?: return null
        val diskUsed = diskTotal - diskFree

        println("disk total: $diskTotal")
        println("disk free: $diskFree")
        println("disk used: $diskUsed")

        return (diskUsed / diskTotal) * 100
    }

    fun getDiskUsedFormattedPercentage(): String {
        return "${String.format("%.0f", getDiskUsedPercentage())}%"
    }

}

@JvmInline
value class ProcessUpTime(private val time: Duration) {
    fun getFormattedTime(): String {
        return this.time.toString()
    }
}

@JvmInline
value class CpuUsage(private val percentage: Double) {

    fun getPercentage() = percentage.toFloat()
    fun getFormattedPercentage(): String {
        return "${String.format("%.2f", percentage * 100)} %"
    }
}

@JvmInline
value class MaxMemory(private val memoryInBytes: Double) {
    fun value() = memoryInBytes
}

@JvmInline
value class MemoryUsed(private val memoryInBytes: Double) {
    fun value() = memoryInBytes
}

@JvmInline
value class DiskTotal(private val diskUsedInBytes: Double) {
    fun value() = diskUsedInBytes
}

@JvmInline
value class DiskFree(private val diskFreeInBytes: Double) {
    fun value() = diskFreeInBytes
}