package home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import applicationInfo.ApplicationInfoScreen
import cafe.adriel.voyager.core.screen.Screen
import client.ApplicationsDb
import common.ui.composables.ApplicationsDropDownItem
import common.ui.composables.DeleteApplicationDialog
import common.ui.composables.EditApplicationDialog
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import common.ui.sampleApplication
import common.ui.sampleApplications
import dashboard.DashboardScreen
import domain.models.Application
import environmentVariables.EnvironmentVariablesScreen
import httpRequests.HttpRequestsScreen
import io.github.oshai.kotlinlogging.KotlinLogging
import setupApplication.composables.ApplicationItem
import theme.SpringMonitorTheme

data class HomeScreenDestination(val selectedApplicationId: Int) : Screen {

    private val logger = KotlinLogging.logger { }

    @Composable
    override fun Content() {

        var currentApplicationId by rememberSaveable {
            mutableStateOf(selectedApplicationId)
        }

        val scope = rememberCoroutineScope()


        val viewModel by remember {
            mutableStateOf(HomeScreenViewModel())
        }

        LaunchedEffect(key1 = currentApplicationId) {
            viewModel.onEvent(HomeScreenEvent.GetSelectedApplication(applicationId = currentApplicationId, scope))
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.onEvent(HomeScreenEvent.GetAllApplications)
        }

        val state by viewModel.state.collectAsState()


        when (state.loadingState) {
            LoadingState.Loading -> LoadingScreen()
            LoadingState.SuccessLoading -> {

                if (state.currentApplication != null) {
                    HomeScreen(
                        selectedApplication = state.currentApplication!!,
                        allApplications = state.allApplications,
                        onApplicationSelected = { selectedApplication ->
                            currentApplicationId = selectedApplication.applicationId!!
                        }
                    )
                }

            }

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.exception)
        }
    }

}

@Composable
fun HomeScreen(
    selectedApplication: Application,
    allApplications: List<Application>,
    onApplicationSelected: (application: Application) -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize()) {

        val defaultMonitor: Monitor = Monitor.DASHBOARD

        var selectedMonitor by rememberSaveable {
            mutableStateOf(defaultMonitor)
        }

        var navigationExpanded by rememberSaveable {
            mutableStateOf(true)
        }

        var showAllApplicationsDropDown by rememberSaveable {
            mutableStateOf(false)
        }


        val monitors = Monitor.entries.toTypedArray()


        val iconVectors = listOf(
            Icons.Filled.Dashboard,
            Icons.Filled.Info,
            Icons.Filled.Http,
            Icons.Filled.Terminal,
            Icons.Filled.Code
        )

        Column(modifier = Modifier.fillMaxSize()) {

            var showEditApplicationDialog by remember {
                mutableStateOf(false)
            }
            var showDeleteApplicationDialog by remember {
                mutableStateOf(false)
            }


            Row(modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)) {
                IconButton(
                    onClick = { navigationExpanded = !navigationExpanded },
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                ) {
                    Icon(Icons.Filled.List, contentDescription = "toggle navigation")
                }

                Spacer(modifier = Modifier.width(20.dp))

                ApplicationItem(
                    application = selectedApplication,
                    showDropDownArrow = true,
                    onEditApplicationClicked = {
                        showEditApplicationDialog = true
                    },
                    onDeleteApplicationClicked = {
                        showDeleteApplicationDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(0.2f).clickable {
                        showAllApplicationsDropDown = !showAllApplicationsDropDown
                    }
                )

                if (showAllApplicationsDropDown) {
                    AllApplicationsDropDown(
                        isExpanded = true,
                        allApplications = allApplications,
                        onDismiss = {
                            showAllApplicationsDropDown = false
                        },
                        onApplicationClicked = { application ->
                            onApplicationSelected(application)
                            showAllApplicationsDropDown = false
                        }

                    )
                }
            }

            when (navigationExpanded) {
                true -> ExpandedNavigationDrawer(
                    monitors = monitors,
                    selectedMonitor = selectedMonitor,
                    iconVectors = iconVectors,
                    currentApplication = selectedApplication,
                    onMonitorClicked = { selectedMonitor = it }
                )

                false -> NavigationRailUi(
                    monitors = monitors,
                    selectedMonitor = selectedMonitor,
                    iconVectors = iconVectors,
                    currentApplication = selectedApplication,
                    onMonitorClicked = { selectedMonitor = it }
                )
            }


            EditApplicationDialog(
                isDialogVisible = showEditApplicationDialog,
                onSetUpButtonClicked = { newApplication ->
                    ApplicationsDb.updateApplication(application = newApplication)
                    showEditApplicationDialog = false
                },
                onDialogClosed = {
                    showEditApplicationDialog = false
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
                onConfirm = { applicationId ->
                    ApplicationsDb.deleteApplication(applicationId)
                    showDeleteApplicationDialog = false
                }
            )


        }

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedNavigationDrawer(
    modifier: Modifier = Modifier,
    monitors: Array<Monitor>,
    selectedMonitor: Monitor,
    iconVectors: List<ImageVector>,
    onMonitorClicked: (Monitor) -> Unit,
    currentApplication: Application
) {
    PermanentNavigationDrawer(
        modifier = modifier.fillMaxWidth().fillMaxHeight().padding(start = 8.dp),
        drawerContent = {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.2f)
            ) {

                monitors.forEachIndexed { index, monitor ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = monitor.title, maxLines = 1)
                        },
                        selected = selectedMonitor == monitor,
                        icon = {
                            Icon(iconVectors[index], contentDescription = "Dashboard")
                        },
                        onClick = {
                            onMonitorClicked(monitor)

                        },
                        modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                    )

                }
            }

        },
        content = {

            NavigationContent(
                modifier = Modifier,
                selectedMonitor = selectedMonitor,
                currentApplication = currentApplication
            )
        }
    )
}


