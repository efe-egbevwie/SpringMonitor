package environmentVariables

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import client.ActuatorRemoteClient
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.GetDataResult
import domain.models.environment.EnvironmentVariable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class EnvironmentVariablesViewModel : Serializable {

    var state = MutableStateFlow(EnvironmentVariablesScreenState())
        private set
    var environmentVariablesListState: LazyListState by mutableStateOf(LazyListState(0, 0))


    fun onEvent(event: EnvironmentVariablesScreenEvent) {
        when (event) {
            is EnvironmentVariablesScreenEvent.GetEnvironmentVariables -> getEnvironmentVariables(
                application = event.application,
                scope = event.scope
            )

            is EnvironmentVariablesScreenEvent.RefreshEnvironmentVariables -> getEnvironmentVariables(
                application = event.application,
                scope = event.scope,
                refresh = true
            )
        }
    }

    private fun getEnvironmentVariables(application: Application, scope: CoroutineScope, refresh: Boolean = false) {

        val environmentVariableAlreadyLoaded = state.value.environmentVariables.isNotEmpty()

        if (environmentVariableAlreadyLoaded and !refresh) return

        scope.launch {
            setStateToLoading()

            when (val environmentVariablesResponse = ActuatorRemoteClient.getEnvironmentVariables(application)) {
                is GetDataResult.Success -> {
                    state.update { currentState ->
                        currentState.copy(
                            loadingState = LoadingState.SuccessLoading,
                            environmentVariables = environmentVariablesResponse.data ?: emptyList()
                        )
                    }
                }

                is GetDataResult.Failure -> {
                    state.update { currentState ->
                        currentState.copy(
                            loadingState = LoadingState.FailedToLoad,
                            exception = environmentVariablesResponse.exception
                        )
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


sealed class EnvironmentVariablesScreenEvent {
    data class GetEnvironmentVariables(val application: Application, val scope: CoroutineScope) :
        EnvironmentVariablesScreenEvent()

    data class RefreshEnvironmentVariables(val application: Application, val scope: CoroutineScope) :
        EnvironmentVariablesScreenEvent()
}

data class EnvironmentVariablesScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
    val environmentVariables: List<EnvironmentVariable> = emptyList(),
    val exception: Exception? = null
)
