package domain.models.info

data class ApplicationInfo(
    val gitInfo: GitInfo? = null,
    val build: ApplicationBuildInfo? = null,
    val javaInfo: JavaInfo,
    val operatingSystemInfo: OperatingSystemInfo
)


data class GitInfo(
    val commit: GitCommitInfo,
    val branch: String
)

data class GitCommitInfo(
    val commitMessage: GitCommitMessage? = null,
    val user: GitUserInfo? = null,
    val idInfo: GitCommitIdInfo? = null,
    val timeStamp: String

)

data class GitCommitMessage(
    val shortMessage: String,
    val fullMessage: String? = null
)

data class GitUserInfo(
    val name: String? = null,
    val email: String? = null
)

data class GitCommitIdInfo(
    val abbrev: String,
    val full: String
)

data class JavaInfo(
    val version: String,
    val vendor: JavaDetails? = null,
    val runtime: JavaDetails? = null,
    val jvmDetails: JvmDetails? = null
)


data class JavaDetails(
    val name: String,
    val version: String
)

data class JvmDetails(
    val name: String,
    val vendor: String,
    val version: String
)


data class ApplicationBuildInfo(
    val artifact: String,
    val name: String,
    val time: String,
    val version: String,
    val group: String
)

data class OperatingSystemInfo(
    val name: String,
    val version: String,
    val architecture: String
)