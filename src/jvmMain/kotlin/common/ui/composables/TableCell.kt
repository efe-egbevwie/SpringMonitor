package common.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text = text, Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .weight(weight)
            .padding(6.dp)
    )
}