package dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.domain.Application
import common.domain.DashboardMetrics
import dashboard.composables.ApplicationStatusCard
import dashboard.composables.ApplicationUpTimeUi
import dashboard.composables.ResourceMetricCardUi


@Composable
fun DashboardScreen(modifier: Modifier = Modifier, application: Application) {

    val viewModel by remember {
        mutableStateOf(DashboardViewModel())
    }

    val coroutineScope = rememberCoroutineScope()
    val state: State<DashBoardScreenState> = viewModel.state.collectAsState()

    val dashBoardMetrics = state.value.dashboardMetrics

    LaunchedEffect(key1 = application) {
        viewModel.onEvent(DashBoardScreenEvent.GetSystemMetrics(application, coroutineScope))
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.value.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (dashBoardMetrics != null) {
            DashBoardScreenContent(modifier = modifier, metrics = dashBoardMetrics)
        }
    }


}

@Composable
fun DashBoardScreenContent(modifier: Modifier = Modifier, metrics: DashboardMetrics) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.verticalScroll(scrollState)) {

        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
            ApplicationStatusCard(modifier = Modifier.weight(1f), appStatus = "Up")
            Spacer(modifier = Modifier.width(20.dp))
            ApplicationUpTimeUi(modifier = Modifier.weight(1f), upTime = metrics.upTime?.getFormattedTime().orEmpty())
        }


        Spacer(modifier = Modifier.height(30.dp))


        ResourceMetricCardUi(
            resourceName = "CPU Usage",
            resourcePercentage = metrics.cpuUsagePercent?.getPercentage() ?: 0F,
            formattedPercentage = metrics.cpuUsagePercent?.getFormattedPercentage(),
            modifier = Modifier.fillMaxWidth().height(300.dp).padding(start = 20.dp, end = 20.dp)
        )

//        ResourceMetricCardUi(
//            resourceName = "CPU Usage",
//            resourcePercentage = 43f,
//            formattedPercentage = "43%",
//            modifier = Modifier.fillMaxWidth().height(300.dp).padding(start = 20.dp, end = 20.dp)
//        )


        Spacer(modifier = Modifier.height(30.dp))


        Row(modifier = Modifier.fillMaxWidth().height(300.dp).padding(start = 20.dp, end = 20.dp)) {

            ResourceMetricCardUi(
                resourceName = "Memory Usage",
                formattedPercentage = metrics.getMemoryUsedFormattedPercentage(),
                resourcePercentage = metrics.getMemoryUsedPercentage()?.toFloat() ?: 0f,
                modifier = Modifier.weight(2f)
            )

            Spacer(modifier = Modifier.width(20.dp))

            ResourceMetricCardUi(
                resourceName = "System Disk",
                formattedPercentage = metrics.getDiskUsedFormattedPercentage(),
                resourcePercentage = metrics.getDiskUsedPercentage()?.toFloat() ?: 0f,
                modifier = Modifier.weight(2f)
            )

        }


    }


}