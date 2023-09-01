package common.domain

data class Application(
    val alias:String,
    val applicationId: Int? = null,
    val actuatorUrl: String,
    val bearerToken: String
)
