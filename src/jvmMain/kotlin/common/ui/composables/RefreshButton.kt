package common.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RefreshButton(modifier: Modifier = Modifier, onReload: () -> Unit) {

    Row (modifier, verticalAlignment = Alignment.CenterVertically){
        IconButton(onClick = { onReload() }) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = "Refresh", style = MaterialTheme.typography.titleLarge)
    }
}