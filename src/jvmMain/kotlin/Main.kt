import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import setupApplication.SetUpScreenDestination
import theme.SpringMonitorTheme

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
    val windowState = rememberWindowState(size = DpSize.Unspecified)
    Window(onCloseRequest = ::exitApplication, title = "Spring Monitor", state = windowState) {
        App()
    }
}
