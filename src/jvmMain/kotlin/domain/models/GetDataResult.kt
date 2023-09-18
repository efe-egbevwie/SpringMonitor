package domain.models

import kotlin.Exception

sealed class GetDataResult<out T : Any> {
    data class Success<out T : Any>(val data: T? = null) : GetDataResult<T>()
    data class Failure(val exception: Exception) : GetDataResult<Nothing>()
}