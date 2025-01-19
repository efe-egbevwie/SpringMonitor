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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class EnvironmentVariablesViewModel : Serializable {

    var state = MutableStateFlow(EnvironmentVariablesScreenState())
        private set
    var environmentVariablesListState: LazyListState by mutableStateOf(LazyListState(0, 0))

    val scope = CoroutineScope(Dispatchers.Default)


    fun onEvent(event: EnvironmentVariablesScreenEvent) {
        when (event) {
            is EnvironmentVariablesScreenEvent.GetEnvironmentVariables -> getEnvironmentVariables(
                application = event.application
            )

            is EnvironmentVariablesScreenEvent.RefreshEnvironmentVariables -> getEnvironmentVariables(
                application = event.application,
                refresh = true
            )

            is EnvironmentVariablesScreenEvent.FilterEnvironmentVariables -> {
                updateSearchKeyWord(keyWord = event.searchKeyWord)
                filterEnvironmentVariables(
                    searchKeyWord = event.searchKeyWord
                )
            }

            is EnvironmentVariablesScreenEvent.SetFiltering -> setIsFiltering(isFiltering = event.isFiltering)
        }
    }

    private fun getEnvironmentVariables(application: Application, refresh: Boolean = false) {

        val environmentVariableAlreadyLoaded = state.value.allEnvironmentVariables.isNotEmpty()

        if (environmentVariableAlreadyLoaded and !refresh) return

        scope.launch {
            setStateToLoading()
            when (val environmentVariablesResponse = ActuatorRemoteClient.getEnvironmentVariables(application)) {
                is GetDataResult.Success -> {
                    state.update { currentState ->
                        currentState.copy(
                            loadingState = LoadingState.SuccessLoading,
                            allEnvironmentVariables = environmentVariablesResponse.data ?: emptyList()
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

    private fun updateSearchKeyWord(keyWord: String) {
        state.update { it.copy(searchKeyWord = keyWord) }
    }

    private fun filterEnvironmentVariables(searchKeyWord: String) {
        if (searchKeyWord.isNotEmpty() && searchKeyWord.isNotBlank()) {
            setIsFiltering(true)
        } else {
            setIsFiltering(false)
            return
        }
        val searchResults =
            state.value.allEnvironmentVariables.filter { it.name.contains(searchKeyWord, ignoreCase = true) }
        state.update { currentState ->
            currentState.copy(filteredEnvironmentVariables = searchResults)
        }
    }

    private fun setIsFiltering(isFiltering: Boolean) {
        state.update { currentState ->
            currentState.copy(isFiltering = isFiltering)
        }
    }

    private fun setStateToLoading() {
        state.update { currentState ->
            currentState.copy(loadingState = LoadingState.Loading)
        }
    }
}


sealed class EnvironmentVariablesScreenEvent {
    data class GetEnvironmentVariables(val application: Application) :
        EnvironmentVariablesScreenEvent()

    data class RefreshEnvironmentVariables(val application: Application) :
        EnvironmentVariablesScreenEvent()

    data class FilterEnvironmentVariables(val searchKeyWord: String) : EnvironmentVariablesScreenEvent()

    data class SetFiltering(val isFiltering: Boolean) : EnvironmentVariablesScreenEvent()
}

data class EnvironmentVariablesScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
    val isFiltering: Boolean = false,
    val searchKeyWord: String = "",
    val allEnvironmentVariables: List<EnvironmentVariable> = emptyList(),
    val filteredEnvironmentVariables: List<EnvironmentVariable> = emptyList(),
    val exception: Exception? = null
) {
    val environmentVariables: List<EnvironmentVariable> get() = if (isFiltering) filteredEnvironmentVariables else allEnvironmentVariables
}
