package domain.exception

sealed class ActuatorException : RuntimeException() {
    data object ActuatorNotEnabledException : ActuatorException()
    data object BearerTokenInvalidException : ActuatorException()
    data object CouldNotReachActuatorException : ActuatorException()
}

