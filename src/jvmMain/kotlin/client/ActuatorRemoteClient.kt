package client

import client.models.ActuatorEndpoints
import client.models.HttpTraceApiResponse
import client.models.toDomainHttptrace
import common.domain.Application
import common.domain.GetDataResult
import common.domain.HttpTrace
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

object ActuatorRemoteClient {

    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun getActuatorEndpoints(actuatorUrl: String, authToken: String): GetDataResult<ActuatorEndpoints> {
        try {
            val response = ktorClient.request(actuatorUrl) {
                method = HttpMethod.Get
                headers {
                    append("Authorization", "Bearer $authToken")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val application = Application(
                    alias = "Cards",
                    actuatorUrl = actuatorUrl,
                    bearerToken = authToken
                )
                //ActuatorLocalClient.insertApplication(application)
            }

            val body = response.body<ActuatorEndpoints>()

            logger.info(body.toString())

            return GetDataResult.Sucess(body)
        } catch (e: Exception) {
            logger.error("exception getting actuator endpoints: $e")
            return GetDataResult.Failure(e)
        }

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