package home

import client.ActuatorLocalClient
import common.domain.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel {

    var state = MutableStateFlow(HomeScreenState())
        private set


    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.GetAllApplications -> getAllApplications()
        }
    }

    private fun getAllApplications() {
        CoroutineScope(Dispatchers.IO).launch {
            ActuatorLocalClient.getAllApplications.collect { applications ->
                state.update { currentState ->
                    currentState.copy(allApplications = applications)
                }
            }
        }
    }

}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val allApplications: List<Application> = emptyList()
)

sealed class HomeScreenEvent {
    data object GetAllApplications : HomeScreenEvent()
}