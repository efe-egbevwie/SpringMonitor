package client

import client.models.ActuatorEndpoints
import client.models.HttpTraceApiResponse
import client.models.dashboard.*
import client.models.info.ApplicationInfoResponse
import client.models.info.toDomainAppInfo
import client.models.toDomainHttptrace
import domain.exception.ActuatorNotEnabledException
import domain.exception.BearerTokenNotValidException
import domain.exception.CouldNotReachApplicationException
import domain.models.*
import domain.models.info.ApplicationInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

object ActuatorRemoteClient {

    private val logger = KotlinLogging.logger { }

    suspend fun getActuatorEndpoints(application: Application): GetDataResult<ActuatorEndpoints> {
        try {
            val response = ktorClient.request(application.actuatorUrl) {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }


            when (response.status) {
                HttpStatusCode.OK -> {
                    saveApplicationDetails(application)
                    val body = response.body<ActuatorEndpoints>()
                    return GetDataResult.Sucess(body)
                }

                HttpStatusCode.NotFound -> {
                    return GetDataResult.Failure(ActuatorNotEnabledException())
                }

                HttpStatusCode.Unauthorized -> {
                    return GetDataResult.Failure(BearerTokenNotValidException())
                }

                else -> {
                    return GetDataResult.Failure(CouldNotReachApplicationException())
                }
            }


        } catch (e: Exception) {
            logger.error("exception getting actuator endpoints: $e")
            return GetDataResult.Failure(e)
        }

    }

    private fun saveApplicationDetails(application: Application) {
        ActuatorLocalClient.insertApplication(application)
    }

    fun getLiveHttpTraces(application: Application, shouldFetchLiveUpdates: Boolean) = flow {

        var isFetchingLiveUpdates: Boolean = true

        while (isFetchingLiveUpdates and currentCoroutineContext().isActive) {

            try {

                val apiResponse = ktorClient.get("${application.actuatorUrl}/httptrace") {
                    method = HttpMethod.Get
                    headers {
                        append("Authorization", "Bearer ${application.bearerToken}")
                    }

                }

                if (apiResponse.status == HttpStatusCode.OK) {
                    val apiTraceResponse = apiResponse.body<HttpTraceApiResponse>()

                    val httpTraces: List<HttpTrace> = apiTraceResponse.traces.map { it.toDomainHttptrace() }.filterNot {
                        it.request.url.endsWith("httptrace")
                    }

                    emit(GetDataResult.Sucess(httpTraces))
                } else {
                    emit(GetDataResult.Failure(CouldNotReachApplicationException()))
                }
            } catch (e: Exception) {
                println("exception getting trace: $e")
                emit(GetDataResult.Failure(e))
            }

            if (!shouldFetchLiveUpdates) {
                isFetchingLiveUpdates = false
            } else {
                delay(2.seconds)
            }

        }
    }

    fun getHttpTraces(application: Application) = flow {

        try {

            val apiResponse = ktorClient.get("${application.actuatorUrl}/httptrace") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }

            }

