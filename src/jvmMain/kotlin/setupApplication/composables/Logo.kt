package setupApplication.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import common.domain.Application
import common.ui.sampleApplication
import common.ui.sampleApplications
import theme.SpringMonitorTheme

@Composable
fun HomeScreenDescription(modifier: Modifier = Modifier.fillMaxHeight(), existingApplications: List<Application>) {

    Column(modifier = modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(30.dp)) {


        SpringMonitorLogo(modifier = Modifier.padding(top = 50.dp))

        Text(
            "Monitor your spring applications through the spring boot actuator",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
        )

        ExistingApplicationsUi(
            applications = existingApplications,
            onApplicationItemClicked = {
                println("application is $it")
            },
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .padding(bottom = 20.dp)
        )


    }
}

@Composable
fun SpringMonitorLogo(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.wrapContentWidth()
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


@Composable
@Preview
fun ExistingApplicationsUi(
    applications: List<Application>,
    modifier: Modifier = Modifier,
    onApplicationItemClicked: (Application) -> Unit
) {
    LazyColumn {
        items(applications) { application ->
            ApplicationItem(
                application,
                modifier = modifier
                    .clickable { onApplicationItemClicked(application) }
                    .padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun ApplicationItem(
    application: Application,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row {
            Icon(imageVector = Icons.Filled.Cloud, contentDescription = "Server icon")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = application.alias, style = MaterialTheme.typography.titleMedium)
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
fun HomeScreenDescriptionPreview() {
    SpringMonitorTheme {
        Surface {
            HomeScreenDescription(existingApplications = sampleApplications)
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


@Preview
@Composable
fun ApplicationItemPreview() {
    SpringMonitorTheme {
        Surface {
            ApplicationItem(application = sampleApplication)
        }
    }
}


@Preview
@Composable
fun ExistingApplicationsUiPreview() {
    SpringMonitorTheme {
        Surface {
            ExistingApplicationsUi(applications = sampleApplications, onApplicationItemClicked = { application -> })
        }
    }
}