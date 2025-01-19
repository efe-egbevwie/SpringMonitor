package domain.models

import androidx.compose.runtime.Stable
import comefe.Applications

@Stable
data class Application(
    val alias: String,
    val applicationId: Int? = null,
    val actuatorUrl: String,
    val bearerToken: String
)


fun Applications.toDomainApplication(): Application {
    return Application(
        alias = this.application_alias,
        applicationId = this.application_id.toInt(),
        actuatorUrl = this.actuator_url,
        bearerToken = this.bearer_token
    )
}
