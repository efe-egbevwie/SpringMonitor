package httpRequests

import AppViewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.ui.composables.ScreenTitle
import common.ui.composables.ScrollBar
import common.ui.composables.TableCell
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import common.ui.sampleHttpTrace
import domain.models.Application
import domain.models.HttpTrace
import domain.models.TraceRequest
import domain.models.TraceResponse
import theme.SpringMonitorTheme


@Composable
fun HttpRequestsScreen(modifier: Modifier = Modifier, application: Application) {


    val viewModel: HttpTraceViewModel by rememberSaveable {
        mutableStateOf(AppViewModels.httpRequestsViewModel)
    }

    val state: HttpTraceScreenState by viewModel.state.collectAsState()


    LaunchedEffect(key1 = application) {
        viewModel.onEvent(HttpTraceEvent.GetHttpTraces(application))
    }

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {

        ScreenTitle(
            titleText = "Http Trace",
            iconVector = Icons.Filled.Refresh,
            onRefreshIconClicked = {
                viewModel.onEvent(HttpTraceEvent.RefreshTraces(application))
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        when (state.loadingState) {
            is LoadingState.Loading -> LoadingScreen()
            is LoadingState.SuccessLoading -> HttpTraceList(
                modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                httpTraces = state.httpTraces,
                listState = viewModel.traceListState
            )

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.error)
        }

    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HttpTraceList(modifier: Modifier = Modifier, httpTraces: List<HttpTrace>, listState: LazyListState) {


    Box {

        LazyColumn(state = listState, modifier = modifier) {
            stickyHeader {
                Row(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(corner = CornerSize(8.dp))
                    )
                ) {
                    TableCell(text = "Request Method", weight = 2F)
                    TableCell(text = "URL", weight = 3F)
                    TableCell(text = "Time stamp", weight = 3F)
                    TableCell(text = "Status", weight = 1F)

                }
            }

            items(httpTraces, key = { trace -> trace }) { trace ->
                HttpTraceItem(httpTrace = trace, modifier = Modifier)
            }
        }

        ScrollBar(
            listState = listState,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(top = 10.dp)
        )

    }


}

@Composable
fun HttpTraceItem(modifier: Modifier = Modifier, httpTrace: HttpTrace) {

    var showTraceDetail by remember(key1 = 1) {
        mutableStateOf(false)
    }

    Column {

        HttpTraceSummaryUi(
            httpTrace,
            modifier,
            detailVisible = showTraceDetail,
            onSummaryClicked = {
                showTraceDetail = !showTraceDetail
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        HttpTraceDetail(
            trace = httpTrace,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(start = 20.dp, end = 20.dp),
            isVisible = showTraceDetail
        )
    }
}


@Composable
fun HttpTraceSummaryUi(
    httpTrace: HttpTrace,
    modifier: Modifier = Modifier,
    detailVisible: Boolean = false,
    onSummaryClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 30.dp)
            .clickable {
                onSummaryClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = modifier.weight(2f)) {

            Text(
                text = httpTrace.request.requestMethod,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(corner = CornerSize(8.dp))
                )
                    .padding(8.dp)
            )

        }

        Text(text = httpTrace.request.url, modifier = modifier.weight(3f))

        Text(text = httpTrace.getFormattedTimeStamp(), modifier = modifier.weight(3f))

        val responseCodeColor = when (httpTrace.response.status.toInt()) {
            200 -> Color(red = 0, green = 95, blue = 53)
            500, 401 -> Color.Red
            else -> Color(red = 95, green = 85, blue = 0)
        }

        Box(
            modifier = modifier.weight(1f).padding(start = 10.dp)
        ) {
            Text(
                text = httpTrace.response.status.toString(),
                modifier = modifier.background(responseCodeColor, shape = RoundedCornerShape(corner = CornerSize(8.dp)))
                    .padding(8.dp)
            )
        }


        IconButton(onClick = {}, modifier = Modifier.weight(0.1f)) {
            val iconVector = when (detailVisible) {
                true -> Icons.Filled.ArrowUpward
                false -> Icons.Filled.ArrowDownward
            }
            Icon(imageVector = iconVector, contentDescription = "Show request details")
        }

    }

}

@Composable
fun HttpTraceDetail(trace: HttpTrace, modifier: Modifier = Modifier, isVisible: Boolean = false) {
    val tabTitles = listOf("Request", "Response")

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }


    val expandingTransition = remember {
        expandVertically(expandFrom = Alignment.Top, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
    }

    val closeTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandingTransition,
        exit = closeTransition,
    ) {

        Column(modifier = modifier.fillMaxWidth()) {

            TabRow(selectedTabIndex = selectedTabIndex) {

                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(text = title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTabIndex) {
                0 -> HttpTraceRequestUi(traceRequest = trace.request)
                1 -> HttpTraceResponseUi(traceResponse = trace.response)
            }


        }
    }


}

@Composable
fun HttpTraceRequestUi(traceRequest: TraceRequest) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(30.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Request Method:", style = MaterialTheme.typography.titleMedium)
            Text(traceRequest.requestMethod, style = MaterialTheme.typography.bodyMedium)
        }


        Row(horizontalArrangement = Arrangement.spacedBy(30.dp), modifier = Modifier.fillMaxWidth()) {
            Text("URL", style = MaterialTheme.typography.titleMedium)
            Text(traceRequest.url, style = MaterialTheme.typography.bodyMedium)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(30.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Host", style = MaterialTheme.typography.titleMedium)
            Text(traceRequest.host.toString(), style = MaterialTheme.typography.bodyMedium)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(30.dp), modifier = Modifier.fillMaxWidth()) {
            Text("User agent", style = MaterialTheme.typography.titleMedium)
            Text(traceRequest.userAgent.toString(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun HttpTraceResponseUi(traceResponse: TraceResponse) {
    Column {
        Row {
            Text("Response code")
            Spacer(modifier = Modifier.width(20.dp))
            Text(traceResponse.status.toString())
        }


        Row {
            Text("Headers")
            Spacer(modifier = Modifier.width(20.dp))
            Text(traceResponse.headers.toString())
        }
    }
}


@Preview
@Composable
fun HttpTraceListPreview() {
    val sampleTraces: List<HttpTrace> = List(50) {
        sampleHttpTrace
    }

    SpringMonitorTheme {
        Surface {
            HttpTraceList(httpTraces = sampleTraces, listState = rememberLazyListState())
        }
    }
}

@Composable
@Preview
fun HttpTraceSummaryPreview() {
    HttpTraceSummaryUi(httpTrace = sampleHttpTrace, onSummaryClicked = {})
}

@Composable
@Preview
fun HttpTraceDetailPreview() {
    HttpTraceDetail(trace = sampleHttpTrace)
}

@Preview
@Composable
fun HttpTraceItemPreview() {
    SpringMonitorTheme {

        Surface(modifier = Modifier.fillMaxSize()) {
            HttpTraceItem(httpTrace = sampleHttpTrace)
        }

    }
}

