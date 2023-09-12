package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import common.domain.Application
import common.ui.sampleApplication
import theme.SpringMonitorTheme


@Composable
fun DeleteApplicationDialog(
    application: Application? = null,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (applicationId: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (application == null) return

    Dialog(
        visible = isDialogVisible,
        undecorated = true,
        resizable = false,
        onCloseRequest = {
            onDismiss()
        }
    ) {
        SpringMonitorTheme {
            Surface(
                modifier = Modifier.fillMaxSize().border(width = 1.dp, color = MaterialTheme.colorScheme.tertiary)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        "Delete ${application.alias} ?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { onConfirm(application.applicationId) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Delete")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { onDismiss() }) {
                            Text("Cancel")
                        }

                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DeleteApplicationDialogPreview() {
    SpringMonitorTheme {
        Surface {
            DeleteApplicationDialog(
                application = sampleApplication,
                isDialogVisible = true,
                onDismiss = {},
                onConfirm = {})
        }
    }

}