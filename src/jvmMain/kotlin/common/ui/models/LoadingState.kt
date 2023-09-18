package common.ui.models

sealed class LoadingState {

    data object Loaading : LoadingState()

    data object SuccessLoading : LoadingState()

    data object FailedToLoad : LoadingState()
}
