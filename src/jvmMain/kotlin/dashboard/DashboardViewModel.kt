package dashboard

import client.ActuatorRemoteClient
import common.domain.Application
import common.domain.DashboardMetrics
import common.domain.GetDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel {

    var state = MutableStateFlow(DashBoardScreenState())
        private set

    fun onEvent(event: DashBoardScreenEvent) {
        when (event) {
            is DashBoardScreenEvent.GetSystemMetrics -> getDashBoardMetrics(
                application = event.application,
                scope = event.coroutineScope
            )
        }
    }


    private fun getDashBoardMetrics(application: Application, scope: CoroutineScope) {
        scope.launch {
            when (val metrics = ActuatorRemoteClient.getDashBoardMetrics(application)) {
                is GetDataResult.Sucess -> {
                    state.update { currentState ->
                        println("metrics are ${metrics.data}")
                        currentState.copy(isLoading = false, dashboardMetrics = metrics.data)
                    }
                }

                is GetDataResult.Failure -> {
                    state.update { currentState ->
                        currentState.copy(isLoading = false, exception = metrics.exception)
                    }
                }
            }
        }

    }

}

sealed class DashBoardScreenEvent {
    data class GetSystemMetrics(val application: Application, val coroutineScope: CoroutineScope) :
        DashBoardScreenEvent()
}

data class DashBoardScreenState(
    val isLoading: Boolean = true,
    val dashboardMetrics: DashboardMetrics? = null,
    val exception: Exception? = null
)