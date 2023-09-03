package setupApplication

import home.HomeScreenDestination
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import client.ActuatorLocalClient
import common.domain.Application
import common.ui.sampleApplications
import setupApplication.composables.ActuatorDetails
import setupApplication.composables.ExistingApplicationsUi
import setupApplication.composables.HomeScreenDescription
import theme.SpringMonitorTheme


object SetUpScreenDestination : Screen {
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        val existingApplications = remember {
            mutableStateListOf<Application>()
        }

        LaunchedEffect(1) {
            ActuatorLocalClient.getAllApplications.collect { applications ->
                existingApplications.addAll(applications)
                println("apps are: $applications")
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

    Surface {

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(start = 50.dp)) {
                HomeScreenDescription()

                Spacer(modifier = Modifier.height(20.dp))

                ExistingApplicationsUi(
                    applications = existingApplications,
                    onApplicationItemClicked = { application ->
                        onApplicationItemClicked(application)
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .padding(bottom = 20.dp)
                )


            }

            Spacer(modifier = Modifier.width(70.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {

                Spacer(modifier = Modifier.height(150.dp))

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