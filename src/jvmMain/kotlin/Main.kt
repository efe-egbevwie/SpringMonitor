import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import setupApplication.SetUpScreenDestination
import setupApplication.composables.ExistingApplicationsUiPreview
import theme.SpringMonitorTheme
import java.awt.Dimension
import kotlin.time.Duration.Companion.seconds

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
    val windowState = rememberWindowState(size = DpSize.Unspecified, placement = WindowPlacement.Maximized)
    Window(onCloseRequest = ::exitApplication, title = "Spring Monitor", state = windowState) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}
