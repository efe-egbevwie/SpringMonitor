import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import common.domain.Application
import common.ui.sampleApplication
import httpRequests.HttpRequestsScreen
import theme.SpringMonitorTheme


data class HomeScreenDestination(val selectedApplication: Application) : Screen {
    @Composable
    override fun Content() {

        val currentApplication by remember {
            mutableStateOf(selectedApplication)
        }

        HomeScreen(currentApplication)
    }

}

@Composable
fun HomeScreen(selectedApplication: Application) {

    Surface(modifier = Modifier.fillMaxSize()) {

        val defaultMonitor: Monitor = Monitor.DASHBOARD

        var selectedMonitor by remember {
            mutableStateOf(defaultMonitor)
        }

        var navigationExpanded by remember {
            mutableStateOf(true)
        }

        val monitors = Monitor.values()

        val painters = listOf(
            painterResource("images/dashboard.svg"),
            painterResource("images/info.svg"),
            painterResource("images/web.svg"),
            painterResource("images/environment.svg"),
            painterResource("images/controller.svg")
        )

        val iconVectors = listOf(
            Icons.Filled.Dashboard,
            Icons.Filled.Info,
            Icons.Filled.Http,
            Icons.Filled.Terminal,
            Icons.Filled.Code
        )

        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { navigationExpanded = !navigationExpanded },
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
            ) {
                Icon(Icons.Filled.List, contentDescription = "toggle navigation")
            }


            if (navigationExpanded) {
                ExpandedNavigationDrawer(
                    monitors = monitors,
                    selectedMonitor = selectedMonitor,
                    iconVectors = iconVectors,
                    currentApplication = selectedApplication,
                    onMonitorClicked = { selectedMonitor = it }
                )
            } else {
                NavigationRailUi(
                    monitors = monitors,
                    selectedMonitor = selectedMonitor,
                    iconVectors = iconVectors,
                    currentApplication = selectedApplication,
                    onMonitorClicked = { selectedMonitor = it }
                )
            }
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
        modifier = modifier.wrapContentWidth(align = Alignment.Start).fillMaxHeight(),
        drawerContent = {
            Column(
                modifier = Modifier.fillMaxHeight().wrapContentWidth()
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
                        modifier = Modifier.padding(top = 10.dp)
                    )

                }
            }

        },
        content = {
            Surface {
                Row(modifier = Modifier.padding(start = 20.dp)) {

                    Divider(modifier = Modifier.width(1.dp).fillMaxHeight())

                    Box(modifier = Modifier.fillMaxSize().padding(start = 20.dp)) {
                        when (selectedMonitor) {
                            Monitor.HTTP -> {
                                HttpRequestsScreen(application = currentApplication, modifier = Modifier.padding(20.dp))
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

        Spacer(modifier = Modifier.width(30.dp))

        Divider(modifier = Modifier.width(1.dp).fillMaxHeight())


        Box(modifier = Modifier.fillMaxSize().padding(start = 20.dp)) {
            when (selectedMonitor) {
                Monitor.HTTP -> {
                    HttpRequestsScreen(modifier = Modifier.padding(20.dp), application = currentApplication)
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
        HomeScreen(selectedApplication = sampleApplication)
    }

}

