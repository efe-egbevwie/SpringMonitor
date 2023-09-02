package setupApplication

import HomeScreenDestination
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
import setupApplication.composables.ActuatorDetails
import setupApplication.composables.HomeScreenDescription
import theme.SpringMonitorTheme


object SetUpScreenDestination : Screen {
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        SetUpScreen(
            onSetUpSuccess = {
                navigateToDashBoard(navigator)
            }
        )
    }

}

@Composable
fun SetUpScreen(
    onSetUpSuccess: () -> Unit
) {

    val existingApplications = remember {
        mutableStateListOf<Application>()
    }

    LaunchedEffect(1) {
        ActuatorLocalClient.getAllApplications.collect { applications ->
            existingApplications.addAll(applications)
            println("apps are: $applications")
        }
    }

    val setUpScreenViewModel by remember { mutableStateOf(SetUpScreenViewModel()) }

    val setUpScreenState = setUpScreenViewModel.state.collectAsState()

    val setUpSuccess = setUpScreenState.value.getActuatorSuccess
    if (setUpSuccess) onSetUpSuccess()

    Surface {

        Row(modifier = Modifier.fillMaxSize()) {
            HomeScreenDescription(
                existingApplications = existingApplications,
                modifier = Modifier.fillMaxHeight().padding(start = 50.dp)
            )

            Spacer(modifier = Modifier.width(70.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {

                Spacer(modifier = Modifier.height(150.dp))

                ActuatorDetails(
                    modifier = Modifier.padding(),
                    isLoading = setUpScreenState.value.isLoading,
                    onSetUpButtonClicked = { actuatorUrl, bearerToken ->
                        setUpScreenViewModel.onEvent(
                            SetUpScreenEvent.GetActuatorEndPoints(
                                actuatorUrl,
                                bearerToken
                            )
                        )
                    }
                )

            }


        }


    }


}

private fun navigateToDashBoard(navigator: Navigator) {
    navigator.replace(HomeScreenDestination)
}


@Composable
@Preview
fun SetUpScreenPreview() {
    SpringMonitorTheme {
        SetUpScreen(onSetUpSuccess = {})
    }

}