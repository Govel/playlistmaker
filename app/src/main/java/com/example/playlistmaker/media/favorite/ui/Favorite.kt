package com.example.playlistmaker.media.favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.playlistmaker.root.ui.YpBlue
import com.example.playlistmaker.root.ui.YpGray
import com.example.playlistmaker.root.ui.yandexDisplayFonts
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils

@Composable
fun Favorite(
    onClick: (Track) -> Unit,
    state: FavoriteState
) {
    Render(state = state, onClick = onClick)
}

@Composable
fun FavoriteTracks(tracks: List<Track>, onClick: (Track) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                items = tracks,
                key = { it.trackId }
            ) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable(onClick = { onClick(track) }),
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
                            .height(45.dp)
                            .padding(start = 12.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(2.dp)),
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = track.trackName, style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
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
}

@Composable
fun FavoriteProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = YpBlue,
            strokeWidth = 4.dp,
        )
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
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ), modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Render(state: FavoriteState, onClick: (Track) -> Unit) {
    when (state) {
        is FavoriteState.Empty -> FavoriteEmpty()
        is FavoriteState.Content -> FavoriteTracks(state.tracks, onClick)
        is FavoriteState.Loading -> FavoriteProgressBar()
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    Favorite({}, FavoriteState.Loading)
}