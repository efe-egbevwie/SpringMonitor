package client.models.dashboard

enum class SystemMetric(private val pathUrl: String) {
    PROCESS_UP_TIME("process.uptime"),
    CPU_USAGE("process.cpu.usage"),
    MEMORY_USED("jvm.memory.used"),
    MEMORY_MAX("jvm.memory.max"),
    DISK_FREE("disk.free"),
    DISK_TOTAL("disk.total");

    companion object {
        fun getMetricPathUrl(metricType: SystemMetric): String {
            return when (metricType) {
                PROCESS_UP_TIME -> PROCESS_UP_TIME.pathUrl
                CPU_USAGE -> CPU_USAGE.pathUrl
                MEMORY_USED -> MEMORY_USED.pathUrl
                MEMORY_MAX -> MEMORY_MAX.pathUrl
                DISK_FREE -> DISK_FREE.pathUrl
                DISK_TOTAL -> DISK_TOTAL.pathUrl
            }
        }
    }

}