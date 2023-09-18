package dashboard

import client.ActuatorRemoteClient
import domain.models.Application
import domain.models.DashboardMetrics
import domain.models.GetDataResult
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel {

    private val logger = KotlinLogging.logger {  }
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

                when (metricsResponse) {
                    is GetDataResult.Sucess -> {
                        state.update { currentState ->
                            logger.info {
                                "metrics are ${metricsResponse.data}"
                            }
                            currentState.copy(isLoading = false, dashboardMetrics = metricsResponse.data)
                        }
                    }

                    is GetDataResult.Failure -> {
                        state.update { currentState ->
                            currentState.copy(isLoading = false, exception = metricsResponse.exception)
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
    val isLoading: Boolean = true,
    val dashboardMetrics: DashboardMetrics? = null,
    val exception: Exception? = null
)