package common.ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theme.SpringMonitorTheme

@Composable
fun ScrollBar(listState: LazyListState, modifier: Modifier = Modifier) {

    VerticalScrollbar(
        style = ScrollbarStyle(
            minimalHeight = 40.dp,
            thickness = 8.dp,
            hoverDurationMillis = 0,
            shape = RoundedCornerShape(8.dp),
            unhoverColor = MaterialTheme.colorScheme.secondary,
            hoverColor = MaterialTheme.colorScheme.primary

        ),
        modifier = modifier,
        adapter = rememberScrollbarAdapter(
            scrollState = listState
        )
    )

}

@Composable
@Preview
fun ScrollBarPreview() {
    SpringMonitorTheme {
        Surface {
            ScrollBar(listState = rememberLazyListState(), modifier = Modifier.size(100.dp))
        }
    }

}