package client.models.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String

)
