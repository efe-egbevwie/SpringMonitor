package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.sampleApplication
import domain.models.Application
import theme.SpringMonitorTheme

@Composable
fun ApplicationsDropDownItem(
    application: Application,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Filled.Cloud, contentDescription = "Server icon")
            Text(text = application.alias, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = application.actuatorUrl,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
@Preview
fun ApplicationsDropDownItemPreview() {
    SpringMonitorTheme {
        Surface {
            ApplicationsDropDownItem(application = sampleApplication)
        }
    }
}