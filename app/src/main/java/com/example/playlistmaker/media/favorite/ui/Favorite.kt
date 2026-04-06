package com.example.playlistmaker.media.favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.YpBlack
import com.example.playlistmaker.root.ui.YpGray
import com.example.playlistmaker.root.ui.yandexDisplayFonts
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils

@Composable
fun Favorite() {
    FavoriteEmpty()
}

@Composable
fun FavoriteTracks() {
    var tracks by remember {
        mutableStateOf(
            listOf(
                Track(
                    1451960630,
                    "Горы по колено",
                    "Max Korzh",
                    239910,
                    "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/87/28/20/872820bb-3eb6-e8a3-1703-12a7874126cf/cover.jpg/100x100bb.jpg",
                    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview126/v4/11/05/11/1105112a-7f78-9bb8-93c3-9fe1aa028ac1/mzaf_11336116043782615341.plus.aac.p.m4a",
                    "Малый повзрослел, Ч. 2",
                    "2017-10-25T12:00:00Z",
                    "Hip-Hop/Rap",
                    "USA",
                    true
                ),
                Track(
                    1451960630,
                    "Горы по колено",
                    "Max Korzh",
                    239910,
                    "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/87/28/20/872820bb-3eb6-e8a3-1703-12a7874126cf/cover.jpg/100x100bb.jpg",
                    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview126/v4/11/05/11/1105112a-7f78-9bb8-93c3-9fe1aa028ac1/mzaf_11336116043782615341.plus.aac.p.m4a",
                    "Малый повзрослел, Ч. 2",
                    "2017-10-25T12:00:00Z",
                    "Hip-Hop/Rap",
                    "USA",
                    true
                ),
                Track(
                    1451960630,
                    "Горы по колено",
                    "Max Korzh",
                    239910,
                    "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/87/28/20/872820bb-3eb6-e8a3-1703-12a7874126cf/cover.jpg/100x100bb.jpg",
                    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview126/v4/11/05/11/1105112a-7f78-9bb8-93c3-9fe1aa028ac1/mzaf_11336116043782615341.plus.aac.p.m4a",
                    "Малый повзрослел, Ч. 2",
                    "2017-10-25T12:00:00Z",
                    "Hip-Hop/Rap",
                    "USA",
                    true
                ),
            )
        )
    }
    Column() {
        Spacer(modifier = Modifier.height(16.dp))
        for (track in tracks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.artworkUrl100)
                        .crossfade(true)
                        .build(),
                    contentDescription = "artwork url",
                    placeholder = painterResource(R.drawable.placeholder_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
                )

                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)) {
                    Text(
                        text = track.trackName, style = TextStyle(
                            color = YpBlack,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Normal,
                            fontFamily = yandexDisplayFonts,
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.artistName, style = TextStyle(
                                color = YpGray,
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = yandexDisplayFonts,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Icon(
                            painter = painterResource(R.drawable.dot),
                            contentDescription = null,
                            tint = YpGray
                        )
                        Text(
                            text = LocalUtils().dateFormat(track.trackTimeMillis),
                            style = TextStyle(
                                color = YpGray,
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = yandexDisplayFonts,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.arrowforward),
                    contentDescription = null,
                    tint = YpGray,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 106.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.placeholder_empty_search),
            contentDescription = stringResource(R.string.media_library_is_empty)
        )
        Text(
            stringResource(R.string.media_library_is_empty), style = TextStyle(
                color = YpBlack,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ), modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center
        )
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    Favorite()
}