            if (apiResponse.status == HttpStatusCode.OK) {
                val apiTraceResponse = apiResponse.body<HttpTraceApiResponse>()

                val httpTraces: List<HttpTrace> = apiTraceResponse.traces.map { it.toDomainHttptrace() }.filterNot {
                    it.request.url.endsWith("httptrace")
                }

                emit(GetDataResult.Sucess(httpTraces))
            } else {
                emit(GetDataResult.Failure(CouldNotReachApplicationException()))
            }
        } catch (e: Exception) {
            println("exception getting trace: $e")
            emit(GetDataResult.Failure(e))
        }
    }

    suspend fun getDashBoardMetrics(
        application: Application,
        shouldFetchLiveUpdates: Boolean = false
    ): Flow<GetDataResult<DashboardMetrics>> = flow {

        var isFetchingLiveUpdates = true

        while (isFetchingLiveUpdates and currentCoroutineContext().isActive) {

            val healthStatus = when (val healthResponse = getHealthStatus(application)) {
                is GetDataResult.Sucess -> healthResponse.data?.status
                is GetDataResult.Failure -> {
                    emit(GetDataResult.Failure(healthResponse.exception))
                    null
                }
            }

            val processUpTime: ProcessUpTime? =
                when (val upTimeApiResponse = getSystemMetric(application, SystemMetric.PROCESS_UP_TIME)) {
                    is GetDataResult.Sucess -> upTimeApiResponse.data?.toProcessUpTime()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(upTimeApiResponse.exception))
                        null
                    }
                }

            val cpuUsage: CpuUsage? = when (val cpuApiResponse = getSystemMetric(application, SystemMetric.CPU_USAGE)) {
                is GetDataResult.Sucess -> cpuApiResponse.data?.toCpuUsage()
                is GetDataResult.Failure -> {
                    emit(GetDataResult.Failure(cpuApiResponse.exception))
                    null
                }
            }

            val memoryUsed: MemoryUsed? =
                when (val memoryUsedApiResponse = getSystemMetric(application, SystemMetric.MEMORY_USED)) {
                    is GetDataResult.Sucess -> memoryUsedApiResponse.data?.toMemoryUsed()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(memoryUsedApiResponse.exception))
                        null
                    }
                }

            val memoryMax: MaxMemory? =
                when (val memoryMaxApiResponse = getSystemMetric(application, SystemMetric.MEMORY_MAX)) {
                    is GetDataResult.Sucess -> memoryMaxApiResponse.data?.toMemoryMax()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(memoryMaxApiResponse.exception))
                        null
                    }
                }

            val diskTotal: DiskTotal? =
                when (val diskUsedApiResponse = getSystemMetric(application, SystemMetric.DISK_TOTAL)) {
                    is GetDataResult.Sucess -> diskUsedApiResponse.data?.toDiskTotal()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(diskUsedApiResponse.exception))
                        null
                    }
                }

            val diskFree: DiskFree? =
                when (val diskFreeApiResponse = getSystemMetric(application, SystemMetric.DISK_FREE)) {
                    is GetDataResult.Sucess -> diskFreeApiResponse.data?.toDiskFree()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(diskFreeApiResponse.exception))
                        null
                    }
                }

            emit(
                GetDataResult.Sucess(
                    DashboardMetrics(
                        status = healthStatus.orEmpty(),
                        upTime = processUpTime,
                        cpuUsagePercent = cpuUsage,
                        maxMemory = memoryMax,
                        memoryUsed = memoryUsed,
                        diskTotal = diskTotal,
                        diskFree = diskFree
                    )
                )
            )

            if (!shouldFetchLiveUpdates) {
                isFetchingLiveUpdates = false
            } else {
                delay(4.seconds)
            }


        }


    }

    private suspend fun getSystemMetric(
        application: Application,
        metricType: SystemMetric
    ): GetDataResult<MetricUsageResponse> {
        try {
            val pathUrl = SystemMetric.getMetricPathUrl(metricType)

            val apiResponse = ktorClient.get("${application.actuatorUrl}/metrics/$pathUrl") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }

            }

            return if (apiResponse.status == HttpStatusCode.OK) {
                val healthResponse = apiResponse.body<MetricUsageResponse>()

                (GetDataResult.Sucess(healthResponse))
            } else {
                GetDataResult.Failure(CouldNotReachApplicationException())
            }
        } catch (e: Exception) {
            println("exception getting trace: $e")
            return (GetDataResult.Failure(e))
        }

    }

    private suspend fun getHealthStatus(application: Application): GetDataResult<HealthResponse> {
        try {

            val apiResponse = ktorClient.get("${application.actuatorUrl}/health") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }

            }

            return if (apiResponse.status == HttpStatusCode.OK) {
                val healthResponse = apiResponse.body<HealthResponse>()

                (GetDataResult.Sucess(healthResponse))
            } else {
                GetDataResult.Failure(CouldNotReachApplicationException())
            }
        } catch (e: Exception) {
            println("exception getting trace: $e")
            return (GetDataResult.Failure(e))
        }

    }

    suspend fun getApplicationInfo(application: Application): GetDataResult<ApplicationInfo> {
        try {
            val appInfoApiResponse = ktorClient.get("${application.actuatorUrl}/info") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }

            return if (appInfoApiResponse.status == HttpStatusCode.OK) {
                val appInfo = appInfoApiResponse.body<ApplicationInfoResponse>().toDomainAppInfo()

                GetDataResult.Sucess(appInfo)
            } else {
                GetDataResult.Failure(CouldNotReachApplicationException())
            }

        } catch (e: Exception) {
            logger.info {
                "Exception getting app info: $e"
            }

            return GetDataResult.Failure(e)
        }
    }


}