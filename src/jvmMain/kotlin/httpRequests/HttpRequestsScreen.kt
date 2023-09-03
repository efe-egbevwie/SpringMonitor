package httpRequests

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.domain.Application
import common.domain.HttpTrace
import common.ui.composables.TableCell
import common.ui.sampleHttpTrace
import kotlinx.coroutines.cancelChildren
import theme.SpringMonitorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HttpRequestsScreen(modifier: Modifier = Modifier, application: Application) {

    println("current app from requests screen: $application")
    val viewModel by remember {
        mutableStateOf(HttpTraceViewModel())
    }

    val state = viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(key1 = application) {
        coroutineScope.coroutineContext.cancelChildren()
        viewModel.onEvent(HttpTraceEvent.GetAllTraces(application, coroutineScope = coroutineScope))
    }


    Box(modifier = modifier.fillMaxSize()) {
        if (state.value.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.value.httpTraces.isNotEmpty()) {
            HttpTraceList(httpTraces = state.value.httpTraces)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HttpTraceList(modifier: Modifier = Modifier, httpTraces: List<HttpTrace>) {

    Column {
        LazyColumn {

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

            items(httpTraces) { trace ->
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
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 10.dp, top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = modifier.weight(2f)) {

            Text(
                text = httpTrace.request.requestMethod,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(corner = CornerSize(8.dp))
                )
                    .padding(8.dp)
            )

        }

        Text(text = httpTrace.request.url, modifier = modifier.weight(3f))

        Text(text = httpTrace.getFormattedTimeStamp(), modifier = modifier.weight(3f))

        Text(text = httpTrace.response.status.toString(), modifier = modifier.weight(1f))

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

@Preview
@Composable
fun HttpTraceListPreview() {
    val sampleTraces: List<HttpTrace> = List(50) { index ->
        sampleHttpTrace
    }

    SpringMonitorTheme {
        Surface {
            HttpTraceList(httpTraces = sampleTraces)
        }
    }
}

