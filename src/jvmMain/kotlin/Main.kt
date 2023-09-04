import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import httpRequests.HttpTraceDetailPreview
import httpRequests.HttpTraceItemPreview
import setupApplication.SetUpScreenDestination
import theme.SpringMonitorTheme
import java.awt.Dimension

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App() {
    SpringMonitorTheme {
        Surface {
            Navigator(SetUpScreenDestination) { navigator ->
                SlideTransition(navigator)
            }
        }

    }
}


fun main() = application {
    val windowState = rememberWindowState(size = DpSize.Unspecified, placement = WindowPlacement.Fullscreen)
    Window(onCloseRequest = ::exitApplication, title = "Spring Monitor", state = windowState) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}
