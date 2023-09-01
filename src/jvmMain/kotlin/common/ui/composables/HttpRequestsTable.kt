package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable
import common.domain.HttpTrace
import common.ui.sampleHttpTraceList
import theme.SpringMonitorTheme

@Composable
fun HttpTraceTable(traceList: List<HttpTrace>, modifier: Modifier = Modifier) {

    val scrollableState = rememberScrollState()
    DataTable(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
            .scrollable(state = scrollableState, orientation = Orientation.Vertical),
        columns = listOf(
            DataColumn {
                Text(text = "Request Method", style = MaterialTheme.typography.titleLarge)
            },
            DataColumn {
                Text(text = "URl", style = MaterialTheme.typography.titleLarge)
            },
            DataColumn {
                Text(text = "TimeStamp	", style = MaterialTheme.typography.titleLarge)
            },
            DataColumn {
                Text(text = "Response Code", style = MaterialTheme.typography.titleLarge)
            }
        ),
        separator = {
            Divider(modifier = Modifier.height(1.dp))
        },
        content = {
            traceList.forEach { trace ->

                row {
                    cell {
                        Text(text = trace.request.requestMethod)
                    }

                    cell {
                        Text(text = trace.request.url)
                    }

                    cell {
                        Text(text = trace.getFormattedTimeStamp())
                    }

                    cell {
                        Text(text = trace.response.status.toString())
                    }
                }
            }

        }
    )
}


@Preview
@Composable
fun HttpTracePreview() {
    SpringMonitorTheme {
        Surface {
            HttpTraceTable(traceList = sampleHttpTraceList)
        }
    }
}