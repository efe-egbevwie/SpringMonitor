package client

import client.models.ActuatorEndpoints
import client.models.HttpTraceApiResponse
import client.models.Trace
import client.models.dashboard.*
import client.models.environment.EnvironmentVariablesResponse
import client.models.environment.getEnvVariables
import client.models.info.ApplicationInfoResponse
import client.models.info.toDomainAppInfo
import client.models.toDomainHttptrace
import domain.exception.ActuatorException.*
import domain.models.*
import domain.models.environment.EnvironmentVariable
import domain.models.info.ApplicationInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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

        val actuatorEndpointResponse = executeAPiCall<ActuatorEndpoints> {
            ktorClient.request(application.actuatorUrl) {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }
        }

        return when (actuatorEndpointResponse) {
            is GetDataResult.Success -> {
                saveApplicationDetails(application)
                val actuatorEndpoints: ActuatorEndpoints? = actuatorEndpointResponse.data
                GetDataResult.Success(data = actuatorEndpoints)
            }

            is GetDataResult.Failure -> {
                GetDataResult.Failure(actuatorEndpointResponse.exception)
            }
        }
    }

    private fun saveApplicationDetails(application: Application) {
        ApplicationsDb.insertApplication(application)
    }

    fun getLiveHttpTraces(application: Application, shouldFetchLiveUpdates: Boolean) = flow {
        var isFetchingLiveUpdates = true
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

                    emit(GetDataResult.Success(httpTraces))
                } else {
                    emit(GetDataResult.Failure(CouldNotReachActuatorException))
                }
            } catch (e: Exception) {
                println("exception getting trace: $e")
                emit(GetDataResult.Failure(CouldNotReachActuatorException))
            }

            if (!shouldFetchLiveUpdates) {
                isFetchingLiveUpdates = false
            } else {
                delay(2.seconds)
            }
        }
    }

    fun getHttpTraces(application: Application): Flow<GetDataResult<List<HttpTrace>>> = flow {
        try {
            val response = executeAPiCall<HttpTraceApiResponse> {
                ktorClient.get("${application.actuatorUrl}/httptrace") {
                    method = HttpMethod.Get
                    headers {
                        append("Authorization", "Bearer ${application.bearerToken}")
                    }
                }
            }

            when (response) {
                is GetDataResult.Success -> {
                    val apiTraces: List<Trace>? = response.data?.traces
                    if (apiTraces?.isEmpty() == true || apiTraces == null) {
                        emit(GetDataResult.Success(data = emptyList()))
                        return@flow
                    }
                    val httpTraces: List<HttpTrace> = apiTraces.map { it.toDomainHttptrace() }.filterNot {
                        it.request.url.endsWith("httptrace")
                    }
                    emit(GetDataResult.Success(data = httpTraces))
                }

                is GetDataResult.Failure -> emit(GetDataResult.Failure(response.exception))
            }

        } catch (e: Exception) {
            println("exception getting trace: $e")
            emit(GetDataResult.Failure(CouldNotReachActuatorException))
        }
    }

    suspend fun getDashBoardMetrics(
        application: Application,
        shouldFetchLiveUpdates: Boolean = false
    ): Flow<GetDataResult<DashboardMetrics>> = flow {
        var isFetchingLiveUpdates = true

        while (isFetchingLiveUpdates and currentCoroutineContext().isActive) {

            val healthStatus = when (val healthResponse = getHealthStatus(application)) {
                is GetDataResult.Success -> healthResponse.data?.status
                is GetDataResult.Failure -> {
                    emit(GetDataResult.Failure(healthResponse.exception))
                    return@flow
                }
            }

            val processUpTime: ProcessUpTime? =
                when (val upTimeApiResponse = getSystemMetric(application, SystemMetric.PROCESS_UP_TIME)) {
                    is GetDataResult.Success -> upTimeApiResponse.data?.toProcessUpTime()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(upTimeApiResponse.exception))
                        return@flow
                    }
                }

            val cpuUsage: CpuUsage? = when (val cpuApiResponse = getSystemMetric(application, SystemMetric.CPU_USAGE)) {
                is GetDataResult.Success -> cpuApiResponse.data?.toCpuUsage()
                is GetDataResult.Failure -> {
                    emit(GetDataResult.Failure(cpuApiResponse.exception))
                    return@flow
                }
            }

            val memoryUsed: MemoryUsed? =
                when (val memoryUsedApiResponse = getSystemMetric(application, SystemMetric.MEMORY_USED)) {
                    is GetDataResult.Success -> memoryUsedApiResponse.data?.toMemoryUsed()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(memoryUsedApiResponse.exception))
                        return@flow
                    }
                }

            val memoryMax: MaxMemory? =
                when (val memoryMaxApiResponse = getSystemMetric(application, SystemMetric.MEMORY_MAX)) {
                    is GetDataResult.Success -> memoryMaxApiResponse.data?.toMemoryMax()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(memoryMaxApiResponse.exception))
                        return@flow
                    }
                }

            val diskTotal: DiskTotal? =
                when (val diskUsedApiResponse = getSystemMetric(application, SystemMetric.DISK_TOTAL)) {
                    is GetDataResult.Success -> diskUsedApiResponse.data?.toDiskTotal()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(diskUsedApiResponse.exception))
                        return@flow
                    }
                }

            val diskFree: DiskFree? =
                when (val diskFreeApiResponse = getSystemMetric(application, SystemMetric.DISK_FREE)) {
                    is GetDataResult.Success -> diskFreeApiResponse.data?.toDiskFree()
                    is GetDataResult.Failure -> {
                        emit(GetDataResult.Failure(diskFreeApiResponse.exception))
                        return@flow
                    }
                }

            emit(
                GetDataResult.Success(
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
        return executeAPiCall<MetricUsageResponse> {
            val pathUrl = SystemMetric.getMetricPathUrl(metricType)
            ktorClient.get("${application.actuatorUrl}/metrics/$pathUrl") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }
        }
    }

    private suspend fun getHealthStatus(application: Application): GetDataResult<HealthResponse> {
        val healthStatusResponse: GetDataResult<HealthResponse> = executeAPiCall<HealthResponse> {
            ktorClient.get("${application.actuatorUrl}/health") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }
        }
        return healthStatusResponse
    }

    suspend fun getApplicationInfo(application: Application): GetDataResult<ApplicationInfo> {
        val applicationInfoResponse = executeAPiCall<ApplicationInfoResponse> {
            ktorClient.get("${application.actuatorUrl}/info") {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }
        }

        return when (applicationInfoResponse) {
            is GetDataResult.Success -> {
                val appInfo = applicationInfoResponse.data?.toDomainAppInfo()
                GetDataResult.Success(appInfo)
            }

            is GetDataResult.Failure -> {
                GetDataResult.Failure(applicationInfoResponse.exception)
            }
        }
    }

    suspend fun getEnvironmentVariables(application: Application): GetDataResult<List<EnvironmentVariable>> {
        val environmentVariablesResponse = executeAPiCall<EnvironmentVariablesResponse> {
            ktorClient.get("${application.actuatorUrl}/env") {
                headers {
                    append("Authorization", "Bearer ${application.bearerToken}")
                }
            }
        }

        return when (environmentVariablesResponse) {
            is GetDataResult.Success -> {
                val envApiList =
                    environmentVariablesResponse.data?.propertySources
                if (envApiList?.isEmpty() == true || envApiList == null) return GetDataResult.Success(emptyList())
                val envList: List<EnvironmentVariable> =
                    envApiList.flatMap { property -> property.getEnvVariables() }
                GetDataResult.Success(envList)
            }

            is GetDataResult.Failure -> {
                GetDataResult.Failure(environmentVariablesResponse.exception)
            }
        }
    }

    private suspend inline fun <reified T : Any> executeAPiCall(
        apiCall: () -> HttpResponse
    ): GetDataResult<T> {
        return try {
            val apiResult = apiCall()
            when (apiResult.status) {
                HttpStatusCode.OK -> GetDataResult.Success(data = apiResult.body())
                HttpStatusCode.NotFound -> GetDataResult.Failure(exception = ActuatorNotEnabledException)
                HttpStatusCode.Unauthorized -> GetDataResult.Failure(BearerTokenInvalidException)
                HttpStatusCode.Forbidden -> GetDataResult.Failure(BearerTokenInvalidException)
                else -> GetDataResult.Failure(CouldNotReachActuatorException)
            }

        } catch (e: Exception) {
            logger.info { "failure executing api call: $e" }
            GetDataResult.Failure(CouldNotReachActuatorException)
        }
    }
}