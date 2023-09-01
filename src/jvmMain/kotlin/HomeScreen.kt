import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import httpRequests.HttpRequestsScreen


object HomeScreenDestination : Screen {
    @Composable
    override fun Content() {
        HomeScreen()
    }

}

@Composable
fun HomeScreen() {

    LaunchedEffect(1){
//        ActuatorLocalClient.getAllActuators
    }

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
                    iconPainters = painters,
                    onMonitorClicked = { selectedMonitor = it }
                )
            } else {
                NavigationRailUi(
                    monitors = monitors,
                    selectedMonitor = selectedMonitor,
                    iconPainters = painters,
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
    iconPainters: List<Painter>,
    onMonitorClicked: (Monitor) -> Unit
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
                            Icon(iconPainters[index], contentDescription = "Dashboard")
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
                                HttpRequestsScreen(modifier = Modifier.padding(20.dp))
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
    monitors: Array<Monitor>,
    selectedMonitor: Monitor,
    iconPainters: List<Painter>,
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
                        Icon(iconPainters[index], contentDescription = "Dashboard")
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
                    HttpRequestsScreen(modifier = Modifier.padding(20.dp))
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

