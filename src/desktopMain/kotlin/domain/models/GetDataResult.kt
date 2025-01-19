package domain.models

import domain.exception.ActuatorException

sealed class GetDataResult<out T : Any> {
    data class Success<out T : Any>(val data: T? = null) : GetDataResult<T>()
    data class Failure(val exception: ActuatorException) : GetDataResult<Nothing>()
}