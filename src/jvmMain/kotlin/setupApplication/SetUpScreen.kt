package setupApplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import client.ActuatorLocalClient
import common.domain.Application
import common.ui.composables.DeleteApplicationDialog
import common.ui.composables.EditApplicationDialog
import common.ui.sampleApplications
import home.HomeScreenDestination
import io.github.oshai.kotlinlogging.KotlinLogging
import setupApplication.composables.ActuatorDetails
import setupApplication.composables.ExistingApplicationsUi
import setupApplication.composables.HomeScreenDescription
import theme.SpringMonitorTheme


private val logger = KotlinLogging.logger { }


object SetUpScreenDestination : Screen {
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        val existingApplications = remember {
            mutableStateListOf<Application>()
        }

        LaunchedEffect(1) {
            ActuatorLocalClient.getAllApplications.collect { applications ->
                existingApplications.clear()
                existingApplications.addAll(applications)
                logger.info { "existing apps are: $applications" }
                println("apps are: $existingApplications")
            }
        }


        SetUpScreen(
            existingApplications = existingApplications,
            onSetUpSuccess = { newApplication ->
                navigateToDashBoard(navigator, newApplication)
            },
            onApplicationItemClicked = { selectedApplication ->
                navigateToDashBoard(navigator, selectedApplication)
            }
        )
    }

}

@Composable
fun SetUpScreen(
    existingApplications: List<Application>,
    onSetUpSuccess: (newApplication: Application) -> Unit,
    onApplicationItemClicked: (Application) -> Unit
) {


    val setUpScreenViewModel by remember { mutableStateOf(SetUpScreenViewModel()) }

    val setUpScreenState = setUpScreenViewModel.state.collectAsState()

    val setUpSuccess = setUpScreenState.value.getActuatorSuccess
    val newApplication = setUpScreenState.value.newApplication
    if (setUpSuccess && newApplication != null) {
        onSetUpSuccess(newApplication)
    }

    var showEditAppDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteApplicationDialog by remember {
        mutableStateOf(false)
    }

    var selectedApplication: Application? = remember {
        null
    }


    Surface {

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(start = 50.dp)) {
                HomeScreenDescription()

                Spacer(modifier = Modifier.height(40.dp))

                ExistingApplicationsUi(
                    applications = existingApplications,
                    onApplicationItemClicked = { application ->
                        onApplicationItemClicked(application)
                    },
                    onEditApplicationClicked = { application ->
                        showEditAppDialog = true
                        selectedApplication = application
                    },
                    onDeleteApplicationClicked = { application ->
                        showDeleteApplicationDialog = true
                        selectedApplication = application
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth(0.2f)
                        .padding(bottom = 20.dp)
                )


            }

            Spacer(modifier = Modifier.width(70.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {

                Spacer(modifier = Modifier.height(150.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(50.dp))

                    Text("Set up an Application", style = MaterialTheme.typography.headlineLarge)

                    Spacer(modifier = Modifier.height(50.dp))

                    ActuatorDetails(
                        modifier = Modifier.padding(),
                        isLoading = setUpScreenState.value.isLoading,
                        onSetUpButtonClicked = { application ->
                            setUpScreenViewModel.onEvent(
                                SetUpScreenEvent.GetActuatorEndPoints(application)
                            )
                        }
                    )

                }


            }


        }

        EditApplicationDialog(
            isDialogVisible = showEditAppDialog,
            onSetUpButtonClicked = { newApplication ->
                logger.info { "new app is : $newApplication" }
                ActuatorLocalClient.updateApplication(application = newApplication)
                showEditAppDialog = false
            },
            onDialogClosed = {
                showEditAppDialog = false
            },
            application = selectedApplication,
            modifier = Modifier.padding(20.dp)
        )

        DeleteApplicationDialog(
            application = selectedApplication,
            isDialogVisible = showDeleteApplicationDialog,
            onDismiss = {
                showDeleteApplicationDialog = false
            },
            onConfirm = {applicationId ->
                ActuatorLocalClient.deleteApplication(applicationId)
                showDeleteApplicationDialog = false
            }
        )


    }


}

private fun navigateToDashBoard(navigator: Navigator, newApplication: Application) {
    navigator.replace(HomeScreenDestination(selectedApplication = newApplication))
}


@Composable
@Preview
fun SetUpScreenPreview() {
    SpringMonitorTheme {
        SetUpScreen(onSetUpSuccess = {}, onApplicationItemClicked = {}, existingApplications = sampleApplications)
    }

}