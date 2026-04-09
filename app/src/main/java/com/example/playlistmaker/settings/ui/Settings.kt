package com.example.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Switch
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun Settings(
    isChecked: Boolean,
    themeSwitcher: (Boolean) -> Unit,
    shareApp: () -> Unit,
    writeInSupport: () -> Unit,
    userAgreement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(stringResource(R.string.settings))
        Spacer(modifier = Modifier.height(24.dp))
        DarkTheme(isChecked = isChecked, onCheckedChange = themeSwitcher)
        SettingsButton(
            text = stringResource(R.string.share_app),
            icon = painterResource(R.drawable.share),
            onClick = shareApp
        )
        SettingsButton(
            text = stringResource(R.string.write_in_support),
            icon = painterResource(R.drawable.support),
            onClick = writeInSupport
        )
        SettingsButton(
            text = stringResource(R.string.user_agreement),
            icon = painterResource(R.drawable.arrowforward),
            onClick = userAgreement
        )
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
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = icon,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = text
        )

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
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.dark_theme),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { newValue ->
                onCheckedChange(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = YpBlue,
                checkedTrackColor = YpBlueLight,
                uncheckedThumbColor = YpGray,
                uncheckedTrackColor = YpLightGray
            )
        )
    }
}


@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    PlaylistMakerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Settings(false, {}, {}, {}, {})
        }
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewDark() {
    PlaylistMakerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Settings(true, {}, {}, {}, {})
        }
    }
}