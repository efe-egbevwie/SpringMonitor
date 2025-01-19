import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import applicationInfo.ApplicationInfoViewModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dashboard.DashboardViewModel
import environmentVariables.EnvironmentVariablesViewModel
import httpRequests.HttpTraceViewModel
import setupApplication.SetUpScreenDestination
import theme.SpringMonitorTheme
import java.awt.Dimension

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


object AppViewModels {
    val environmentVariableViewModel: EnvironmentVariablesViewModel by lazy {
        EnvironmentVariablesViewModel()
    }

    val dashBoardViewModel: DashboardViewModel by lazy {
        DashboardViewModel()
    }

    val infoViewModel: ApplicationInfoViewModel by lazy {
        ApplicationInfoViewModel()
    }

    val httpRequestsViewModel: HttpTraceViewModel by lazy {
        HttpTraceViewModel()
    }
}


@Composable
fun ResizableContainer() {
    var containerWidth by remember { mutableStateOf(200.dp) }
    val minWidth = 100.dp
    val maxWidth = 400.dp

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(containerWidth)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        // Add content inside the container
        Text(
            text = "Resizable Container",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge
        )

        // Resizable handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(10.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            change?.consume() // Consume the gesture
                        }
                    }
                }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        containerWidth = (containerWidth + delta.toDp())

                    }
                )
        )
    }
}

// Helper to convert Float to Dp
fun Float.toDp(): Dp = (this / 1f).dp