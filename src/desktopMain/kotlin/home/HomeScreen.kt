package home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import applicationInfo.ApplicationInfoScreen
import cafe.adriel.voyager.core.screen.Screen
import client.ApplicationsDb
import common.ui.composables.ApplicationsDropDownItem
import common.ui.composables.DeleteApplicationDialog
import common.ui.composables.EditApplicationDialog
import common.ui.composables.IslandCard
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import common.ui.modifiers.hoverClickable
import common.ui.sampleApplication
import common.ui.sampleApplications
import dashboard.DashboardScreen
import domain.models.Application
import environmentVariables.EnvironmentVariablesScreen
import httpRequests.HttpRequestsScreen
import setupApplication.composables.ApplicationItem
import theme.SpringMonitorTheme

data class HomeScreenDestination(val selectedApplicationId: Int) : Screen {

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

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        val defaultMonitor: Monitor = Monitor.DASHBOARD
        var selectedMonitor by rememberSaveable {
            mutableStateOf(defaultMonitor)
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

        Row {
            IslandCard {
                Column {
                    var showEditApplicationDialog by remember {
                        mutableStateOf(false)
                    }
                    var showDeleteApplicationDialog by remember {
                        mutableStateOf(false)
                    }

                    Row(modifier = Modifier.wrapContentWidth()) {
                        ApplicationItem(
                            application = selectedApplication,
                            showDropDownArrow = true,
                            onEditApplicationClicked = {
                                showEditApplicationDialog = true
                            },
                            onDeleteApplicationClicked = {
                                showDeleteApplicationDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.2f)
                                .clickable {
                                    showAllApplicationsDropDown = !showAllApplicationsDropDown
                                }
                        )

                        AllApplicationsDropDown(
                            isExpanded = showAllApplicationsDropDown,
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

                    ExpandedNavigationDrawer(
                        modifier = Modifier.fillMaxWidth(0.2f).wrapContentHeight().padding(start = 8.dp, end = 8.dp),
                        monitors = monitors,
                        selectedMonitor = selectedMonitor,
                        iconVectors = iconVectors,
                        onMonitorClicked = { selectedMonitor = it }
                    )

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

            IslandCard(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
            ) {
                NavigationContent(
                    modifier = Modifier.fillMaxHeight().padding(15.dp),
                    selectedMonitor = selectedMonitor,
                    currentApplication = selectedApplication
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedNavigationDrawer(
    modifier: Modifier = Modifier,
    monitors: Array<Monitor>,
    selectedMonitor: Monitor,
    iconVectors: List<ImageVector>,
    onMonitorClicked: (Monitor) -> Unit
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth()
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
        content = {}
    )
}


@Composable
private fun AllApplicationsDropDown(
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
                    Surface {
                        Column(
                            modifier = Modifier.hoverClickable(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = RoundedCornerShape(0.dp),
                                onClicked = { onApplicationClicked(application) }
                            )
                        ) {
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


@Composable
private fun NavigationContent(
    modifier: Modifier = Modifier,
    selectedMonitor: Monitor,
    currentApplication: Application
) {

    Box(modifier = modifier) {
        when (selectedMonitor) {
            Monitor.DASHBOARD -> {
                DashboardScreen(modifier = Modifier, application = currentApplication)
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
                    "Selected item is $selectedMonitor",
                    modifier = Modifier.align(Alignment.TopStart)
                )
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
private fun HomeScreenPreview() {
    SpringMonitorTheme {
        HomeScreen(
            selectedApplication = sampleApplication,
            allApplications = sampleApplications,
            onApplicationSelected = {}
        )
    }
}

