package setupApplication.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import common.ui.modifiers.hoverClickable
import common.ui.sampleApplication
import common.ui.sampleApplications
import domain.models.Application
import theme.SpringMonitorTheme

@Composable
@Preview
fun ExistingApplicationListsUi(
    applications: List<Application>,
    modifier: Modifier = Modifier,
    onApplicationItemClicked: (Application) -> Unit,
    onEditApplicationClicked: (application: Application) -> Unit,
    onDeleteApplicationClicked: (application: Application) -> Unit
) {
    Column {
        Text("Applications", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(applications) { application ->
                ApplicationItem(
                    application,
                    onDeleteApplicationClicked = onDeleteApplicationClicked,
                    onEditApplicationClicked = onEditApplicationClicked,
                    modifier = modifier
                        .wrapContentWidth()
                        .hoverClickable(color = MaterialTheme.colorScheme.primaryContainer) {
                            onApplicationItemClicked(application)
                        }
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationItem(
    application: Application,
    modifier: Modifier = Modifier,
    onEditApplicationClicked: (application: Application) -> Unit,
    onDeleteApplicationClicked: (application: Application) -> Unit,
    showDropDownArrow: Boolean = false
) {
    Column(modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Icon(
                imageVector = Icons.Filled.Cloud,
                contentDescription = "Server icon",
                modifier = Modifier.padding(end = 10.dp)
            )


            Text(
                text = application.alias,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(2f)
            )

            Spacer(modifier = Modifier.weight(2f))
            IconButton(
                onClick = {
                    onEditApplicationClicked(application)
                },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Application")
            }

            IconButton(
                modifier = Modifier,
                onClick = {
                    onDeleteApplicationClicked(application)
                }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Application")
            }

            if (showDropDownArrow) Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = application.actuatorUrl,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun ApplicationItemPreview() {
    SpringMonitorTheme {
        Surface {
            ApplicationItem(
                application = sampleApplication,
                onDeleteApplicationClicked = {},
                onEditApplicationClicked = {})
        }
    }
}

@Preview
@Composable
fun ExistingApplicationsUiPreview() {
    SpringMonitorTheme {
        Surface {
            ExistingApplicationListsUi(
                applications = sampleApplications,
                onApplicationItemClicked = { },
                onEditApplicationClicked = {},
                onDeleteApplicationClicked = {})
        }
    }
}