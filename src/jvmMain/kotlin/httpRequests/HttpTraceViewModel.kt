package httpRequests

import client.ActuatorRemoteClient
import common.domain.Application
import common.domain.GetDataResult
import common.domain.HttpTrace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpTraceViewModel {

    var state = MutableStateFlow(HttpTraceScreenState())
        private set


    fun onEvent(screenEvent: HttpTraceEvent) {
        when (screenEvent) {
            is HttpTraceEvent.GetAllTraces -> getAllTraces(
                application = screenEvent.application,
                coroutineScope = screenEvent.coroutineScope
            )
        }
    }

    private fun getAllTraces(application: Application, coroutineScope: CoroutineScope) {

        state.update { currentState ->
            currentState.copy(isLoading = true)
        }

        coroutineScope.launch {
            ActuatorRemoteClient.getHttpTraces(application)
                .collect { traceResponse ->
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
}


data class HttpTraceScreenState(
    val isLoading: Boolean = false,
    val httpTraces: List<HttpTrace> = emptyList(),
    val error: Exception? = null
)

sealed class HttpTraceEvent {
    data class GetAllTraces(
        val application: Application,
        val liveUpdates: Boolean,
        val coroutineScope: CoroutineScope
    ) : HttpTraceEvent()

}