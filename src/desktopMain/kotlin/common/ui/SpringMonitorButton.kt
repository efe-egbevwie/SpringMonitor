package common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theme.SpringMonitorTheme

@Composable
fun ProgressButton(
    onclick: () -> Unit,
    buttonText: String,
    isLoading: Boolean = false
) {

    Button(
        contentPadding = PaddingValues(20.dp),
        onClick = {
            if (isLoading) return@Button
            onclick()
        },
        modifier = Modifier.fillMaxWidth(0.4f)
    ) {
        if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp)) else Text(text = buttonText)
    }
}


@Preview
@Composable
fun SpringMonitorButtonPreview() {
    SpringMonitorTheme {
        ProgressButton(onclick = {}, buttonText = "Set Up", isLoading = false)
    }

}