package environmentVariables.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import common.ui.composables.TableCell
import domain.models.environment.EnvironmentVariable
import kotlinx.coroutines.launch
import theme.SpringMonitorTheme
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection


const val environmentVariableNameWeight = 2F
const val environmentVariableValueWeight = 2F
const val environmentVariableActionWeight = 1f


@Composable
fun EnvironmentVariableListUi(
    modifier: Modifier = Modifier,
    environmentVariables: List<EnvironmentVariable>,
    listState: LazyListState,
    onItemActionClicked: (value: String) -> Unit
) {
    Column(modifier = modifier) {
        EnvironmentVariableTableHeaderUi(modifier = modifier)

        LazyColumn(state = listState, modifier = modifier) {

            items(environmentVariables) { environmentVariable ->
                EnvironmentVariableTableItemUi(
                    name = environmentVariable.name,
                    value = environmentVariable.value,
                    modifier = modifier.padding(start = 6.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                    onItemActionClicked = onItemActionClicked
                )
            }
        }
    }


}


@Composable
fun EnvironmentVariableTableHeaderUi(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        TableCell(text = "Name", weight = environmentVariableNameWeight)
        TableCell(text = "Value", weight = environmentVariableValueWeight)
        TableCell(text = "Action", weight = environmentVariableActionWeight)

    }
}


@Composable
fun EnvironmentVariableTableItemUi(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    onItemActionClicked: (value: String) -> Unit
) {

    Box(modifier = modifier) {
        Row(modifier = modifier) {
            Text(text = name, modifier = Modifier.weight(environmentVariableNameWeight), textAlign = TextAlign.Start)

            Text(text = value, modifier = Modifier.weight(environmentVariableValueWeight), textAlign = TextAlign.Start)

            Icon(
                imageVector = Icons.Filled.CopyAll,
                contentDescription = null,
                modifier = Modifier
                    .weight(environmentVariableActionWeight)
                    .wrapContentSize()
                    .clickable {
                        onItemActionClicked(value)
                        setClipboard(value)
                    }
            )


        }

    }
}


fun setClipboard(s: String) {
    val selection = StringSelection(s)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}

@Composable
fun showSnackBar(showSnackBar: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val snackBarState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    SnackbarHost(hostState = snackBarState, modifier = modifier)

    if (!showSnackBar) return

    scope.launch {
        val result = snackBarState.showSnackbar(message = "Text copied", withDismissAction = true)
        when (result) {
            SnackbarResult.Dismissed -> {
                onDismiss()
            }

            else -> {}
        }
    }
}

@Composable
@Preview
fun EnvironmentVariableListUiPreview() {
    SpringMonitorTheme {
        Surface {
            EnvironmentVariableListUi(
                environmentVariables = previewEnvironmentVariables,
                listState = rememberLazyListState(),
                onItemActionClicked = {}
            )
        }
    }

}