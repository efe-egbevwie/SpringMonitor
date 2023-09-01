package httpRequests

import client.ActuatorRemoteClient
import common.domain.GetDataResult
import common.domain.HttpTrace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpTraceViewModel {

    var state = MutableStateFlow(HttpTraceScreenState())
        private set


    private val scope = CoroutineScope(Dispatchers.IO)

    fun onEvent(screenEvent: HttpTraceEvent) {
        when (screenEvent) {
            is HttpTraceEvent.GetAllTraces -> getAllTraces()
        }
    }

    private fun getAllTraces() {

        state.update { currentState ->
            currentState.copy(isLoading = true)
        }

        scope.launch {
            val traceResponse = ActuatorRemoteClient.getHttpTrace(
                traceEndpoint = "http://localhost:8080/actuator/httptrace",
                bearerToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY5Mzg2NTQyNiwiaWF0IjoxNjkzNDMzNDI2LCJ1c2VySWQiOjIsInJvbGVzIjoiQURNSU4ifQ.EwOc7X9sbjTcV6s5vz7CU0MK3xsIdk-SM22aLaA4OAc"
            )

            when (traceResponse) {
                is GetDataResult.Sucess -> {
                    state.update { currentState ->
                        currentState.copy(isLoading = false, httpTraces = traceResponse.data ?: emptyList())
                    }
                }

                is GetDataResult.Failure -> {
                    state.update { currentState ->
                        currentState.copy(isLoading = false, error = traceResponse.exception)
                    }
                }
            }
        }

    }
}


data class HttpTraceScreenState(
    val isLoading: Boolean = false,
    val httpTraces: List<HttpTrace> = emptyList(),
    val error: Exception? = null
)

sealed class HttpTraceEvent {
    object GetAllTraces: HttpTraceEvent()
    //data class GetAllTraces(val actuatorUrl: String, val bearerToken: String) : HttpTraceEvent()
}