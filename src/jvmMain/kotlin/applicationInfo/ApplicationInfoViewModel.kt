package applicationInfo

import client.ActuatorRemoteClient
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.GetDataResult
import domain.models.info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApplicationInfoViewModel {

    val state = MutableStateFlow(ApplicationInfoScreenState())

    fun onEvent(event: ApplicationInfoScreenEvent) {
        when (event) {
            is ApplicationInfoScreenEvent.GetApplicationInfo -> getApplicationInfo(
                application = event.application,
                scope = event.scope,
                refresh = event.refresh
            )
        }
    }

    private fun getApplicationInfo(application: Application, scope: CoroutineScope, refresh: Boolean = false) {

        val applicationInfoAlreadyLoaded: Boolean = state.value.applicationInfo != null

        if (applicationInfoAlreadyLoaded and !refresh) return

        setStateToLoading()

        scope.launch {
            when (val appInfoFromApi = ActuatorRemoteClient.getApplicationInfo(application)) {
                is GetDataResult.Success -> {
                    val appInfo: ApplicationInfo? = appInfoFromApi.data
                    state.update { currentState ->
                        currentState.copy(loadingState = LoadingState.SuccessLoading, applicationInfo = appInfo)
                    }
                }

                is GetDataResult.Failure -> {
                    state.update { currentState ->
                        currentState.copy(
                            loadingState = LoadingState.FailedToLoad,
                            exception = appInfoFromApi.exception
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

data class ApplicationInfoScreenState(
    val loadingState: LoadingState = LoadingState.Loading,
    val applicationInfo: ApplicationInfo? = null,
    val exception: Exception? = null
) {
    fun buildAppInfoForUi(): Map<String, List<AppInfoDetail>> {
        return buildMap {
            val gitInfo = buildGitInfo(gitInfo = applicationInfo?.gitInfo)
            val osInfo = buildOsInfo(osInfo = applicationInfo?.operatingSystemInfo)
            val applicationBuildInfo = buildApplicationBuildInfo(buildInfo = applicationInfo?.build)
            val javaInfo = buildJavaInfo(javaInfo = applicationInfo?.javaInfo)

            if (gitInfo.isNotEmpty()) {
                put("Git", gitInfo)
            }

            if (osInfo.isNotEmpty()) {
                put("OS", osInfo)
            }

            if (applicationBuildInfo.isNotEmpty()) {
                put("Build", applicationBuildInfo)
            }

            if (javaInfo.isNotEmpty()) {
                put("Java", javaInfo)
            }

        }
    }

    private fun buildGitInfo(gitInfo: GitInfo?): List<AppInfoDetail> {

        if (gitInfo == null) return emptyList()

        return buildList {
            add(AppInfoDetail(title = "Branch", value = gitInfo.branch))

            if (gitInfo.commit.commitMessage != null) add(
                AppInfoDetail(
                    title = "Commit",
                    value = gitInfo.commit.commitMessage.shortMessage
                )
            )

            gitInfo.commit.let { commit ->

                if (commit.commitMessage?.shortMessage != null) {
                    add(AppInfoDetail(title = "Commit", value = commit.commitMessage.shortMessage))
                }

                if (commit.user?.name != null) {
                    add(AppInfoDetail(title = "User", value = commit.user.name))
                }


                add(AppInfoDetail(title = "Time", value = commit.timeStamp))
            }

        }
    }


    private fun buildOsInfo(osInfo: OperatingSystemInfo?): List<AppInfoDetail> {
        if (osInfo == null) return emptyList()
        return buildList {
            add(AppInfoDetail(title = "OS", value = osInfo.name))
            add(AppInfoDetail(title = "Version", value = osInfo.version))
            add(AppInfoDetail(title = "Architecture", value = osInfo.architecture))
        }
    }


    private fun buildApplicationBuildInfo(buildInfo: ApplicationBuildInfo?): List<AppInfoDetail> {
        if (buildInfo == null) return emptyList()
        return buildList {
            add(AppInfoDetail(title = "Version", value = buildInfo.version))
            add(AppInfoDetail(title = "Built At", value = buildInfo.time))
            add(AppInfoDetail(title = "Group", value = buildInfo.group))
            add(AppInfoDetail(title = "Artifact", value = buildInfo.artifact))
            add(AppInfoDetail(title = "Name", value = buildInfo.name))
        }
    }

    private fun buildJavaInfo(javaInfo: JavaInfo?): List<AppInfoDetail> {
        if (javaInfo == null) return emptyList()
        return buildList {
            add(AppInfoDetail(title = "Version", value = javaInfo.version))

            if (javaInfo.vendor != null) {
                add(AppInfoDetail(title = "Vendor", value = javaInfo.vendor.name))
                add(AppInfoDetail(title = "Vendor version", value = javaInfo.vendor.version))
            }

            if (javaInfo.runtime != null) {
                add(AppInfoDetail(title = "Runtime", value = javaInfo.runtime.name))
                add(AppInfoDetail(title = "Vendor version", value = javaInfo.runtime.version))
            }

            if (javaInfo.jvmDetails != null) {
                add(AppInfoDetail(title = "JVM", value = javaInfo.jvmDetails.name))
                add(AppInfoDetail(title = "JVM version", value = javaInfo.jvmDetails.version))
                add(AppInfoDetail(title = "JVM Vendor", value = javaInfo.jvmDetails.vendor))
            }

        }
    }
}

sealed class ApplicationInfoScreenEvent {
    data class GetApplicationInfo(
        val application: Application,
        val scope: CoroutineScope = CoroutineScope(context = Dispatchers.Default),
        val refresh: Boolean = false
    ) :
        ApplicationInfoScreenEvent()
}