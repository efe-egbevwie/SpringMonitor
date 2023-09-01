package setupApplication.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.ProgressButton
import theme.SpringMonitorTheme

@Composable
fun ActuatorDetails(
    onSetUpButtonClicked: (actuatorUrl: String, bearerToken: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading:Boolean = false
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {


        var actuatorUrl by remember {
            mutableStateOf("")
        }

        var bearerToken by remember {
            mutableStateOf("")
        }

        var appName by remember {
            mutableStateOf("")
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text("Set up your Application", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(50.dp))

        OutlinedTextField(
            label = {
                Text("Application Name *")
            },
            value = appName,
            onValueChange = { newValue ->
                appName = newValue
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(30.dp))


        OutlinedTextField(
            label = {
                Text("Actuator URL *")
            },
            placeholder = {
                Text("https://myapplication.com/actuator")
            },
            value = actuatorUrl,
            onValueChange = { newValue ->
                actuatorUrl = newValue
            },

            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            label = {
                Text("Bearer Token *")
            },
            value = bearerToken,
            onValueChange = { newValue ->
                bearerToken = newValue
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(30.dp))


        ProgressButton(
            onclick = {
                onSetUpButtonClicked(actuatorUrl, bearerToken)
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
            ActuatorDetails(onSetUpButtonClicked = { _, _ -> })
        }
    }

}