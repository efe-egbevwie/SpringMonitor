package environmentVariables

import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.composables.ScreenTitle
import common.ui.composables.screens.ErrorScreen
import common.ui.composables.screens.LoadingScreen
import common.ui.models.LoadingState
import domain.models.Application
import domain.models.environment.EnvironmentVariable
import environmentVariables.composables.EnvironmentVariableListUi
import environmentVariables.composables.previewEnvironmentVariables
import kotlinx.coroutines.launch
import theme.SpringMonitorTheme
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

@Composable
fun EnvironmentVariablesScreen(
    application: Application,
    modifier: Modifier = Modifier
) {


    val viewModel by remember {
        mutableStateOf(EnvironmentVariablesViewModel())
    }

    val state by viewModel.state.collectAsState()

    val scope = rememberCoroutineScope()

    var refreshContent by remember {
        mutableStateOf(false)
    }

    var showValueCopiedSnackBar by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = application, key2 = refreshContent) {
        viewModel.onEvent(EnvironmentVariablesScreenEvent.GetEnvironmentVariables(application, scope))
    }

    Column(modifier = modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(),
            verticalAlignment = Alignment.Top
        ) {
            ScreenTitle(
                titleText = "Environment Variables",
                iconVector = Icons.Filled.Refresh,
                onIconClicked = {
                    refreshContent = true
                }
            )

            Spacer(modifier = Modifier.weight(2f))

            if (showValueCopiedSnackBar) {
                ValueCopiedSnackBar(
                    showSnackBar = true,
                    onDismiss = { showValueCopiedSnackBar = false },
                    modifier = modifier
                )
            }

        }


        Spacer(modifier = Modifier.height(10.dp))

        when (state.loadingState) {
            is LoadingState.Loading -> LoadingScreen()
            is LoadingState.SuccessLoading ->
                EnvironmentVariablesScreenContent(
                    modifier = modifier.padding(end = 16.dp),
                    environmentVariables = state.environmentVariables,
                    onCopyValueItemClicked = { value ->
                        copyEnvironmentVariableToClipBoard(value)
                        showValueCopiedSnackBar = true
                    }
                )

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.exception)
        }

    }


}


@Composable
fun EnvironmentVariablesScreenContent(
    modifier: Modifier = Modifier,
    environmentVariables: List<EnvironmentVariable>,
    onCopyValueItemClicked: (value: String) -> Unit
) {
    Box(modifier = modifier) {

        val listState = rememberLazyListState()

        EnvironmentVariableListUi(
            environmentVariables = environmentVariables,
            listState = listState,
            onItemActionClicked = onCopyValueItemClicked
        )


        VerticalScrollbar(
            style = ScrollbarStyle(
                minimalHeight = 40.dp,
                thickness = 8.dp,
                hoverDurationMillis = 0,
                shape = RoundedCornerShape(8.dp),
                unhoverColor = MaterialTheme.colorScheme.secondary,
                hoverColor = MaterialTheme.colorScheme.primary

            ),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(top = 10.dp),
            adapter = rememberScrollbarAdapter(
                scrollState = listState
            )
        )
    }
}


@Composable
fun ValueCopiedSnackBar(
    showSnackBar: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {

    if (!showSnackBar) return

    val snackBarState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    SnackbarHost(hostState = snackBarState, modifier = modifier.fillMaxWidth(0.3f))


    scope.launch {
        val snackBarResult = snackBarState.showSnackbar(
            message = "Value copied",
            withDismissAction = true,
            duration = SnackbarDuration.Short
        )
        when (snackBarResult) {
            SnackbarResult.Dismissed -> {
                onDismiss()
            }

            else -> {}
        }
    }
}


private fun copyEnvironmentVariableToClipBoard(variable: String) {
    val selection = StringSelection(variable)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}

@Composable
@Preview
fun EnvironmentVariablesScreenPreview() {
    SpringMonitorTheme {
        Surface {
            EnvironmentVariablesScreenContent(
                environmentVariables = previewEnvironmentVariables,
                onCopyValueItemClicked = {}
            )
        }
    }
}
