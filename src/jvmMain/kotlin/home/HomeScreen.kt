package home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import applicationInfo.ApplicationInfoScreen
import cafe.adriel.voyager.core.screen.Screen
import client.ActuatorLocalClient
import common.ui.composables.ApplicationsDropDownItem
import common.ui.composables.DeleteApplicationDialog
import common.ui.composables.EditApplicationDialog
import common.ui.sampleApplication
import common.ui.sampleApplications
import dashboard.DashboardScreen
import domain.models.Application
import environmentVariables.EnvironmentVariablesScreen
import httpRequests.HttpRequestsScreen
import setupApplication.composables.ApplicationItem
import theme.SpringMonitorTheme


data class HomeScreenDestination(val selectedApplication: Application) : Screen {
    @Composable
    override fun Content() {

        var currentApplication by remember {
            mutableStateOf(selectedApplication)
        }

        println("current app is $currentApplication")

        val viewModel by remember {
            mutableStateOf(HomeScreenViewModel())
        }

        LaunchedEffect(1) {
            viewModel.onEvent(HomeScreenEvent.GetAllApplications)
        }

        val state = viewModel.state.collectAsState()

        HomeScreen(
            selectedApplication = currentApplication,
            allApplications = state.value.allApplications,
            onApplicationSelected = { newApplication ->
                currentApplication = newApplication
            }
        )
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

        var selectedMonitor by remember {
            mutableStateOf(defaultMonitor)
        }

        var navigationExpanded by remember {
            mutableStateOf(true)
        }

        var showAllApplicationsDropDown by remember {
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
                    ActuatorLocalClient.updateApplication(application = newApplication)
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
                    ActuatorLocalClient.deleteApplication(applicationId)
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
            Surface(modifier.fillMaxWidth()) {
                Row(modifier.fillMaxWidth()) {

                    Spacer(modifier = Modifier.width(10.dp))

                    Divider(modifier = modifier.width(1.dp).fillMaxHeight())

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = modifier.fillMaxSize().padding(start = 8.dp)) {
                        when (selectedMonitor) {
                            Monitor.DASHBOARD -> {
                                DashboardScreen(modifier = Modifier.fillMaxSize(), application = currentApplication)
                            }

                            Monitor.HTTP -> {
                                HttpRequestsScreen(application = currentApplication, modifier = Modifier.padding(20.dp))
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


    Row(modifier = Modifier.fillMaxSize()) {

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


        Divider(modifier = Modifier.width(1.dp).fillMaxHeight())


        Box(modifier = Modifier.fillMaxSize().padding(start = 20.dp)) {
            when (selectedMonitor) {

                Monitor.DASHBOARD -> {
                    DashboardScreen(modifier = Modifier.fillMaxSize(), application = currentApplication)
                }

                Monitor.HTTP -> {
                    HttpRequestsScreen(modifier = Modifier.padding(20.dp), application = currentApplication)
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

//                                Divider(
//                                    modifier = Modifier.height(2.dp).fillMaxWidth().padding(start = 2.dp, end = 2.dp)
//                                )


                            }

                        }
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

