package setupApplication.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.ProgressButton
import domain.models.Application
import theme.SpringMonitorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActuatorDetails(
    onSetUpButtonClicked: (application: Application) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        var appName by remember {
            mutableStateOf("")
        }

        val appNameValid by remember(key1 = appName) {
            mutableStateOf(appName.isNotEmpty())
        }

        var actuatorUrl by remember {
            mutableStateOf("")
        }
        val actuatorUrlValid by remember(key1 = actuatorUrl) {
            mutableStateOf(actuatorUrl.isNotEmpty())
        }

        var bearerToken by remember {
            mutableStateOf("")
        }

        val bearerTokenValid by remember(key1 = bearerToken) {
            mutableStateOf(bearerToken.isNotEmpty())
        }

        var showValidationErrors by remember {
            mutableStateOf(false)
        }

        val allDetailsValid by remember(keys = arrayOf(appNameValid, actuatorUrlValid, bearerTokenValid)) {
            mutableStateOf(appNameValid and actuatorUrlValid and bearerTokenValid)
        }

        OutlinedTextField(
            singleLine = true,
            label = {
                if (showValidationErrors and !appNameValid) {
                    Text("Enter a valid name ")
                    return@OutlinedTextField
                }
                Text("Application Name *")
            },
            value = appName,
            onValueChange = { newValue ->
                appName = newValue
            },
            isError = showValidationErrors and !appNameValid,
            modifier = Modifier.fillMaxWidth(0.5f),
        )

        Spacer(modifier = Modifier.height(30.dp))


        OutlinedTextField(
            singleLine = true,
            label = {
                if (showValidationErrors && !actuatorUrlValid) {
                    Text("Enter a valid URl")
                    return@OutlinedTextField
                }
                if (errorMessage != null) {
                    Text(errorMessage)
                    return@OutlinedTextField
                }
                Text("Actuator URL *")
            },
            placeholder = {
                Text("https://myapplication.com/actuator")
            },
            value = actuatorUrl,
            onValueChange = { newValue ->
                actuatorUrl = newValue
            },
            isError = (showValidationErrors and !actuatorUrlValid) || errorMessage != null,
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            singleLine = true,
            label = {
                if (showValidationErrors and !bearerTokenValid) {
                    Text("Enter a valid bearer token *")
                    return@OutlinedTextField
                }
                Text("Bearer Token *")
            },
            value = bearerToken,
            onValueChange = { newValue ->
                bearerToken = newValue
            },
            isError = showValidationErrors and !bearerTokenValid,
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(30.dp))


        ProgressButton(
            onclick = {
                showValidationErrors = true
                if (!allDetailsValid) return@ProgressButton
                onSetUpButtonClicked(
                    Application(alias = appName, actuatorUrl = actuatorUrl, bearerToken = bearerToken)
                )
            },
            buttonText = "Set up",
            isLoading = isLoading
        )

    }
}


@Preview
@Composable
fun ActuatorDetailsPreview() {
    SpringMonitorTheme {

        Surface {
            ActuatorDetails(onSetUpButtonClicked = { _ -> })
        }
    }

}