package setupApplication.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import theme.SpringMonitorTheme

@Composable
fun HomeScreenDescription(modifier: Modifier = Modifier.fillMaxHeight()) {
    Box(modifier = modifier.fillMaxHeight()) {
        SpringMonitorLogo(modifier = Modifier.align(Alignment.TopStart).padding(top = 50.dp))

        Text(
            "Monitor your spring applications through the spring boot actuator",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 50.dp)
        )


    }
}

@Composable
fun SpringMonitorLogo(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 50.dp).wrapContentWidth()
    ) {
        Image(
            painter = painterResource("images/spring-logo.svg"),
            contentDescription = "spring logo"
        )

        Text(
            text = "Monitor",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
        )
    }
}


@Preview
@Composable
fun HomeScreenDescriptionPreview() {
    SpringMonitorTheme {
        Surface {
            HomeScreenDescription()
        }

    }

}


@Composable
@Preview
fun SpringMonitorLogoPreview() {
    SpringMonitorTheme {
        Surface {
            SpringMonitorLogo()
        }
    }
}