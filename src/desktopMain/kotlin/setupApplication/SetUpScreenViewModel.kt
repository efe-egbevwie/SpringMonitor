package setupApplication

import client.ActuatorRemoteClient
import client.ApplicationsDb
import client.models.ActuatorEndpoints
import domain.exception.ActuatorException
import domain.models.Application
import domain.models.GetDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class SetUpScreenViewModel {
    private val logger = LoggerFactory.getLogger(javaClass)
    var state = MutableStateFlow(SetUpScreenState())
        private set

    private var viewModelScope = CoroutineScope(Dispatchers.IO)

    fun onEvent(event: SetUpScreenEvent) {

        when (event) {
            is SetUpScreenEvent.GetActuatorEndPoints -> getActuatorEndpoint(
                application = event.application
            )
        }
    }

    private fun getActuatorEndpoint(application: Application) {
        state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result = ActuatorRemoteClient.getActuatorEndpoints(application)
            logger.info("set up actuator result: $result")
            when (result) {
                is GetDataResult.Success -> {

                    val newApplicationId = saveApplicationAndGetId(application)
                    val newApplication = application.copy(applicationId = newApplicationId.toInt())

                    state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            actuatorEndpoints = result.data,
                            getActuatorSuccess = true,
                            newApplication = newApplication
                        )
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

    private fun saveApplicationAndGetId(application: Application): Long {
        return ApplicationsDb.insertApplication(application)
    }
}


data class SetUpScreenState(
    val isLoading: Boolean = false,
    val getActuatorSuccess: Boolean = false,
    val actuatorEndpoints: ActuatorEndpoints? = null,
    val newApplication: Application? = null,
    val error: ActuatorException? = null
) {
    val errorMessage: String? get() = formatErrorMessage()
    private fun formatErrorMessage(): String? {
        if (error == null) return null
        return when (error) {
            is ActuatorException.ActuatorNotEnabledException -> "Spring Boot Actuator is not enabled for this application"
            ActuatorException.ActuatorNotEnabledException -> "Spring Boot Actuator is not enabled for this application"
            ActuatorException.BearerTokenInvalidException -> "Authentication denied"
            ActuatorException.CouldNotReachActuatorException -> "Application Unreachable"
            else -> "An Error Has occurred please try again"
        }
    }
}

sealed class SetUpScreenEvent {
    data class GetActuatorEndPoints(val application: Application) :
        SetUpScreenEvent()
}