package common.exception

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.handleFlowErrors(onError: (error: Throwable) -> Unit): Flow<T> = catch { error ->
    onError(error)
}