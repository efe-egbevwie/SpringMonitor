package common.domain

import kotlin.Exception

sealed class GetDataResult<out T : Any> {
    data class Sucess<out T : Any>(val data: T? = null) : GetDataResult<T>()
    data class Failure(val exception: Exception) : GetDataResult<Nothing>()
}