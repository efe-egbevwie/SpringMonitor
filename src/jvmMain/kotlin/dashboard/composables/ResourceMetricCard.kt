package dashboard.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import common.ui.composables.GaugeChart
import theme.SpringMonitorTheme


@Composable
fun ResourceMetricCardUi(
    resourceName: String,
    resourcePercentage: Float,
    formattedPercentage: String? = null,
    modifier: Modifier = Modifier
) {

    Card(modifier = modifier.padding(top = 10.dp, bottom = 20.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = resourceName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(30.dp))

//            androidx.compose.material.CircularProgressIndicator(
//                progress = 0.7f,
//                color = Color(0xFF306C00),
//                modifier = Modifier.size(70.dp),
//                backgroundColor = Color(0xFF90D960)
//            )

            GaugeChart(
                percentValue = resourcePercentage,
                modifier = Modifier.fillMaxHeight(0.70f).fillMaxWidth(0.70f),
                primaryColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "$formattedPercentage", style = MaterialTheme.typography.bodyLarge)
        }


    }
}


@Preview
@Composable
fun ResourceMetricCardUiPreview() {
    SpringMonitorTheme {
        Surface {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ResourceMetricCardUi(
                    resourceName = "CPU usage",
                    resourcePercentage = 100f,
                    modifier = Modifier.size(500.dp)
                )
            }

        }
    }

}