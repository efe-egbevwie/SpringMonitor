package applicationInfo

import AppViewModels
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import applicationInfo.composables.ApplicationInfoCard
import applicationInfo.models.ApplicationInfoForUi
import common.ui.appInfoPreviewData
import common.ui.composables.ScreenTitle
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import domain.models.Application
import theme.SpringMonitorTheme


@Composable
fun ApplicationInfoScreen(application: Application, modifier: Modifier = Modifier) {

    val viewModel: ApplicationInfoViewModel by rememberSaveable {
        mutableStateOf(AppViewModels.infoViewModel)
    }


    LaunchedEffect(application) {
        viewModel.onEvent(ApplicationInfoScreenEvent.GetApplicationInfo(application))
    }

    val state: ApplicationInfoScreenState by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {

        ScreenTitle(
            titleText = "Info",
            iconVector = Icons.Filled.Refresh,
            onRefreshIconClicked = {
                viewModel.onEvent(ApplicationInfoScreenEvent.GetApplicationInfo(application, refresh = true))
            }
        )

        Spacer(modifier.height(8.dp))

        when (state.loadingState) {
            is LoadingState.Loading -> LoadingScreen()
            is LoadingState.SuccessLoading -> ApplicationInfoScreenContent(
                modifier = modifier,
                appInfo = state.buildAppInfoForUi()
            )

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.exception)
        }
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationInfoScreenContent(modifier: Modifier = Modifier, appInfo: ApplicationInfoForUi) {

    FlowColumn(
        verticalArrangement = Arrangement.Top,
        maxItemsInEachColumn = 2,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {

        appInfo.forEach { infoItem ->


            ApplicationInfoCard(
                infoItems = infoItem.value,
                infoTitle = infoItem.key,
                modifier = Modifier.fillMaxWidth(0.5f).padding(top = 10.dp, end = 10.dp, bottom = 10.dp)
            )
        }

    }


}


@Composable
@Preview
fun ApplicationInfoScreenPreview() {
    SpringMonitorTheme {

        Surface {
            ApplicationInfoScreenContent(appInfo = appInfoPreviewData)
        }

    }


}

