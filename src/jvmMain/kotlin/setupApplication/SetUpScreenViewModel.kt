package setupApplication

import client.ActuatorRemoteClient
import client.models.ActuatorEndpoints
import common.domain.GetDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetUpScreenViewModel {
    var state = MutableStateFlow(SetUpScreenState())
        private set

    private var viewModelScope = CoroutineScope(Dispatchers.IO)

    fun onEvent(event: SetUpScreenEvent) {
        when (event) {
            is SetUpScreenEvent.GetActuatorEndPoints -> getActuatorEndpoint(
                actuatorUrl = event.actuatorUrl,
                bearerToken = event.bearerToken
            )
        }
    }

    private fun getActuatorEndpoint(actuatorUrl: String, bearerToken: String) {
        state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        viewModelScope.launch {
            when (val result = ActuatorRemoteClient.getActuatorEndpoints(actuatorUrl, bearerToken)) {
                is GetDataResult.Sucess -> {
                    state.update { currentState ->
                        currentState.copy(isLoading = false, actuatorEndpoints = result.data, getActuatorSuccess = true)
                    }
                }

                is GetDataResult.Failure -> {
                    state.update { currentState ->
                        currentState.copy(isLoading = false, error = result.exception)
                    }
                }
            }

        }

    }
}


data class SetUpScreenState(
    val isLoading: Boolean = false,
    val getActuatorSuccess: Boolean = false,
    val actuatorEndpoints: ActuatorEndpoints? = null,
    val error: Exception? = null
)

sealed class SetUpScreenEvent {
    data class GetActuatorEndPoints(val actuatorUrl: String, val bearerToken: String) : SetUpScreenEvent()
}