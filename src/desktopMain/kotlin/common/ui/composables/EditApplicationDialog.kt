package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import common.ui.ProgressButton
import common.ui.sampleApplication
import domain.models.Application
import theme.SpringMonitorTheme


@Composable
fun EditApplicationDialog(
    isDialogVisible: Boolean = false,
    onSetUpButtonClicked: (Application) -> Unit,
    onDialogClosed: () -> Unit,
    application: Application? = null,
    modifier: Modifier = Modifier
) {
    if (application == null) return

    Dialog(
        onCloseRequest = { onDialogClosed() },
        title = "Edit Application Details",
        undecorated = false,
        visible = isDialogVisible,
        state = rememberDialogState(size = DpSize(700.dp, 700.dp)),
    ) {

        SpringMonitorTheme {

            Surface(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = application.alias,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    EditApplicationDialogContent(
                        existingApplication = application,
                        onSetUpButtonClicked = { newApplication ->
                            onSetUpButtonClicked(newApplication)
                        },
                        modifier = modifier
                    )
                }

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditApplicationDialogContent(
    existingApplication: Application,
    onSetUpButtonClicked: (Application) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        var appName by remember {
            mutableStateOf(existingApplication.alias)
        }

        val appNameValid by remember(key1 = appName) {
            mutableStateOf(appName.isNotEmpty())
        }

        var actuatorUrl by remember {
            mutableStateOf(existingApplication.actuatorUrl)
        }
        val actuatorUrlValid by remember(key1 = actuatorUrl) {
            mutableStateOf(actuatorUrl.isNotEmpty())
        }

        var bearerToken by remember {
            mutableStateOf(existingApplication.bearerToken)
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
                Text("Actuator URL *")
            },
            placeholder = {
                Text("https://myapplication.com/actuator")
            },
            value = actuatorUrl,
            onValueChange = { newValue ->
                actuatorUrl = newValue
            },

            isError = showValidationErrors and !actuatorUrlValid,
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
                    Application(
                        alias = appName,
                        actuatorUrl = actuatorUrl,
                        bearerToken = bearerToken,
                        applicationId = existingApplication.applicationId
                    )
                )
            },
            buttonText = "Set up",
            isLoading = false
        )

    }
}


@Composable
@Preview
fun EditApplicationDialogPreview() {
    EditApplicationDialog(
        isDialogVisible = true,
        onSetUpButtonClicked = {},
        onDialogClosed = {},
        application = sampleApplication
    )
}

@Composable
@Preview
fun EditApplicationDialogContentPreview() {
    SpringMonitorTheme {
        Surface {
            EditApplicationDialogContent(existingApplication = sampleApplication, onSetUpButtonClicked = { })
        }
    }
}