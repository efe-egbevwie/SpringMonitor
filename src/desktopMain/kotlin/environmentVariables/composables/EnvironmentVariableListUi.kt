package environmentVariables.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.efe.dataTable.DataTable
import domain.models.environment.EnvironmentVariable
import theme.SpringMonitorTheme
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

@Composable
fun EnvironmentVariableListUi(
    modifier: Modifier = Modifier,
    environmentVariables: List<EnvironmentVariable>,
    verticalScrollState: ScrollState,
    horizontalScrollState: ScrollState,
    onItemActionClicked: (value: String) -> Unit
) {
    var tableWidth by remember {
        mutableStateOf(0)
    }
    BoxWithConstraints(modifier = modifier) {
        DataTable(
            tableModifier = Modifier.onGloballyPositioned {
                tableWidth = it.size.width
            },
            columnCount = 3,
            verticalScrollState = verticalScrollState,
            horizontalScrollState = horizontalScrollState,
            rowCount = environmentVariables.size,
            headerBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            tableBackgroundColor = MaterialTheme.colorScheme.surface,
            tableHeaderContent = { columnIndex ->
                val columnTitle = when (columnIndex) {
                    0 -> "Name"
                    1 -> "Value"
                    2 -> "Action"
                    else -> ""
                }
                Text(text = columnTitle, modifier = Modifier.padding(8.dp))
            },
            divider = { rowWidth ->
                Divider(
                    color = Color.White,
                    modifier = Modifier.width(rowWidth).padding(top = 8.dp, bottom = 8.dp)
                )
            },
            cellContent = { columnIndex, rowIndex ->
                val cellText = when (columnIndex) {
                    0 -> environmentVariables[rowIndex].name
                    1 -> environmentVariables[rowIndex].value
                    else -> ""
                }

                when (columnIndex) {
                    0, 1 -> SelectionContainer {
                        Text(
                            text = cellText.breakIntoLines(),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    2 -> Icon(
                        imageVector = Icons.Filled.CopyAll,
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(8.dp)
                            .clickable {
                                val value = environmentVariables[rowIndex].value
                                onItemActionClicked(value)
                                setClipboard(value)
                            }
                    )
                }
            }
        )
    }
}

fun String.breakIntoLines(maxLength: Int = 300): String {
    val lines = mutableListOf<String>()
    var index = 0

    while (index < this.length) {
        // Determine the end index of the current segment
        val endIndex = (index + maxLength).coerceAtMost(this.length)
        val segment = this.substring(index, endIndex)

        if (segment.contains(" ") && endIndex < this.length) {
            // Break at the last space within the segment
            val lastSpace = segment.lastIndexOf(' ')
            if (lastSpace != -1) {
                lines.add(segment.substring(0, lastSpace))
                index += lastSpace + 1
                continue
            }
        }

        // If no spaces are found, or we're at the end, take the full segment
        lines.add(segment)
        index += maxLength
    }

    return lines.joinToString("\n")
}


private fun setClipboard(s: String) {
    val selection = StringSelection(s)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}

@Composable
@Preview
fun EnvironmentVariableListUiPreview() {
    SpringMonitorTheme {
        Surface {
            EnvironmentVariableListUi(
                environmentVariables = previewEnvironmentVariables,
                verticalScrollState = rememberScrollState(),
                horizontalScrollState = rememberScrollState(),
                onItemActionClicked = {}
            )
        }
    }
}