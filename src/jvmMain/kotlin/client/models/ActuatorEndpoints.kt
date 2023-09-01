package client.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActuatorEndpoints (
    @SerialName("_links")
    val endpoints: Map<String, ActuatorEndpoint>
)

@Serializable
data class ActuatorEndpoint (
    @SerialName("href")
    val actuatorEndpointUrl: String,
    val templated: Boolean
)

