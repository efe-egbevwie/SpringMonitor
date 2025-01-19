package client.models.info

import common.util.formateTimeStamp
import common.util.gitCommitTimeStampFormat
import domain.models.info.*
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationInfoResponse(
    val git: GitInfoResponse? = null,
    val build: ApplicationBuildInfoResponse,
    val java: ApplicationJavaInfoResponse,
    val os: MachineOsInfoResponse
)

@Serializable
data class ApplicationBuildInfoResponse(
    val artifact: String,
    val name: String,
    val time: String,
    val version: String,
    val group: String
)

@Serializable
data class GitInfoResponse(
    val commit: ApiGitCommitInfo? = null,
    val branch: String
)

@Serializable
data class ApiGitCommitInfo(
    val message: Message,
    val user: ApiGitCommitUserInfo? = null,
    val id: ApiGitCommitId? = null,
    val time: String? = null
)

@Serializable
data class ApiGitCommitId(
    val abbrev: String,
    val full: String
)

@Serializable
data class Message(
    val short: String,
    val full: String
)

@Serializable
data class ApiGitCommitUserInfo(
    val name: String,
    val email: String
)

@Serializable
data class ApplicationJavaInfoResponse(
    val version: String,
    val vendor: ApiRuntimeInfo,
    val runtime: ApiRuntimeInfo,
    val jvm: ApiApplicationJvmInfo
)

@Serializable
data class ApiApplicationJvmInfo(
    val name: String,
    val vendor: String,
    val version: String
)

@Serializable
data class ApiRuntimeInfo(
    val name: String,
    val version: String? = null
)

@Serializable
data class MachineOsInfoResponse(
    val name: String,
    val version: String,
    val arch: String
)


fun ApplicationInfoResponse.toDomainAppInfo(): ApplicationInfo {

    val gitInfo: GitInfo? = this.git?.toDomainGitInfo()
    val appBuildInfo: ApplicationBuildInfo? = this.build.toDomainApplicationBuildInfo()
    val javaInfo: JavaInfo = this.java.toDomainJavaInfo()
    val osInfo = this.os.toDomainOperatingSystemInfo()

    return ApplicationInfo(
        gitInfo = gitInfo,
        build = appBuildInfo,
        javaInfo = javaInfo,
        operatingSystemInfo = osInfo
    )

}

fun GitInfoResponse.toDomainGitInfo(): GitInfo {

    val commitMessage: GitCommitMessage? = if (this.commit == null) null else
        GitCommitMessage(
            shortMessage = this.commit.message.short,
            fullMessage = this.commit.message.full
        )

    val userInfo: GitUserInfo? = if (this.commit?.user == null) null else GitUserInfo(
        name = this.commit.user.name,
        email = this.commit.user.email
    )

    val idInfo: GitCommitIdInfo? = if (this.commit?.id == null) null else GitCommitIdInfo(
        abbrev = this.commit.id.abbrev,
        full = this.commit.id.full
    )

    val commitInfo = GitCommitInfo(
        commitMessage = commitMessage,
        user = userInfo,
        idInfo = idInfo,
        timeStamp = this.commit?.time?.let { formateTimeStamp(timeStamp = it, formatter = gitCommitTimeStampFormat) }
            ?: ""
    )




    return GitInfo(commit = commitInfo, branch = this.branch)
}

fun ApplicationBuildInfoResponse.toDomainApplicationBuildInfo(): ApplicationBuildInfo {


    return ApplicationBuildInfo(
        artifact = this.artifact,
        name = this.name,
        time = formateTimeStamp(this.time) ?: "",
        version = this.version,
        group = this.group
    )
}

fun ApplicationJavaInfoResponse.toDomainJavaInfo(): JavaInfo {

    val vendor = JavaDetails(name = this.vendor.name, version = this.vendor.version.orEmpty())

    val runTime = JavaDetails(name = this.runtime.name, version = this.version)

    val jvm = JvmDetails(name = this.jvm.name, vendor = this.jvm.vendor, version = this.jvm.version)

    return JavaInfo(version = this.version, vendor = vendor, runtime = runTime, jvmDetails = jvm)
}

fun MachineOsInfoResponse.toDomainOperatingSystemInfo(): OperatingSystemInfo {

    return OperatingSystemInfo(name = this.name, version = this.version, architecture = this.arch)
}