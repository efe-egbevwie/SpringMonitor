package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theme.SpringMonitorTheme

@Composable
fun LiveUpdatesSwitch(
    modifier: Modifier = Modifier,
    onLiveUpdateToggled: (shouldFetchLiveUpdate: Boolean) -> Unit
) {

    var shouldLoadLiveTraces by remember {
        mutableStateOf(false)
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Switch(
            checked = shouldLoadLiveTraces,
            onCheckedChange = { checkedValue ->
                shouldLoadLiveTraces = checkedValue
                onLiveUpdateToggled(checkedValue)
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = "LIVE updates", style = MaterialTheme.typography.titleLarge)

    }
}


@Composable
@Preview
fun LiveUpdatesSwitchPreview() {
    SpringMonitorTheme {
        Surface {
            LiveUpdatesSwitch { _: Boolean ->

            }
        }

    }

}