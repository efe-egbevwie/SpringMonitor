package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import theme.SpringMonitorTheme

@Composable
fun ScreenTitle(
    modifier: Modifier = Modifier,
    titleText: String,
    iconVector: ImageVector? = null,
    onRefreshIconClicked: () -> Unit = {}
) {

    Row (verticalAlignment = Alignment.CenterVertically, modifier = modifier){
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.width(10.dp))


        if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = "Action icon",
                modifier = Modifier.clickable { onRefreshIconClicked() })
        }
    }

}


@Composable
@Preview
fun ScreenTitleTextPreview() {
    SpringMonitorTheme {
        Surface {
            ScreenTitle(titleText = "Info", iconVector = Icons.Filled.Refresh)
        }
    }
}