package com.example.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Switch
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.Header
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.root.ui.YpBlue
import com.example.playlistmaker.root.ui.YpBlueLight
import com.example.playlistmaker.root.ui.YpGray
import com.example.playlistmaker.root.ui.YpLightGray
import com.example.playlistmaker.root.ui.yandexDisplayFonts

@Composable
fun Settings() {
    var isChecked by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.primary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp)
        ) {
            Header(stringResource(R.string.settings))
            Spacer(modifier = Modifier.height(24.dp))
            DarkTheme(isChecked = isChecked) { isChecked }
            SettingsButton(stringResource(R.string.share_app), painterResource(R.drawable.share)) {}
            SettingsButton(stringResource(R.string.write_in_support), painterResource(R.drawable.support)) {}
            SettingsButton(stringResource(R.string.user_agreement), painterResource(R.drawable.arrowforward)) {}
        }
    }

}


@Composable
fun SettingsButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1F)) {
            Text(
                text = text,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Box(
            modifier = Modifier.weight(1F).padding(end = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = icon,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = text
            )
        }
    }
}

@Composable
fun DarkTheme(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1F)) {
            Text(
                text = stringResource(R.string.dark_theme),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Box(
            modifier = Modifier.weight(1F).padding(end = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = YpBlue,
                    checkedTrackColor = YpBlueLight,
                    uncheckedThumbColor = YpGray,
                    uncheckedTrackColor = YpLightGray
                )
            )
        }
    }
}


@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    PlaylistMakerTheme(darkTheme = false) {
        Settings()
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewDark() {
    PlaylistMakerTheme(darkTheme = true) {
        Settings()
    }
}