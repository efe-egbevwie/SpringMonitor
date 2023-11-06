import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
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

    val httpRequestsViewModel:HttpTraceViewModel by lazy {
        HttpTraceViewModel()
    }
}
