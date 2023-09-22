package common.ui.composables.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.exception.ActuatorNotEnabledException
import domain.exception.BearerTokenNotValidException
import domain.exception.CouldNotReachApplicationException
import theme.SpringMonitorTheme

@Composable
fun ErrorScreen(exception: Exception?, modifier: Modifier = Modifier) {

    val errorMessage = when (exception) {
        is ActuatorNotEnabledException -> "Spring boot actuator is not enabled for this application"
        is BearerTokenNotValidException -> "Bearer token is expired or invalid, please update application"
        is CouldNotReachApplicationException -> "Could not reach instance, application may be down"
        else -> "Failed to perform operation"
    }

    SpringMonitorTheme {
        Surface {
            Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize().padding(20.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ArrowCircleDown,
                        contentDescription = "Error image",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxSize(0.3f)
                    )

                    Spacer(modifier.height(20.dp))

                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }



}

@Composable
@Preview
fun ErrorScreenPreview() {
    SpringMonitorTheme {
        Surface {
            ErrorScreen(exception = ActuatorNotEnabledException())
        }
    }

}