package domain.models.environment

data class EnvironmentVariable(
    val name: String,
    val value: String,
    val origin: String,
)