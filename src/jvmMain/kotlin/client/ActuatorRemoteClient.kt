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
import org.slf4j.LoggerFactory

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

    suspend fun getHttpTrace(traceEndpoint: String, bearerToken: String): GetDataResult<List<HttpTrace>> {
        return try {
            val apiResponse = ktorClient.get(traceEndpoint) {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer $bearerToken")
                }

            }.body<HttpTraceApiResponse>()

            val httpTraces: List<HttpTrace> = apiResponse.traces.map { it.toDomainHttptrace() }

            logger.info("traces: $httpTraces")
            GetDataResult.Sucess(httpTraces)
        } catch (e: Exception) {
            logger.error("exception getting traces: $e")
            GetDataResult.Failure(e)
        }
    }
}