package httpRequests

import client.ActuatorRemoteClient
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.GetDataResult
import domain.models.HttpTrace
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpTraceViewModel {

    private val logger = KotlinLogging.logger { }
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

        setStateToLoading()

        coroutineScope.launch {
            ActuatorRemoteClient.getHttpTraces(application)
                .collect { traceResponse ->
                    logger.info { "trace response: $traceResponse" }
                    when (traceResponse) {
                        is GetDataResult.Success -> {
                            state.update { currentState ->
                                currentState.copy(
                                    loadingState = LoadingState.SuccessLoading,
                                    httpTraces = traceResponse.data ?: emptyList()
                                )
                            }
                        }

                        is GetDataResult.Failure -> {
                            state.update { currentState ->
                                currentState.copy(
                                    loadingState = LoadingState.FailedToLoad,
                                    error = traceResponse.exception
                                )
                            }
                        }
                    }
                }

        }

    }

    private fun setStateToLoading() {
        state.update { currentState ->
            currentState.copy(loadingState = LoadingState.Loading)
        }
    }


}


data class HttpTraceScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
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