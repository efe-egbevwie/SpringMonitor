package dashboard

import AppViewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.composables.LiveUpdatesSwitch
import common.ui.composables.ScreenTitle
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import dashboard.composables.ApplicationStatusCard
import dashboard.composables.ApplicationUpTimeUi
import dashboard.composables.ResourceMetricCardUi
import domain.models.Application
import domain.models.DashboardMetrics
import kotlinx.coroutines.cancelChildren


@Composable
fun DashboardScreen(modifier: Modifier = Modifier, application: Application) {

    val viewModel:DashboardViewModel by rememberSaveable {
        mutableStateOf(AppViewModels.dashBoardViewModel)
    }

    var fetchLiveUpdates by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val state: DashBoardScreenState by viewModel.state.collectAsState()

    val dashBoardMetrics = state.dashboardMetrics

    LaunchedEffect(key1 = application, key2 = fetchLiveUpdates) {
        coroutineScope.coroutineContext.cancelChildren()
        viewModel.onEvent(DashBoardScreenEvent.GetSystemMetrics(application, coroutineScope, fetchLiveUpdates))
    }

    Column(modifier = modifier.fillMaxSize()) {

        ScreenTitle(titleText = "Dashboard")

        Spacer(Modifier.height(10.dp))


        when (state.loadingState) {
            is LoadingState.Loading -> LoadingScreen()
            is LoadingState.SuccessLoading -> {
                if (dashBoardMetrics == null) return

                DashBoardScreenContent(
                    modifier = modifier.padding(end = 20.dp),
                    metrics = dashBoardMetrics,
                    onFetchLiveUpdatesToggled = {
                        fetchLiveUpdates = it
                    }
                )
            }

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.exception)
        }
    }


}

@Composable
fun DashBoardScreenContent(
    modifier: Modifier = Modifier,
    metrics: DashboardMetrics,
    onFetchLiveUpdatesToggled: (liveUpdates: Boolean) -> Unit
) {

    val scrollState = rememberScrollState()


    Column(modifier = modifier.verticalScroll(scrollState).padding(bottom = 10.dp)) {

        LiveUpdatesSwitch(modifier = Modifier) {
            onFetchLiveUpdatesToggled(it)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier) {
            ApplicationStatusCard(modifier = Modifier.weight(1f), appStatus = "Up")
            Spacer(modifier = Modifier.width(20.dp))
            ApplicationUpTimeUi(
                modifier = Modifier.weight(1f),
                upTime = metrics.upTime?.getFormattedTime().orEmpty()
            )
        }


        Spacer(modifier = Modifier.height(30.dp))


        ResourceMetricCardUi(
            resourceName = "CPU Usage",
            resourcePercentage = metrics.cpuUsagePercent?.getPercentage() ?: 0F,
            formattedPercentage = metrics.cpuUsagePercent?.getFormattedPercentage(),
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )


        Spacer(modifier = Modifier.height(30.dp))


        Row(modifier = Modifier.fillMaxWidth().height(300.dp)) {

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