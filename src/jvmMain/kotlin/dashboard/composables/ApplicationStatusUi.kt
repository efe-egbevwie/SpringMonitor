package dashboard.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationStatusCard(modifier: Modifier = Modifier, appStatus: String) {
    Card(modifier = modifier) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        val statusColor = when (appStatus) {
            "Up" -> Color.Green
            else -> Color.Red
        }

        Text(
            text = appStatus,
            color = statusColor,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )
    }
}