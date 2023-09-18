package applicationInfo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import applicationInfo.composables.ApplicationInfoCard
import applicationInfo.models.ApplicationInfoForUi
import common.ui.appInfoPreviewData
import common.ui.composables.screens.LoadingScreen
import domain.models.Application
import theme.SpringMonitorTheme


@Composable
fun ApplicationInfoScreen(application: Application, modifier: Modifier = Modifier) {

    val viewModel by remember {
        mutableStateOf(ApplicationInfoViewModel())
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(application) {
        viewModel.onEvent(ApplicationInfoScreenEvent.GetApplictionInfo(application, scope))
    }

    val state: ApplicationInfoScreenState by viewModel.state.collectAsState()


    if (state.isLoading) {
        LoadingScreen()
    } else {
        ApplicationInfoScreenContent(modifier = modifier, appInfo = state.buildAppInfoForUi())
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationInfoScreenContent(modifier: Modifier = Modifier, appInfo: ApplicationInfoForUi) {

    Column(modifier = modifier) {
        Text(
            text = "Info",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier.height(8.dp))

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

