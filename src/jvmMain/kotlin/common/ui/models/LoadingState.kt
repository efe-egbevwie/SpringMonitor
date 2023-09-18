package common.ui.models

sealed class LoadingState {

    data object Loading : LoadingState()

    data object SuccessLoading : LoadingState()

    data object FailedToLoad : LoadingState()
}
