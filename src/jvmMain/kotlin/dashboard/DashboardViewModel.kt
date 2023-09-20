package dashboard

import client.ActuatorRemoteClient
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.DashboardMetrics
import domain.models.GetDataResult
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel {

    private val logger = KotlinLogging.logger { }
    var state = MutableStateFlow(DashBoardScreenState())
        private set

    fun onEvent(event: DashBoardScreenEvent) {
        when (event) {
            is DashBoardScreenEvent.GetSystemMetrics -> getDashBoardMetrics(
                application = event.application,
                scope = event.coroutineScope,
                fetchLiveUpdates = event.fetchLiveUpdates
            )
        }
    }


    private fun getDashBoardMetrics(application: Application, scope: CoroutineScope, fetchLiveUpdates: Boolean) {


        scope.launch {

            ActuatorRemoteClient.getDashBoardMetrics(
                application = application,
                shouldFetchLiveUpdates = fetchLiveUpdates
            ).collect { metricsResponse ->

                logger.info {
                    "metrics response: $metricsResponse"
                }
                when (metricsResponse) {
                    is GetDataResult.Success -> {
                        state.update { currentState ->
                            logger.info {
                                "metrics are ${metricsResponse.data}"
                            }
                            currentState.copy(
                                dashboardMetrics = metricsResponse.data,
                                loadingState = LoadingState.SuccessLoading
                            )
                        }
                    }

                    is GetDataResult.Failure -> {
                        state.update { currentState ->
                            currentState.copy(
                                exception = metricsResponse.exception,
                                loadingState = LoadingState.FailedToLoad
                            )
                        }
                    }
                }
            }

        }

    }

}

sealed class DashBoardScreenEvent {
    data class GetSystemMetrics(
        val application: Application,
        val coroutineScope: CoroutineScope,
        val fetchLiveUpdates: Boolean = false
    ) :
        DashBoardScreenEvent()
}

data class DashBoardScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
    val dashboardMetrics: DashboardMetrics? = null,
    val exception: Exception? = null
)