package client

import client.models.ActuatorEndpoints
import client.models.HttpTraceApiResponse
import client.models.toDomainHttptrace
import common.domain.Application
import common.domain.GetDataResult
import common.domain.HttpTrace
import common.domain.exception.ActuatorNotEnabledException
import common.domain.exception.BearerTokenNotValidException
import common.domain.exception.CouldNotReachApplicationException
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

object ActuatorRemoteClient {

    private val logger = LoggerFactory.getLogger(javaClass)

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

    fun getHttpTraces(application: Application) = flow {


        while (currentCoroutineContext().isActive) {

            try {

                val apiResponse = ktorClient.get("${application.actuatorUrl}/httptrace") {
                    method = HttpMethod.Get
                    headers {
                        append("Authorization", "Bearer ${application.bearerToken}")
                    }

                }

                if (apiResponse.status == HttpStatusCode.OK) {
                    val apiTraceResponse = apiResponse.body<HttpTraceApiResponse>()
                    val httpTraces: List<HttpTrace> = apiTraceResponse.traces.map { it.toDomainHttptrace() }
                    emit(GetDataResult.Sucess(httpTraces))
                } else {
                    emit(GetDataResult.Failure(CouldNotReachApplicationException()))
                }
            } catch (e: Exception) {
                println("exception getting trace: $e")
                emit(GetDataResult.Failure(e))
            }
            delay(2.seconds)

        }
    }
}