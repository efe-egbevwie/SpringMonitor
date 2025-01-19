package httpRequests

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import client.ActuatorRemoteClient
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.GetDataResult
import domain.models.HttpTrace
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpTraceViewModel {

    private val logger = KotlinLogging.logger { }
    var state = MutableStateFlow(HttpTraceScreenState())
        private set

    private val scope = CoroutineScope(context = Dispatchers.Default)

    var traceListState: LazyListState by mutableStateOf(LazyListState(0, 0))



    fun onEvent(screenEvent: HttpTraceEvent) {
        when (screenEvent) {
            is HttpTraceEvent.GetHttpTraces -> getAllTraces(application = screenEvent.application)

            is HttpTraceEvent.RefreshTraces -> reloadHttpTraces(application = screenEvent.application)
        }
    }

    private fun getAllTraces(application: Application) {

        val httpTraceAlreadyFetched: Boolean = state.value.httpTraces.isNotEmpty()

        if (httpTraceAlreadyFetched) return

        fetchHttpTraces(application)
    }


    private fun reloadHttpTraces(application: Application) {
        fetchHttpTraces(application)
    }

    private fun fetchHttpTraces(application: Application) {
        scope.launch {

            setStateToLoading()

            ActuatorRemoteClient.getHttpTraces(application)
                .collect { traceResponse ->
                    logger.info { "trace response: $traceResponse" }
                    when (traceResponse) {
                        is GetDataResult.Success -> {
                            state.update { currentState ->

                                val currentTraceList = state.value.httpTraces
                                    .toMutableList()
                                currentTraceList.addAll(traceResponse.data ?: emptyList())

                                currentState.copy(
                                    loadingState = LoadingState.SuccessLoading,
                                    httpTraces = currentTraceList.distinct()
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
    data class GetHttpTraces(val application: Application) : HttpTraceEvent()

    data class RefreshTraces(val application: Application) : HttpTraceEvent()
}