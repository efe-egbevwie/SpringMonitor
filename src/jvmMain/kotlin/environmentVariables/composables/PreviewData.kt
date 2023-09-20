package environmentVariables.composables

import domain.models.environment.EnvironmentVariable

val previewEnvironmentVariables = List(30) { index ->
    EnvironmentVariable(name = "Env variable: $index", value = "Env value: $index", origin = "Env origin: $index")
}