@Composable
fun NavigationRailUi(
    modifier: Modifier = Modifier,
    currentApplication: Application,
    monitors: Array<Monitor>,
    selectedMonitor: Monitor,
    iconVectors: List<ImageVector>,
    onMonitorClicked: (Monitor) -> Unit
) {


    Row(modifier = modifier.fillMaxSize()) {

        NavigationRail(
            modifier = Modifier.wrapContentWidth().fillMaxHeight(),
        ) {
            monitors.forEachIndexed { index, monitor ->
                NavigationRailItem(

                    selected = selectedMonitor == monitor,
                    icon = {
                        Icon(iconVectors[index], contentDescription = "Dashboard")
                    },
                    onClick = {
                        onMonitorClicked(monitor)

                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

            }
        }


        NavigationContent(
            modifier = Modifier,
            selectedMonitor = selectedMonitor,
            currentApplication = currentApplication
        )

    }


}

@Composable
fun AllApplicationsDropDown(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    allApplications: List<Application>,
    onApplicationClicked: (Application) -> Unit
) {
    SpringMonitorTheme {
        Surface {
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onDismiss.invoke() }
            ) {
                allApplications.forEach { application ->
                    SpringMonitorTheme {
                        Surface {

                            Column {
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.padding(10.dp),
                                    onClick = {
                                        onApplicationClicked(application)
                                    },
                                    text = {
                                        ApplicationsDropDownItem(application)
                                    }
                                )

                            }

                        }
                    }

                }
            }

        }
    }
}


@Composable
fun NavigationContent(modifier: Modifier = Modifier, selectedMonitor: Monitor, currentApplication: Application) {

    Surface(modifier.fillMaxWidth()) {
        Row(modifier.fillMaxWidth()) {

            Spacer(modifier = Modifier.width(10.dp))

            Divider(modifier = modifier.width(1.dp).fillMaxHeight())

            Spacer(modifier = Modifier.width(10.dp))

            Box(modifier = Modifier.fillMaxSize().padding(start = 4.dp)) {

                when (selectedMonitor) {
                    Monitor.DASHBOARD -> {
                        DashboardScreen(modifier = Modifier.fillMaxSize(), application = currentApplication)
                    }

                    Monitor.HTTP -> {
                        HttpRequestsScreen(application = currentApplication, modifier = Modifier)
                    }

                    Monitor.INFO -> {
                        ApplicationInfoScreen(modifier = Modifier, application = currentApplication)
                    }

                    Monitor.ENVIRONMENT -> {
                        EnvironmentVariablesScreen(application = currentApplication)
                    }

                    else -> {
                        Text(
                            "selected item is $selectedMonitor",
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                    }
                }


            }
        }

    }


}

enum class Monitor(val title: String) {
    DASHBOARD("Dashboard"),
    INFO("Info"),
    HTTP("Http requests"),
    ENVIRONMENT("Environment"),
    CONTROLLERS("Controllers")

}

@Composable
@Preview
fun HomeScreenPreview() {
    SpringMonitorTheme {
        HomeScreen(
            selectedApplication = sampleApplication,
            allApplications = sampleApplications,
            onApplicationSelected = {})
    }

}

