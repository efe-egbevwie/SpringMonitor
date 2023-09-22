package common.ui.composables.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import theme.SpringMonitorTheme

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    SpringMonitorTheme {
        Surface {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}


@Composable
@Preview
fun LoadingScreenPreview() {
    LoadingScreen()
}