package environmentVariables

import AppViewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
    val viewModel: EnvironmentVariablesViewModel by rememberSaveable {
        mutableStateOf(AppViewModels.environmentVariableViewModel)
    }
    val state: EnvironmentVariablesScreenState by viewModel.state.collectAsState()
    var showValueCopiedSnackBar by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = application) {
        viewModel.onEvent(EnvironmentVariablesScreenEvent.GetEnvironmentVariables(application))
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
                onRefreshIconClicked = {
                    viewModel.onEvent(EnvironmentVariablesScreenEvent.RefreshEnvironmentVariables(application))
                }
            )

            Spacer(modifier = Modifier.weight(2f))

            ValueCopiedSnackBar(
                showSnackBar = showValueCopiedSnackBar,
                onDismiss = { showValueCopiedSnackBar = false },
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (state.loadingState) {
            is LoadingState.Loading -> LoadingScreen()
            is LoadingState.SuccessLoading ->
                AnimatedVisibility(visible = true) {
                    EnvironmentVariablesScreenContent(
                        modifier = modifier.padding(end = 16.dp),
                        environmentVariables = state.environmentVariables,
                        onCopyValueItemClicked = { value ->
                            copyEnvironmentVariableToClipBoard(value)
                            showValueCopiedSnackBar = true
                        },
                        searchKeyWord = state.searchKeyWord,
                        onSearchKeyWordChanged = { keyword ->
                            viewModel.onEvent(EnvironmentVariablesScreenEvent.FilterEnvironmentVariables(keyword))
                        }
                    )

                }

            is LoadingState.FailedToLoad -> ErrorScreen(exception = state.exception)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnvironmentVariablesScreenContent(
    modifier: Modifier = Modifier,
    environmentVariables: List<EnvironmentVariable>,
    onCopyValueItemClicked: (value: String) -> Unit,
    searchKeyWord: String,
    onSearchKeyWordChanged: (String) -> Unit
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier,
            value = searchKeyWord,
            onValueChange = {
                onSearchKeyWordChanged(it)
            },
            label = {
                Text("Search")
            },
            trailingIcon = {
                if (searchKeyWord.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onSearchKeyWordChanged("")
                        }
                    ) {
                        Icon(Icons.Filled.Cancel, contentDescription = "Clear filter")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (list, horizontalScrollBar, verticalScollBar) = createRefs()

            Box(modifier = Modifier.constrainAs(list) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end, margin = 20.dp)
            }
                .wrapContentWidth(align = Alignment.Start)
            ) {
                EnvironmentVariableListUi(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(bottom = 8.dp, end = 8.dp)
                        .align(Alignment.TopStart),
                    environmentVariables = environmentVariables,
                    verticalScrollState = verticalScrollState,
                    horizontalScrollState = horizontalScrollState,
                    onItemActionClicked = { value ->
                        onCopyValueItemClicked(value)
                    }
                )
            }


            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(verticalScrollState),
                modifier = Modifier.constrainAs(verticalScollBar) {
                    start.linkTo(list.end)
                    end.linkTo(list.end)
                    top.linkTo(list.top)
                },
                style = ScrollbarStyle(
                    minimalHeight = 40.dp,
                    thickness = 8.dp,
                    hoverDurationMillis = 0,
                    shape = RoundedCornerShape(6.dp),
                    unhoverColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    hoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            )

            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(horizontalScrollState),
                modifier = Modifier.padding(bottom = 8.dp).constrainAs(horizontalScrollBar) {
                    top.linkTo(list.bottom)
                    bottom.linkTo(list.bottom)
                    start.linkTo(list.start)
                    end.linkTo(list.end)
                },
                style = ScrollbarStyle(
                    minimalHeight = 20.dp,
                    thickness = 8.dp,
                    hoverDurationMillis = 0,
                    shape = RoundedCornerShape(8.dp),
                    unhoverColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    hoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )

        }
    }
}


@Composable
private fun ValueCopiedSnackBar(
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
                onCopyValueItemClicked = {},
                searchKeyWord = "",
                onSearchKeyWordChanged = {}
            )
        }
    }
}
