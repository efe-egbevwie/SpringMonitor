package applicationInfo.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import common.ui.gitInfoPreview
import domain.models.info.AppInfoDetail
import theme.SpringMonitorTheme

@Composable
fun ApplicationInfoCard(modifier: Modifier = Modifier, infoItems: List<AppInfoDetail>, infoTitle: String) {

    Card(modifier = modifier) {
        AppInfoTitle(title = infoTitle)

        Spacer(modifier = Modifier.height(10.dp))

        infoItems.forEach { item ->
            AppInfoItem(item)
        }
    }
}

@Composable
fun AppInfoItem(infoDetails: AppInfoDetail, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        SelectionContainer {
            Text(text = infoDetails.title)
        }
        SelectionContainer {
            Text(text = infoDetails.value, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun AppInfoTitle(title: String, icon: ImageVector? = null, modifier: Modifier = Modifier) {

    Row(modifier = modifier.padding(start = 10.dp, top = 10.dp)) {

        SelectionContainer {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
        }


        if (icon != null) {
            Icon(imageVector = icon, contentDescription = title)
        }
    }
}


@Composable
@Preview
fun ApplicationInfoCardPreview() {

    SpringMonitorTheme {
        Surface {
            ApplicationInfoCard(infoItems = gitInfoPreview, infoTitle = "Git")
        }
    }

}






