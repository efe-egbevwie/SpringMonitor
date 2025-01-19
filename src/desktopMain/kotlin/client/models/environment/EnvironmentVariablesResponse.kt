package client.models.environment

import domain.models.environment.EnvironmentVariable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable
data class EnvironmentVariablesResponse(
    val activeProfiles: List<String>,
    val propertySources: List<PropertySource>
)

@Serializable
data class PropertySource(
    val name: String,
    val properties: JsonObject
)

fun PropertySource.getEnvVariables(): List<EnvironmentVariable> {
    return this.properties.map { property ->
        EnvironmentVariable(
            name = property.key,
            value = property.value.jsonObject["value"].toString(),
            origin = property.value.jsonObject["origin"].toString()
        )
    }

}

