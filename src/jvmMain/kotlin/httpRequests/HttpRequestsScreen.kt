package httpRequests

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.domain.Application
import common.domain.HttpTrace
import common.domain.TraceRequest
import common.domain.TraceResponse
import common.ui.composables.LiveUpdatesCheckBox
import common.ui.composables.TableCell
import common.ui.sampleHttpTrace
import kotlinx.coroutines.cancelChildren
import theme.SpringMonitorTheme


@Composable
fun HttpRequestsScreen(modifier: Modifier = Modifier, application: Application) {

    println("current app from requests screen: $application")

    var showLiveUpdates by remember {
        mutableStateOf(false)
    }

    val viewModel by remember {
        mutableStateOf(HttpTraceViewModel())
    }

    val state = viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(key1 = application, key2 = showLiveUpdates) {
        coroutineScope.coroutineContext.cancelChildren()
        viewModel.onEvent(
            HttpTraceEvent.GetAllTraces(
                application,
                coroutineScope = coroutineScope,
                liveUpdates = showLiveUpdates
            )
        )
    }

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {

        LiveUpdatesCheckBox(modifier = modifier) { fetchLiveUpdates ->
            showLiveUpdates = fetchLiveUpdates
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxSize()) {

            if (state.value.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.value.httpTraces.isNotEmpty()) {
                HttpTraceList(
                    modifier = Modifier.fillMaxSize(),
                    httpTraces = state.value.httpTraces,
                )
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HttpTraceList(modifier: Modifier = Modifier, httpTraces: List<HttpTrace>) {

    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        LazyColumn(state = listState) {

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

            items(httpTraces, key = { trace -> trace.timeStamp }) { trace ->
                HttpTraceItem(httpTrace = trace, modifier = Modifier)
            }
        }
    }

}

@Composable
fun HttpTraceHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Request Method", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(100.dp))
        Text(text = "URl", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(300.dp))
        Text(text = "TimeStamp", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(100.dp))
        Text(text = "Response Code", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun HttpTraceItem(modifier: Modifier = Modifier, httpTrace: HttpTrace) {

    var showTraceDetail by remember {
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


@Preview
@Composable
fun HttpTraceItemPreview() {
    SpringMonitorTheme {

        Surface(modifier = Modifier.fillMaxSize()) {
            HttpTraceItem(httpTrace = sampleHttpTrace)
        }

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
            HttpTraceList(httpTraces = sampleTraces)
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

