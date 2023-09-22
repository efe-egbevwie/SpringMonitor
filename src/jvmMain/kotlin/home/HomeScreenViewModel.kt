package home

import client.ApplicationsDb
import common.ui.models.LoadingState
import domain.models.Application
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel {

    private val logger = KotlinLogging.logger { }

    var state = MutableStateFlow(HomeScreenState())
        private set


    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.GetAllApplications -> getAllApplications()
            is HomeScreenEvent.GetSelectedApplication -> getSelectedApplication(
                applicationId = event.applicationId,
                scope = event.scope
            )
        }
    }

    private fun getAllApplications() {
        CoroutineScope(Dispatchers.IO).launch {
            ApplicationsDb.getAllApplications.collect { applications ->
                state.update { currentState ->
                    currentState.copy(allApplications = applications)
                }
            }
        }
    }


    private fun getSelectedApplication(applicationId: Int, scope: CoroutineScope) {

        setStateToLoading()

        scope.launch {
            ApplicationsDb.findApplicationById(applicationId)
                .catch { error ->
                    logger.error { "flow error: $error" }
                    state.update { currentState ->
                        currentState.copy(loadingState = LoadingState.FailedToLoad, exception = error as Exception)
                    }
                }
                .collect { application ->
                    state.update { currentState ->
                        currentState.copy(loadingState = LoadingState.SuccessLoading, currentApplication = application)
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


data class HomeScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
    val currentApplication: Application? = null,
    val allApplications: List<Application> = emptyList(),
    val exception: Exception? = null
)

sealed class HomeScreenEvent {

    data class GetSelectedApplication(val applicationId: Int, val scope: CoroutineScope) : HomeScreenEvent()
    data object GetAllApplications : HomeScreenEvent()
}